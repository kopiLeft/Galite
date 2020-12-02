/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.form.dsl

import org.kopi.galite.form.VConstants

enum class Border(val value: Int) {
  /**
   * this border is used to frame the block with lines.
   */
  BORDER_LINE(VConstants.BRD_LINE),

  /**
   * this border is used to enhance a block by setting it on the foreground.
   */
  BORDER_RAISED(VConstants.BRD_RAISED),

  /**
   * this border is used to put it at the background.
   */
  BORDER_LOWERED(VConstants.BRD_LOWERED),

  /**
   * this border is used to carve a frame in the form.
   */
  BORDER_ETCHED(VConstants.BRD_ETCHED),

  /**
   * no border
   */
  NO_BORDER(VConstants.BRD_NONE),
}
