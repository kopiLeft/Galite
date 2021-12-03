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
package org.kopi.galite.visual.ui.vaadin.form

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

import javax.activation.MimetypesFileTypeMap

import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.form.VImageField
import org.kopi.galite.visual.form.VStringField
import org.kopi.galite.visual.visual.VException

import com.vaadin.flow.component.upload.Receiver
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer
import com.vaadin.flow.server.StreamVariable

/**
 * The `DBlockDropHandler` Is handling drop on a block.
 *
 * @param block The block model.
 */
class DBlockDropHandler(private val block: VBlock) {

  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------
  private var fileList: MutableList<File>? = null
  private var filesCount = 0
  private var isUploadStarted = false
  internal val streamHandler = StreamHandler()

  fun onStart() {
    if (!isUploadStarted) {
      fileList = mutableListOf()
      isUploadStarted = true
    }
  }

  fun onFinish(buffer: Receiver) {
    if(buffer is MultiFileBuffer) {
      filesCount = buffer.files.size
    }
    isUploadStarted = false
  }

  //---------------------------------------------------------
  // UTILS
  //---------------------------------------------------------
  /**
   * Launches the drop operation for a given file.
   * @param file The file instance.
   * @throws VException Visual errors.
   */
  private fun acceptDrop(file: File?) {
    if (file != null) {
      try {
        if (isChartBlockContext) {
          fileList!!.add(file)
          if (fileList!!.size == filesCount) {
            handleDrop(fileList!!)
          }
        } else {
          handleDrop(file, getExtension(file))
        }
      } catch (e: VException) {
        // nothing to do
        e.printStackTrace()
      }
    }
  }

  /**
   * Returns the accepted flavors for drop operation.
   *
   * @return the set of accepted flavors for drop operation.
   */
  internal val acceptedFlavors: MutableSet<String> get() = block.acceptedFlavors

  /**
   * Handles drop action for multiple files in a chart block.
   * @param files The list of files to be dropped.
   * @return `true` when the drop operation succeeded.
   * @throws VException Visual errors.
   */
  private fun handleDrop(files: MutableList<File>): Boolean {
    for (i in files.indices) {
      val file = files[i]
      if (!handleDrop(file, getExtension(file))) {
        return false
      }
    }
    return true
  }

  /**
   * Handles drop operations for given flavors.
   * @param file The file instance.
   * @param flavor The data flavors.
   * @return `true` when the drop operation succeeded.
   * @throws VException Visual errors.
   */
  private fun handleDrop(file: File, flavor: String?): Boolean {
    val target: VField = block.getDropTarget(flavor.orEmpty()) ?: return false // TODO: orEmpty() ?
    target.onBeforeDrop()
    return if (target is VStringField) {
      if (target.width < file.absolutePath.length) {
        false
      } else {
        if (isChartBlockContext) {
          val rec = getFirstUnfilledRecord(block, target)
          block.activeRecord = rec
          block.currentRecord = rec
          target.setString(rec, file.absolutePath)
          target.onAfterDrop()
          block.activeRecord = rec + 1
          block.currentRecord = rec + 1
          block.gotoRecord(block.activeRecord)
          true
        } else {
          target.setString(file.absolutePath)
          target.onAfterDrop()
          true
        }
      }
    } else if (target is VImageField) {
      if (!target.isInternal()) {
        if (isImage(file)) {
          handleImage(target, file)
        } else {
          false
        }
      } else {
        handleImage(target, file)
      }
    } else {
      false
    }
  }

  /**
   * Handles the drop process for image files.
   * @param target The target image field.
   * @param file The file instance.
   * @return `true` is the drop operation succeeded.
   * @throws VException Visual errors.
   */
  private fun handleImage(target: VImageField, file: File): Boolean {
    return if (isChartBlockContext) {
      val rec = getFirstUnfilledRecord(block, target)
      block.activeRecord = rec
      block.currentRecord = rec
      target.setImage(rec, toByteArray(file))
      target.onAfterDrop()
      block.activeRecord = rec + 1
      block.currentRecord = rec + 1
      block.gotoRecord(block.activeRecord)
      true
    } else {
      target.setImage(toByteArray(file))
      target.onAfterDrop()
      true
    }
  }

