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

package org.kopi.galite.visual.util

import java.util.Vector

import org.kopi.galite.visual.util.base.InconsistencyException

/**
 * Local printer
 */
abstract class GroupHandler {
  private var key: Any? = null
  private val elements = Vector<Any>()

  fun add(key: Any?, value: Any) {
    if (key == null) {
      throw InconsistencyException("KEY IS NULL")
    }
    if (key != this.key) {
      if (this.key != null) {
        foot(this.key, elements)
      }
      this.key = key
      head(key, value)
      elements.setSize(0)
    }
    body(key, value)
    elements.addElement(value)
  }

  fun close() {
    if (elements.size > 0) {
      foot(key, elements)
    }
    key = null
    elements.setSize(0)
  }

  /**
   * This method is called at the beginning of a new group, with the new key
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
