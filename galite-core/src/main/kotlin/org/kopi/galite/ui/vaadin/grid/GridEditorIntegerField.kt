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

import com.vaadin.flow.component.textfield.NumberField

/**
 * An integer editor field.
 * @param minValue The minimum value to be accepted by the field.
 * @param minValue The maximum value to be accepted by the field.
 */
class GridEditorIntegerField(width: Int, val minValue: Int, val maxValue: Int) : GridEditorTextField(width) {
   var numberField = NumberField()
  init {
    super.remove(field)
    numberField.width = width.toString()
    numberField.min = minValue.toDouble()
    numberField.max = maxValue.toDouble()
    add(numberField)
  }
}

