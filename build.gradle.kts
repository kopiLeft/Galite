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
  `maven-publish`
  `java-library`
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
  signing
}

group = "io.github.kopiLeft"
version = "1.0.0"

subprojects {
  apply(plugin = "org.jetbrains.kotlin.jvm")

  repositories {
    jcenter()
    maven {
      url = uri("https://maven.vaadin.com/vaadin-addons")
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

publishing {
  publications {
    create<MavenPublication>("Galite") {
      pom {
        description.set("Galite, an framework for accelerating business applications building")
        name.set(artifactId)
        url.set("https://github.com/kopiLeft/Galite")
        licenses {
          license {
            name.set("The Apache Software License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
          }
        }
        developers {
          developer {
            id.set("KopiLeft")
            name.set("KopiLeft Team")
            organization.set("kopiLeft Services")
            organizationUrl.set("www.kopileft.com")
            email.set("office@kopileft.com")
          }
          developer {
            id.set("KopiRight")
            name.set("KopiRight Team")
            organization.set("KopiRight")
            organizationUrl.set("www.kopiright.com")
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

nexusPublishing {
  repositories {
    sonatype {
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
      username.set(project.property("sonatype.username").toString())
      password.set(project.property("sonatype.password").toString())
    }
  }
}

java {
  withSourcesJar()
  withJavadocJar()
}
