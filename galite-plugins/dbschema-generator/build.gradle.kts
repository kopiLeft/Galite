/*
 * Copyright (c) 2013-2025 kopiLeft Services SARL, Tunis TN
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
  `kotlin-dsl`
  id("java-gradle-plugin")
}

dependencies {
  api("org.reflections", "reflections", "0.10.2")
  implementation("org.jetbrains.kotlin", "kotlin-gradle-plugin", "1.9.0")
  // Exposed dependency
  implementation("org.jetbrains.exposed", "exposed-core", Versions.EXPOSED)
  implementation("org.jetbrains.exposed", "exposed-jodatime", Versions.EXPOSED)
  implementation("org.jetbrains.exposed", "exposed-java-time", Versions.EXPOSED)
  implementation("org.jetbrains.exposed", "exposed-jdbc", Versions.EXPOSED)
}

gradlePlugin {
  plugins {
    create("") {
      id = "org.kopi.dbschema-generator" // Unique plugin ID
      implementationClass = "org.kopi.galite.plugins.DBSchemaGeneratorPlugin" // The main plugin class
    }
  }
}
