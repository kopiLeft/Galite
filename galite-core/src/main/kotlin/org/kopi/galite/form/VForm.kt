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
package org.kopi.galite.form

import java.sql.SQLException

import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VWindow

open class VForm : VWindow() {
  fun isChanged(): Boolean = TODO()

  fun ask(s: String): Boolean = TODO()

  override fun reset(): Unit = TODO()

  override fun close(code: Int): Unit = TODO()

  open fun startProtected(message: String) {
    TODO()
  }

  open fun commitProtected() {
    TODO()
  }

  open fun abortProtected(interrupt: Boolean) {
    TODO()
  }

  open fun abortProtected(reason: SQLException) {
    TODO()
  }

  open fun abortProtected(reason: Error) {
    TODO()
  }

  open fun abortProtected(reason: RuntimeException) {
    TODO()
  }

  open fun abortProtected(reason: VException) {
    TODO()
  }

  fun getBlockCount(): Int {
    TODO()
  }

  fun getBlock(i: Int) : VBlock {
    TODO()
  }

  fun gotoBlock(block: VBlock?) {
    TODO()
  }

  fun getActiveBlock(): VBlock? {
    TODO()
  }

  fun setFieldSearchOperator(searchOperator: Any) {
    TODO()
  }
}
