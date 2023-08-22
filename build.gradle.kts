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

import org.kopi.galite.gradle._java
import org.kopi.galite.gradle._publishing
import org.kopi.galite.gradle.configureMavenCentralPom
import org.kopi.galite.gradle.signPublication

plugins {
  id("org.jetbrains.kotlin.jvm") version "1.6.10" apply false
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

val releasableProjects = listOf(
  "galite-core",
  "galite-data",
  "galite-util",
  "galite-testing",
)

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
  }

  dependencies {
    "implementation"(kotlin("stdlib"))
    "implementation"(kotlin("reflect"))
  }
}

allprojects {
  if (this.name in releasableProjects) {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    _java {
      withJavadocJar()
      withSourcesJar()
    }

    _publishing {
      publications {
        create<MavenPublication>("Galite") {
          artifactId = project.name
          from(project.components["java"])
          pom {
//            configureMavenCentralPom(project)
            name.set(project.name)
          }
//          signPublication(project)
        }
      }
    }
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
