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

class VBlock {

  fun getBufferSize(): Int {
    TODO()
  }

  fun noDetail(): Boolean {
    TODO()
  }

  fun noChart(): Boolean {
    TODO()
  }

  fun getActiveRecord(): Int {
    TODO()
  }

  fun getActiveField(): VField {
    TODO()
  }

  fun getForm(): VForm {
    TODO()
  }

  fun gotoNextField() {
    TODO()
  }

  fun getName(): String {
    TODO()
  }

  fun setActiveField(field: VField?) {
    TODO()
  }

  fun getMode(): Int {
    TODO()
  }

  internal fun getCurrentRecord(): Int {
    TODO()
  }

  fun isRecordInsertAllowed(rec: Int): Boolean {
    TODO()
  }

  fun setActiveRecord(rec: Int) {
    TODO()
  }

  internal fun trailRecord(rec: Int) {
    TODO()
  }

  fun setRecordChanged(rec: Int, `val`: Boolean) {
    TODO()
  }

  fun updateAccess(record: Int) {
    TODO()
  }

  fun executeObjectTrigger(VKT_Type: Int): Any? {
    TODO()
  }

  internal fun callTrigger(event: Int, index: Int): Any {
    TODO()
  }

  internal fun callProtectedTrigger(event: Int, index: Int): Any {
    TODO()
  }

  internal fun hasTrigger(event: Int, index: Int): Boolean {
    TODO()
  }

  fun getTitle(): String {
    TODO()
  }

  fun getFieldPos(field: VField): Int {
    TODO()
  }
}
