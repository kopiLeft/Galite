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
package org.kopi.galite.ui.vaadin.event

import java.io.Serializable

/**
 * A text field listener to be registered actions to perform on a text field.
 */
interface TextFieldListener : Serializable {
  /**
   * Performs a request to go to the next field.
   */
  fun gotoNextField()

  /**
   * Performs a request to go to the previous field.
   */
  fun gotoPrevField()

  /**
   * Performs a request to go to the next block.
   */
  fun gotoNextBlock()

  /**
   * Performs a request to go to the previous record.
   */
  fun gotoPrevRecord()

  /**
   * Performs a request to go to the next record.
   */
  fun gotoNextRecord()

  /**
   * Performs a request to go to the first record.
   */
  fun gotoFirstRecord()

  /**
   * Performs a request to go to the last field.
   */
  fun gotoLastRecord()

  /**
   * Performs a request to go to the next empty must fill field field.
   */
  fun gotoNextEmptyMustfill()

  /**
   * Performs a request to go to close the window.
   */
  fun closeWindow()

  /**
   * Performs a request to go to print the form.
   */
  fun printForm()

  /**
   * Performs a request to go to the previous entry.
   */
  fun previousEntry()

  /**
   * Performs a request to go to the next entry.
   */
  fun nextEntry()

  /**
   * Fired when query is being performed.
   * @param query The query to be performed.
   */
  fun onQuery(query: String?)

  /**
   * Fired when suggestion is selected.
   * @param suggestion The selected suggestion.
   */
  //fun onSuggestion(suggestion: AutocompleteSuggestion?) TODO

  /**
   * Fires an autofill action for thie attached field.
   */
  fun autofill()
}
