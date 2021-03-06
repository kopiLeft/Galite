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

plugins {
  kotlin("jvm") apply true
  id("org.springframework.boot") version "2.4.0"
  id("io.spring.dependency-management") version "1.0.10.RELEASE"
  id("com.vaadin") version "0.17.0.1"
}

val vaadinVersion = "18.0.3"
val jdomVersion = "2.0.5"
val karibuTestingVersion = "1.2.5"
val h2Version = "1.4.199"
val exposedVersion = "0.27.1"
val postgresNGVersion = "0.8.6"
val apachePoi = "4.1.2"

vaadin {
  pnpmEnable = true
}

dependencies {
  implementation(project(":galite-core"))

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
  implementation("com.github.mvysny.kaributesting", "karibu-testing-v10", karibuTestingVersion)

  implementation("org.jdom", "jdom2", jdomVersion)

  // Exposed dependencies
  testImplementation ("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)

  testImplementation("com.h2database", "h2", h2Version)
  testImplementation("com.impossibl.pgjdbc-ng", "pgjdbc-ng", postgresNGVersion)

  //Apache POI
  testImplementation("org.apache.poi", "poi", apachePoi)
  testImplementation("org.apache.poi", "poi-ooxml", apachePoi)
}

tasks {
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}

dependencyManagement {
  imports {
    mavenBom("com.vaadin:vaadin-bom:${vaadinVersion}")
  }
}

fun ExternalModuleDependency.excludeWebJars() {
  listOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
         "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
         "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
          .forEach { group -> exclude(group = group) }
}
