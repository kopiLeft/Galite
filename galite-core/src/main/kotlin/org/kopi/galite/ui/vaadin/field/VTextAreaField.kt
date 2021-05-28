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

import com.vaadin.flow.component.textfield.TextArea

/**
 * A text area input zone.
 * TODO : All the class must be implemented
 */
class VTextAreaField : InputTextField<TextArea>(TextArea()) {
  var cols: Int = 0

  fun setRows(rows: Int, visibleRows: Int) {
    element.setProperty("rows", visibleRows.toString())
    // TODO
  }

  /**
   * Sets the text size.
   */
  override var size: Int
    get() = element.getProperty("cols").toInt()
    set(value) { element.setProperty("cols", value.toString()) }

  fun setWordwrap(b: Boolean) {
    // TODO
  }

  fun setFixedNewLine(b: Boolean) {
    // TODO
  }

  override fun setMaxLength(maxLength: Int) {
    field.maxLength = maxLength
  }

  override fun getMaxLength(): Double = field.maxLength.toDouble()
}
