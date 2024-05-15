/*
 * SonarQube Roslyn SDK Template Plugin
 * Copyright (C) 2016-2024 SonarSource SA
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

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RoslynSdkConfigurationTest {

  @Test
  public void invalid_xml() {
    assertThat(assertThrows(IllegalStateException.class, () -> new RoslynSdkConfiguration("/RoslynSdkConfigurationTest/invalid_xml.xml")).getMessage())
      .isEqualTo("Invalid Roslyn SDK XML configuration file: /RoslynSdkConfigurationTest/invalid_xml.xml");
  }

  @Test
  public void invalid_root() {
    assertThat(assertThrows(IllegalStateException.class,
      () -> new RoslynSdkConfiguration("/RoslynSdkConfigurationTest/invalid_root.xml")).getMessage())
      .isEqualTo("Expected <RoslynSdkConfiguration> as root element in configuration file: /RoslynSdkConfigurationTest/invalid_root.xml " +
        "but got: <foo>");
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
    RoslynSdkConfiguration config = new RoslynSdkConfiguration("/RoslynSdkConfigurationTest/valid.xml");
    assertThat(assertThrows(IllegalStateException.class, () -> config.mandatoryProperty("MissingPropertyKey")).getMessage())
      .isEqualTo("Mandatory <MissingPropertyKey> element not found in the Roslyn SDK XML configuration file: " +
        "/RoslynSdkConfigurationTest/valid.xml");
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
