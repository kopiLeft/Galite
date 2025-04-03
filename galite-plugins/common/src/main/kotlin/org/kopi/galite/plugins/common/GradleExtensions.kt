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

package org.kopi.galite.plugins.common

import java.io.File
import java.nio.file.Files

import org.apache.tools.ant.taskdefs.condition.Os

import org.gradle.api.Project

open class GradleExtensions(private val project: Project) {
  /**
   * removes the file with name [fileName] existing in [folder]
   */
  fun clean(fileName: String, folder: String? = null) {
    var success: Boolean? = null
    val fileToClean = if (folder != null) {
      project.file(folder + File.separator + fileName)
    } else {
      project.file(fileName)
    }
    when {
      fileToClean.isDirectory -> success = fileToClean.deleteRecursively()
      fileToClean.isFile -> success = fileToClean.delete()
      Os.isFamily(Os.FAMILY_UNIX) && Files.isSymbolicLink(fileToClean.toPath()) -> success = fileToClean.delete()
      folder != null -> {
        val files = project.fileTree(folder)
        files.include("**/$fileName")
        files.forEach {
          success = it.deleteRecursively()
        }
      }
    }
    if (success == true) {
      project.logger.lifecycle("${fileToClean.path} is deleted")
    } else {
      project.logger.lifecycle("Unable to delete ${fileToClean.path}")
    }
  }
}
