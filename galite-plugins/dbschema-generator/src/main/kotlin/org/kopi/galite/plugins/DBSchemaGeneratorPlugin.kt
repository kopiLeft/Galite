/*
 * Copyright (c) 2013-2025 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2025 kopiRight Managed Solutions GmbH, Wien AT
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
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

import org.kopi.galite.plugins.common.GradleExtensions
import org.kopi.galite.plugins.generator.DBSchemaGenerator
import org.kopi.galite.plugins.common.GradleExtensionsPlugin

class DBSchemaGeneratorPlugin : GradleExtensionsPlugin() {
  override fun apply(project: Project) {
    super.apply(project)

    val generatedSourceSet = createGeneratedSourceSet(project)

    // Create and register extension
    project.extensions.create("dbSchemaGenerator", DBSchemaGeneratorExtension::class.java)
    project.tasks.apply {
      register<DBSchemaGeneratorTask>("generateDBSchemas")
      withType(KotlinCompile::class.java) {
        dependsOn("generateDBSchemas")
        source(generatedSourceSet.allSource)
      }
      named("clean") {
        doLast {
          project.extensions.getByType<GradleExtensions>().clean(
            project.layout.projectDirectory.dir(DBSchemaGenerator.GENERATED_SRC).asFile.path
          )
        }
      }
    }
  }

  /**
   * Generate a custom source set to be compiled
   */
  private fun createGeneratedSourceSet(project: Project): SourceSet {
    val kotlinExtension = project.extensions.getByType(KotlinProjectExtension::class.java)
    val javaExtension = project.extensions.getByType(JavaPluginExtension::class.java)
    val mainSourceSet = javaExtension.sourceSets.getByName("main")
    // Create a new source set named "generated"
    val generatedSourceSet = javaExtension.sourceSets.create("generated") {
      // Add source directory for Kotlin
      java.srcDir(DBSchemaGenerator.GENERATED_KOTLIN_SRC)
    }

    // Configure Kotlin sources
    kotlinExtension.sourceSets.getByName("generated").apply {
      kotlin.srcDir(DBSchemaGenerator.GENERATED_KOTLIN_SRC)
    }

    // Set classpath dependencies
    generatedSourceSet.compileClasspath += mainSourceSet.compileClasspath
    generatedSourceSet.runtimeClasspath += mainSourceSet.runtimeClasspath

    project.logger.lifecycle("Generated Kotlin SourceSet created: ${generatedSourceSet.name}")

    return generatedSourceSet
  }
}
