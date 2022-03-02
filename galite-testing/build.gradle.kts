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
import org.kopi.galite.gradle.excludeWebJars

plugins {
  kotlin("jvm") apply true
  id("io.spring.dependency-management") version "1.0.10.RELEASE"
  `maven-publish`
  `java-library`
  signing
}

dependencies {
  implementation(project(":galite-core"))

  implementation(kotlin("test-junit"))

  implementation("com.vaadin", "vaadin-core") {
    excludeWebJars()
  }

  // UI tests dependencies
  implementation("com.github.mvysny.kaributesting", "karibu-testing-v10", Versions.KARIBU_TESTING)
  implementation("com.vaadin.componentfactory", "enhanced-dialog", Versions.ENHANCED_DIALOG)
}

dependencyManagement {
  imports {
    mavenBom("com.vaadin:vaadin-bom:${Versions.VAADIN}")
  }
}

publishing {
  publications {
    create<MavenPublication>("Galite") {
      pom {
        description.set("Galite framework for building business applications.")
        name.set(artifactId)
        url.set("https://github.com/kopiLeft/Galite")
        licenses {
          license {
            name.set("The GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1")
            url.set("https://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt")
          }
        }
        developers {
          developer {
            id.set("kopiLeft")
            name.set("kopiLeft Team")
            organization.set("kopiLeft Services")
            organizationUrl.set("http://www.kopileft.com")
            email.set("office@kopileft.com")
          }
          developer {
            id.set("kopiRight")
            name.set("kopiRight Team")
            organization.set("kopiRight")
            organizationUrl.set("http://www.kopiright.com")
            email.set("office@kopiright.fr")
          }
        }
        scm {
          url.set("https://github.com/kopiLeft/Galite.git")
          connection.set("scm:git:git://github.com/kopiLeft/Galite.git")
          developerConnection.set("scm:git:git@github.com:kopiLeft/Galite.git")
        }
      }
      from(components["java"])
    }
  }
}

signing {
  useInMemoryPgpKeys(System.getenv("GPG_PRIVATE_KEY"), System.getenv("GPG_PRIVATE_PASSWORD"))
  sign(publishing.publications)
}

java {
  withSourcesJar()
  withJavadocJar()
}
