/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.ui.vaadin.visual

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import org.kopi.galite.ui.vaadin.upload.FileUploader
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.FileHandler
import org.kopi.galite.visual.UWindow

class VFileHandler : FileHandler() {

  // --------------------------------------------------
  // FILE HANDLER IMPLEMENTATION
  // --------------------------------------------------
  override fun chooseFile(window: UWindow, defaultName: String): File? {
    return chooseFile(window, null, defaultName)
  }

  override fun chooseFile(window: UWindow, dir: File?, defaultName: String): File? {
    return try {
      createTempFile(dir, defaultName)
    } catch (e: IOException) {
      e.printStackTrace()
      getApplication().displayError(window, e.message)
      null
    }
  }

  override fun openFile(window: UWindow, defaultName: String): File? = openFile(window, null, defaultName)

  override fun openFile(window: UWindow, filter: FileFilter): File? =
          openFile(window, null, null, getMimeType(filter))

  override fun openFile(window: UWindow, dir: File?, defaultName: String): File? =
          openFile(window, dir, defaultName, null)

  /**
   * Uploads a file from client side with the given mime type.
   *
   * @param window            The window display.
   * @param dir               The directory to be used to store the uploaded file.
   * @param defaultName       The file name to be used.
   * @param mimeType          The searched mime type.
   * @return                  The uploaded file.
   */
  internal fun openFile(window: UWindow, dir: File?, defaultName: String?, mimeType: String?): File? {
    val uploader = FileUploader()
    val file: ByteArray? = uploader.upload(mimeType)
    return if (file != null) {
      toFile(window, file, dir, if (uploader.fileName == null) defaultName else uploader.fileName)
    } else null
  }

  /**
   * Returns the current application instance.
   * @return The current application instance.
   */
  internal fun getApplication(): VApplication = ApplicationContext.applicationContext.getApplication() as VApplication

  /**
   * Converts the given bytes to a file. The file is created under OS temp directory.
   *
   * @param file          The file bytes.
   * @param directory     The parent directory.
   * @param defaultName   The default file name.
   * @return
   */
  internal fun toFile(window: UWindow, file: ByteArray, directory: File?, defaultName: String?): File? {
    return try {
      val destination = createTempFile(directory, defaultName)
      val out = FileOutputStream(destination)
      out.write(file)
      out.close()
      destination
    } catch (e: IOException) {
      getApplication().displayError(window, e.message)
      null
    }
  }

  /**
   * Creates a temporary file.
   *
   * @param directory       The parent directory.
   * @param defaultName     The default file name.
   * @return                The created temporary file.
   * @throws IOException    I/O errors.
   */
  internal fun createTempFile(directory: File?, defaultName: String?): File {
    var directory = directory

    // if parent directory does not exist, create file in java.io.tempdir directly.
    if (directory != null && !directory.exists()) {
      directory = null
    }
    // add a blank between the prefix and the random text
    val basename = ensurePrefixLength(getBaseFileName(defaultName)) + " "
    val extension = getExtension(defaultName)
    return File.createTempFile(basename, extension, directory)
  }

  /**
   * Returns the file extension of a given file name.
   *
   * @param defaultName   The default file name.
   * @return              The file extension.
   */
  internal fun getExtension(defaultName: String?): String? {
    if (defaultName != null) {
      val index = defaultName.lastIndexOf('.')
      if (index != -1) {
        return "." + defaultName.substring(defaultName.length.coerceAtMost(index + 1))
      }
    }
    return null // ".tmp" will be added when calling File#createTempFile(String, String, File).
  }

  /**
   * Returns the base file name (without file extension).
   *
   * @param defaultName     The default file name.
   * @return                The base file name
   */
  internal fun getBaseFileName(defaultName: String?): String? {
    if (defaultName != null) {
      val index = defaultName.lastIndexOf('.')
      if (index != -1) {
        return defaultName.substring(0, Math.min(defaultName.length, index))
      }
    }
    return defaultName // returns the entire default name.
  }

  /**
   * When calling [File.createTempFile] the name prefix should at least have 3 characters.
   * This method aims to satisfy this condition and complete missing length with X character
   * to avoid [IllegalArgumentException] when creating temporary files.
   *
   * @param prefix  The file name prefix.
   * @return        A prefix having at least three characters. If `prefix == null`, "XXX" is returned.
   */
  private fun ensurePrefixLength(prefix: String?): String {
    return if (prefix != null && prefix.length > 3) {
      prefix
    } else {
      val buffer = StringBuffer()
      buffer.append(prefix)
      while (buffer.length < 3) {
        buffer.insert(prefix!!.length, "X")
      }
      buffer.toString()
    }
  }

