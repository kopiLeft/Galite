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
package org.kopi.galite.ui.vaadin.field

import java.util.Arrays

import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.dependency.CssImport

/**
 * An Code field.
 */
@CssImport.Container(value = [
  CssImport(value = "./styles/galite/combobox.css", themeFor = "vaadin-text-field"),
  CssImport(value = "./styles/galite/combobox.css", themeFor = "vaadin-combo-box"),
  CssImport(value = "./styles/galite/combobox.css", themeFor = "vaadin-combo-box-item")
])
class VCodeField(enumerations : Array<String>?) : InputTextField<ComboBox<String>>(ComboBox<String>()), KeyNotifier {

  init {
    internalField.setItems(Arrays.stream(enumerations))
    element.themeList.add("galite-combobox")
  }


  override fun setMaxLength(maxLength: Int) {
    //Nothing to Implement
  }

  override fun initContent(): ComboBox<String> {
    return internalField // FIXME
  }
}
