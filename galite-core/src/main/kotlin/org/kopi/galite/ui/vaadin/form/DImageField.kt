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
import java.io.InputStream

import org.kopi.galite.form.VFieldUI
import org.kopi.galite.form.VImageField
import org.kopi.galite.type.Date
import org.kopi.galite.ui.vaadin.field.ImageField

import com.vaadin.flow.component.upload.FailedEvent
import com.vaadin.flow.component.upload.SucceededEvent
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.dom.DomEvent
import com.vaadin.flow.server.StreamResource

/**
 * The image field implementation.
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
) : DObjectField(model, label, align, options, detail) {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var image: ByteArray? = null
  private var buffer = MemoryBuffer()
  private val field = ImageField(width.toFloat(), height.toFloat(), buffer)
  // --------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------
  /**
   * Creates a new `DImageField` instance.
   */
  init {
    field.imageWidth = width
    field.imageHeight = height
    field.addObjectFieldListener(this)
    field.upload.addSucceededListener(::onUploadSucceeded)
    field.upload.addFailedListener(::onUploadFailed)
    field.upload.element.addEventListener("upload-abort", ::onRemove)
    setFieldContent(field)
  }

  // --------------------------------------------------
  // IMPLEMENTATION OF ABSTRACTS METHODS
  // --------------------------------------------------
  override fun getObject(): Any? = image

  /**
   * Sets the object associated to current f
   * @param s The object to set in
   */
  fun setObject(s: Any?) {
    //BackgroundThreadHandler.access(Runnable { TODO
      if (s == null) {
        field.setData(s)
      } else {
        field.setData(DynamicImageResource(createFileName("image")) {
          ByteArrayInputStream(s as ByteArray?)
        })
        setBlink(false)
        setBlink(true)
      }
    //})
    image = s as ByteArray?
  }

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

  fun onRemove(event: DomEvent) {
    setObject(null)
  }

  /*fun onImageClick() { TODO
    performAutoFillAction()
  }*/

  /**
   * Creates the dynamic image name.
   * @param baseName The base name.
   * @return The dynamic image name.
   */
  protected fun createFileName(baseName: String): String =
          baseName + Date.now().format("yyyyMMddHHmmssSSS") + ".png"
  //---------------------------------------------------
  // STREAM RESOURCE
  //---------------------------------------------------

  /**
   * A dynamic [StreamResource] for an image field.
   *
   * @param streamSource Returns an [InputStream] object.
   * @param fileName The file name.
   */
  internal class DynamicImageResource(fileName: String,
                                      streamSource: () -> InputStream)
    : StreamResource(fileName, streamSource) {

    init {
      cacheTime = 0L
    }
  }

  fun onUploadSucceeded(event: SucceededEvent) {
    try {
      setObject(buffer.inputStream.readBytes())
    } finally {
      if (event.contentLength > 50 * 1024 * 1024) {
        getModel().getForm().unsetProgressDialog()
      }
    }
  }

  fun onUploadFailed(event: FailedEvent) {
    event.reason.printStackTrace(System.err)
    Thread {
      getModel().getForm().error(event.reason.message)
      //BackgroundThreadHandler.updateUI() TODO
    }.start()
  }
}
