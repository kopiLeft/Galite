/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

import org.kopi.galite.visual.db.Query
import org.kopi.galite.visual.list.VBooleanCodeColumn
import org.kopi.galite.visual.list.VListColumn
import org.kopi.galite.visual.util.base.InconsistencyException

open class VBooleanCodeField : VCodeField {

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
              codes: Array<Boolean?>,
              localizedByGalite: Boolean = false
  ) : super(bufferSize, ident, source, names, localizedByGalite) {
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
              codes: BooleanArray
  ) : super(bufferSize, ident, source, names) {
    this.codes = arrayOfNulls(codes.size)
    for (i in codes.indices) {
      this.codes[i] = codes[i]
    }
  }
  /*
   * ----------------------------------------------------------------------
   * Interface Display
   * ----------------------------------------------------------------------
   */

  /**
   * Returns a list column for list.
   */
  override fun getListColumn(): VListColumn {
    return VBooleanCodeColumn(getHeader(), null, labels, codes, getPriority() >= 0)
  }

  /*
   * ----------------------------------------------------------------------
   * Interface bd/Triggers
   * ----------------------------------------------------------------------
   */

  /**
   * Sets the field value of given record to a boolean value.
   */
  override fun setBoolean(r: Int, v: Boolean?) {
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
   * Warning:	This method will become inaccessible to galite users in next release
   */
  override fun setObject(r: Int, v: Any?) {
    setBoolean(r, v as? Boolean)
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
      query.getBoolean(column)
    }
  }

  override fun getCodes(): Array<Any?> = codes as Array<Any?>

  /**
   * Returns the field value of given record as a boolean value.
   */
  override fun getBoolean(r: Int): Boolean? = if (value[r] == -1) null else codes[value[r]]

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? = getBoolean(r)

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): Boolean? {
    return if (value[r] == -1) {
      null
    } else {
      codes[value[r]]
    }
  }

  /**
   * Returns the data type handled by this field.
   */
  override fun getDataType(): KClass<*> = Boolean::class

  /*
   * ----------------------------------------------------------------------
   * FORMATTING VALUES WRT FIELD TYPE
   * ----------------------------------------------------------------------
   */

  /**
   * Returns a string representation of a boolean value wrt the field type.
   */
  override fun formatBoolean(value: Boolean): String {
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

  private var codes: Array<Boolean?>
}
