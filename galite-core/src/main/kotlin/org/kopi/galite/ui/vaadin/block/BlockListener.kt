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
package org.kopi.galite.ui.vaadin.block

import java.io.Serializable

/**
 * Registered objects are notified with block performed actions.
 */
interface BlockListener : Serializable {

  /**
   * Fired when the scroll position is changed.
   * @param value The new scroll position.
   */
  fun onScroll(value: Int)

  /**
   * Fired when the active record is changed from the client side.
   * @param record The new active record.
   * @param sortedTopRec The sorted top record.
   */
  fun onActiveRecordChange(record: Int, sortedTopRec: Int)
}
