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

import java.time.LocalDate
import java.util.StringTokenizer

import kotlin.reflect.KClass

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.kopi.galite.visual.list.VDateColumn
import org.kopi.galite.visual.list.VListColumn
import org.kopi.galite.visual.type.Date
import org.kopi.galite.visual.visual.MessageCode
import org.kopi.galite.visual.visual.VException
import org.kopi.galite.visual.visual.VlibProperties

class VDateField(val bufferSize: Int) : VField(10, 1) {

  override fun hasAutofill(): Boolean = true

  /**
   * return the name of this field
   */
  override fun getTypeInformation(): String = VlibProperties.getString("date-type-field")

  /**
   * return the name of this field
   */
  override fun getTypeName(): String = VlibProperties.getString("Date")

  override fun isNumeric(): Boolean = true

  // ----------------------------------------------------------------------
  // Interface Display
  // ----------------------------------------------------------------------

  /**
   * return a list column for list
   */
  override fun getListColumn(): VListColumn {
    return VDateColumn(getHeader(), null, null, getPriority() >= 0)
  }

  /**
   * verify that text is valid (during typing)
   */
  override fun checkText(s: String): Boolean {
    if (s.length > 10) {
      return false
    }
    for (element in s) {
      if (!isDateChar(element)) {
        return false
      }
    }
    return true
  }

  /**
   * verify that value is valid (on exit)
   * @exception    org.kopi.galite.visual.visual.VException    an exception is raised if text is bad
   */
  override fun checkType(rec: Int, s: Any?) {
    val s = s as? String

    if (s == "") {
      setNull(rec)
    } else {
      parseDate(rec, s)
    }
  }

  private fun parseDate(rec: Int, s: String?) {
    var month = 0
    var year = -2
    val tokens = StringTokenizer(s, "/.#")

    if (!tokens.hasMoreTokens()) {
      throw VFieldException(this, MessageCode.getMessage("VIS-00003"))
    }
    val day = stringToInt(tokens.nextToken())

    if (tokens.hasMoreTokens()) {
      month = stringToInt(tokens.nextToken())
    }
    if (tokens.hasMoreTokens()) {
      year = stringToInt(tokens.nextToken())
    }
    if (tokens.hasMoreTokens() || day == -1 || month == -1 || year == -1) {
      throw VFieldException(this, MessageCode.getMessage("VIS-00003"))
    }
    when {
      month == 0 -> {
        val now: Date = Date.now()
        month = now.month
        year = now.year
      }
      year == -2 -> {
        val now: Date = Date.now()
        year = now.year
      }
      year < 50 -> {
        year += 2000
      }
      year < 100 -> {
        year += 1900
      }
      year < 1000 -> {
        // less than 4 digits cause an error in database while paring the
        // sql statement
        throw VFieldException(this, MessageCode.getMessage("VIS-00003"))
      }
      !isDate(day, month, year) -> {
        throw VFieldException(this, MessageCode.getMessage("VIS-00003"))
      }
    }
    setDate(rec, Date(year, month, day))
  }

  // ----------------------------------------------------------------------
  // Interface bd/Triggers
  // ----------------------------------------------------------------------

  /**
   * Sets the field value of given record to a null value.
   */
  override fun setNull(r: Int) {
    setDate(r, null)
  }

