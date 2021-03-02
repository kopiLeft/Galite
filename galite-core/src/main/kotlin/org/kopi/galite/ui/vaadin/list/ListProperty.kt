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
package org.kopi.galite.ui.vaadin.list

import kotlin.reflect.KClass

import org.kopi.galite.form.VListDialog

/**
 * A list dialog property model based on object container property
 */
open class ListProperty(val model: VListDialog, val row: Int, val col: Int) {
  // : PropertyData<Any>() TODO

  fun getDefaultValue(): Any? = model.getValueAt(model.translatedIdents[row], col)

  fun isReadOnly(): Boolean = true

  val type: KClass<*>
    get() = model.columns[col]!!.getDataType()

  override fun toString(): String {
    return formatObject(model.getValueAt(model.translatedIdents[row], col))
  }

  /**
   * Formats an object according to the property nature.
   * @param o The object to be formatted.
   * @return The formatted property object.
   */
  open fun formatObject(o: Any?): String {
    return model.columns[col]!!.formatObject(o).toString()
  }
}

