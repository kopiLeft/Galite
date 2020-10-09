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
  fun noChart(): Boolean {
    TODO()
  }

  fun showHideFilter() {
    TODO()
  }

  fun isMulti(): Boolean {
    TODO()
  }

  fun getDisplay(): Any {
    TODO()
  }

  fun isChanged(): Boolean {
    TODO()
  }

  fun getForm(): VForm {
    TODO()
  }

  fun clear() {
    TODO()
  }

  fun setMode(modQuery: Int) {
    TODO()
  }

  fun getActiveField(): VField? {
    TODO()
  }

  fun gotoFirstField() {
    TODO()
  }

  fun validate() {
    TODO()
  }

  fun singleMenuQuery(b: Boolean): Any {
    TODO()
  }

  fun fetchRecord(id: Unit) {
    TODO()
  }

  fun fetchRecord(id: Int) {
    TODO()
  }

  fun load() {
    TODO()
  }

  fun getMode(): Int {
    TODO()
  }

  fun getName(): Any {
    TODO()
  }

  fun isRecordChanged(i: Int): Boolean {
    TODO()
  }

  fun setDefault() {
    TODO()
  }

  fun setRecordFetched(i: Int, b: Boolean) {
    TODO()
  }

  fun setRecordChanged(i: Int, changed: Boolean) {
    TODO()
  }

  fun gotoFirstUnfilledField() {
    TODO()
  }

  fun save() {
    TODO()
  }

  fun fetchNextRecord(i: Int) {
    TODO()
  }

  fun delete() {
    TODO()
  }

  fun leaveRecord(b: Boolean) {
    TODO()
  }

  fun insertEmptyRecord(recno: Int) {
    TODO()
  }

  fun gotoRecord(recno: Int) {
    TODO()
  }

  fun getTitle(): String? {
    TODO()
  }

  fun isAccessible(): Boolean {
    TODO()
  }


  var bufferSize = 0 // max number of buffered records

  // dynamic data
  var activeRecord = 0 // current record
}
