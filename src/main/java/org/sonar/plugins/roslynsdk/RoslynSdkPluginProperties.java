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

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;

public class RoslynSdkPluginProperties {

  private RoslynSdkConfiguration config;

  public RoslynSdkPluginProperties(RoslynSdkConfiguration config) {
    this.config = config;
  }

  public List<PropertyDefinition> getProperties() {
    ImmutableList.Builder<PropertyDefinition> builder = ImmutableList.builder();
    for (Map.Entry<String, String> pluginProperty: config.pluginProperties().entrySet()) {
      builder.add(newHiddenPropertyDefinition(pluginProperty.getKey(), pluginProperty.getValue()));
    }
    return builder.build();
  }

  private static PropertyDefinition newHiddenPropertyDefinition(String key, String defaultValue) {
    return PropertyDefinition.builder(key)
      .type(PropertyType.STRING)
      .defaultValue(defaultValue)
      .hidden()
      .build();
  }

}
