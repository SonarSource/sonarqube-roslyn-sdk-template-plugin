/*
 * SonarQube Roslyn SDK Template Plugin
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.junit.Test;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.debt.DebtRemediationFunction;
import org.sonar.api.server.rule.RulesDefinition.Context;
import org.sonar.api.server.rule.RulesDefinition.Repository;
import org.sonar.api.server.rule.RulesDefinition.Rule;
import org.sonar.api.utils.log.LogTester;
import org.sonar.api.utils.log.LoggerLevel;

import static org.fest.assertions.Assertions.assertThat;

public class RoslynSdkRulesDefinitionTest {

  @org.junit.Rule
  public LogTester logTester = new LogTester();

  @Test
  public void test_rules_defined_with_sqale() {
    rulesDefinedWithSqaleXml("/org/sonar/plugins/roslynsdk/sqale.xml");
  }

  @Test
  public void test_rules_defined_without_sqale() {
    rulesDefinedWithSqaleXml(null);
  }

  private void rulesDefinedWithSqaleXml(@Nullable String sqaleXmlResourcePath) {
    rulesDefinedWithSqaleXml(sqaleXmlResourcePath, false);
  }

  private void rulesDefinedWithSqaleXml(@Nullable String sqaleXmlResourcePath, boolean withBOM) {
    Context context = new Context();
    assertThat(context.repositories()).isEmpty();

    Map<String, String> properties = new HashMap<>();
    properties.put("RepositoryKey", "MyRepoKey");
    properties.put("RepositoryLanguage", "MyLangKey");
    properties.put("RepositoryName", "MyRepoName");
    properties.put("RulesXmlResourcePath", "/org/sonar/plugins/roslynsdk/rules.xml");
    if (sqaleXmlResourcePath != null) {
      properties.put("SqaleXmlResourcePath", sqaleXmlResourcePath);
    }

    RoslynSdkConfiguration config = new RoslynSdkConfiguration(
      "/configuration.xml",
      Collections.unmodifiableMap(properties),
      Collections.unmodifiableMap(new HashMap<String, String>()));

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
    assertThat(rule.tags()).containsOnly("my-first-tag", "my-second-tag");
    assertThat(rule.type()).isEqualTo(RuleType.BUG);
    assertThat(rule.params()).isEmpty();

    if (sqaleXmlResourcePath != null) {
      // sub-charactestic has been dropped with 6.7 LTS
      assertThat(rule.debtSubCharacteristic()).isNull();
      assertThat(rule.effortToFixDescription()).isNull();

      assertThat(logTester.logs()).hasSize(1);
      assertThat(logTester.logs(LoggerLevel.WARN))
        .contains("SQALE Model is deprecated and not supported anymore by SonarQube. "
          + "Please rely on SonarQube rules definition XML format. "
          + "'SqaleXmlResourcePath' property will be ignored.");
    } else {
      assertThat(rule.debtSubCharacteristic()).isNull();
      assertThat(rule.effortToFixDescription()).isNull();
      assertThat(rule.debtRemediationFunction()).isNotNull();
      assertThat(rule.debtRemediationFunction().type()).isEqualTo(DebtRemediationFunction.Type.CONSTANT_ISSUE);
      assertThat(rule.debtRemediationFunction().baseEffort()).isEqualTo("15min");

      assertThat(logTester.logs()).isEmpty();
    }
  }

}
