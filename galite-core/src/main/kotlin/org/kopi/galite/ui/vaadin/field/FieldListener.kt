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

import java.io.Serializable

/**
 * The field listener that notifies registered objects with
 * actions performed on a field.
 */
interface FieldListener : Serializable {

  /**
   * Fired when increment button is clicked.
   */
  fun onIncrement()

  /***
   * Fired when decrement action is fired.
   */
  fun onDecrement()

  /**
   * Fired when the field is clicked.
   */
  fun onClick()

  /**
   * Transfers the focus to this field.
   */
  fun transferFocus()

  /**
   * Navigates to the next field.
   */
  fun gotoNextField()

  /**
   * Navigates to the previous field.
   */
  fun gotoPrevField()

  /**
   * Navigates to the next empty must fill field.
   */
  fun gotoNextEmptyMustfill()

  /**
   * Navigates to the next record.
   */
  fun gotoNextRecord()

  /**
   * Navigates to the previous record.
   */
  fun gotoPrevRecord()

  /**
   * Navigates to the first record.
   */
  fun gotoFirstRecord()

  /**
   * Navigates to the last record.
   */
  fun gotoLastRecord()

  /**
   * Fires the action trigger associated with the field.
   */
  fun fireAction()
}
