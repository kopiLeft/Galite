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

import org.kopi.galite.gradle.configureMavenCentralPom

plugins {
  id("maven-publish")
  id("signing")
}

subprojects {
  apply(plugin = "java-library")
  apply(plugin = "maven-publish")
  apply(plugin = "signing")

  java {
    withJavadocJar()
    withSourcesJar()
  }

  project.plugins.withId("java-gradle-plugin") {
    publishing {
      publications {
        create<MavenPublication>("pluginMaven") {
          artifactId = project.name
          pom {
           configureMavenCentralPom(project)
          }
          signing {
            useInMemoryPgpKeys(System.getenv("GPG_PRIVATE_KEY"), System.getenv("GPG_PRIVATE_PASSWORD"))
            sign(publishing.publications["pluginMaven"])
          }
        }
      }
    }
    afterEvaluate {
      publishing {
        publications.named<MavenPublication>("PluginMarkerMaven") {
          pom {
            configureMavenCentralPom(project)
          }
        }
        signing {
          useInMemoryPgpKeys(System.getenv("GPG_PRIVATE_KEY"), System.getenv("GPG_PRIVATE_PASSWORD"))
          sign(publishing.publications["PluginMarkerMaven"])
        }
      }
    }
  }
}
