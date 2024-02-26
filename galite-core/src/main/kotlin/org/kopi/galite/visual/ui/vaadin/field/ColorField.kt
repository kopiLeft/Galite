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
package org.kopi.galite.visual.ui.vaadin.field

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.notification.Notification

import org.kopi.galite.visual.ui.vaadin.common.ColorPicker

class ColorField : ObjectField<Any?>() {

  /**
   *  Creates a new `ColorPicker` instance.
   */
  private val colorPicker = ColorPicker()

  init {
    colorPicker.addValueChangeListener { e: ComponentValueChangeEvent<ColorPicker?, String?>? ->
      Notification.show("Color picked " + colorPicker.value,
                        2000,
                        Notification.Position.MIDDLE)
    }
    add(colorPicker)
  }

    override val isNull: Boolean
    get() = colorPicker.isEmpty

  override fun setColor(foreground: String?, background: String?) {
    // No foreground color, neither background color for color field
  }

  override fun checkValue(rec: Int) {
    // nothing to perform
  }

  override fun setParentVisibility(visible: Boolean) {
    // not implemented yet
  }

  override fun addFocusListener(function: () -> Unit) {
    // Focusable not implemented
  }

  override fun getContent(): Component {
    return colorPicker
  }

  override fun getValue(): Any? {
    return colorPicker.value
  }

  override fun setPresentationValue(newPresentationValue: Any?) {
    TODO("Not yet implemented")
  }

  override fun setValue(value: Any?) {
    colorPicker.value = value as String
  }

  fun setData(color: String?) {
    colorPicker.value = color?.let { "#$it" } ?: "#000000"
  }
}
