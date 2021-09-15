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

package org.kopi.galite.form

import org.kopi.galite.base.UComponent
import org.kopi.galite.visual.UWindow

/**
 * `UListDialog` is the top level interface for all list dialogs ui components.
 */
interface UListDialog : UComponent {

  /**
   * Shows The `UListDialog` with a window and a field reference.
   * @param window The caller [UWindow]
   * @param field The [UField] reference.
   * @param showSingleEntry Show the `UListDialog` when it contains only a single entry ?
   * @return The selected element id.
   */
  fun selectFromDialog(window: UWindow?, field: UField?, showSingleEntry: Boolean): Int

  /**
   * Shows The `UListDialog` with a window reference.
   * @param window The reference [UWindow].
   * @param showSingleEntry Show the `UListDialog` when it contains only a single entry ?
   * @return The selected element id.
   */
  fun selectFromDialog(window: UWindow?, showSingleEntry: Boolean): Int
}
