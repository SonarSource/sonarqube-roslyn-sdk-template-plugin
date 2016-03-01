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
