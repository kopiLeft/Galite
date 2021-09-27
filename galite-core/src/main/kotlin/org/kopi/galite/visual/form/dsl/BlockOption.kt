/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.visual.form.dsl

import org.kopi.galite.visual.form.VConstants

enum class BlockOption(val value: Int) {
  /**
   * Disables the chart(grid) rendering of a multiple bloc to make it look like a single block.
   * It used on multiple blocks displaying only one row, Besides the fields must be positioned with the AT command.
   */
  NOCHART(VConstants.BKO_NOCHART),

  /**
   * Prevent the user from removing fields content.
   */
  NODELETE(VConstants.BKO_NODELETE),

  /**
   * Disables the positioning of fields and displays the block as a chart (grid),
   * this option is used only on multiple blocks, the fields should not be positioned with the AT command.
   */
  NODETAIL(VConstants.BKO_NODETAIL),

  /**
   * Prevent the user from inserting data in fields.
   */
  NOINSERT(VConstants.BKO_NOINSERT),

  /**
   * Prevent the user from moving between records.
   */
  NOMOVE(VConstants.BKO_NOMOVE),

  /**
   * If used, saving a block would delete all its rows and reinsert them one by one, by doing so,
   * you can update the table rows even when you change the index fields without worrying about the
   * "row already exist exception".
   */
  UPDATE_INDEX(VConstants.BKO_INDEXED),

  /**
   * Makes the block accessible even if or its fields have SKIPPED access.
   */
  ACCESS_ON_SKIPPED(VConstants.BKO_ALWAYS_ACCESSIBLE),
}
