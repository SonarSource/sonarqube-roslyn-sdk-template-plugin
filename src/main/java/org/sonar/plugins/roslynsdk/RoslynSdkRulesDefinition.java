/*
 * SonarQube Roslyn SDK
 * Copyright (C) 2016-2021 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
        "SQALE Model is deprecated and not supported anymore by SonarQube. "
          + "Please rely on SonarQube rules definition XML format. "
          + "'SqaleXmlResourcePath' property will be ignored."));

    repository.done();
  }
}
