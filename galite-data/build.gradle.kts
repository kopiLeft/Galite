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

plugins {
  kotlin("jvm") apply true
}

dependencies {
  api(project(":galite-util"))

  // Exposed dependency
  api("org.jetbrains.exposed", "exposed-core", Versions.EXPOSED)
  api("org.jetbrains.exposed", "exposed-jodatime", Versions.EXPOSED)
  api("org.jetbrains.exposed", "exposed-java-time", Versions.EXPOSED)
  api("org.jetbrains.exposed", "exposed-jdbc", Versions.EXPOSED)
  // HikariCP dependency : for pool connexion
  api("com.zaxxer", "HikariCP", Versions.HIKARI)

  // getOpt dependency
  implementation("gnu.getopt", "java-getopt", Versions.GETOPT)
}
