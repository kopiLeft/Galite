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

import org.kopi.galite.db.Query
import org.kopi.galite.list.VListColumn

import kotlin.reflect.KClass

class VImageField : VField(1,1) {
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

  override fun getSqlImpl(r: Int): String {
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

  fun getIconWidth(): Int {
    TODO()
  }

  fun getIconHeight(): Int {
    TODO()
  }

  override fun getImage(r: Int): ByteArray {
    TODO()
  }

  override fun setImage(r: Int, v: ByteArray) {
    TODO()
  }
}
