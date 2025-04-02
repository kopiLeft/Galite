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

import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.the

import org.kopi.galite.plugins.generator.DBSchemaGenerator

abstract class DBSchemaGeneratorTask : JavaExec() {
  init {
    description = "Generates DBSchema files"
    mainClass.set("org.kopi.galite.plugins.generator.DBSchemaGenerator")
    classpath = project.the<SourceSetContainer>()["main"].runtimeClasspath
  }

  @TaskAction
  fun execute() {
    val extension = project.extensions.getByType(DBSchemaGeneratorExtension::class.java)
    val outputDirectory = project.layout.projectDirectory.dir(DBSchemaGenerator.GENERATED_KOTLIN_SRC)

    if (extension.data.getOrElse(emptyList()).isEmpty()) {
      project.logger.lifecycle("No schemas defined for DBSchema generation.")
      return
    }

    extension.data.get().forEach { data ->
      val currentArgs = listOf(data.packageName, data.schemaName, outputDirectory.asFile.absolutePath)
      project.logger.lifecycle("Running DBSchemaGenerator with arguments: ${currentArgs.joinToString()}")

      project.javaexec {
        mainClass.set("org.kopi.galite.plugins.generator.DBSchemaGenerator")
        classpath = this@DBSchemaGeneratorTask.classpath
        args = currentArgs
      }
    }
  }
}
