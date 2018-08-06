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
