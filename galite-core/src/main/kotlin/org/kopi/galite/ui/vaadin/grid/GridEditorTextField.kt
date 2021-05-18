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

import com.vaadin.flow.component.textfield.TextField

/**
 * A text field used as editor
 */
open class GridEditorTextField(width: Int) : GridEditorField<String>() {
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  val wrappedField = TextField()

  init {
    className = "editor-field"
    add(wrappedField)
    wrappedField.setWidthFull()
    wrappedField.maxLength = width
    addValueChangeListener {
      if(!check(it.value)) {
        value = it.oldValue
      }
    }
  }

  override fun setPresentationValue(newPresentationValue: String?) {
    wrappedField.value = newPresentationValue.toString()
  }

  override fun generateModelValue(): String? = wrappedField.value

  override fun doFocus() {
    wrappedField.focus()
  }

  override fun addFocusListener(focusFunction: () -> Unit) {
    wrappedField.addFocusListener {
      focusFunction()
    }
  }

  /**
   * Validates the given text according to the field type.
   * @param text The text to be validated
   * @return true if the text is valid.
   */
  protected open fun check(text: String): Boolean {
    return true
  }

  open fun validate() {
    // to be overridden when needed
  }

  /**
   * Sets the blink state of the boolean field.
   * @param blink The blink state.
   */
  override fun setBlink(blink: Boolean) {
    if(className != null) {
      if (blink) {
        element.classList.add("$className-blink")
      } else {
        element.classList.remove("$className-blink")
      }
    }
  }
}
