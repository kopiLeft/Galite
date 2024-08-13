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

package org.kopi.galite.util.xsdToFactory.utils

import java.io.*
import java.nio.file.Paths
import java.nio.file.Files

import org.apache.xmlbeans.SystemProperties

object IOUtil {
  /**
   * Create a new directory from a base path and a java package
   * @param baseDir The base directory
   * @param pkg The package name
   * @return A new File representing the new created directory
   */
  fun createSourceDir(baseDir: String?, pkg: String): File {
    val pkgPath = pkg.replace('.', '/')
    val newdir = File(baseDir, pkgPath)
    val created = (newdir.exists() && newdir.isDirectory) || newdir.mkdirs()

    assert(created) { "Could not create " + newdir.absolutePath }

    return newdir
  }

  /**
   * Create a file in a given directory
   * @param baseDir The base directory
   * @param name The file name
   * @param ext The file extension
   * @return A `java.io.OutputStream` instance
   * @throws FileNotFoundException
   */
  @Throws(FileNotFoundException::class)
  fun createFactoryStream(baseDir: File, name: String, ext: String): OutputStream {
    createDir(baseDir,null)
    val absolutePath = baseDir.absolutePath
    val factory = File("$absolutePath/$name.$ext")

    // Add destination directory if not exists
    Files.createDirectories(Paths.get(absolutePath))

    return FileOutputStream(factory)
  }

  /**
   * Get the standard factory writer
   * @param output The output stream
   * @return The writer
   */
  fun getFactoryWriter(output: OutputStream): Writer {
    return PrintWriter(BufferedWriter(OutputStreamWriter(output)), true)
  }

  /**
   * Create a temporary directory
   * @return The file representing the temporary directory
   * @throws IOException
   */
  @Throws(IOException::class)
  fun createTempdir(): File {
    val tmpDirFile = File(SystemProperties.getProperty("java.io.tmpdir"))

    if (!tmpDirFile.exists()) {
      tmpDirFile.mkdirs()
    }

    val tmpFile = File.createTempFile("xbean", ".tmp")
    val tmpSrcDir = File(tmpFile.absolutePath + ".d")

    if (!tmpSrcDir.exists()) {
      tmpSrcDir.mkdirs()
    }

    tmpFile.deleteOnExit()

    return tmpSrcDir
  }

  /**
   * Create a directory according to a given root path and a subdir
   * @param rootdir The root path
   * @param subdir The subdir path
   * @return A new `File` representing the new directory
   */
  fun createDir(rootdir: File?, subdir: String?): File {
    val newdir = if ((subdir == null)) rootdir else File(rootdir, subdir)
    val created = (newdir!!.exists() && newdir.isDirectory) || newdir.mkdirs()

    assert(created) { "Could not create " + newdir.absolutePath }

    return newdir
  }
}
