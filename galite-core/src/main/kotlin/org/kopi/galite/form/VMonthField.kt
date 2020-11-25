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

import java.util.Calendar
import java.util.GregorianCalendar

import kotlin.reflect.KClass

import org.kopi.galite.db.Query
import org.kopi.galite.list.VListColumn
import org.kopi.galite.list.VMonthColumn
import org.kopi.galite.type.Month
import org.kopi.galite.type.NotNullMonth
import org.kopi.galite.db.Utils
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VlibProperties

class VMonthField(val bufferSize: Int) : VField(7, 1) {

  override fun hasAutofill(): Boolean = true

  /**
   * Returns true if this field implements "enumerateValue"
   */
  override fun hasNextPreviousEntry(): Boolean = true

  /**
   * Returns the name of this field
   */
  override fun getTypeInformation(): String = VlibProperties.getString("month-type-field")

  /**
   * Return the name of this field
   */
  override fun getTypeName(): String = VlibProperties.getString("Month")

  override fun isNumeric(): Boolean = true

  // ----------------------------------------------------------------------
  // Interface Display
  // ----------------------------------------------------------------------
  /**
   * return a list column for list
   */
  override fun getListColumn(): VListColumn = VMonthColumn(getHeader(), null, getPriority() >= 0)

  /**
   * verify that text is valid (during typing)
   */
  override fun checkText(s: String): Boolean {
    if (s.length > 7) {
      return false
    }
    for (element in s) {
      if (!isMonthChar(element)) {
        return false
      }
    }
    return true
  }

  /**
   * verify that value is valid (on exit)
   * @exception    org.kopi.galite.visual.VException    an exception is raised if text is bad
   */
  override fun checkType(rec: Int, o: Any?) {
    val s = o as String

    if (s == "") {
      setNull(rec)
    } else {
      if (s.indexOf(".") != -1 && s.indexOf(".") == s.lastIndexOf(".")) {
        // one "." and only one
        try {
          val month = s.substring(0, s.indexOf(".")).toInt()
          var year = s.substring(s.indexOf(".") + 1).toInt()

          if (year < 50) {
            year += 2000
          } else if (year < 100) {
            year += 1900
          }

          if (isMonth(month, year)) {
            setMonth(rec, NotNullMonth(year, month))
          } else {
            throw VFieldException(this, MessageCode.getMessage("VIS-00005"))
          }
        } catch (e: Exception) {
          throw VFieldException(this, MessageCode.getMessage("VIS-00005"))
        }
      } else if (s.indexOf(".") == -1) {
        // just the month, complete
        try {
          val month = s.toInt()
          val year = GregorianCalendar()[Calendar.YEAR]

          if (isMonth(month, year)) {
            setMonth(rec, NotNullMonth(year, month))
          } else {
            throw VFieldException(this, MessageCode.getMessage("VIS-00005"))
          }
        } catch (e: Exception) {
          throw VFieldException(this, MessageCode.getMessage("VIS-00005"))
        }
      } else {
        throw VFieldException(this, MessageCode.getMessage("VIS-00005"))
      }
    }
  }

  // ----------------------------------------------------------------------
  // Interface bd/Triggers
  // ----------------------------------------------------------------------
  /**
   * Sets the field value of given record to a null value.
   */
  override fun setNull(r: Int) {
    setMonth(r, null)
  }

  /**
   * Sets the field value of given record to a month value.
   */
  override fun setMonth(r: Int, v: Month?) {
    if (changedUI
        || value[r] == null && v != null
        || value[r] != null && value[r]!! != v) {
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
    setMonth(r, v as? Month)
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
      query.getMonth(column)
    }
  }

  /**
   * Is the field value of given record null ?
   */
  override fun isNullImpl(r: Int): Boolean = value[r] == null

  /**
   * Returns the field value of given record as a date value.
   */
  override fun getMonth(r: Int): Month = getObject(r) as Month

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? = value[r]

  override fun toText(o: Any?): String = if (o == null) "" else (o as? Month).toString()

  override fun toObject(s: String): Any? {
    return if (s == "") {
      null
    } else {
      if (s.indexOf(".") != -1 && s.indexOf(".") == s.lastIndexOf(".")) {
        // one "." and only one
        try {
          val month = s.substring(0, s.indexOf(".")).toInt()
          var year = s.substring(s.indexOf(".") + 1).toInt()

          if (year < 50) {
            year += 2000
          } else if (year < 100) {
            year += 1900
          }

          if (isMonth(month, year)) {
            NotNullMonth(year, month)
          } else {
            throw VFieldException(this, MessageCode.getMessage("VIS-00005"))
          }
        } catch (e: Exception) {
          throw VFieldException(this, MessageCode.getMessage("VIS-00005"))
        }
      } else if (s.indexOf(".") == -1) {
        // just the month, complete
        try {
          val month = s.toInt()
          val year = GregorianCalendar()[Calendar.YEAR]

          if (isMonth(month, year)) {
            NotNullMonth(year, month)
          } else {
            throw VFieldException(this, MessageCode.getMessage("VIS-00005"))
          }
        } catch (e: Exception) {
          throw VFieldException(this, MessageCode.getMessage("VIS-00005"))
        }
      } else {
        throw VFieldException(this, MessageCode.getMessage("VIS-00005"))
      }
    }
  }

  /**
   * Returns the display representation of field value of given record.
   */
  override fun getTextImpl(r: Int): String = if (value[r] == null) "" else value[r].toString()

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): String = Utils.toSql(value[r])

  /**
   * Copies the value of a record to another
   */
  override fun copyRecord(f: Int, t: Int) {
    val oldValue: Month? = value[t]

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
  override fun getDataType(): KClass<*> = Month::class

  // ----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  // ----------------------------------------------------------------------
  /**
   * Returns a string representation of a date value wrt the field type.
   */
  protected fun formatMonth(value: Month): String = value.toString()

  private fun isMonth(m: Int, y: Int): Boolean = !(y < 1 || m < 1 || m > 12)

  private fun isMonthChar(c: Char): Boolean = c in '0'..'9' || c == '.'

  override fun fillField(handler: PredefinedValueHandler?): Boolean {
    return if (list == null) {
      setMonth(block!!.activeRecord, Month.now())
      true
    } else {
      super.fillField(handler)
    }
  }

  /**
   * Checks that field value exists in list
   */
  override fun enumerateValue(desc: Boolean) {
    val record: Int = block!!.activeRecord

    when {
      list != null -> {
        super.enumerateValue(desc)
      }
      isNull(record) -> {
        setMonth(record, Month.now())
      }
      else -> {
        // try to read date
        try {
          checkType(getText(record))
        } catch (e: VException) {
          // not valid, get now
          setMonth(record, Month.now())
        }
        setMonth(record, getMonth(record).add(if (desc) -1 else 1))
      }
    }
  }

  private var value: Array<Month?> = arrayOfNulls(2 * bufferSize)
}
