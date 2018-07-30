/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2016 SonarSource SA
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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.annotation.CheckForNull;
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
    try {
      return new InputStreamReader(new BOMInputStream(RoslynSdkConfiguration.class.getResourceAsStream(resourcePath)), StandardCharsets.UTF_8);
    } catch (Exception e) {
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
