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

abstract class FactoryGeneratorTask : JavaExec() {
  init {
    description = "Task to generate factories from .xsd and .xsdConfig files"
    mainClass.set("org.kopi.galite.util.xsdToFactory.generator.FactoryGenerator")
  }

  @TaskAction
  override fun exec() {
    val extension = project.extensions.getByType(FactoryGeneratorExtention::class.java)
    if (extension.xsdFiles.isNotEmpty()) {
      val argsList = listOf(
        "-n", extension.classPrefix,
        "-p", extension.packageName,
        "-s", extension.src,
        "-d", extension.destinationDirectory,
        "-a", extension.getAbstract,
        "", extension.xsdConfigFile,
        "-e",
        ""
      ) + extension.xsdFiles.flatMap { listOf("", it) }

      workingDir = project.file(extension.src)
      classpath = project.the<SourceSetContainer>()["main"].runtimeClasspath
      args(*argsList.toTypedArray())
      super.exec()
    }
  }
}
