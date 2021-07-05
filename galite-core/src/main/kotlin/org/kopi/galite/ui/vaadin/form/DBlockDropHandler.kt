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
package org.kopi.galite.ui.vaadin.form

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dnd.DragSource
import com.vaadin.flow.server.StreamVariable
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VField
import org.kopi.galite.form.VImageField
import org.kopi.galite.form.VStringField
import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler
import org.kopi.galite.visual.VException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import javax.activation.MimetypesFileTypeMap

/**
 * The `DBlockDropHandler` is the block implementation
 * of the [DropHandler] specifications.
 *
 * @param block The block model.
 */
class DBlockDropHandler(private val block: VBlock,
                        component: Component)
  : DragSource<Component> by DragSource.configure(component) {

  //---------------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------------
  private var fileList: ArrayList<File>? = null
  private var filesCount = 0

  //---------------------------------------------------
  // DROPTARGETLISTENER IMPLEMENTATION
  //---------------------------------------------------
  /*fun drop(event: DragAndDropEvent) { TODO
    if (isAccepted(event.getTransferable() as WrapperTransferable)) {
      if (isChartBlockContext) {
        fileList = ArrayList()
        filesCount = (event.getTransferable() as WrapperTransferable).getFiles().length
        for (i in 0 until filesCount) {
          (event.getTransferable() as WrapperTransferable).getFiles().get(i).setStreamVariable(
                  StreamHandler())
        }
      } else {
        (event.getTransferable() as WrapperTransferable).getFiles().get(0).setStreamVariable(StreamHandler())
      }
    }
  }*/

  // val acceptCriterion: AcceptCriterion TODO
  //   get() = AcceptAll.get() TODO

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
            handleDrop(fileList)
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
   * Returns `true` is the data flavor is accepted.
   * @param transferable The [WrapperTransferable] instance.
   * @return `true` is the data flavor is accepted.
   */
  /*private fun isAccepted(transferable: WrapperTransferable): Boolean { TODO
    val flavors: ArrayList<Html5File>
    flavors = ArrayList<Html5File>()
    for (i in 0 until transferable.getFiles().length) {
      flavors.add(transferable.getFiles().get(i))
    }
    return if (isChartBlockContext) {
      isAccepted(flavors)
    } else {
      if (flavors.size > 1) {
        false
      } else {
        isAccepted(getExtension(flavors[0]))
      }
    }
  }*/

  /**
   * Returns `true` is the given data flavor is accepted for drop operation.
   * @param flavor The data flavor.
   * @return `true` is the given data flavor is accepted for drop operation.
   */
  private fun isAccepted(flavor: String?): Boolean {
    return flavor != null && flavor.isNotEmpty() && block.isAccepted(flavor)
  }

  /**
   * A List of flavors is accepted if all elements
   * of the list are accepted and have the same extension
   * @param flavors The data flavors.
   * @return `true` when the drop operation succeeded.
   */
  /*private fun isAccepted(flavors: ArrayList<Html5File>): Boolean { TODO
    var oldFlavor: String? = null
    for (i in flavors.indices) {
      val newFlavor: String = getExtension(flavors[i])
      if (oldFlavor != null && newFlavor != oldFlavor
              || !isAccepted(newFlavor)) {
        return false
      }
      oldFlavor = newFlavor
    }
    return true
  }*/

  /**
   * Handles drop action for multiple files in a chart block.
   * @param files The list of files to be dropped.
   * @return `true` when the drop operation succeeded.
   * @throws VException Visual errors.
   */
  private fun handleDrop(files: ArrayList<File>?): Boolean {
    for (i in files!!.indices) {
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
  private val isChartBlockContext: Boolean
    get() = block.noDetail() || block.isMulti() && !block.isDetailMode

  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  /**
   * The `StreamHandler` is the block drop target handler
   * of the [StreamVariable] specifications.
   */
  private inner class StreamHandler : StreamVariable {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun getOutputStream(): OutputStream = bas

    override fun listenProgress(): Boolean = true

    override fun onProgress(event: StreamVariable.StreamingProgressEvent) {
      if (event.contentLength > 50 * 1024 * 1024) {
        // show progress bar only for file larger than 50 MB
        block.form.setCurrentJob(event.bytesReceived.toInt())
      }
    }

    override fun streamingStarted(event: StreamVariable.StreamingStartEvent) {
      if (event.contentLength > 50 * 1024 * 1024) {
        block.form.setProgressDialog("", java.lang.Long.valueOf(event.getContentLength()).toInt())
      }
    }

    override fun streamingFinished(event: StreamVariable.StreamingEndEvent) {
      try {
        val out: FileOutputStream
        val temp = createTempFile(event.fileName)
        out = FileOutputStream(temp)
        bas.writeTo(out)
        acceptDrop(temp)
      } catch (e: IOException) {
        acceptDrop(null)
      } finally {
        if (event.contentLength > 50 * 1024 * 1024) {
          block.form.unsetProgressDialog()
        }
      }
    }

    override fun streamingFailed(event: StreamVariable.StreamingErrorEvent) {
      event.exception.printStackTrace(System.err)
      Thread {
        block.form.error(event.exception.message)
        // BackgroundThreadHandler.updateUI() TODO
      }.start()
    }

    override fun isInterrupted(): Boolean = false

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

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    private val bas = ByteArrayOutputStream()
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
    private const val serialVersionUID = 3924306391945432925L

    init {
      // missing PNG files in initial map
      MIMETYPES_FILE_TYPEMAP.addMimeTypes("image/png png")
    }
  }
}
