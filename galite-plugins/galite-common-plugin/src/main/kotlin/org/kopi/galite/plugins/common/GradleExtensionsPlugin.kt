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

package org.kopi.galite.plugins.common

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

open class GradleExtensionsPlugin: Plugin<Project> {

  /**
   * Register the [projectExtensions] extension function for use in the consuming project
   */
  override fun apply(project: Project) {
    val projectExtensions = project.extensions.findByType(GaliteGradleExtensions::class.java)
        ?: project.extensions.create("projectExtensions", GaliteGradleExtensions::class.java)
  }

  /**
   * Generate a custom directory to contain generated classes and attach it to the [main] source set.
   */
  protected fun createGeneratedSourceSet(project: Project) {
    val kotlinExtension = project.extensions.getByType(KotlinJvmProjectExtension::class.java)
    val javaExtension = project.extensions.getByType(JavaPluginExtension::class.java)

    // Java
    javaExtension.sourceSets.getByName("main") {
      java.srcDir(GENERATED_JAVA_DIRECTORY)
    }
    // Kotlin
    kotlinExtension.sourceSets.getByName("main") {
      kotlin.srcDir(GENERATED_KOTLIN_DIRECTORY)
    }

    project.logger.lifecycle("Attached generated sources in $GENERATED_DIRECTORY to main")
  }

  companion object {
    const val GENERATED_DIRECTORY = "src/generated"
    const val GENERATED_KOTLIN_DIRECTORY = "src/generated/kotlin"
    const val GENERATED_JAVA_DIRECTORY = "src/generated/java"
  }
}
