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

import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import org.kopi.galite.gradle.configureMavenCentralPom

plugins {
  id("org.jetbrains.kotlin.jvm") version "1.9.20" apply false
  id("com.vanniktech.maven.publish") version "0.34.0"
}

val releasableProjects = listOf(
  "galite-core",
  "galite-data",
  "galite-util",
  "galite-testing"
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
    maven {
      url = uri("https://mvnrepository.com/artifact/org.vaadin.addons")
    }
  }

  dependencies {
    "implementation"(kotlin("stdlib"))
    "implementation"(kotlin("reflect"))
  }

  tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
  }

  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
  }
}

allprojects {
  if (this.name in releasableProjects) {
    apply(plugin = "java-library")
    apply(plugin = "com.vanniktech.maven.publish")

    mavenPublishing {
      publishToMavenCentral(automaticRelease = true)
      signAllPublications()
      configure(JavaLibrary(sourcesJar = true, javadocJar = JavadocJar.Javadoc()))
      pom {
        configureMavenCentralPom(project)
      }
    }
  }
}
