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

package org.kopi.galite.report

import java.io.Serializable

/**
 * A point representing a location in `(x,y)` coordinate space,
 * specified in integer precision.
 */
class Point(var x: Int = 0, var y: Int = 0) : Serializable {

  /**
   * Determines whether or not two points are equal. Two instances of
   * `Point2D` are equal if the values of their
   * `x` and `y` member fields, representing
   * their position in the coordinate space, are the same.
   * @param obj an object to be compared with this `Point2D`
   * @return `true` if the object to be compared is
   * an instance of `Point2D` and has
   * the same values; `false` otherwise.
   */
  override fun equals(obj: Any?): Boolean {
    if (obj is Point) {
      val pt = obj
      return x == pt.x && y == pt.y
    }
    return super.equals(obj)
  }

  /**
   * Returns a string representation of this point and its location
   * in the `(x,y)` coordinate space. This method is
   * intended to be used only for debugging purposes, and the content
   * and format of the returned string may vary between implementations.
   * The returned string may be empty but may not be `null`.
   *
   * @return  a string representation of this point
   */
  override fun toString(): String {
    return javaClass.name + "[x=" + x + ",y=" + y + "]"
  }
}
