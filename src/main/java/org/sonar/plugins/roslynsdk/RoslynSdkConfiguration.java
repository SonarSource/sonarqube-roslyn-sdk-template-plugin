/*
 * SonarQube Roslyn SDK Template Plugin
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.io.input.BOMInputStream;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.api.server.ServerSide;

@ServerSide
public class RoslynSdkConfiguration {

  private final String resourcePath;
  private final Map<String, String> properties;
  private final Map<String, String> pluginProperties;

  public RoslynSdkConfiguration() {
    this("/org/sonar/plugins/roslynsdk/configuration.xml");
  }

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
      if (!root.hasLocalName("RoslynSdkConfiguration")) {
        throw new IllegalStateException(
          String.format("Expected <RoslynSdkConfiguration> as root element in configuration file: %s but got: <%s>", resourcePath, root.getLocalName()));
      }
      SMInputCursor rootChildren = root.childElementCursor();

      Map<String, String> propertiesBuilder = new HashMap<>();
      Map<String, String> foundPluginProperties = null;
      while (rootChildren.getNext() != null) {
        if (rootChildren.hasLocalName("PluginProperties")) {
          if (foundPluginProperties != null) {
            throw new IllegalStateException("<PluginProperties> can be present at most once");
          }
          foundPluginProperties = readPluginProperties(rootChildren.childElementCursor());
        } else {
          propertiesBuilder.put(rootChildren.getLocalName(), rootChildren.getElemStringValue());
        }
      }
      if (foundPluginProperties == null) {
        foundPluginProperties = new HashMap<>();
      }

      properties = Collections.unmodifiableMap(propertiesBuilder);
      pluginProperties = foundPluginProperties;
    } catch (XMLStreamException | IOException e) {
      throw new IllegalStateException(String.format("Invalid Roslyn SDK XML configuration file: %s", resourcePath), e);
    }
  }

  RoslynSdkConfiguration(String resourcePath, Map<String, String> properties, Map<String, String> pluginProperties) {
    this.resourcePath = resourcePath;
    this.properties = properties;
    this.pluginProperties = pluginProperties;
  }

  private static InputStreamReader reader(String resourcePath) {
    try {
      return new InputStreamReader(new BOMInputStream(RoslynSdkConfiguration.class.getResourceAsStream(resourcePath)), StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Could not read %s", resourcePath), e);
    }
  }

  private static Map<String, String> readPluginProperties(SMInputCursor pluginPropertiesChildren) throws XMLStreamException {
    Map<String, String> properties = new HashMap<>();
    while (pluginPropertiesChildren.getNext() != null) {
      properties.put(pluginPropertiesChildren.getLocalName(), pluginPropertiesChildren.getElemStringValue());
    }
    return Collections.unmodifiableMap(properties);
  }

  public String mandatoryProperty(String key) {
    return property(key)
      .orElseThrow(() -> new IllegalStateException(String.format("Mandatory <%s> element not found in the Roslyn SDK XML configuration file: %s", key, resourcePath)));
  }

  public Optional<String> property(String key) {
    return Optional.ofNullable(properties.get(key));
  }

  public Map<String, String> properties() {
    return properties;
  }

  public Map<String, String> pluginProperties() {
    return pluginProperties;
  }

}
