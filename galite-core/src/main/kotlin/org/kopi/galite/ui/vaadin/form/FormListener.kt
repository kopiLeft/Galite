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
package org.kopi.galite.ui.vaadin.form

import java.io.Serializable

/**
 * Registered objects are notified with actions performed
 * on a form connector.
 */
interface FormListener : Serializable {
  /**
   * Fired when a page is selected inside a form.
   * @param page The page index.
   */
  fun onPageSelection(page: Int)

  /**
   * Requests to go to the next position.
   */
  fun gotoNextPosition()

  /**
   * Requests to go to the previous position.
   */
  fun gotoPrevPosition()

  /**
   * Requests to go to the last position.
   */
  fun gotoLastPosition()

  /**
   * Requests to go to the last position.
   */
  fun gotoFirstPosition()

  /**
   * Requests to go to the specified position.
   * @param posno The position number.
   */
  fun gotoPosition(posno: Int)
}
