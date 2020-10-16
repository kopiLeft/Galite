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

import org.kopi.galite.visual.VCommand

abstract class VBlock {

  fun getForm(): VForm = TODO()

  fun fetchLookup(fld: VField) {
    TODO()
  }

  var bufferSize = 0 // max number of buffered records

  // dynamic data
  // current record
  var activeRecord = 0

  var activeField: VField? = null

  var detailMode = false
  internal lateinit var fields // fields
          : Array<VField>
  internal lateinit var commands // commands
          : Array<VCommand>

  fun getMode(): Int = TODO()

  interface OrderListener

  class OrderModel

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------

  fun gotoNextField() {
    TODO()
  }

  fun executeObjectTrigger(VKT_Type: Int?): Any = TODO()

  fun isChart(): Boolean = TODO()

  fun isMulti(): Boolean = bufferSize > 1

  open fun executeVoidTrigger(VKT_Type: Int) {
    // default: does nothing
  }
  fun isAccessible(): Boolean {
    TODO()
  }

  open fun isRecordAccessible(rec: Int): Boolean {
    TODO()
  }

  open fun gotoRecord(recno: Int) {
    TODO()
  }
  open fun gotoField(fld: VField) {
    TODO()
  }
  open fun hasTrigger(event: Int, index: Int): Boolean {
    TODO()
  }
  protected open fun callTrigger(event: Int): Any? {
    TODO()
  }
  internal fun callTrigger(event: Int, index: Int): Any {
    TODO()
  }
  open fun noDetail(): Boolean {
    TODO()
  }
  internal fun getDisplay(): UBlock? {
    TODO()
  }
  open fun getFieldIndex(fld: VField): Int {
    TODO()
  }
  internal var align: BlockAlignment? = null

  internal var orderModel: OrderModel = OrderModel()
  open fun noChart(): Boolean {
    TODO()
  }

  internal var displaySize // max number of displayed records
          = 0

  open fun helpOnBlock(help: VHelpGenerator) {
    TODO()
  }
}
