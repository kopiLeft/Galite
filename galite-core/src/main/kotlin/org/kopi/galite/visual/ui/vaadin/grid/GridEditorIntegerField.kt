/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

import java.lang.NumberFormatException

/**
 * An integer field for grid inline edit
 *
 * @param width the width of this integer editor field.
 * @param minValue the minimum accepted value for this integer editor field.
 * @param maxValue the maximum accepted value for this integer editor field.
 */
class GridEditorIntegerField(width: Int, val minValue: Int, val maxValue: Int) : GridEditorTextField(width) {

  init {
    wrappedField.pattern = "[0-9]*"
    wrappedField.isPreventInvalidInput = true
    wrappedField.element.setProperty("min", minValue.toDouble())
    wrappedField.element.setProperty("max", maxValue.toDouble())
  }

  override fun check(text: String): Boolean {
    for (element in text) {
      val c = element
      if (!(Character.isDigit(c) || c == '.' || c == '-')) {
        return false
      }
    }
    return true
  }

  override fun validate() {
    val value = intValue()
    if (value < minValue) {
      throw InvalidEditorFieldException(this, "00012", minValue)
    }
    if (value > maxValue) {
      throw InvalidEditorFieldException(this, "00009", maxValue)
    }
  }

  protected fun isNumeric(): Boolean {
    return true
  }

  /**
   * Returns the integer value of this editor field.
   * @return The integer value of this editor field.
   */
  protected fun intValue(): Int {
    return try {
      value!!.toInt()
    } catch (e: NumberFormatException) {
      throw InvalidEditorFieldException(this, "00006")
    }
  }
}
