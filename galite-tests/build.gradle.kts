/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

import org.kopi.galite.gradle.Versions
import org.kopi.galite.gradle.excludeWebJars

plugins {
  kotlin("jvm") apply true
  id("org.springframework.boot") version "2.7.14"
  id("io.spring.dependency-management") version "1.0.10.RELEASE"
  id("com.vaadin") version "23.3.8"
}

vaadin {
  optimizeBundle = false
  pnpmEnable = true
}

dependencies {
  implementation(project(":galite-core"))
  implementation(project(":galite-testing"))

  implementation(kotlin("test-junit"))

  implementation("com.vaadin", "vaadin-core") {
    excludeWebJars()
  }
  implementation("com.vaadin", "vaadin-spring-boot-starter") {
    excludeWebJars()
    exclude(module = "spring-boot-starter-logging")
  }
  testImplementation("org.springframework.boot", "spring-boot-devtools") {
    excludeWebJars()
  }

  // Logging
  implementation("org.slf4j", "slf4j-simple", Versions.SLF4J)

  // UI tests dependencies
  implementation("com.github.mvysny.kaributesting", "karibu-testing-v10", Versions.KARIBU_TESTING)

  implementation("org.jdom", "jdom2", Versions.JDOM)

  implementation("com.h2database", "h2", Versions.H2)
  implementation("com.impossibl.pgjdbc-ng", "pgjdbc-ng", Versions.POSTGRES_NG)

  //Apache POI
  testImplementation("org.apache.poi", "poi", Versions.APACHE_POI)
  testImplementation("org.apache.poi", "poi-ooxml", Versions.APACHE_POI)

  // Vaadin addons dependency
  testImplementation("com.vaadin.componentfactory", "enhanced-dialog", Versions.ENHANCED_DIALOG)
  testImplementation("org.vaadin.stefan", "fullcalendar2", Versions.FULL_CALENDAR)

  // Pivot Table dependency
  implementation("org.vaadin.addons.componentfactory", "pivottable-flow", Versions.PIVOT_TABLE)
}

tasks {
  findByName("bootJar")?.apply {
    enabled = false
  }

  findByName("jar")?.apply {
    enabled = true
  }
}

dependencyManagement {
  imports {
    mavenBom("com.vaadin:vaadin-bom:${Versions.VAADIN}")
  }
}
