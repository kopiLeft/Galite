/*
 * Copyright (c) 2013-2026 kopiLeft Services SARL, Tunis TN
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

import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import org.kopi.galite.gradle.configureMavenCentralPom

plugins {
  id("com.vanniktech.maven.publish")
}

subprojects {
  apply(plugin = "java-gradle-plugin")
  apply(plugin = "com.vanniktech.maven.publish")

  dependencies {
    if (project.name != "galite-common-plugin") {
      api(project(":galite-plugins:galite-common-plugin"))
    }
  }

  mavenPublishing {
    publishToMavenCentral(automaticRelease = true)

    signAllPublications()

    configure(GradlePlugin(javadocJar = JavadocJar.Javadoc(), sourcesJar = true))

    pom {
      configureMavenCentralPom(project)
    }
  }
}
