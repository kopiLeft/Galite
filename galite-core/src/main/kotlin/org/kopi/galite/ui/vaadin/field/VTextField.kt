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

import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant

/**
 * A text field widget that can support many validation
 * strategies to restrict field input.
 */
open class VTextField(val col: Int) : TextField(), UTextField {

  init {
    style()
  }

  /**
   * TODO: Temporary styling but it Should be enhanced.
   */
  fun style() {
    setWidthFull()
    addThemeVariants(TextFieldVariant.LUMO_SMALL, TextFieldVariant.MATERIAL_ALWAYS_FLOAT_LABEL)
  }

  override fun hasAutoComplete(): Boolean {
    TODO("Not yet implemented")
  }

  /**
   * Sets the input field type attribute to [type]
   */
  fun setInputType(type: String) {
    element.node.runWhenAttached { ui ->
      ui.page.executeJs("$0.focusElement.type=$1", this, type)
    }
  }
}
