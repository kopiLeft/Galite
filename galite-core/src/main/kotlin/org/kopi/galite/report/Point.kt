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

package org.kopi.galite.report

import java.io.Serializable

/**
 * A point representing a location in `(x,y)` coordinate space,
 * specified in integer precision.
 */
data class Point(val x: Int = 0, val y: Int = 0) : Serializable {

  /**
   * Returns a string representation of this point and its location
   * in the `(x,y)` coordinate space. This method is
   * intended to be used only for debugging purposes, and the content
   * and format of the returned string may vary between implementations.
   * The returned string may be empty but may not be `null`.
   *
   * @return  a string representation of this point
   */
  override fun toString() = javaClass.name + "[x=" + x + ",y=" + y + "]"
}

