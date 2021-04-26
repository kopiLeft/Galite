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

import org.kopi.galite.ui.vaadin.field.BooleanField

/**
 * An editor for boolean field.
 *
 * The component is an association of two check buttons
 * working exclusively to handle the three possible values handled
 * by a boolean field.
 *
 * yes is checked & no is not checked --> true
 * no is checked & yes is not checked --> false
 * yes and no are both unchecked --> null
 *
 * yes and no cannot be checked at the same time
 */
class GridEditorBooleanField(trueRepresentation: String?, falseRepresentation: String?) : GridEditorField<Boolean?>() {

  val wrappedField = BooleanField(trueRepresentation, falseRepresentation)

  init {
    add(wrappedField)
  }

  override fun setPresentationValue(newPresentationValue: Boolean?) {
    wrappedField.value = newPresentationValue
  }

  override fun generateModelValue(): Boolean? = wrappedField.value

  override fun focus() {
    wrappedField.focus()
  }

  override fun addFocusListener(focusFunction: () -> Unit) {
    wrappedField.addFocusListener {
      focusFunction()
    }
  }
}
