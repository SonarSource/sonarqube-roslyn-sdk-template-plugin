/*
 * SonarQube Roslyn SDK Template Plugin
 * Copyright (C) 2016-2023 SonarSource SA
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
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
