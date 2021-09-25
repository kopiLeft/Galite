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

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.kopi.galite.visual.db.Query
import org.kopi.galite.visual.list.VListColumn
import org.kopi.galite.visual.list.VWeekColumn
import org.kopi.galite.visual.type.Week
import org.kopi.galite.visual.visual.MessageCode
import org.kopi.galite.visual.visual.VException
import org.kopi.galite.visual.visual.VlibProperties


class VWeekField(val bufferSize: Int) : VField(7, 1) {

  override fun hasAutofill(): Boolean = true

  override fun isNumeric(): Boolean = true

  /**
   * return the name of this field
   */
  override fun getTypeInformation(): String = VlibProperties.getString("week-type-field")

  /**
   * return the name of this field
   */
  override fun getTypeName(): String = VlibProperties.getString("Week")

  // ----------------------------------------------------------------------
  // Interface Display
  // ----------------------------------------------------------------------

  /**
   * return a list column for list
   */
  override fun getListColumn(): VListColumn = VWeekColumn(getHeader(), null, getPriority() >= 0)

  /**
   * verify that text is valid (during typing)
   */
  override fun checkText(s: String): Boolean {
    if (s.length > 10) {
      return false
    }
    for (element in s) {
      if (!isWeekChar(element)) {
        return false
      }
    }
    return true
  }

  /**
   * verify that value is valid (on exit)
   * @exception    org.kopi.galite.visual.VException    an exception is raised if text is bad
   */
  override fun checkType(rec: Int, s: Any?) {
    val s = s as? String

    if (s == "") {
      setNull(rec)
    } else {
      parseWeek(rec, s)
    }
  }

  private fun parseWeek(rec: Int, s: String?) {
    var week = 0
    var year = -1
    var bp = 0
    var state = 1
    val buffer = s + '\u0000'

    while (state > 0) {
      when (state) {
        1 -> if (buffer[bp] in '0'..'9') { /* The first week's digit */
          week = buffer[bp] - '0'
          state = 2
        } else {
          state = -1
        }
        2 -> if (buffer[bp] in '0'..'9') { /* The second week's digit */
          week = 10 * week + (buffer[bp] - '0')
          state = 3
        } else if (buffer[bp] == '.' || buffer[bp] == '/') {
          state = 4
        } else if (buffer[bp] == '\u0000') {
          state = 0
        } else {
          state = -1
        }
        3 -> state = if (buffer[bp] == '.' || buffer[bp] == '/') { /* The first point : between week and year */
          4
        } else if (buffer[bp] == '\u0000') {
          0
        } else {
          -1
        }
        4 -> when { /* The first year's digit */
          buffer[bp] in '0'..'9' -> {
            year = buffer[bp] - '0'
            state = 5
          }
          buffer[bp] == '\u0000' -> {
            state = 0
          }
          else -> {
            state = -1
          }
        }
        5 -> if (buffer[bp] in '0'..'9') { /* The second year's digit */
          year = 10 * year + (buffer[bp] - '0')
          state = 6
        } else {
          state = -1
        }
        6 -> when { /* The third year's digit */
          buffer[bp] in '0'..'9' -> {
            year = 10 * year + (buffer[bp] - '0')
            state = 7
          }
          buffer[bp] == '\u0000' -> {
            state = 0
          }
          else -> {
            state = -1
          }
        }
        7 -> if (buffer[bp] in '0'..'9') { /* The fourth year's digit */
          year = 10 * year + (buffer[bp] - '0')
          state = 8
        } else {
          state = -1
        }
        8 -> state = if (buffer[bp] == '\u0000') { /* The end */
          0
        } else {
          -1
        }
        else -> throw VFieldException(this, MessageCode.getMessage("VIS-00008"))
      }
      bp += 1
    }
    if (state == -1) {
      throw VFieldException(this, MessageCode.getMessage("VIS-00008"))
    }
    when {
      year == -1 -> {
        val now: Week = Week.now()

        year = now.year
      }
      year < 50 -> {
        year += 2000
      }
      year < 100 -> {
        year += 1900
      }
    }
    setWeek(rec, Week(year, week))
  }

  // ----------------------------------------------------------------------
  // Interface bd/Triggers
  // ----------------------------------------------------------------------

  /**
   * Sets the field value of given record to a null value.
   */
  override fun setNull(r: Int) {
    setWeek(r, null)
  }

  /**
   * Sets the field value of given record to a week value.
   */
  override fun setWeek(r: Int, v: Week?) {
    if (isChangedUI
            || value[r] == null && v != null
            || value[r] != null && value[r] != v) {
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
    setWeek(r, v as? Week)
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
      query.getWeek(column)
    }
  }

