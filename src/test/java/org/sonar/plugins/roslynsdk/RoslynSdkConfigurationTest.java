/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2019 SonarSource SA
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

import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.Assertions.assertThat;

public class RoslynSdkConfigurationTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void invalid_xml() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Invalid Roslyn SDK XML configuration file: ");
    thrown.expectMessage("invalid_xml.xml");

    new RoslynSdkConfiguration("/RoslynSdkConfigurationTest/invalid_xml.xml");
  }

  @Test
  public void invalid_root() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Expected <RoslynSdkConfiguration> as root element in configuration file:");
    thrown.expectMessage("invalid_root.xml");
    thrown.expectMessage("<foo>");

    new RoslynSdkConfiguration("/RoslynSdkConfigurationTest/invalid_root.xml");
  }

  @Test
  public void valid() {
    RoslynSdkConfiguration config = new RoslynSdkConfiguration("/RoslynSdkConfigurationTest/valid.xml");
    assertThat(config.mandatoryProperty("Foo")).isEqualTo("FooValue");
    assertThat(config.property("Foo").get()).isEqualTo("FooValue");
    assertThat(config.mandatoryProperty("Bar")).isEqualTo("BarValue");
    assertThat(config.property("Bar").get()).isEqualTo("BarValue");

    assertThat(config.property("NonExisting").isPresent()).isFalse();

    Map<String, String> expectedProperties = new HashMap<>();
    expectedProperties.put("Foo", "FooValue");
    expectedProperties.put("Bar", "BarValue");
    assertThat(config.properties()).isEqualTo(expectedProperties);

    Map<String, String> expectedPluginProperties = new HashMap<>();
    expectedPluginProperties.put("PluginFoo", "PluginFooValue");
    expectedPluginProperties.put("PluginBar", "PluginBarValue");
    assertThat(config.pluginProperties()).isEqualTo(expectedPluginProperties);
  }

  @Test
  public void missing_mandatory() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Mandatory <MissingPropertyKey> element not found in the Roslyn SDK XML configuration file: ");
    thrown.expectMessage("valid.xml");

    RoslynSdkConfiguration config = new RoslynSdkConfiguration("/RoslynSdkConfigurationTest/valid.xml");
    config.mandatoryProperty("MissingPropertyKey");
  }

  @Test
  public void empty() {
    RoslynSdkConfiguration config = new RoslynSdkConfiguration("/RoslynSdkConfigurationTest/empty.xml");
    assertThat(config.properties()).isEmpty();
    assertThat(config.pluginProperties()).isEmpty();
  }

  @Test
  public void default_path() {
    assertThat(new RoslynSdkConfiguration().mandatoryProperty("PluginKeyDifferentiator")).isEqualTo("example");
  }

}
