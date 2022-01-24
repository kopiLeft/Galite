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

import org.kopi.galite.visual.list.VListColumn
import org.kopi.galite.visual.list.VStringCodeColumn
import org.kopi.galite.visual.util.base.InconsistencyException

/**
 *
 * @param     ident           the identifier of the type in the source file
 * @param     source          the qualified name of the source file defining the list
 */
class VStringCodeField(bufferSize: Int,
                       ident: String,
                       source: String,
                       names: Array<String>,
                       private val codes: Array<String?>)
          : VCodeField(bufferSize,
                       ident,
                       source,
                       names) {

  /*
   * ----------------------------------------------------------------------
   * Interface Display
   * ----------------------------------------------------------------------
   */

  /**
   * return a list column for list
   */
  override fun getListColumn(): VListColumn =
          VStringCodeColumn(getHeader(), null, null, labels, codes, getPriority() >= 0)

  /**
   * Returns the array of codes.
   */
  @Suppress("UNCHECKED_CAST")
  override fun getCodes(): Array<Any?> = codes as Array<Any?>

  /*
   * ----------------------------------------------------------------------
   * Interface bd/Triggers
   * ----------------------------------------------------------------------
   */
  /**
   * Sets the field value of given record to a fixed value.
   */
  override fun setString(r: Int, v: String?) {
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
        throw InconsistencyException("bad code value " + v + "field " + name
                                             + " for " + getType()
                                             + " in " + source)
      }
      setCode(r, code)
    }
  }

  /**
   * Sets the field value of given record.
   * Warning:	This method will become inaccessible to users in next release
   */
  override fun setObject(r: Int, v: Any?) {
    setString(r, v as? String)
  }

  /**
   * Returns the field value of given record as a int value.
   */
  override fun getString(r: Int): String? = getObject(r) as? String

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? = if (value[r] == -1) null else codes[value[r]]

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): String? = if (value[r] == -1) null else codes[value[r]]

  /**
   * Returns the data type handled by this field.
   */
  override fun getDataType(): KClass<*> = String::class

  /*
   * ----------------------------------------------------------------------
   * FORMATTING VALUES WRT FIELD TYPE
   * ----------------------------------------------------------------------
   */
  /**
   * Returns a string representation of a int value wrt the field type.
   */
  protected fun formatString(value: String): String {
    var code = -1 // cannot be null
    var i = 0

    while (code == -1 && i < codes.size) {
      if (value === codes[i]) {
        code = i
      }
      i++
    }
    if (code == -1) {
      throw InconsistencyException("bad code value " + value + "field " + name)
    }
    return formatCode(code)
  }
}
