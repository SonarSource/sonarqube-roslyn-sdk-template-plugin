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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.annotation.CheckForNull;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.api.ServerExtension;

public class RoslynSdkConfiguration implements ServerExtension {

  private final String resourcePath;
  private final Map<String, String> properties;
  private final Map<String, String> pluginProperties;

  public RoslynSdkConfiguration() {
    this("/org/sonar/plugins/roslynsdk/configuration.xml");
  }

  @VisibleForTesting
  RoslynSdkConfiguration(String resourcePath) {
    this.resourcePath = resourcePath;

    try (InputStreamReader reader = reader(resourcePath)) {
      XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
      xmlFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
      xmlFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
      xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
      xmlFactory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
      SMInputFactory inputFactory = new SMInputFactory(xmlFactory);

      SMInputCursor root = inputFactory.rootElementCursor(reader).advance();
      Preconditions.checkState(
        root.hasLocalName("RoslynSdkConfiguration"),
        "Expected <RoslynSdkConfiguration> as root element in configuration file: " + resourcePath + " but got: <" + root.getLocalName() + ">");
      SMInputCursor rootChildren = root.childElementCursor();

      ImmutableMap.Builder<String, String> propertiesBuilder = ImmutableMap.builder();
      Map<String, String> foundPluginProperties = null;
      while (rootChildren.getNext() != null) {
        if (rootChildren.hasLocalName("PluginProperties")) {
          Preconditions.checkState(foundPluginProperties == null, "<PluginProperties> can be present at most once");
          foundPluginProperties = readPluginProperties(rootChildren.childElementCursor());
        } else {
          propertiesBuilder.put(rootChildren.getLocalName(), rootChildren.getElemStringValue());
        }
      }
      if (foundPluginProperties == null) {
        foundPluginProperties = ImmutableMap.of();
      }

      properties = propertiesBuilder.build();
      pluginProperties = foundPluginProperties;
    } catch (XMLStreamException e) {
      throw new IllegalStateException("Invalid Roslyn SDK XML configuration file: " + resourcePath, e);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  @VisibleForTesting
  RoslynSdkConfiguration(String resourcePath, Map<String, String> properties, Map<String, String> pluginProperties) {
    this.resourcePath = resourcePath;
    this.properties = properties;
    this.pluginProperties = pluginProperties;
  }

  private static InputStreamReader reader(String resourcePath) {
    URL url = Resources.getResource(RoslynSdkConfiguration.class, resourcePath);
    try {
      return Resources.newReaderSupplier(url, StandardCharsets.UTF_8).getInput();
    } catch (IOException e) {
      throw new IllegalArgumentException("Could not read " + resourcePath, e);
    }
  }

  private static Map<String, String> readPluginProperties(SMInputCursor pluginPropertiesChildren) throws XMLStreamException {
    ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    while (pluginPropertiesChildren.getNext() != null) {
      builder.put(pluginPropertiesChildren.getLocalName(), pluginPropertiesChildren.getElemStringValue());
    }
    return builder.build();
  }

  public String mandatoryProperty(String key) {
    String value = property(key);
    Preconditions.checkState(value != null, "Mandatory <" + key + "> element not found in the Roslyn SDK XML configuration file: " + resourcePath);
    return value;
  }

  @CheckForNull
  public String property(String key) {
    return properties.get(key);
  }

  public Map<String, String> properties() {
    return properties;
  }

  public Map<String, String> pluginProperties() {
    return pluginProperties;
  }

}
