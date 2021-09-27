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

package org.kopi.galite.visual.form

import java.io.Serializable
import java.util.EventListener

interface FieldChangeListener : EventListener, Serializable {
  /**
   * Fired when the field label is changed.
   */
  fun labelChanged()

  /**
   * Fires when the field search operator is changed.
   */
  fun searchOperatorChanged()

  /**
   * Fired when field value is changed.
   * @param r The current record.
   */
  fun valueChanged(r: Int)

  /**
   * Fired when a field access is changed.
   * @param r The current record.
   */
  fun accessChanged(r: Int)

  /**
   * Fired when the color properties of a field are changed.
   *
   *
   * Setting both the background and the foreground colors to
   * `null` will lead to reset the field color properties.
   * @param r The current record.
   */
  fun colorChanged(r: Int)
}
