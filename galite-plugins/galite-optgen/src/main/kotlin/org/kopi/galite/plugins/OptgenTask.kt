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

abstract class OptgenTask : JavaExec() {
  init {
    description = "Task to generate java classes to parse *Options.xml files."
    mainClass.set("org.kopi.galite.util.optgen.Main")
  }
  @OutputDirectory
  val src = project.layout.projectDirectory.dir(GradleExtensionsPlugin.GENERATED_JAVA_DIRECTORY)

  @TaskAction
  override fun exec() {
    val extension = project.extensions.getByType(OptgenExtention::class.java)

    if (extension.parameters.getOrElse(emptyList()).isEmpty()) {
      project.logger.lifecycle("No Options.xml defined for option parser generation.")
      return
    }
    // Check if generated directory is created
    if (!src.asFile.exists()) { src.asFile.mkdirs() }

    // Generate an option parser class per each parameter element
    extension.parameters.get().forEach { param ->
      if (!param.optionFiles.isEmpty) {
        val argsList = buildList {
          if (param.release.isNotBlank()) {
            add("--release=${param.release}")
          }
          addAll(param.optionFiles.files.map { it.absolutePath })
        }


        project.javaexec {
          workingDir = project.file(src)
          mainClass.set("org.kopi.galite.util.optgen.Main")
          classpath = project.the<SourceSetContainer>()["main"].runtimeClasspath
          args(*argsList.toTypedArray())
        }
      }
    }
  }
}
