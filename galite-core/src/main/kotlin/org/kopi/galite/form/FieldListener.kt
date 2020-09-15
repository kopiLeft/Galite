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
package org.kopi.galite.form

import org.kopi.galite.visual.VException
import java.io.Serializable
import java.util.EventListener

interface FieldListener : EventListener, Serializable {

  /**
   * Updates the field model.
   * @throws VException Update operation may fail.
   */
  fun updateModel()

  /**
   * Returns the displayed value in the field.
   * @param trim Should be the text be trim ?
   * @return The displayed value in the field.
   * @throws VException May fail when retrieving value.
   */
  fun getDisplayedValue(trim: Boolean): Any

  /**
   * Returns the current [UField] display.
   * @return The current [UField] display.
   */
  fun getCurrentDisplay() : UField // please do not use!

  /**
   * Display a field error.
   * @param message The error to be displayed.
   */
  fun fieldError(message: String)

  /**
   * Requests the focus on this field.
   * @return True if the focus has been gained.
   * @throws VException Focus may have not been gained.
   */
  fun requestFocus(): Boolean

  /**
   * Loads an item from a given mode `mode`.
   * @param mode The load mode.
   * @return True if the item was loaded.
   * @throws VException Load operation may fail.
   */
  fun loadItem(mode: Int): Boolean

  /**
   * Predefined value fill for this field.
   * @return True if value is loaded.
   * @throws VException Fill operation may fail.
   */
  fun predefinedFill(): Boolean

  /**
   * Enters the field. This operation leads to a focus gain.
   */
  fun enter()

  /**
   * Leaves the field. This operation leads to a focus loss.
   */
  fun leave()
}
