/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2018 SonarSource SA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.sonar.plugins.roslynsdk;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.input.BOMInputStream;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class RoslynSdkRulesDefinition implements RulesDefinition {

  private RoslynSdkConfiguration config;
  private static final Logger LOG = Loggers.get(RoslynSdkRulesDefinition.class);

  public RoslynSdkRulesDefinition(RoslynSdkConfiguration config) {
    this.config = config;
  }

  @Override
  public void define(Context context) {
    NewRepository repository = context
      .createRepository(config.mandatoryProperty("RepositoryKey"), config.mandatoryProperty("RepositoryLanguage"))
      .setName(config.mandatoryProperty("RepositoryName"));

    String rulesXmlResourcePath = config.mandatoryProperty("RulesXmlResourcePath");

    try (InputStreamReader rulesReader = new InputStreamReader(new BOMInputStream(getClass().getResourceAsStream(rulesXmlResourcePath)), StandardCharsets.UTF_8)) {
      new RulesDefinitionXmlLoader().load(repository, rulesReader);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    config
      .property("SqaleXmlResourcePath")
      .ifPresent(sqaleXmlResourcePath -> LOG.warn(
        "SQALE Model is deprecated and not supported anymore by SonarQube."
          + "Please rely on SonarQube rules definition XML format. "
          + "'SqaleXmlResourcePath' property will be ignored."));

    repository.done();
  }
}
