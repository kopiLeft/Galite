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

import org.kopi.galite.base.Query
import org.kopi.galite.list.VIntegerCodeColumn
import org.kopi.galite.list.VListColumn
import org.kopi.galite.type.Utils
import org.kopi.galite.util.base.InconsistencyException


/**
 *
 * @param     ident           the identifier of the type in the source file
 * @param     source          the qualified name of the source file defining the list
 * @param     names           the names of the fields
 * @param     codes           the codes of the fields
 */
class VIntegerCodeField : VCodeField {

  constructor(ident: String,
              source: String,
              names: Array<String>,
              codes: Array<Int?>) : super(ident, source, names) {
    this.codes = codes
  }

  constructor(ident: String,
              source: String,
              names: Array<String>,
              codes: IntArray) : super(ident, source, names) {
    this.codes = arrayOfNulls(codes.size)
    codes.indices.forEach() {
      this.codes[it] = codes[it]
    }
  }
  // ----------------------------------------------------------------------
  // INTERFACE DISPLAY
  // ----------------------------------------------------------------------

  /**
   * Return a list column for list
   */
  override fun getListColumn(): VListColumn = VIntegerCodeColumn(getHeader(), null, labels, codes, getPriority() >= 0)

  /**
   * Returns the array of codes.
   */
  override fun getCodes(): Array<Any> = codes as Array<Any>

  // ----------------------------------------------------------------------
  // INTERFACE BD/TRIGGERS
  // ----------------------------------------------------------------------

  /**
   * Sets the field value of given record to a int value.
   */
  fun setInt(r: Int, v: Int?) {
    if (v == null) {
      setCode(r, -1)
    } else {
      var code = -1 // cannot be null
      var i = 0

      while (code == -1 && i < codes.size) {
        if (v.toInt() == codes[i]!!.toInt()) {
          code = i
        }
        i++
      }
      if (code == -1) {
        throw InconsistencyException("bad code value " + v + "field " + name)
      }

      setCode(r, code)
    }
  }

  /**
   * Sets the field value of given record.
   * Warning:	This method will become inaccessible to kopi users in next release
   */
  override fun setObject(r: Int, v: Any) {
    setInt(r, v as Int)
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param    query        the query holding the tuple
   * @param    column        the index of the column in the tuple
   */
  override fun retrieveQuery(query: Query, column: Int): Any? {
    return if (query.isNull(column)) {
      null
    } else {
      query.getInt(column)
    }
  }

  /**
   * Returns the field value of given record as a int value.
   */
  override fun getInt(r: Int): Int? = if (value[r] == -1) null else codes[value[r]]

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? = getInt(r)

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): String = Utils.toSql(if (value[r] == -1) null else codes[value[r]])

  /**
   * Returns the data type handled by this field.
   */
  override fun getDataType(): KClass<*> = Int::class

  // ----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  // ----------------------------------------------------------------------

  /**
   * Returns a string representation of a int value wrt the field type.
   */
  override fun formatInt(value: Int): String {
    var code = -1 // cannot be null
    var i = 0

    while (code == -1 && i < codes.size) {
      if (value == codes[i]!!.toInt()) {
        code = i
      }
      i++
    }
    if (code == -1) {
      throw InconsistencyException("bad code value " + value + "field " + name)
    }

    return formatCode(code)
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  // dynamic data
  // code array
  private val codes: Array<Int?>
}
