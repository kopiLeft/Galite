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

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

import org.kopi.galite.form.VFieldUI
import org.kopi.galite.form.VImageField
import org.kopi.galite.type.Date
import org.kopi.galite.ui.vaadin.field.ImageField
import org.kopi.galite.ui.vaadin.field.ImageFieldListener

import com.vaadin.flow.component.Unit
import com.vaadin.flow.server.StreamResource
import com.vaadin.flow.server.StreamVariable

/**
 * The image field implementation based on the customized VAADIN
 * addons.
 *
 * @param model The field model.
 * @param label The field label.
 * @param align The field alignment.
 * @param options The field options
 * @param width The field width.
 * @param height The field height.
 * @param detail Does the field belongs to detail view ?
 */
class DImageField(
        model: VFieldUI,
        label: DLabel?,
        align: Int,
        options: Int,
        width: Int,
        height: Int,
        detail: Boolean
) : DObjectField(model, label, align, options, detail),
        ImageFieldListener
/*DropHandler TODO */ {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var image: ByteArray? = null
  private val field = ImageField()
  //private val wrapper: DragAndDropWrapper TODO

  init {
    field.imageWidth = width
    field.imageHeight = height
    //field.addObjectFieldListener(this) TODO
    //field.addImageFieldListener(this) TODO
    field.setWidth(width.toFloat(), Unit.PIXELS)
    field.setHeight(height.toFloat(), Unit.PIXELS)
    //wrapper = DragAndDropWrapper(field) TODO
    //wrapper.setImmediate(true)  TODO
    //wrapper.setDropHandler(this) TODO
    //wrapper.setDragStartMode(DragStartMode.HTML5) TODO
    //setContent(wrapper) TODO
  }

  // --------------------------------------------------
  // IMPLEMENTATION OF ABSTRACTS METHODS
  // --------------------------------------------------
  override fun getObject(): Any? = image

  override fun setBlink(b: Boolean) {
    // TODO
  }

  // ----------------------------------------------------------------------
  // UI MANAGEMENT
  // ----------------------------------------------------------------------
  override fun updateAccess() {
    label!!.update(model, position)
  }

  override fun updateText() {
    setObject((getModel() as VImageField).getImage(model.blockView.getRecordFromDisplayLine(position)))
    super.updateText()
  }

  override fun updateFocus() {
    label!!.update(model, position)
    super.updateFocus()
  }

  override fun updateColor() {
    // color properties are not set for an image field.
  }

  override fun onRemove() {
    setObject(null)
  }

  override fun onImageClick() {
    performAutoFillAction()
  }

  /**
   * Sets the object associated to record r
   * @param r The position of the record
   * @param s The object to set in
   */
  fun setObject(s: Any?) {
    /*BackgroundThreadHandler.access(Runnable { TODO
      if (s == null) {
        field.setIcon(null)
      } else {
        field.setIcon(DynamicImageResource(ImageStreamSource(s as ByteArray?), createFileName("image")))
        setBlink(false)
        setBlink(true)
      }
    })*/
    image = s as ByteArray?
  }

  /**
   * Creates the dynamic image name.
   * @param baseName The base name.
   * @return The dynamic image name.
   */
  protected fun createFileName(baseName: String): String =
          baseName + Date.now().format("yyyyMMddHHmmssSSS").toString() + ".png"
  //---------------------------------------------------
  // STREAM RESOURCE
  //---------------------------------------------------

  /**
   * The image stream handler for reading uploaded stream from DnD
   * operations.
   */
  internal inner class ImageStreamHandler : StreamVariable {
    private val output = ByteArrayOutputStream()

    override fun getOutputStream(): OutputStream = output

    override fun listenProgress(): Boolean = true

    override fun onProgress(event: StreamVariable.StreamingProgressEvent) {
      // show progress only when the file is bigger than 50MB
      if (event.contentLength > 50 * 1024 * 1024) {
        getModel().getForm().setCurrentJob(event.bytesReceived as Int)
      }
    }

    override fun streamingStarted(event: StreamVariable.StreamingStartEvent) {
      // show progress only when the file is bigger than 50MB
      if (event.contentLength > 50 * 1024 * 1024) {
        getModel().getForm().setProgressDialog("", event.contentLength.toInt())
      }
    }

    override fun streamingFinished(event: StreamVariable.StreamingEndEvent) {
      try {
        setObject(output.toByteArray())
      } finally {
        if (event.contentLength > 50 * 1024 * 1024) {
          getModel().getForm().unsetProgressDialog()
        }
      }
    }

    override fun streamingFailed(event: StreamVariable.StreamingErrorEvent) {
      event.exception.printStackTrace(System.err)
      Thread {
        getModel().getForm().error(event.exception.message)
        //BackgroundThreadHandler.updateUI() TODO
      }.start()
    }

    override fun isInterrupted(): Boolean = false // never interrupt
  }
}
