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
  id("org.springframework.boot") version "2.2.4.RELEASE"
}

val vaadinVersion = "16.0.0"

repositories {
  jcenter()
  mavenCentral()
  maven {
    url = uri("https://maven.vaadin.com/vaadin-addons")
  }
}

dependencies {
  implementation(project(":galite-core"))

  implementation(kotlin("test-junit"))

  implementation("com.vaadin:vaadin-core:$vaadinVersion") {
    listOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
        "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
        "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
        .forEach { group -> exclude(group = group) }
  }

  implementation("org.seleniumhq.selenium:selenium-java:3.141.59")
  implementation("io.github.bonigarcia:webdrivermanager:4.0.0"){
    exclude("org.jsoup","jsoup")
  }
  implementation("io.github.sukgu:automation:0.0.13")


}

tasks {
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
    source.buildDependencies
  }
}
