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
package org.kopi.galite.gradle

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension
import org.gradle.api.plugins.JavaPluginExtension

fun MavenPom.configureMavenCentralPom(project: Project) {
  name.set(project.name)
  description.set("Galite framework for building business applications.")
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

fun MavenPublication.signPublication(project: Project) {
  project.extensions.configure<SigningExtension>("signing") {
    useInMemoryPgpKeys(System.getenv("GPG_PRIVATE_KEY"),
                       System.getenv("GPG_PRIVATE_PASSWORD"))
    sign(this@signPublication)
  }
}

fun Project._publishing(configure: PublishingExtension.() -> Unit) {
  extensions.configure("publishing", configure)
}

fun Project._java(configure: JavaPluginExtension.() -> Unit) {
  extensions.configure("java", configure)
}
