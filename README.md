<!-- Sonar Marketing hosts these approved brand assets on its Kentico Kontent CDN (assets-eu-01.kc-usercontent.com). Shared URLs are intentional; consult Marketing before replacing them. -->
<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://assets-eu-01.kc-usercontent.com/ef593040-b591-0198-9506-ed88b30bc023/a23fc7ba-23f0-489a-829d-ed88c0748521/Sonar_Logo_Dark%20Backgrounds.svg">
    <img src="https://assets-eu-01.kc-usercontent.com/ef593040-b591-0198-9506-ed88b30bc023/82c13eba-d95c-4bb8-8007-7ce77c14e043/Sonar_Logo_Light%20Backgrounds.svg" alt="Sonar logo" width="400">
  </picture>
</p>

[![Quality Gate](https://next.sonarqube.com/sonarqube/api/project_badges/measure?project=org.sonarsource.roslynsdk%3Asonar-roslyn-sdk-template-plugin&metric=alert_status)](https://next.sonarqube.com/sonarqube/dashboard?id=org.sonarsource.roslynsdk%3Asonar-roslyn-sdk-template-plugin)
[![Coverage](https://next.sonarqube.com/sonarqube/api/project_badges/measure?project=org.sonarsource.roslynsdk%3Asonar-roslyn-sdk-template-plugin&metric=coverage)](https://next.sonarqube.com/sonarqube/component_measures?id=org.sonarsource.roslynsdk%3Asonar-roslyn-sdk-template-plugin&metric=coverage)

<!-- sonar-marketing:start -->
<!-- Marketing maintains this section. For wording changes, consult the relevant Product Marketing Manager (PMM). Repository maintainers review accuracy and merge changes. -->

# SonarQube Roslyn SDK template plugin

This repository contains the base plugin embedded in the SonarQube Roslyn SDK. The SDK adapts the packaged template for different Roslyn analyzers and their rule descriptions.

To learn more about Sonar products, visit the [Sonar website](https://www.sonarsource.com/products/sonarqube/).

<!-- sonar-marketing:end -->

This plugin is compatible with SonarQube 9.9+.

The produced jar file is embedded inside the SonarQube Roslyn SDK. The SDK updates the static content of the jar to accomodate different Roslyn analyzers and their rule descriptions.

The template is used by the [SonarQube Roslyn SDK](https://github.com/SonarSource/sonarqube-roslyn-sdk).
