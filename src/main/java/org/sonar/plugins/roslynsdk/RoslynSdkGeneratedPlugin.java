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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.sonar.api.Plugin;

public class RoslynSdkGeneratedPlugin implements Plugin {

  private static final RoslynSdkConfiguration config = new RoslynSdkConfiguration();

  @Override
  public String toString() {
    return config.mandatoryProperty("PluginKeyDifferentiator");
  }

  @Override
  public void define(Context context) {
    List<Object> extensions = new ArrayList<>();

    extensions.add(RoslynSdkConfiguration.class);
    extensions.add(RoslynSdkRulesDefinition.class);

    extensions.addAll(new RoslynSdkPluginProperties(config).getProperties());

    context.addExtensions(Collections.unmodifiableList(extensions));
  }

}
