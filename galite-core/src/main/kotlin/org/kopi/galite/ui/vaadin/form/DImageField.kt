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

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * The image field implementation based on the customized VAADIN
 * addons.
 */
class DImageField(
        model: VFieldUI?,
        label: DLabel?,
        align: Int,
        options: Int,
        width: Int,
        height: Int,
        detail: Boolean
) : DObjectField(model, label, align, options, detail), DropHandler, ImageFieldListener {
  // --------------------------------------------------
  // IMPLEMENTATION OF ABSTRACTS METHODS
  // --------------------------------------------------
  override fun getObject(): Any? {
    return image
  }

  override fun setBlink(b: Boolean) {
    // TODO
  }

  // ----------------------------------------------------------------------
  // UI MANAGEMENT
  // ----------------------------------------------------------------------
  override fun updateAccess() {
    label!!.update(model, getPosition())
  }

  override fun updateText() {
    setObject((getModel() as VImageField).getImage(model.getBlockView().getRecordFromDisplayLine(getPosition())))
    super.updateText()
  }

  override fun updateFocus() {
    label!!.update(model, getPosition())
    super.updateFocus()
  }

  override fun updateColor() {
    // color properties are not set for an image field.
  }

  fun onRemove() {
    setObject(null)
  }

  fun onImageClick() {
    performAutoFillAction()
  }

  fun drop(event: DragAndDropEvent) {
    val files: Array<Html5File> = (event.getTransferable() as WrapperTransferable).getFiles()
    if (files.size > 0) {
      // even if there are multiple images dropped, we take only the first one.
      val image: Html5File
      image = files[0]
      // look if it is an image
      if (image.getType() != null && image.getType().contains("image/")) {
        image.setStreamVariable(ImageStreamHandler())
      }
    }
  }

  val acceptCriterion: AcceptCriterion
    get() = AcceptAll.get()

  /**
   * Sets the object associated to record r
   * @param r The position of the record
   * @param s The object to set in
   */
  fun setObject(s: Any?) {
    BackgroundThreadHandler.access(Runnable {
      if (s == null) {
        field.setIcon(null)
      } else {
        field.setIcon(DynamicImageResource(ImageStreamSource(s as ByteArray?), createFileName("image")))
        setBlink(false)
        setBlink(true)
      }
    })
    image = s as ByteArray?
  }

  /**
   * Creates the dynamic image name.
   * @param baseName The base name.
   * @return The dynamic image name.
   */
  protected fun createFileName(baseName: String): String {
    return baseName + Date.now().format("yyyyMMddHHmmssSSS").toString() + ".png"
  }
  //---------------------------------------------------
  // STREAM RESOURCE
  //---------------------------------------------------
  /**
   * The `ImageStreamSource` is the [StreamSource]
   * for an image field.
   */
  /*package*/
  internal inner class ImageStreamSource
  /**
   * Creates a new `ImageStreamSource` instance.
   * @param image The image content.
   */(
          //---------------------------------------
          // DATA MEMBERS
          //---------------------------------------
          private val image: ByteArray?
  ) : StreamSource {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    val stream: InputStream?
      get() = image?.let { ByteArrayInputStream(it) }
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
  }

  /**
   * A dynamic [StreamResource] for an image field.
   */
  /*package*/
  internal inner class DynamicImageResource(streamSource: StreamSource?, fileName: String?) : StreamResource(
          streamSource, fileName) {
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    /**
     * Creates a new `DynamicImageResource` instance.
     * @param streamSource The [StreamSource] object.
     * @param fileName The file name.
     */
    init {
      setCacheTime(0L)
    }
  }

  /**
   * The image stream handler for reading uploaded stream from DnD
   * operations.
   */
  /*package*/
  internal inner class ImageStreamHandler : StreamVariable {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    val outputStream: OutputStream
      get() = output

    fun listenProgress(): Boolean {
      return true
    }

    fun onProgress(event: StreamingProgressEvent) {
      // show progress only when the file is bigger than 50MB
      if (event.getContentLength() > 50 * 1024 * 1024) {
        getModel().getForm().setCurrentJob(event.getBytesReceived() as Int)
      }
    }

    fun streamingStarted(event: StreamingStartEvent) {
      // show progress only when the file is bigger than 50MB
      if (event.getContentLength() > 50 * 1024 * 1024) {
        getModel().getForm().setProgressDialog("", event.getContentLength() as Int)
      }
    }

    fun streamingFinished(event: StreamingEndEvent) {
      try {
        setObject(output.toByteArray())
      } finally {
        if (event.getContentLength() > 50 * 1024 * 1024) {
          getModel().getForm().unsetProgressDialog()
        }
      }
    }

    fun streamingFailed(event: StreamingErrorEvent) {
      event.getException().printStackTrace(System.err)
      Thread {
        getModel().getForm().error(event.getException().getMessage())
        BackgroundThreadHandler.updateUI()
      }.start()
    }

    // never interrupt
    val isInterrupted: Boolean
      get() = false // never interrupt

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    private val output: ByteArrayOutputStream

    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    init {
      output = ByteArrayOutputStream()
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var image: ByteArray?
  private val field: ImageField
  private val wrapper: DragAndDropWrapper
  // --------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------
  /**
   * Creates a new `DImageField` instance.
   * @param model The field model.
   * @param label The field label.
   * @param align The field alignment.
   * @param options The field options
   * @param width The field width.
   * @param height The field height.
   * @param detail Does the field belongs to detail view ?
   */
  init {
    field = ImageField()
    field.setImmediate(true)
    field.setImageWidth(width)
    field.setImageHeight(height)
    field.addObjectFieldListener(this)
    field.addImageFieldListener(this)
    field.setWidth(width, Unit.PIXELS)
    field.setHeight(height, Unit.PIXELS)
    wrapper = DragAndDropWrapper(field)
    wrapper.setImmediate(true)
    wrapper.setDropHandler(this)
    wrapper.setDragStartMode(DragStartMode.HTML5)
    setContent(wrapper)
  }
}
