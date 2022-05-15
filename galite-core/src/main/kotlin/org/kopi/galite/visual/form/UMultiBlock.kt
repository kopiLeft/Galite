/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.form

import org.kopi.galite.visual.base.UComponent

/**
 * `UMultiBlock` is the top-level interface that represents a double layout
 * block.
 * `UMultiBlock` is a both chart block and simple block with the possibility
 * to switch between the two layouts.
 */
interface UMultiBlock : UBlock {

  /**
   * Switches view between list and detail mode.
   * @param row The selected record.
   * @throws org.kopi.galite.visual.VException Switch operation may fail.
   */
  fun switchView(row: Int)

  /**
   * Adds a component to the detail view. The add to the chart block
   * is ensured by [.add].
   * @param comp The [UComponent] to be added to detail view.
   * @param constraint The [Alignment] component constraints.
   */
  fun addToDetail(comp: UComponent?, constraint: Alignment)
}
