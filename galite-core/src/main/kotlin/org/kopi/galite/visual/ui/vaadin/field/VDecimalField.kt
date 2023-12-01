/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.textfield.TextField
import org.kopi.galite.visual.ui.vaadin.base.DecimalFormatSymbols
import org.kopi.galite.visual.ui.vaadin.main.MainWindow

/**
 * A decimal field.
 */
@JsModule("./src/decimal-field.js")
class VDecimalField()
  : InputTextField<TextField>(TextField()) {
  init {
    internalField.allowedCharPattern = "[0-9-.,]*"
    val dfs = DecimalFormatSymbols.get(MainWindow.locale)

    if (dfs!!.decimalSeparator != '.') {
      internalField.element.executeJs(
        "addCheckDecimalListeners($0, $1);",
        internalField.element,
        dfs.decimalSeparator.toString()
      )
    }
  }
}
