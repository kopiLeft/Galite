/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2024 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.grid

import org.kopi.galite.visual.VColor
import org.kopi.galite.visual.ui.vaadin.common.ColorPicker

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.Component

/**
 * The server side implementation of a Color grid editor.
 */
class GridEditorColorField : GridEditorField<Any?>() {

  private val colorPicker = ColorPicker()

  init {
    colorPicker.addValueChangeListener { _: AbstractField.ComponentValueChangeEvent<ColorPicker?, String?>? -> }
  }

  override fun setPresentationValue(newPresentationValue: Any?) {
    colorPicker.value = newPresentationValue.toString()
  }

  override fun getValue(): Any? = colorPicker.value

  override fun initContent(): Component {
    colorPicker.addValueChangeListener { _: AbstractField.ComponentValueChangeEvent<ColorPicker?, String?>? -> }
    return colorPicker
  }

  override fun doFocus() {
    // not implemeted
  }

  override fun addFocusListener(focusFunction: () -> Unit) {
    // not implemented yet
  }

  override fun setBlink(blink: Boolean) {}

  override fun setColor(align: Int, foreground: VColor?, background: VColor?) {
    // can not set Color of a color field.
  }

  fun setData(color: String?) {
    colorPicker.value = color?.let { "#$it" } ?: "#000000"
  }
}
