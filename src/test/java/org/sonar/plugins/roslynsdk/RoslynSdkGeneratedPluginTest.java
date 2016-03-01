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
import org.junit.Test;
import org.sonar.api.config.PropertyDefinition;

import static org.fest.assertions.Assertions.assertThat;

public class RoslynSdkGeneratedPluginTest {

  @Test
  public void getExtensions() {
    List extensions = new RoslynSdkGeneratedPlugin().getExtensions();

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
    ImmutableList.Builder builder = ImmutableList.builder();
    for (Object extension : extensions) {
      if (!(extension instanceof PropertyDefinition)) {
        builder.add(extension);
      }
    }
    return builder.build();
  }

}
