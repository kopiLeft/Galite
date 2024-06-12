/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2024 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.plugin

import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.the

abstract class FactoryGeneratorTask : JavaExec() {
  @Input
  var namee = ""

  @Input
  var fpackage = ""

  @OutputDirectory
  var src = ""

  @OutputDirectory
  var directory = ""

  @Input
  var xsdFiles = ""

  @Input
  var getAbstract = false

  @Input
  var xsdConfigFile = ""

  init {
    description = "Task to generate factories from .xsd and .xsdConfig files"
    mainClass.set("org.kopi.galite.util.xsdToFactory.generator.FactoryGenerator")
  }

  @TaskAction
  override fun exec() {
    workingDir = project.file(src)
    classpath = project.the<SourceSetContainer>()["main"].runtimeClasspath

    args(listOfArgs("-n", namee, "-p", fpackage, "-s", src, "-d", directory, "-a", getAbstract, xsdFiles, xsdConfigFile))

    super.exec()
  }
}

/**
 * returns a flat list of arguments.
 *
 * @param args program arguments
 */
fun listOfArgs(vararg args: Any?): List<String> {
  val flattenArgs = mutableListOf<String>()

  args.forEach { arg ->
    when (arg) {
      is String? -> flattenArgs.add(arg.orEmpty())
      is List<*>? -> arg.orEmpty().forEach { flattenArgs.add(it as String) }
    }
  }

  return flattenArgs
}
