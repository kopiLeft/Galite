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

import java.io.Serializable

/**
 * The object field listener that informs registered objects
 * about actions performed on an object field.
 */
interface ObjectFieldListener : Serializable {

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
}
