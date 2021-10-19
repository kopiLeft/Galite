/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.kotlin.jvm") version "1.5.30" apply false
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

subprojects {
  apply(plugin = "org.jetbrains.kotlin.jvm")

  repositories {
    mavenCentral()
    maven {
      url = uri("https://maven.vaadin.com/vaadin-addons")
    }
    maven {
      url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
    maven {
      url = uri("https://s01.oss.sonatype.org/content/repositories/releases/")
    }
  }

  dependencies {
    "implementation"(kotlin("stdlib"))
    "implementation"(kotlin("reflect"))
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
  }
}

nexusPublishing {
  repositories {
    sonatype {
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
      username.set(System.getenv("SONATYPE_USERNAME"))
      password.set(System.getenv("SONATYPE_PASSWORD"))
    }
  }
}
