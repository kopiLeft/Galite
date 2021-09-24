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

package org.kopi.galite.form

import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.Op
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Arrays

import kotlin.reflect.KClass

import org.kopi.galite.db.Query
import org.kopi.galite.list.VImageColumn
import org.kopi.galite.list.VListColumn
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.VlibProperties

class VImageField(val bufferSize: Int, val iconWidth: Int, val iconHeight: Int) : VField(1, 1) {

  override fun hasAutofill(): Boolean = true

  /**
   * return the name of this field
   */
  override fun getTypeInformation(): String = VlibProperties.getString("image-type-field")

  /**
   * return the name of this field
   */
  override fun getTypeName(): String = VlibProperties.getString("Image")

  /*
   * ----------------------------------------------------------------------
   * Interface Display
   * ----------------------------------------------------------------------
   */

  /**
   * return a list column for list
   */
  override fun getListColumn(): VListColumn = VImageColumn(getHeader(), null, getPriority() >= 0)

  /**
   * verify that text is valid (during typing)
   */
  override fun checkText(s: String): Boolean = true

  /**
   * verify that value is valid (on exit)
   * @exception    org.kopi.galite.visual.VException    an exception is raised if text is bad
   */
  override fun checkType(rec: Int, s: Any?) {}

  override fun getType(): Int = MDL_FLD_IMAGE

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
   * Returns the search conditions for this field.
   */
  override fun <T> getSearchCondition(column: ExpressionWithColumnType<T>): Op<Boolean>? = null

  /**
   * Sets the field value of given record to a null value.
   */
  override fun setNull(r: Int) {
    setImage(r, null)
  }

  /**
   * Sets the field value of given record to a date value.
   */
  override fun setImage(r: Int, v: ByteArray?) {
    if (isChangedUI || !value[r].contentEquals(v)) {
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
    setImage(r, v as? ByteArray)
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param    query        the query holding the tuple
   * @param    column        the index of the column in the tuple
   */
  override fun retrieveQuery(query: Query, column: Int): Any? {
    TODO()
  }

  /**
   * Is the field value of given record null ?
   */
  override fun isNullImpl(r: Int): Boolean = value[r] == null

  /**
   * Returns the field value of given record as a date value.
   */
  override fun getImage(r: Int): ByteArray? = getObject(r) as? ByteArray

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? = value[r]

  override fun toText(o: Any?): String? = throw InconsistencyException("UNEXPECTED GET TEXT")

  override fun toObject(s: String): String = throw InconsistencyException("UNEXPECTED GET TEXT")

  /**
   * Returns the display representation of field value of given record.
   */
  override fun getTextImpl(r: Int): String = throw InconsistencyException("UNEXPECTED GET TEXT")

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
                    || oldValue != null && !Arrays.equals(oldValue, value[t]))) {
      fireValueChanged(t)
    }
  }

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to users in next release
   */
  override fun hasLargeObject(r: Int): Boolean = value[r] != null

  /**
   * Warning:	This method will become inaccessible to users in next release
   */
  override fun hasBinaryLargeObject(r: Int): Boolean = true

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:	This method will become inaccessible to users in next release
   */
  override fun getLargeObject(r: Int): InputStream? {
    return if (value[r] == null) {
      null
    } else {
      ByteArrayInputStream(value[r])
    }
  }

  /**
   * Returns the data type handled by this field.
   */
  override fun getDataType(): KClass<*> = ByteArray::class

  /*
   * ----------------------------------------------------------------------
   * FORMATTING VALUES WRT FIELD TYPE
   * ----------------------------------------------------------------------
   */

  /**
   * Returns a string representation of a date value wrt the field type.
   */
  protected fun formatImage(value: Any): String = "image"

  /**
   * autofill
   * @exception    org.kopi.galite.visual.VException    an exception may occur in gotoNextField
   */
  override fun fillField(handler: PredefinedValueHandler?): Boolean {
    if (handler != null) {
      val b = handler.selectImage()
      if (b != null) {
        setImage(b)
        return true
      }
    }
    return false
  }

  private var value: Array<ByteArray?> = arrayOfNulls(2 * bufferSize)
}
