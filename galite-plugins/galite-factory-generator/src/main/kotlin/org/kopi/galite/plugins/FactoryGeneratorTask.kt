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

import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.the

import org.kopi.galite.plugins.common.GradleExtensionsPlugin

abstract class FactoryGeneratorTask : JavaExec() {
  init {
    description = "Task to generate factories from .xsd and .xsdConfig files"
    mainClass.set("org.kopi.galite.util.xsdToFactory.generator.FactoryGenerator")
  }
  @OutputDirectory
  val src = project.layout.projectDirectory.dir(GradleExtensionsPlugin.GENERATED_KOTLIN_DIRECTORY)

  @TaskAction
  override fun exec() {
    val extension = project.extensions.getByType(FactoryGeneratorExtention::class.java)

    if (extension.factories.getOrElse(emptyList()).isEmpty()) {
      project.logger.lifecycle("No xsd defined for factory generation.")
      return
    }
    // Check if generated directory is created
    if (!src.asFile.exists()) { src.asFile.mkdirs() }

    // Generate a factory class per each factory element
    extension.factories.get().forEach { factory ->
      if (factory.xsdFiles.isNotEmpty()) {
        val argsList = listOf(
          "-n", factory.classPrefix,
          "-p", factory.packageName,
          "-s", src.asFile.path,
          "-d", factory.destinationDirectory,
          if (factory.getAbstract) "-a" else "",
          "", factory.xsdConfigFile,
          if (factory.keepEmptyStrings) "-e" else "",
          ""
        ) + factory.xsdFiles.flatMap { listOf("", it) }

        project.logger.lifecycle("Running FactoryGenerator for files : ${factory.xsdFiles.joinToString()}, ${factory.xsdConfigFile}")

        project.javaexec {
          workingDir = project.file(src)
          classpath = project.the<SourceSetContainer>()["main"].runtimeClasspath
          args(*argsList.toTypedArray())
        }
      }
    }
  }
}
