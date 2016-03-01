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

import com.google.common.collect.ImmutableMap;
import javax.annotation.Nullable;
import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition.Context;
import org.sonar.api.server.rule.RulesDefinition.Repository;
import org.sonar.api.server.rule.RulesDefinition.Rule;

import static org.fest.assertions.Assertions.assertThat;

public class RoslynSdkRulesDefinitionTest {

  @Test
  public void test_rules_defined_with_sqale() {
    rulesDefinedWithSqaleXml("/org/sonar/plugins/roslynsdk/sqale.xml");
  }

  @Test
  public void test_rules_defined_without_sqale() {
    rulesDefinedWithSqaleXml(null);
  }

  private void rulesDefinedWithSqaleXml(@Nullable String sqaleXmlResourcePath) {
    Context context = new Context();
    assertThat(context.repositories()).isEmpty();

    ImmutableMap.Builder<String, String> properties = ImmutableMap.<String, String>builder()
      .put("RepositoryKey", "MyRepoKey")
      .put("RepositoryLanguage", "MyLangKey")
      .put("RepositoryName", "MyRepoName")
      .put("RulesXmlResourcePath", "/org/sonar/plugins/roslynsdk/rules.xml");
    if (sqaleXmlResourcePath != null) {
      properties.put("SqaleXmlResourcePath", sqaleXmlResourcePath);
    }

    RoslynSdkConfiguration config = new RoslynSdkConfiguration(
      "/configuration.xml",
      properties.build(),
      ImmutableMap.<String, String>of());

    RoslynSdkRulesDefinition rulesDefinition = new RoslynSdkRulesDefinition(config);
    rulesDefinition.define(context);

    assertThat(context.repositories()).hasSize(1);
    Repository repo = context.repository("MyRepoKey");

    assertThat(repo.rules()).hasSize(1);
    Rule rule = repo.rules().get(0);

    assertThat(rule.key()).isEqualTo("S1000");
    assertThat(rule.name()).isEqualTo("My title");
    assertThat(rule.severity()).isEqualTo("CRITICAL");
    assertThat(rule.template()).isFalse();
    assertThat(rule.htmlDescription()).isEqualTo("My description");
    assertThat(rule.markdownDescription()).isNull();
    assertThat(rule.tags()).containsOnly("bug");
    assertThat(rule.params()).isEmpty();

    if (sqaleXmlResourcePath != null) {
      assertThat(rule.debtSubCharacteristic()).isEqualTo("INSTRUCTION_RELIABILITY");
      assertThat(rule.debtRemediationFunction().coefficient()).isNull();
      assertThat(rule.debtRemediationFunction().offset()).isEqualTo("15min");
      assertThat(rule.effortToFixDescription()).isNull();
    } else {
      assertThat(rule.debtSubCharacteristic()).isNull();
      assertThat(rule.debtRemediationFunction()).isNull();
      assertThat(rule.effortToFixDescription()).isNull();
    }
  }

}
