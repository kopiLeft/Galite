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
package org.kopi.galite.visual.ui.vaadin.block

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

  /**
   * Updates the scroll position.
   * @param value The scroll position.
   */
  fun updateScrollPos(value: Int)

  /**
   * Updates the value of the active record in server side.
   * @param record The client active record.
   * @param sortedTopRec the top sorted record.
   */
  fun updateActiveRecord(record: Int, sortedTopRec: Int)

  /**
   * Clears the cached values. This is called by the client side when the cached values are
   * already registered in the client data model.
   */
  fun clearCachedValues(cachedValues: List<Block.CachedValue>)

  /**
   * Clears the cached colors. This is called by the client side when the cached colors are
   * already registered in the client data model.
   */
  fun clearCachedColors(cachedColors: List<Block.CachedColor>)
}
