/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

plugins {
  kotlin("jvm") apply true
  id("org.springframework.boot") version "2.4.0"
  id("io.spring.dependency-management") version "1.0.10.RELEASE"
  id("com.vaadin") version "0.17.0.1"
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
  }
  implementation("org.springframework.boot", "spring-boot-devtools") {
    excludeWebJars()
  }

  // UI tests dependencies
  implementation("com.github.mvysny.kaributesting", "karibu-testing-v10", Versions.KARIBU_TESTING)

  implementation("org.jdom", "jdom2", Versions.JDOM)

  // Exposed dependencies
  testImplementation ("org.jetbrains.exposed", "exposed-jdbc", Versions.EXPOSED)

  testImplementation("com.h2database", "h2", Versions.H2)
  testImplementation("com.impossibl.pgjdbc-ng", "pgjdbc-ng", Versions.POSTGRES_NG)

  //Apache POI
  testImplementation("org.apache.poi", "poi", Versions.APACHE_POI)
  testImplementation("org.apache.poi", "poi-ooxml", Versions.APACHE_POI)

  // EnhancedDialog dependency
  testImplementation("com.vaadin.componentfactory", "enhanced-dialog", Versions.ENHANCED_DIALOG)
}

tasks {
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}

dependencyManagement {
  imports {
    mavenBom("com.vaadin:vaadin-bom:${Versions.VAADIN}")
  }
}

fun ExternalModuleDependency.excludeWebJars() {
  listOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
         "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
         "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
          .forEach { group -> exclude(group = group) }
}
