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
  id("org.springframework.boot") version "2.4.0"
  id("io.spring.dependency-management") version "1.0.10.RELEASE"
  id("com.vaadin") version "21.0.9"
  application
}

application {
  mainClass.set("org.kopi.galite.demo.ApplicationKt")
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
  implementation("org.springframework.boot", "spring-boot-devtools") {
    excludeWebJars()
  }

  // Logging
  implementation("org.slf4j", "slf4j-simple", Versions.SLF4J)

  // UI tests dependencies
  implementation("com.github.mvysny.kaributesting", "karibu-testing-v10", Versions.KARIBU_TESTING)

  implementation("com.h2database", "h2", Versions.H2)

  // EnhancedDialog dependency
  testImplementation("com.vaadin.componentfactory", "enhanced-dialog", Versions.ENHANCED_DIALOG)
}

dependencyManagement {
  imports {
    mavenBom("com.vaadin:vaadin-bom:${Versions.VAADIN}")
  }
}
