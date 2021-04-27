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
package org.kopi.galite.ui.vaadin.grid

import org.kopi.galite.ui.vaadin.common.VImage

/**
 * The server side implementation of an image grid editor.
 */
class GridEditorImageField : GridEditorField<Any?>() {

  private val image: VImage = VImage()

  init {
    className = "editor-imagefield"
    image.setBorder(0)
    add(image)
  }

  override fun setPresentationValue(newPresentationValue: Any?) {
    image.src = newPresentationValue.toString()
  }

  override fun generateModelValue(): Any? = image.src

  override fun focus() {
    image.focus()
  }

  override fun addFocusListener(focusFunction: () -> Unit) {
    image.addFocusListener {
      focusFunction()
    }
  }

  override fun setBlink(blink: Boolean) {}

  fun setImageWidth(width: Int) {
    image.width = "${width}px"
  }

  fun setImageHeight(height: Int) {
    image.height = "${height}px"
  }
}
