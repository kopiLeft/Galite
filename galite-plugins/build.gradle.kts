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

import org.kopi.galite.gradle._java
import org.kopi.galite.gradle._publishing
import org.kopi.galite.gradle.configureMavenCentralPom
import org.kopi.galite.gradle.signPublication

subprojects {
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
          configureMavenCentralPom(project)
        }
        signPublication(project)
      }
    }
  }
}
