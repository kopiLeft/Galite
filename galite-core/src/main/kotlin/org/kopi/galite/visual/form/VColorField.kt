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

import java.awt.Color
import java.io.ByteArrayInputStream
import java.io.InputStream

import kotlin.reflect.KClass

import org.kopi.galite.visual.db.Query
import org.kopi.galite.visual.list.VColorColumn
import org.kopi.galite.visual.list.VListColumn
import org.kopi.galite.visual.util.base.InconsistencyException
import org.kopi.galite.visual.visual.VlibProperties

class VColorField(val bufferSize: Int, width: Int, height: Int) : VField(1, 1) {

  override fun hasAutofill(): Boolean = true

  /**
   * return the name of this field
   */
  override fun getTypeInformation(): String = VlibProperties.getString("color-type-field")

  /**
   * return the name of this field
   */
  override fun getTypeName(): String = VlibProperties.getString("Color")

  /*
   * ----------------------------------------------------------------------
   * Interface Display
   * ----------------------------------------------------------------------
   */

  /**
   * return a list column for list
   */
  override fun getListColumn(): VListColumn = VColorColumn(getHeader(), null, getPriority() >= 0)

  /**
   * verify that text is valid (during typing)
   */
  override fun checkText(s: String): Boolean = true

  /**
   * verify that value is valid (on exit)
   * @exception    VException    an exception is raised if text is bad
   */
  override fun checkType(rec: Int, s: Any?) {}

  override fun getType(): Int = MDL_FLD_COLOR

  // ---------------------------------------------------------------------
  // INTERFACE BD/TRIGGERS
  // ---------------------------------------------------------------------

  /**
   * @return the type of search condition for this field.
   *
   * @see VConstants
   */
  override fun getSearchType(): Int = VConstants.STY_NO_COND

  /**
   * Sets the field value of given record to a null value.
   */
  override fun setNull(r: Int) {
    setColor(r, null)
  }

  /**
   * Sets the field value of given record to a date value.
   */
  override fun setColor(r: Int, v: Color?) {
    if (isChangedUI || value[r] !== v) {
      // trails (backup) the record if necessary
      trail(r)
      // set value in the defined row
      value[r] = v
      // inform that value has changed
      setChanged(r)
    }
  }

  /**
   * Sets the field value of given record.
   * Warning:	This method will become inaccessible to users in next release
   */
  override fun setObject(r: Int, v: Any?) {
    if (v is ByteArray) {
      setColor(r, Color(reformat(v[0]), reformat(v[1]), reformat(v[2])))
    } else {
      setColor(r, v as Color?)
    }
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
      val b = query.getObject(column) as ByteArray
      Color(reformat(b[0]), reformat(b[1]), reformat(b[2]))
    }
  }

  /**
   * Is the field value of given record null ?
   */
  override fun isNullImpl(r: Int): Boolean = value[r] == null

  /**
   * Returns the field value of given record as a date value.
   */
  override fun getColor(r: Int): Color = getObject(r) as Color

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? = value[r]

  override fun toText(o: Any?): String {
    throw InconsistencyException("UNEXPECTED GET TEXT")
  }

  override fun toObject(s: String): Any {
    throw InconsistencyException("UNEXPECTED GET TEXT")
  }

  /**
   * Returns the display representation of field value of given record.
   */
  override fun getTextImpl(r: Int): String {
    throw InconsistencyException("UNEXPECTED GET TEXT")
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): String? = if (value[r] == null) null else "?"

  /**
   * Copies the value of a record to another
   */
  override fun copyRecord(f: Int, t: Int) {
    val oldValue = value[t]
    value[t] = value[f]
    // inform that value has changed for non backup records
    // only when the value has really changed.
    if (t < block!!.bufferSize
            && (oldValue != null && value[t] == null
                    || oldValue == null && value[t] != null
                    || oldValue != null && oldValue != value[t])) {
      fireValueChanged(t)
    }
  }

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to users in next release
   */
  override fun hasLargeObject(r: Int): Boolean = value[r] != null

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to users in next release
   */
  override fun getLargeObject(r: Int): InputStream? {
    return value[r]?.let {
      ByteArrayInputStream(getObjectImpl(r) as ByteArray?)
    }
  }

  /**
   * Returns the data type handled by this field.
   */
  override fun getDataType(): KClass<*> = Color::class

  /*
   * ----------------------------------------------------------------------
   * FORMATTING VALUES WRT FIELD TYPE
   * ----------------------------------------------------------------------
   */

  /**
   * Returns a string representation of a date value wrt the field type.
   */
  fun formatImage(value: Any): String = "image"

  /**
   * autofill
   * @exception   org.kopi.galite.visual.VException    an exception may occur in gotoNextField
   */
  override fun fillField(handler: PredefinedValueHandler?): Boolean {
    return handler?.let {
      setColor(block!!.activeRecord,
               handler.selectColor(getColor(block!!.activeRecord)))
      true
    } ?: false
  }

  /**
   * Reformat a unsigned int from a byte
   */
  private fun reformat(b: Byte): Int = if (b < 0) b + 255 else b.toInt()

  /*
   * ----------------------------------------------------------------------
   * DATA MEMBERS
   * ----------------------------------------------------------------------
   */
  private var value: Array<Color?> = arrayOfNulls(2 * bufferSize)
}
