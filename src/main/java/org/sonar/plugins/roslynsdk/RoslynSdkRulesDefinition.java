/*
 * SonarQube Roslyn SDK Template Plugin
 * Copyright (C) 2016-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
