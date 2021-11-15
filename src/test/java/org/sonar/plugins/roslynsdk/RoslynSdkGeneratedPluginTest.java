/*
 * SonarQube Roslyn SDK
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;

import static org.fest.assertions.Assertions.assertThat;

public class RoslynSdkGeneratedPluginTest {

  @Test
  public void getExtensions() {
    RoslynSdkGeneratedPlugin plugin = new RoslynSdkGeneratedPlugin();
    Plugin.Context context = new Plugin.Context(SonarRuntimeImpl.forSonarQube(Version.create(6, 7), SonarQubeSide.SCANNER));
    plugin.define(context);

    List extensions = context.getExtensions();
    assertThat(extensions).hasSize(9);

    Class<?>[] expectedExtensions = new Class<?>[] {
      RoslynSdkConfiguration.class,
      RoslynSdkRulesDefinition.class
    };

    assertThat(nonProperties(extensions)).contains(expectedExtensions);
    assertThat(extensions).hasSize(
      expectedExtensions.length
        + new RoslynSdkPluginProperties(new RoslynSdkConfiguration()).getProperties().size());
  }

  @Test
  public void pico_container_key_differentiator() {
    assertThat(new RoslynSdkGeneratedPlugin().toString()).isEqualTo("example");
  }

  private static List nonProperties(List extensions) {
    List props = new ArrayList<>();
    for (Object extension : extensions) {
      if (!(extension instanceof PropertyDefinition)) {
        props.add(extension);
      }
    }
    return Collections.unmodifiableList(props);
  }

}