  /**
   * Returns the mime type provided by the given file filter.
   * The technique is to loop all over known mime types and to test the if the file is accepted by the filter.
   * The corresponding mime type is returned.
   * If filter provide unknown file, `null` is returned.
   *
   * @param filter  The file filter.
   * @return        The corresponding mime type.
   */
  private fun getMimeType(filter: FileFilter): String? {
    val mimeTypes: MutableList<String>
    mimeTypes = ArrayList()
    for ((key, value) in knownFileTypeToMimeType) {
      /**
       * A dummy file is created. It does not matter if file exists or not.
       * Generally, file filter tests on file extensions.
       */
      if (filter.accept(File("file.$key"))) {
        // we return the first mime type that fits with the filter
        mimeTypes.add(value)
      }
    }
    return toString(mimeTypes)
  }

  /**
   * Converts the given list to a string where elements are separated by a comma.
   *
   * @param list    The list of strings.
   * @return        The separated comma string.
   */
  private fun toString(list: List<String>?): String? {
    return if (list == null || list.isEmpty()) {
      null
    } else {
      buildString {
        for (i in list.indices) {
          append(list[i])
          if (i != list.size - 1) {
            append(", ")
          }
        }
      }
    }
  }

  companion object {
    /**
     * File type to mime type map
     * This will be used to inject mime type according to a given file filter.
     * This map will be initialized with known mime types.
     * If a file filter comes with unknown type, `null` will be returned as mime type.
     *
     * The injected of list of mime types are picked form
     * http://www.sitepoint.com/web-foundations/mime-types-summary-list/
     */
    private var knownFileTypeToMimeType = mutableMapOf(
            "au" to "audio/basic",
            "avi" to "video/msvideo, video/avi, video/x-msvideo, .avi",
            "bmp" to "image/bmp",
            "bz2" to "application/x-bzip2",
            "css" to "text/css",
            "dtd" to "application/xml-dtd",
            "doc" to "application/msword, .doc",
            "docx" to "application/vnd.openxmlformats-officedocument.wordprocessingml.document, .docx",
            "dotx" to "application/vnd.openxmlformats-officedocument.wordprocessingml.template, .dotx",
            "es" to "application/ecmascript",
            "exe" to "application/octet-stream",
            "gif" to "image/gif",
            "gz" to "application/x-gzip",
            "hqx" to "application/mac-binhex40",
            "html" to "text/html",
            "jar" to "application/java-archive",
            "jpg" to "image/jpeg",
            "js" to "application/x-javascript",
            "midi" to "audio/x-midi",
            "mp3" to "audio/mpeg",
            "mpeg" to "video/mpeg",
            "ogg" to "audio/vorbis, application/ogg",
            "pdf" to "application/pdf",
            "pl" to "application/x-perl",
            "png" to "image/png",
            "potx" to "application/vnd.openxmlformats-officedocument.presentationml.template",
            "ppsx" to "application/vnd.openxmlformats-officedocument.presentationml.slideshow",
            "ppt" to "application/vnd.ms-powerpointtd, .ppt",
            "pptx" to "application/vnd.openxmlformats-officedocument.presentationml.presentation, .pptx",
            "ps" to "application/postscript",
            "qt" to "video/quicktime",
            "ra" to "audio/x-pn-realaudio, audio/vnd.rn-realaudio",
            "ram" to "audio/x-pn-realaudio, audio/vnd.rn-realaudio",
            "rdf" to "application/rdf, application/rdf+xml",
            "rtf" to "application/rtf",
            "sgml" to "text/sgml",
            "sit" to "application/x-stuffit",
            "sldx" to "application/vnd.openxmlformats-officedocument.presentationml.slide",
            "svg" to "image/svg+xml",
            "swf" to "application/x-shockwave-flash",
            "tar.gz" to "application/x-tar",
            "tgz" to "application/x-tar",
            "tiff" to "image/tiff",
            "tsv" to "text/tab-separated-values",
            "txt" to "text/plain",
            "wav" to "audio/wav, audio/x-wav",
            "xlam" to "application/vnd.ms-excel.addin.macroEnabled.12",
            "csv" to "text/csv, application/csv, text/comma-separated-values, text/x-comma-separated-values, text/tab-separated-values, .csv",
            "xls" to "application/vnd.ms-excel, .xls",
            "xlsb" to "application/vnd.ms-excel.sheet.binary.macroEnabled.12",
            "xlsx" to "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, .xlsx",
            "xltx" to "application/vnd.openxmlformats-officedocument.spreadsheetml.template",
            "xml" to "application/xml",
            "zip" to "application/zip, application/x-compressed-zip"
    )
  }
}