  /**
   * TODO document!
   */
  override fun retrieveQuery_(result: ResultRow, column: Column<*>): Any? {
    val tmp = result[column] as? Int ?: return null
    return Week(tmp / 100, tmp % 100)
  }

  /**
   * Is the field value of given record null ?
   */
  override fun isNullImpl(r: Int): Boolean = value[r] == null

  /**
   * Returns the field value of given record as a week value.
   */
  override fun getWeek(r: Int): Week = getObject(r) as Week

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? = value[r]

  override fun toText(o: Any?): String = if (o == null) "" else VWeekField.toText(o as Week)

  override fun toObject(s: String): Any? {
    if (s == "") {
      return null
    }
    var week = 0
    var year = -1
    var bp = 0
    var state: Int
    val buffer = s + '\u0000'
    state = 1

    while (state > 0) {
      when (state) {
        1 -> if (buffer[bp] in '0'..'9') {
          week = buffer[bp] - '0'
          state = 2
        } else {
          state = -1
        }
        2 -> if (buffer[bp] in '0'..'9') {
          week = 10 * week + (buffer[bp] - '0')
          state = 3
        } else if (buffer[bp] == '.' || buffer[bp] == '/') {
          state = 4
        } else if (buffer[bp] == '\u0000') {
          state = 0
        } else {
          state = -1
        }
        3 -> state = if (buffer[bp] == '.' || buffer[bp] == '/') {
          4
        } else if (buffer[bp] == '\u0000') {
          0
        } else {
          -1
        }
        4 -> when {
          buffer[bp] in '0'..'9' -> {
            year = buffer[bp] - '0'
            state = 5
          }
          buffer[bp] == '\u0000' -> {
            state = 0
          }
          else -> {
            state = -1
          }
        }
        5 -> if (buffer[bp] in '0'..'9') {
          year = 10 * year + (buffer[bp] - '0')
          state = 6
        } else {
          state = -1
        }
        6 -> when {
          buffer[bp] in '0'..'9' -> {
            year = 10 * year + (buffer[bp] - '0')
            state = 7
          }
          buffer[bp] == '\u0000' -> {
            state = 0
          }
          else -> {
            state = -1
          }
        }
        7 -> if (buffer[bp] in '0'..'9') {
          year = 10 * year + (buffer[bp] - '0')
          state = 8
        } else {
          state = -1
        }
        8 -> state = if (buffer[bp] == '\u0000') {
          0
        } else {
          -1
        }
        else -> throw VFieldException(this, MessageCode.getMessage("VIS-00008"))
      }
      bp += 1
    }
    if (state == -1) {
      throw VFieldException(this, MessageCode.getMessage("VIS-00008"))
    }
    when {
      year == -1 -> {
        val now: Week = Week.now()

        year = now.year
      }
      year < 50 -> {
        year += 2000
      }
      year < 100 -> {
        year += 1900
      }
    }
    return Week(year, week)
  }

  /**
   * Returns the display representation of field value of given record.
   */
  override fun getTextImpl(r: Int): String = if (value[r] == null) "" else VWeekField.toText(value[r])

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): String? = if(value[r] == null) null else value[r]!!.toSql()

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
  override fun getDataType(): KClass<*> = Week::class

  // ----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  // ----------------------------------------------------------------------

  /**
   * Returns a string representation of a week value wrt the field type.
   */
  protected fun formatWeek(value: Week?): String = VWeekField.toText(value)

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------
  private fun isWeekChar(c: Char): Boolean = c in '0'..'9' || c == '.' || c == '/'

  /**
   * autofill
   * @exception    org.kopi.galite.visual.VException    an exception may occur in gotoNextField
   */
  override fun fillField(handler: PredefinedValueHandler?): Boolean {
    return if (list != null) {
      super.fillField(handler)
    } else {

      val force = try {
        val oldText = getDisplayedValue(true) as? String

        checkType(oldText as Any?)
        val newText = getText(block!!.activeRecord)

        oldText == null || newText == null || newText == "" || oldText != newText
      } catch (e: Exception) {
        true
      }
      if (handler == null || force) {
        setWeek(block!!.activeRecord, Week.now())
      } else {
        setWeek(block!!.activeRecord, Week(handler.selectDate(getWeek(block!!.activeRecord).getFirstDay())))
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
    val record: Int = block!!.activeRecord
    when {
      list != null -> {
        super.enumerateValue(desc)
      }
      isNull(record) -> {
        autofill()
      }
      else -> {
        // try to read week
        try {
          checkType(getText(record))
        } catch (e: VException) {
          // not valid, get now
          setWeek(record, Week.now())
        }
        setWeek(record, getWeek(record).add(if (desc) -1 else 1))
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private var value: Array<Week?> = arrayOfNulls(2 * bufferSize)

  companion object {
    fun toText(value: Week?): String = value.toString()
  }
}
