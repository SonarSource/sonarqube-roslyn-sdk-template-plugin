/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2016 SonarSource SA
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

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.io.InputStreamReader;
import org.sonar.api.BatchExtension;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.squidbridge.rules.SqaleXmlLoader;

public class RoslynSdkRulesDefinition implements RulesDefinition, BatchExtension {

  private RoslynSdkConfiguration config;

  public RoslynSdkRulesDefinition(RoslynSdkConfiguration config) {
    this.config = config;
  }

  @Override
  public void define(Context context) {
    NewRepository repository = context
      .createRepository(config.mandatoryProperty("RepositoryKey"), config.mandatoryProperty("RepositoryLanguage"))
      .setName(config.mandatoryProperty("RepositoryName"));

    String rulesXmlResourcePath = config.mandatoryProperty("RulesXmlResourcePath");
    try (InputStreamReader rulesReader = new InputStreamReader(getClass().getResourceAsStream(rulesXmlResourcePath), Charsets.UTF_8)) {
      RulesDefinitionXmlLoader loader = new RulesDefinitionXmlLoader();
      loader.load(repository, rulesReader);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }

    String sqaleXmlResourcePath = config.property("SqaleXmlResourcePath");
    if (sqaleXmlResourcePath != null) {
      SqaleXmlLoader.load(repository, sqaleXmlResourcePath);
    }

    repository.done();
  }

}
