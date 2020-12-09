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

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.UnsupportedEncodingException

import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.list.VListColumn
import org.kopi.galite.list.VTextColumn
import org.kopi.galite.visual.ApplicationConfiguration
import org.kopi.galite.db.Query

/**
 * This class implements multi-line text fields.
 */
class VTextField(width: Int,
                 height: Int,
                 visibleHeight: Int,
                 convert: Int,
                 styled: Boolean)
  : VStringField(width,
                 height,
                 visibleHeight,
                 convert,
                 styled) {

  override fun getType(): Int = MDL_FLD_EDITOR

  // ----------------------------------------------------------------------
  // INTERFACE DISPLAY
  // ----------------------------------------------------------------------
  /**
   * @return a list column for list
   */
  override fun getListColumn(): VListColumn {
    return VTextColumn(getHeader(), null, align, width, getPriority() >= 0)
  }

  /**
   * verify that value is valid (on exit)
   * @exception        org.kopi.galite.visual.VException        an exception may be raised if text is bad
   */
  override fun checkType(o: Any?) {
    setString(block!!.activeRecord, o as? String)
  }

  /**
   * @return the type of search condition for this field.
   *
   * @see VConstants
   */
  override fun getSearchType(): Int = VConstants.STY_NO_COND

  /**
   * Sets the field value of given record.
   */
  override fun setObject(r: Int, v: Any?) {
    if (v is ByteArray?) {
      try {
        val charsetName = if (ApplicationConfiguration.getConfiguration()!!.isUnicodeDatabase()) {
          "UTF-8"
        } else {
          "ISO-8859-1"
        }
        setString(r, java.lang.String(v, charsetName).toString())
      } catch (e: UnsupportedEncodingException) {
        throw InconsistencyException(e)
      }
    } else {
      if (v != null) {
        setString(r, v.toString())
      } else {
        setString(r, null)
      }
    }
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param        query                the query holding the tuple
   * @param        column                the index of the column in the tuple
   */
  override fun retrieveQuery(query: Query, column: Int): Any {
    TODO()
  }

  /**
   * Returns the field value of given record as a string value.
   */
  override fun getString(r: Int): String {
    // lackner 2005.04027
    // !!! this does not work for alias fields
    return super.getObjectImpl(r) as String
  }

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? {
    TODO()
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): String {
    TODO()
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun hasLargeObject(r: Int): Boolean {
    TODO()
  }

  /**
   * Warning:	This method will become inaccessible to kopi users in next release
   */
  override fun hasBinaryLargeObject(r: Int): Boolean {
    TODO()
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getLargeObject(r: Int): InputStream? {
    return if (value[r] == null) {
      null
    } else {
      ByteArrayInputStream(getObjectImpl(r) as ByteArray?)
    }
  }
}
