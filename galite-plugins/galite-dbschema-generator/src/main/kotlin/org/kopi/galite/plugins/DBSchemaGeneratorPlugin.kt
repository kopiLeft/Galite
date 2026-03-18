/*
 * Copyright (c) 2013-2026 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2026 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.plugins

import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

import org.kopi.galite.plugins.common.GaliteGradleExtensions
import org.kopi.galite.plugins.common.GradleExtensionsPlugin

class DBSchemaGeneratorPlugin : GradleExtensionsPlugin() {
  override fun apply(project: Project) {
    super.apply(project)

    createGeneratedSourceSet(project)
    // Create and register extension
    project.extensions.create("dbSchemaGenerator", DBSchemaGeneratorExtension::class.java)
    project.tasks.apply {
      register<DBSchemaGeneratorTask>("generateDBSchemas")
      withType(KotlinCompile::class.java) {
        dependsOn("generateDBSchemas")
      }
      named("clean") {
        doLast {
          project.extensions.getByType<GaliteGradleExtensions>().clean(
            project.layout.projectDirectory.dir(GENERATED_DIRECTORY).asFile.path
          )
        }
      }
    }
  }
}
