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
  id("org.springframework.boot") version "2.2.2.RELEASE"
}

val vaadinVersion = "16.0.0"

dependencies {
  implementation(project(":galite-core"))
  implementation(kotlin("test-junit"))
  implementation("com.vaadin", "vaadin-core", vaadinVersion)
  implementation("com.vaadin", "vaadin-spring-boot-starter", vaadinVersion)
  implementation("org.springframework.boot", "spring-boot-devtools", "2.2.0.RELEASE")
  testImplementation("org.seleniumhq.selenium", "selenium-java", "3.141.59")
  testImplementation("io.github.bonigarcia:webdrivermanager:4.0.0")
  testImplementation("io.github.sukgu:automation:0.0.13")
}




