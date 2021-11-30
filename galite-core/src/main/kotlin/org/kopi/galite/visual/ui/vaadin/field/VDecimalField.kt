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
package org.kopi.galite.visual.ui.vaadin.field

import org.kopi.galite.visual.ui.vaadin.main.MainWindow

import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.dependency.JsModule

/**
 * A decimal field.
 */
@JsModule("./src/decimal-field.js")
class VDecimalField(col: Int,
                    maxScale: Int,
                    minval: Double?,
                    maxval: Double?,
                    fraction: Boolean)
  : InputTextField<TextField>(TextField()) {
  init {
    val dfs = org.kopi.galite.visual.ui.vaadin.base.DecimalFormatSymbols.get(MainWindow.locale)

    internalField.element.executeJs(
      "addCheckDecimalListeners($0, $1);",
      internalField.element,
      dfs!!.decimalSeparator.toString()
    )
  }
}
