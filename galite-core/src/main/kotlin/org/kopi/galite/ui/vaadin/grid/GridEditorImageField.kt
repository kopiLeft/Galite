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

import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import org.kopi.galite.ui.vaadin.field.ImageField

/**
 * The server side implementation of an image grid editor.
 */
class GridEditorImageField(width: Int, height: Int) : GridEditorField<Any?>() {

  private var buffer = MemoryBuffer()
  val wrappedField = ImageField(width.toFloat(), height.toFloat(), buffer)

  init {
    add(wrappedField)
  }

  override fun setPresentationValue(newPresentationValue: Any?) {
    wrappedField.value = newPresentationValue
  }

  override fun generateModelValue(): Any? = wrappedField.value

  override fun focus() {
    wrappedField.focus()
  }

  override fun addFocusListener(focusFunction: () -> Unit) {
    wrappedField.addFocusListener {
      focusFunction()
    }
  }
}
