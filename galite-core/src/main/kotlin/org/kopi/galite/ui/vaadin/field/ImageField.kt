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
package org.kopi.galite.ui.vaadin.field

import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.ui.vaadin.common.VImage

import com.vaadin.flow.component.Unit
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.server.AbstractStreamResource

/**
 * The component of the image field.
 */
class ImageField(width: Float, height: Float, buffer: MemoryBuffer) : ObjectField<Any?>() {

  /**
   * The image width.
   */
  var imageWidth = 0

  /**
   * The image height.
   */
  var imageHeight = 0

  private val image: VImage = VImage()

  lateinit var upload: Upload

  init {
    access {
      upload = Upload(buffer)
    }
    className = "k-imagefield"
    image.element.style["outline"] = "1px solid lightgreen"
    image.setWidth(width, Unit.PIXELS)
    image.setHeight(height, Unit.PIXELS)
    image.setBorder(0)
    image.element.setProperty("borderStyle", "none")
    upload.uploadButton = Button(Icon(VaadinIcon.UPLOAD))
    upload.dropLabelIcon = Div()
    upload.setAcceptedFileTypes("image/*")
    add(upload)
    add(image)
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  override val isNull: Boolean
    get() = image.isEmpty

  override fun setValue(o: Any?) {
    image.src = o as String?
  }


  override fun setPresentationValue(newPresentationValue: Any?) {
    value = newPresentationValue
  }

  override fun setColor(foreground: String?, background: String?) {
    // no color for image field
  }

  override fun getValue(): Any? = image.src

  fun setData(stream: AbstractStreamResource?) {
    if(stream == null) {
      image.element.removeAttribute("src")
    } else {
      image.element.setAttribute("src", stream)
    }
  }

  override fun generateModelValue(): Any? = value

  override fun checkValue(rec: Int) {
    // nothing to perform
  }

  override fun setParentVisibility(visible: Boolean) {}

  override fun addFocusListener(function: () -> kotlin.Unit) {
    image.addFocusListener {
      function()
    }
  }
}