  /**
   * Returns `true` is the context block is chart block.
   * @return `true` is the context block is chart block.
   */
  internal val isChartBlockContext: Boolean
    get() = block.noDetail() || block.isMulti() && !block.isDetailMode

  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  /**
   * The `StreamHandler` is the block drop target handler
   * of the [StreamVariable] specifications.
   */
  inner class StreamHandler {

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------

    fun onProgress(bytesReceived: Long, contentLength: Long) {
      if (contentLength > MAX_SIZE_TO_WAIT) {
        // show progress bar only for file larger than 50 MB
        block.form.setCurrentJob(bytesReceived.toInt())
      }
    }

    fun streamingStarted(contentLength: Long) {
      if (contentLength > MAX_SIZE_TO_WAIT) {
        block.form.setProgressDialog("", java.lang.Long.valueOf(contentLength).toInt())
      }
    }

    fun streamingFinished(fileName: String, mimeType: String, contentLength: Long, bas: ByteArrayOutputStream) {
      try {
        val out: FileOutputStream
        val temp = createTempFile(fileName)
        out = FileOutputStream(temp)
        bas.writeTo(out)
        acceptDrop(temp)
      } catch (e: IOException) {
        acceptDrop(null)
      } finally {
        if (contentLength > MAX_SIZE_TO_WAIT) {
          block.form.unsetProgressDialog()
        }
      }
    }

    fun streamingFailed(fileName: String, mimeType: String, contentLength: Long, exception: Exception) {
      exception.printStackTrace(System.err)
      Thread {
        block.form.error(exception.message)
      }.start()
    }

    /**
     * Creates a temporary file.
     * @param directory The parent directory.
     * @param defaultName The default file name.
     * @return The created temporary file.
     * @throws IOException I/O errors.
     */
    protected fun createTempFile(defaultName: String?): File {
      val basename = getBaseFileName(defaultName)
      val extension = getExtension(defaultName)
      return File.createTempFile(basename, ".$extension", null)
    }

    /**
     * Returns the file extension of a given file name.
     * @param defaultName The default file name.
     * @return The file extension.
     */
    protected fun getExtension(defaultName: String?): String {
      if (defaultName != null) {
        val index = defaultName.lastIndexOf('.')
        if (index != -1) {
          return defaultName.substring(Math.min(defaultName.length, index + 1))
        }
      }
      return "" // no extension.
    }

    /**
     * Returns the base file name (without file extension).
     * @param defaultName The default file name.
     * @return The base file name
     */
    protected fun getBaseFileName(defaultName: String?): String {
      if (defaultName != null) {
        val index = defaultName.lastIndexOf('.')
        if (index != -1) {
          return defaultName.substring(0, Math.min(defaultName.length, index))
        }
      }
      return "" // empty name.
    }
  }

  companion object {
    /**
     * Returns the file extension.
     * @param file The file instance.
     * @return The file extension.
     */
    private fun getExtension(file: File): String? {
      var extension: String? = null
      val name = file.name
      val index = name.lastIndexOf('.')
      if (index > 0 && index < name.length - 1) {
        extension = name.substring(index + 1).toLowerCase()
      }
      return extension
    }

    /**
     * Returns `true` is the given file is an image.
     * @param file The file instance.
     * @return `true` is the given file is an image.
     */
    private fun isImage(file: File): Boolean {
      val mimeType = MIMETYPES_FILE_TYPEMAP.getContentType(file)
      return mimeType.split("/").toTypedArray()[0] == "image"
    }

    /**
     * Returns the bytes of the given file.
     * @param file The file instance.
     * @return The file bytes.
     */
    private fun toByteArray(file: File): ByteArray? {
      return try {
        val baos = ByteArrayOutputStream()
        copy(FileInputStream(file), baos, 1024)
        baos.toByteArray()
      } catch (e: IOException) {
        null
      }
    }

    /**
     * Copies the given input stream into the given output stream.
     * @param input The input stream to be copied.
     * @param output The destination output stream.
     * @param bufferSize The buffer size.
     * @throws IOException I/O errors.
     */
    private fun copy(input: InputStream, output: OutputStream, bufferSize: Int) {
      val buf = ByteArray(bufferSize)
      var bytesRead = input.read(buf)
      while (bytesRead != -1) {
        output.write(buf, 0, bytesRead)
        bytesRead = input.read(buf)
      }
      output.flush()
    }

    /**
     * Looks for the first unfilled record according to the given target field.
     * @param block The block model.
     * @param target The target field.
     * @return The record for which the given field is `null`.
     */
    private fun getFirstUnfilledRecord(block: VBlock, target: VField): Int {
      for (i in 0 until block.bufferSize) {
        if (target.isNull(i)) {
          return i
        }
      }
      return 0
    }

    private val MIMETYPES_FILE_TYPEMAP = MimetypesFileTypeMap()
    const val MAX_SIZE_TO_WAIT = 50 * 1024 * 1024

    init {
      // missing PNG files in initial map
      MIMETYPES_FILE_TYPEMAP.addMimeTypes("image/png png")
    }
  }
}
