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

import org.kopi.galite.base.UComponent

/**
 * `UBlock` is the top-level interface that must be implemented
 * by all blocks. It is the visual component of the [VBlock] model.
 */
interface UBlock : UComponent, BlockListener {

  /**
   * Returns the block model
   * @return The [VBlock] of this `UBlock`
   */
  val model: VBlock

  /**
   * Returns the form view of the block
   * @return The [UForm] of this block.
   */
  val formView: UForm

  /**
   * Returns the displayed line for a record.
   * @param recno The concerned record.
   * @return The displayed line.
   */
  fun getDisplayLine(recno: Int): Int

  /**
   * Returns the displayed line for the active record.
   * @return The displayed line for the active record.
   */
  val displayLine: Int

  /**
   * Returns the record number from display of a given line.
   * @param line The concerned line.
   * @return The record number.
   */
  fun getRecordFromDisplayLine(line: Int): Int

  /**
   * Adds a component to the block view with a corresponding constraints.
   * @param comp The [UComponent] to be added
   * @param constraints The [Alignment] constraints
   */
  fun add(comp: UComponent, constraints: Alignment)

  /**
   * Returns the position of a given column.
   * @param x The column number.
   * @return The position of a given column.
   */
  fun getColumnPos(x: Int): Int

  /**
   * Checks if the block is in detail mode.
   * @return `true` if the block is in detail mode
   */
  fun inDetailMode(): Boolean
}
