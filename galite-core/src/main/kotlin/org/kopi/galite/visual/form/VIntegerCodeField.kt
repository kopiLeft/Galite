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

import kotlin.reflect.KClass

import org.kopi.galite.visual.list.VIntegerCodeColumn
import org.kopi.galite.visual.list.VListColumn
import org.kopi.galite.util.base.InconsistencyException

class VIntegerCodeField : VCodeField {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  // dynamic data
  private val codes: Array<Int?>    // code array

  /**
   * @param     ident           the identifier of the type in the source file
   * @param     source          the qualified name of the source file defining the list
   * @param     names           the names of the fields
   * @param     codes           the codes of the fields
   */
  constructor(bufferSize: Int,
              ident: String,
              source: String,
              names: Array<String>,
              codes: Array<Int?>) : super(bufferSize, ident, source, names) {
    this.codes = codes
  }

  /**
   * @param     ident           the identifier of the type in the source file
   * @param     source          the qualified name of the source file defining the list
   * @param     names           the names of the fields
   * @param     codes           the codes of the fields
   */
  constructor(bufferSize: Int,
              ident: String,
              source: String,
              names: Array<String>,
              codes: IntArray) : super(bufferSize, ident, source, names) {
    this.codes = arrayOfNulls(codes.size)
    codes.forEachIndexed { element, i ->
      this.codes[i] = element
    }
  }
  // ----------------------------------------------------------------------
  // INTERFACE DISPLAY
  // ----------------------------------------------------------------------

  /**
   * Return a list column for list
   */
  override fun getListColumn(): VListColumn = VIntegerCodeColumn(getHeader(), null, null, labels, codes, getPriority() >= 0)

  /**
   * Returns the array of codes.
   */
  override fun getCodes(): Array<Any?> = codes as Array<Any?>

  // ----------------------------------------------------------------------
  // INTERFACE BD/TRIGGERS
  // ----------------------------------------------------------------------

  /**
   * Sets the field value of given record to a int value.
   */
  override fun setInt(r: Int, v: Int?) {
    if (v == null) {
      setCode(r, -1)
    } else {
      var code = -1 // cannot be null
      var i = 0

      while (code == -1 && i < codes.size) {
        if (v == codes[i]) {
          code = i
        }
        i++
      }
      if (code == -1) {
        throw InconsistencyException("bad code value $v field $name")
      }
      setCode(r, code)
    }
  }

  /**
   * Sets the field value of given record.
   * Warning:	This method will become inaccessible to users in next release
   */
  override fun setObject(r: Int, v: Any?) {
    setInt(r, v as? Int)
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
  override fun getSqlImpl(r: Int): Int? = if (value[r] == -1) null else codes[value[r]]

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
      if (value == codes[i]) {
        code = i
      }
      i++
    }
    if (code == -1) {
      throw InconsistencyException("bad code value $value field $name")
    }
    return formatCode(code)
  }
}
