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

import com.vaadin.flow.component.HasElement
import com.vaadin.flow.component.HasSize

interface UTextField: HasElement, HasSize {
  fun hasAutoComplete(): Boolean

  var size: Int
    get() = element.getProperty("size").toInt()
    set(value) { element.setProperty("size", value.toString()) }

  /**
   * Returns the field max length.
   * @return The field max length.
   */
  fun getMaxLength(): Int

  /**
   * Returns the field min length.
   * @return The field min length.
   */
  fun getMinLength(): Int

  /**
   * Sets the field max length.
   * @param maxLength The field max length.
   */
  fun setMaxLength(maxLength: Int)

  /**
   * Sets the field min length.
   * @param minLength The field min length.
   */
  fun setMinLength(minLength: Int)
}
