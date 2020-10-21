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

import kotlin.reflect.KClass

import org.kopi.galite.db.Query
import org.kopi.galite.list.VListColumn
import org.kopi.galite.type.Fixed

class VFixnumField(width: Int, height: Int) : VField(width, height) {

  companion object {
    /**
     * Computes the the width of a fixnum field : FIXNUM(digits, scale)
     *
     * @param     digits          the number of total digits.
     * @param     scale           the number of digits representing the fractional part.
     * @param     minVal          the minimal value the fixnum field can get.
     * @param     maxVal          the maximal value the fixnum field can get.
     */
    fun computeWidth(digits: Int, scale: Int, minVal: Fixed?, maxVal: Fixed?): Int {
      TODO()
    }
  }

  override fun checkText(s: String): Boolean {
    TODO("Not yet implemented")
  }

  override fun checkType(rec: Int, s: Any) {
    TODO("Not yet implemented")
  }

  override fun getDataType(): KClass<*> {
    TODO("Not yet implemented")
  }

  override fun getListColumn(): VListColumn? {
    TODO("Not yet implemented")
  }

  override fun setNull(r: Int) {
    TODO("Not yet implemented")
  }

  override fun setObject(r: Int, v: Any?) {
    TODO("Not yet implemented")
  }

  override fun retrieveQuery(query: Query, column: Int): Any? {
    TODO("Not yet implemented")
  }

  override fun isNullImpl(r: Int): Boolean {
    TODO("Not yet implemented")
  }

  override fun getObjectImpl(r: Int): Any? {
    TODO("Not yet implemented")
  }

  override fun toText(o: Any): String? {
    TODO("Not yet implemented")
  }

  override fun toObject(s: String): Any? {
    TODO("Not yet implemented")
  }

  override fun getTextImpl(r: Int): String? {
    TODO("Not yet implemented")
  }

  override fun getSqlImpl(r: Int): String? {
    TODO("Not yet implemented")
  }

  override fun copyRecord(f: Int, t: Int) {
    TODO("Not yet implemented")
  }

  override fun getTypeInformation(): String {
    TODO("Not yet implemented")
  }

  override fun getTypeName(): String {
    TODO("Not yet implemented")
  }


}
