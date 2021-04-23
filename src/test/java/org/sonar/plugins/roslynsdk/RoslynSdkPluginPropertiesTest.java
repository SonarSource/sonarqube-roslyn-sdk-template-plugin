/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2021 SonarSource SA
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.server.rule.RulesDefinition.Context;

import static org.fest.assertions.Assertions.assertThat;

public class RoslynSdkPluginPropertiesTest {

  @Test
  public void test() {
    Context context = new Context();
    assertThat(context.repositories()).isEmpty();

    Map<String, String> pluginProperties = new LinkedHashMap<>();
    pluginProperties.put("foo", "fooValue");
    pluginProperties.put("bar", "barValue");

    RoslynSdkConfiguration config = new RoslynSdkConfiguration(
      "/configuration.xml",
      Collections.unmodifiableMap(new HashMap<String, String>()),
      Collections.unmodifiableMap(pluginProperties));

    List<PropertyDefinition> properties = new RoslynSdkPluginProperties(config).getProperties();
    assertThat(properties).hasSize(2);

    PropertyDefinition foo = properties.get(0);
    assertThat(foo.key()).isEqualTo("foo");
    assertThat(foo.defaultValue()).isEqualTo("fooValue");
    assertThat(foo.type()).isEqualTo(PropertyType.STRING);
    assertThat(foo.global()).isFalse();

    PropertyDefinition bar = properties.get(1);
    assertThat(bar.key()).isEqualTo("bar");
    assertThat(bar.defaultValue()).isEqualTo("barValue");
    assertThat(bar.type()).isEqualTo(PropertyType.STRING);
    assertThat(bar.global()).isFalse();
  }

}
