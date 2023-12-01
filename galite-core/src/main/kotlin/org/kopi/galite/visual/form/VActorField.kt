/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.form

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.kopi.galite.visual.VlibProperties
import org.kopi.galite.visual.list.VListColumn
import kotlin.reflect.KClass

/**
 * An actor field is a special field that does not handle
 * any value. It consists in a simple action that have
 * a label and an optional help. If an ACTION trigger is defined, it can
 * be fired by a click an the field UI representation.
 *
 * This class creates a new actor field instance.
 */
class VActorField() : VField(1, 1) {

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  override fun checkText(s: String): Boolean = true

  override fun checkType(rec: Int, s: Any?) {}

  override fun getDataType(): KClass<*> = Unit::class

  override fun getListColumn(): VListColumn? = null

  override fun setNull(r: Int) {}

  override fun setObject(r: Int, v: Any?) {}

  override fun retrieveQuery(result: ResultRow, column: Column<*>): Any? = null

  override fun isNullImpl(r: Int): Boolean = false

  override fun getObjectImpl(r: Int): Any? = null

  override fun toText(o: Any?): String? = null

  override fun toObject(s: String): Any? = null

  override fun getTextImpl(r: Int): String? = null

  override fun getSqlImpl(r: Int): String? = null

  override fun copyRecord(f: Int, t: Int) {}

  override fun getTypeInformation(): String = VlibProperties.getString("actor-type-field")

  override fun getTypeName(): String = VlibProperties.getString("Actor")

  override fun getType(): Int = MDL_FLD_ACTOR
}
