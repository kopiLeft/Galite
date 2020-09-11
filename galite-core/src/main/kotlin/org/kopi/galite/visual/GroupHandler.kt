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

package org.kopi.galite.visual

import org.kopi.galite.util.base.InconsistencyException

import java.util.Vector

/**
 * Local printer
 */
abstract class GroupHandler {
  var key: Any? = null
  val elems = Vector<Any>()

  fun add(key: Any?, value: Any) {
    if (key == null) {
      throw InconsistencyException("KEY IS NULL")
    }
    if (key != this.key) {
      if (this.key != null) {
        foot(this.key, elems)
      }
      this.key = key
      head(key, value)
      elems.setSize(0)
    }
    body(key, value)
    elems.addElement(value)
  }

  fun close() {
    if (elems.size > 0) {
      foot(key, elems)
    }
    key = null
    elems.setSize(0)
  }

  /**
   * This method is called at the begining of a new group, with the new key
   */
  abstract fun head(key: Any?, elem: Any)

  /**
   * This method is called for each new element
   */
  abstract fun body(key: Any?, elem: Any)

  /**
   * This method is called at the end of a group, with body containing all elements
   */
  abstract fun foot(key: Any?, body: Vector<Any>)
}