  /**
   * Sets the field value of given record to a date value.
   */
  override fun setDate(r: Int, v: Date?) {
    if (isChangedUI
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
   * Warning:	This method will become inaccessible to kopi users in next release
   */
  override fun setObject(r: Int, v: Any?) {
    setDate(r, v as? Date)
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param    result       the result row
   * @param    column       the column in the tuple
   */
  override fun retrieveQuery(result: ResultRow, column: Column<*>): Any? {
    return when (val date = result[column]) {
      is LocalDate -> Date(date)
      is java.sql.Date -> Date(date)
      else -> null
    }
  }

  /**
   * Is the field value of given record null ?
   */
  override fun isNullImpl(r: Int): Boolean {
    return value[r] == null
  }

  /**
   * Returns the field value of given record as a date value.
   */
  override fun getDate(r: Int): Date {
    return getObject(r) as Date
  }

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? {
    return value[r]
  }

  override fun toText(o: Any?): String = if (o == null) "" else Companion.toText(o as Date)

  override fun toObject(s: String): Any? {
    if (s == "") {
      return null
    }
    var month = 0
    var year = -2
    val tokens = StringTokenizer(s, "/.#")

    if (!tokens.hasMoreTokens()) {
      throw VFieldException(this, MessageCode.getMessage("VIS-00003"))
    }
    val day = stringToInt(tokens.nextToken())
    if (tokens.hasMoreTokens()) {
      month = stringToInt(tokens.nextToken())
    }
    if (tokens.hasMoreTokens()) {
      year = stringToInt(tokens.nextToken())
    }
    if (tokens.hasMoreTokens() || day == -1 || month == -1 || year == -1) {
      throw VFieldException(this, MessageCode.getMessage("VIS-00003"))
    }
    when {
      month == 0 -> {
        val now: Date = Date.now()
        month = now.month
        year = now.year
      }
      year == -2 -> {
        val now: Date = Date.now()
        year = now.year
      }
      year < 50 -> {
        year += 2000
      }
      year < 100 -> {
        year += 1900
      }
      year < 1000 -> {
        // less than 4 digits cause an error in database while paring the
        // sql statement
        throw VFieldException(this, MessageCode.getMessage("VIS-00003"))
      }
      !isDate(day, month, year) -> {
        throw VFieldException(this, MessageCode.getMessage("VIS-00003"))
      }
    }
    return Date(year, month, day)
  }

  /**
   * Returns the display representation of field value of given record.
   */
  override fun getTextImpl(r: Int): String {
    return if (value[r] == null) {
      ""
    } else {
      Companion.toText(value[r]!!)
    }
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): java.sql.Date? = if (value[r] == null) null else value[r]!!.toSql()

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
  override fun getDataType(): KClass<*> {
    return Date::class
  }

  // ----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  // ----------------------------------------------------------------------

  /**
   * Returns a string representation of a date value wrt the field type.
   */
  fun formatDate(value: Date): String = Companion.toText(value)

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  private fun isDateChar(c: Char): Boolean = c in '0'..'9' || c == '.' || c == '/'

  /**
   * autofill
   * @exception    org.kopi.galite.visual.visual.VException    an exception may occur in gotoNextField
   */
  override fun fillField(handler: PredefinedValueHandler?): Boolean {
    val record = block!!.activeRecord

    return if (list != null) {
      super.fillField(handler)
    } else {
      val force = try {
        val oldText = getDisplayedValue(true) as? String
        checkType(oldText)
        val newText = getText(block!!.activeRecord)
        oldText == null || newText == null || newText == "" || oldText != newText
      } catch (e: Exception) {
        true
      }
      if (handler == null || force) {
        setDate(record, Date.now())
      } else {
        setDate(record, handler.selectDate(getDate(record)))
      }
      true
    }
  }

  /**
   * return true if this field implements "enumerateValue"
   */
  override fun hasNextPreviousEntry(): Boolean = true

  /**
   * Checks that field value exists in list
   */
  override fun enumerateValue(desc: Boolean) {
    val record = block!!.activeRecord

    when {
      list != null -> {
        super.enumerateValue(desc)
      }
      isNull(record) -> {
        autofill()
      }
      else -> {
        // try to read date
        try {
          checkType(getText(record))
        } catch (e: VException) {
          // not valid, get now
          setDate(record, Date.now())
        }
        setDate(record, getDate(record).add(if (desc) -1 else 1))
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private var value: Array<Date?> = arrayOfNulls(2 * bufferSize)

  companion object {
    internal fun stringToInt(input: String): Int {
      return try {
        Integer.valueOf(input).toInt()
      } catch (e: Exception) {
        -1
      }
    }

    internal fun isDate(d: Int, m: Int, y: Int): Boolean {
      return if (y < 1 || m < 1 || m > 12 || d < 1) {
        false
      } else {
        when (m) {
          2 -> d <= (if (isLeapYear(y)) 29 else 28)
          4, 6, 9, 11 -> d <= 30
          else -> d <= 31
        }
      }
    }

    private fun isLeapYear(year: Int): Boolean = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

    /**
     *
     */
    fun toText(value: Date): String {
      return value.toString()
    }
  }
}
