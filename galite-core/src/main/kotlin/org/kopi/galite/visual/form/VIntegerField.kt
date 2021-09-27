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

import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

import org.kopi.galite.visual.db.Query
import org.kopi.galite.visual.list.VIntegerColumn
import org.kopi.galite.visual.list.VListColumn
import org.kopi.galite.visual.visual.MessageCode
import org.kopi.galite.visual.visual.VlibProperties

/**
 * @param     width
 * @param     minValue    the min permitted value
 * @param     maxValue    the max permitted value
 */
class VIntegerField(val bufferSize: Int,
                    width: Int,
                    val minValue: Int,
                    val maxValue: Int) : VField(width, 1) {

  /**
   * return the name of this field
   */
  override fun getTypeInformation(): String {
    var min = minValue
    var max = maxValue
    var nines = 1

    for (i in width downTo 1) {
      nines *= 10
    }
    max = min(max, nines - 1)
    min = max(min, -(nines / 10 - 1))
    return VlibProperties.getString("integer-type-field", arrayOf(min, max))
  }

  /**
   * return the name of this field
   */
  override fun getTypeName(): String = VlibProperties.getString("Long")

  override fun isNumeric(): Boolean = true

  // ----------------------------------------------------------------------
  // Interface Display
  // ----------------------------------------------------------------------

  /**
   * return a list column for list
   */
  override fun getListColumn(): VListColumn = VIntegerColumn(getHeader(),
                                                             null,
                                                             align,
                                                             width,
                                                             getPriority() >= 0)

  /**
   * verify that text is valid (during typing)
   */
  override fun checkText(s: String): Boolean {
    if (s.length > width) {
      return false
    }

    s.forEach {
      val c = it
      if (!(Character.isDigit(c) || c == '.' || c == '-')) {
        return false
      }
    }
    return true
  }

  /**
   * verify that value is valid (on exit)
   * @exception    org.kopi.galite.visual.VException    an exception may be raised if text is bad
   */
  override fun checkType(rec: Int, s: Any?) {
    val s = s as? String

    if (s == "") {
      setNull(rec)
    } else {
      val v = try {
        s!!.toInt()
      } catch (e: NumberFormatException) {
        throw VFieldException(this, MessageCode.getMessage("VIS-00006"))
      }
      if (v < minValue) {
        throw VFieldException(this, MessageCode.getMessage("VIS-00012", arrayOf<Any>(minValue) as? Array<Any>))
      }
      if (v > maxValue) {
        throw VFieldException(this, MessageCode.getMessage("VIS-00009", arrayOf<Any>(maxValue) as? Array<Any>))
      }
      setInt(rec, v)
    }
  }

  // ----------------------------------------------------------------------
  // Interface bd/Triggers
  // ----------------------------------------------------------------------

  /**
   * Sets the field value of given record to a null value.
   */
  override fun setNull(r: Int) {
    setInt(r, null)
  }

  /**
   * Sets the field value of given record to a int value.
   */
  override fun setInt(r: Int, v: Int?) {
    var v = v
    if (isChangedUI
            || value[r] == null && v != null
            || value[r] != null && value[r] != v) {
      // trails (backup) the record if necessary
      trail(r)
      if (v == null) {
        value[r] = null
      } else {
        if (v < minValue) {
          v = minValue
        } else if (v > maxValue) {
          v = maxValue
        }
        value[r] = v
      }
      // inform that value has changed
      setChanged(r)
    }
    checkCriticalValue()
  }

  /**
   * Sets the field value of given record.
   * Warning:	This method will become inaccessible to users in next release
   */
  override fun setObject(r: Int, v: Any?) {
    setInt(r, v as? Int)
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
   * Is the field value of given record null ?
   */
  override fun isNullImpl(r: Int): Boolean = value[r] == null

  /**
   * Returns the field value of given record as a int value.
   */
  override fun getInt(r: Int): Int? = getObject(r) as Int?

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? = value[r]

  override fun toText(o: Any?): String = o?.toString() ?: ""

  override fun toObject(s: String): Any? {
    return if (s == "") {
      null
    } else {
      val v = try {
        s.toInt()
      } catch (e: NumberFormatException) {
        throw VFieldException(this, MessageCode.getMessage("VIS-00006"))
      }

      if (v < minValue) {
        throw VFieldException(this, MessageCode.getMessage("VIS-00012", arrayOf<Any>(minValue) as? Array<Any>))
      }

      if (v > maxValue) {
        throw VFieldException(this, MessageCode.getMessage("VIS-00009", arrayOf<Any>(maxValue) as? Array<Any>))
      }

      v
    }
  }

  /**
   * Returns the display representation of field value of given record.
   */
  override fun getTextImpl(r: Int): String = if (value[r] == null) "" else value[r].toString()

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): Int? = value[r]

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
   * Returns the data type handled by this field.
   */
  override fun getDataType(): KClass<*> = Int::class

  // ----------------------------------------------------------------------
  // FIELD VALUE ACCESS
  // ----------------------------------------------------------------------

  /**
   * Returns the sum of the field values of all records.
   *
   * @param     exclude         exclude the current record
   * @return    the sum of the field values, null if none is filled.
   */
  fun computeSum(exclude: Boolean): Int? {
    var sum: Int? = null

    for (i in 0 until block!!.bufferSize) {
      if (!isNullImpl(i)
              && block!!.isRecordFilled(i)
              && (!exclude || i != block!!.activeRecord)) {
        if (sum == null) {
          sum = 0
        }
        sum += getInt(i)!!
      }
    }
    return sum
  }

  /**
   * Returns the sum of the field values of all records.
   *
   * @param     exclude         exclude the current record
   * @param     coalesceValue   the value to take if all fields are empty
   * @return    the sum of the field values or coalesceValue if none is filled.
   */
  fun computeSum(exclude: Boolean, coalesceValue: Int): Int {
    val sum = computeSum(exclude)

    return sum ?: coalesceValue
  }

  /**
   * Returns the sum of the field values of all records.
   *
   * @return    the sum of the field values, null if none is filled.
   */
  fun computeSum(): Int? = computeSum(false)

  /**
   * Returns the sum of every filled records in block
   */
  fun getCoalesceSum(coalesceValue: Int): Int {
    val sum = computeSum()

    return sum ?: coalesceValue
  }

  /**
   * Returns the sum of every filled records in block
   *
   * @param     coalesceValue   the value to take if all fields are empty
   * @return    the sum of the field values or coalesceValue if none is filled.
   */
  fun computeSum(coalesceValue: Int): Int = computeSum(false, coalesceValue)

  /**
   * Returns the sum of every filled records in block
   * @deprecated        use int getCoalesceSum(int) instead
   * */
  fun getSum(): Int {
    val sum = computeSum()

    return sum ?: 0
  }

  //----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  //----------------------------------------------------------------------

  /**
   * Returns a string representation of a int value wrt the field type.
   */
  protected fun formatInt(value: Int): String = value.toString()

  fun setCriticalMinValue(criticalMinValue: Int) {
    this.criticalMinValue = criticalMinValue
  }

  fun setCriticalMaxValue(criticalMaxValue: Int) {
    this.criticalMaxValue = criticalMaxValue
  }

  fun checkCriticalValue() {
    if (value[0] != null && criticalMinValue != null) {
      if (value[0]!! < criticalMinValue!!) {
        setHasCriticalValue(true)
        return
      }
    }
    if (value[0] != null && criticalMaxValue != null) {
      if (value[0]!! > criticalMaxValue!!) {
        setHasCriticalValue(true)
        return
      }
    }
    setHasCriticalValue(false)
  }

  private fun setHasCriticalValue(critical: Boolean) {
    if (getDisplay() != null) {
      (getDisplay() as UTextField).setHasCriticalValue(critical)
    }
  }

  // dynamic data
  // value
  private var value: Array<Int?> = arrayOfNulls(2 * bufferSize)
  private var criticalMinValue: Int? = minValue
  private var criticalMaxValue: Int? = maxValue
}
