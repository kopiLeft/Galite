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
  id("org.springframework.boot") version "2.3.3.RELEASE"
}

val vaadinVersion = "17.0.0"
val jdomVersion = "2.0.5"
val karibuTestingVersion = "1.2.5"

dependencies {
  implementation(project(":galite-core"))

  implementation(kotlin("test-junit"))

  implementation("com.vaadin", "vaadin-core", vaadinVersion) {
    listOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
            "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
            "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
            .forEach { group -> exclude(group = group) }
  }
  implementation("com.vaadin", "vaadin-spring-boot-starter", vaadinVersion) {
    listOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
            "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
            "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
            .forEach { group -> exclude(group = group) }
  }
  // UI tests dependencies
  implementation("com.github.mvysny.kaributesting", "karibu-testing-v10", karibuTestingVersion)

  implementation("org.jdom", "jdom2", jdomVersion)
}

tasks {
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}
