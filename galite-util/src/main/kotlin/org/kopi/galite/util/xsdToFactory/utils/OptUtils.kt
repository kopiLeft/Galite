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

import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class OptUtils(paramArrayOfString: Array<String?>) : Constants {
  /**
   * Get the list of file ending with the extension `ext`
   * @param ext The extension of the file
   * @return The file array containing all file that have `ext` as an extension
   */
  fun filesEndingWith(ext: String): Array<File> {
    val result: MutableList<File> = ArrayList()
    val i = fileList.iterator()

    while (i.hasNext()) {
      val file = i.next()

      if (file!!.name.endsWith(".$ext") && !looksLikeURL(file.path)) {
        result.add(file)
      }
    }

    return result.toTypedArray()
  }

  /**
   * Get the list of the files in a given arguments
   * @return A list of file contained in the given arguments
   */
  private val fileList: Array<File?>
    get() {
      if (files[0] == null) {
        val arrayOfFile = arguments.mapNotNull { it?.let { File(it) } }.toTypedArray()

        arrayOfFile.firstOrNull { baseDir == null }?.let { file ->
          baseDir = file.takeIf { it.isDirectory } ?: file.parentFile
        }

        for (file in arrayOfFile) {
          val uri = file.toURI()
          if (baseDir != null && baseDir!!.toURI().relativize(uri) == uri) {
            baseDir = null
            break
          }
        }

        files = Collections.unmodifiableList(collectFiles(arrayOfFile)).toTypedArray()
      }
      return files
    }


  /**
   * Get the list of URL found in the `OptUtils.arguments`
   * @return A `java.util.List` of `URL` in the arguments
   */
  private val urlList: List<URL>?
    get() {
      if (this.urls == null) {
        val urls: MutableList<URL> = ArrayList()

        arguments.forEach {
          if (looksLikeURL(it!!)) {
            try {
              urls.add(URL(it))
            } catch (mfEx: MalformedURLException) {
              System.err.println("ignoring invalid url: " + it + ": " + mfEx.message)
            }
          }
        }

        this.urls = Collections.unmodifiableList(urls)
      }

      return this.urls
    }

  /**
   * Collect files in a given directories
   * @param dirs The directories where to search for a file
   * @return A list containing all found files in the given directories
   */
  private fun collectFiles(dirs: Array<File>): List<File?> {
    val files: MutableList<File?> = ArrayList()

    for (i in dirs.indices) {
      val file = dirs[i]

      if (!file.isDirectory) {
        files.add(file)
      } else {
        files.addAll(collectFiles(file.listFiles()))
      }
    }

    return files
  }

  /**
   * Check for an URL structure
   * @param str
   * A String representing a file
   * @return
   * True if the given String is an URL
   */
  private fun looksLikeURL(str: String): Boolean {
    return str.startsWith("http:") || str.startsWith("https:") || str.startsWith("ftp:") || str.startsWith("file:")
  }

  /**
   * Get the list of all `URL` contained in the given `OptUtils.arguments`
   * @return An array of `URL` found in the given `OptUtils.arguments`
   */
  val uRLs: Array<URL>
    get() = urlList!!.toTypedArray()

  // Variables
  private var arguments: Array<String?> = paramArrayOfString
  private var files: Array<File?> = arrayOf(null)
  private var urls: List<URL>? = null
  var baseDir: File? = null
    private set
}
