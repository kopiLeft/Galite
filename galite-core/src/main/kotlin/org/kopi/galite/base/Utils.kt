/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.base

import org.kopi.galite.util.base.InconsistencyException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.ByteArrayInputStream
import java.io.PrintWriter
import java.io.FileWriter
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.io.DataInputStream
import java.net.URL
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.Date
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * loading of image
 * usage:
 * To load image first in Application directory, then in default directory:
 * Utils.getImage("name");
 * To load default images (that appear in org.kopi.vkopi.lib.util/resources)
 * Utils.getDefaultImage("name");
 * To load Application images (that appear in resources)
 * Utils.getApplicationImage("name");
 *
 */
class Utils : org.kopi.galite.util.base.Utils() {
  companion object {

    /**
     * Compress a file in a byte[]
     */
    fun compress(file: File): ByteArray {
      val baos = ByteArrayOutputStream()
      val output = GZIPOutputStream(baos)
      val input = FileInputStream(file)
      val buffer = ByteArray(10 * 1024)
      var length = input.read(buffer)
      while (length != -1) {
        output.write(buffer, 0, length)
        length = input.read(buffer)
      }
      output.close()
      return baos.toByteArray()
    }

    /**
     * Decompress a byte array
     */
    fun decompress(b: ByteArray): InputStream {
      return GZIPInputStream(ByteArrayInputStream(b))
    }

    /**
     * <p>Creates a temporary file in the default temporary directory.
     * The filename will look like this : prefixXXXX.extension</p>
     * <p>Please note that this file will be deleted at the shutdown of
     * the program. </p>
     *
     * @param prefix the prefix of the temp file
     * @param extension the extension of the temp file (can be null. in
     * @param deleteOnExit if the file has to be deleted at the end of the program.
     * this case default is "tmp")
     * @return an empty temp file on the local machine
     */
    fun getTempFile(prefix: String,
                    extension: String?,
                    deleteOnExit: Boolean = true): File {
      val extension = extension ?: "tmp"
      val file: File = File.createTempFile(prefix, ".$extension")
      if (deleteOnExit) {
        file.deleteOnExit()
      }
      return file
    }

    /**
     * return file from classpath or jar file
     * @param file must be an fully qualified file from resource directory
     * path separator is "/"
     * @return a File or null if not found
     */
    fun getFile(file: String): InputStream? {
      var fileToFind = getDefaultFile(file)
      if (fileToFind == null) {
        fileToFind = getApplicationFile(file)
      }
      if (fileToFind == null) {
        System.err.println("Utils ==> cant load: $file")
      }
      return fileToFind
    }

    /**
     * return file from classpath or jar file
     * @param img must be an file from resource directory
     * path separator is "/"
     * @return an fileIcon or null if not found
     */
    private fun getDefaultFile(img: String): InputStream? {
      return getFileFromResource(img, RESOURCE_DIR)
    }

    /**
     * return image from classpath or jar file
     * @param img must be an image from resource directory
     * path separator is "/"
     * @return an imageIcon or null if not found
     */
    private fun getApplicationFile(img: String): InputStream? {
      return getFileFromResource(img, APPLICATION_DIR)
    }

    /**
     * return an URL from the resources
     */
    fun getURLFromResource(name: String, directory: String? = RESOURCE_DIR): URL? {
      return if (directory == null) {
        null
      } else Utils::class.java.classLoader.getResource("$directory/$name")
      // Java Web Start needs to get the class loader based on
      // the current class.
    }

    /**
     * return file from resources or null if not found
     */
    fun getFileFromResource(name: String, directory: String?): InputStream? {
      return if (directory == null) {
        null
      } else {
        // Java Web Start needs to get the class loader based on
        // the current class.
        Utils::class.java.classLoader.getResourceAsStream("$directory/$name")
      }
    }

    fun log(mod: String, text: String) {
      System.err.println(mod + "\t" + text)
      // Utils.getTempFile creates a new file kopiXXX.log but we want
      // to use always the same file.
      val filename: String = System.getProperty("java.io.tmpdir") + File.separator + "kopi.log"
      try {
        val writer: PrintWriter = PrintWriter(FileWriter(filename, true))
        writer.println()
        writer.println()
        writer.println(Date().toString() + "\t" + mod + "\t" + text + "   ")
        if (writer.checkError()) {
          writer.close()
          throw IOException("error while writing")
        }
        writer.close()
      } catch (e: IOException) {
        // can't write error:
        System.err.println("Can't write in file: $filename")
        System.err.println(": " + e.message)
      }
    }

    fun convertUTF(str: String): ByteArray {
      try {
        return str.toByteArray(charset("UTF-8"))
      } catch (e: UnsupportedEncodingException) {
        throw InconsistencyException(e)
      }
    }

    fun convertUTF(bytes: ByteArray): String =
            try {
              String(bytes, Charset.forName("UTF-8"))
            } catch (e: UnsupportedEncodingException) {
              throw InconsistencyException(e)
            }

    /**
     * Returns the version of this build
     */
    fun getVersion(): Array<String> {
      try {
        val list = ArrayList<String>()
        val data = DataInputStream(Utils::class.java.classLoader.getResourceAsStream(APPLICATION_DIR + "/version"))
        while (data.available() != 0) {
          list.add(data.readLine())
        }
        data.close()
        return list.toTypedArray()
      } catch (e: Exception) {
        System.err.println("Error while reading version informations.\n$e")
      }
      return DEFAULT_VERSION
    }

    /**
     * 2003.08.14; jdk 1.4.1; Wischeffekt, Speicherverbrauch
     * Ab jdk 1.4.2 gibt es auch die option -XX:MinHeapFreeRatio=0
     */
    fun freeMemory() {
      if (allowExplicitGcCall == null) {
        allowExplicitGcCall = if (System.getProperty("visualKopi.allowExplicitGcCall") != null) {
          java.lang.Boolean.TRUE
        } else {
          java.lang.Boolean.FALSE
        }
      }
      if (allowExplicitGcCall == true) {
        System.gc()
      }
    }

    // ----------------------------------------------------------------------
    // PRIVATE DATA
    // ----------------------------------------------------------------------
    const val APPLICATION_DIR = "resources"
    const val RESOURCE_DIR = "org/kopi/vkopi/lib/resource"
    private val DEFAULT_VERSION = arrayOf(
            "No version information available.",
            "Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN",
            "Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT"
    )
    private var allowExplicitGcCall: Boolean? = true
  }
}
