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

import org.kopi.galite.base.Query
import org.kopi.galite.list.VListColumn
import org.kopi.galite.visual.VlibProperties
import kotlin.reflect.KClass

/**
 * An actor field is a special field that does not handle
 * any value. It consists in a simple action that have
 * a label and an optional help. If an ACTION trigger is defined, it can
 * be fired by a click an the field UI representation.
 *
 * This class creates a new actor field instance.
 */
class VActorField(width: Int, height: Int) : VField(1, 1) {

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  /**
   * @Override
   */
  override fun checkText(s: String): Boolean = true

  /**
   * @Override
   */
  override fun checkType(rec: Int, s: Any) {
  }

  /**
   * @Override
   */
  override fun getDataType(): KClass<*> = Unit::class

  /**
   * @Override
   */
  override fun getListColumn(): VListColumn? = null

  /**
   * @Override
   */
  override fun setNull(r: Int) {}

  /**
   * @Override
   */
  override fun setObject(r: Int, v: Any) {
    TODO()
  }

  /**
   * @Override
   */
  override fun retrieveQuery(query: Query, column: Int): Any? = null

  /**
   * @Override
   */
  override fun isNullImpl(r: Int): Boolean = false

  /**
   * @Override
   */
  override fun getObjectImpl(r: Int): Any? = null

  /**
   * @Override
   */
  override fun toText(o: Any): String? = null

  /**
   * @Override
   */
  override fun toObject(s: String): Any? = null

  /**
   * @Override
   */
  override fun getTextImpl(r: Int): String? = null

  /**
   * @Override
   */
  override fun getSqlImpl(r: Int): String? = null

  /**
   * @Override
   */
  override fun copyRecord(f: Int, t: Int) {}

  /**
   * @Override
   */
  override fun getTypeInformation(): String = VlibProperties.getString("actor-type-field")

  /**
   * @Override
   */
  override fun getTypeName(): String = VlibProperties.getString("Actor")

  /**
   * @Override
   */
  override fun getType(): Int = MDL_FLD_ACTOR
}
