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

import kotlinx.coroutines.Runnable

abstract class KopiAction() : Runnable {
  lateinit var name: String

  constructor(name: String) : this() {
    this.name = name
  }

  @Throws(VException::class)
  abstract fun execute()

  override fun run() {
    try {
      execute()
    } catch (e: VException) {
      throw e.message?.let { VRuntimeException(it, e) }!!
    }
  }

  /**
   * Returns `true` if this action can be cancelled in an action queue context.
   * This means that the action can be removed from an action queue when performing
   * a clear operation. By default, all actions can be cancelled.
   * @return `true` if this action can be cancelled in an action queue context.
   */
  open fun isCancellable(): Boolean {
    return true
  }

  override fun toString(): String {
    return super.toString() + " " + name
  }
}
