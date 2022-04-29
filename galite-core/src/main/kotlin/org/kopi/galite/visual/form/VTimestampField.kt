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

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale
import java.util.StringTokenizer

import kotlin.math.min
import kotlin.reflect.KClass

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.kopi.galite.visual.list.VListColumn
import org.kopi.galite.visual.list.VTimestampColumn
import org.kopi.galite.type.format
import org.kopi.galite.visual.Message
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VlibProperties

class VTimestampField(val bufferSize: Int) : VField(10 + 1 + 8, 1) {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private var value: Array<Instant?> = arrayOfNulls(2 * bufferSize)

  override fun hasAutofill(): Boolean = true

  /**
   * return the name of this field
   */
  override fun getTypeInformation(): String = Message.getMessage("timestamp-type-field")

  /**
   * return the name of this field
   */
  override fun getTypeName(): String = VlibProperties.getString("Timestamp")

  /**
   * return true if this field implements "enumerateValue"
   */
  override fun hasNextPreviousEntry(): Boolean = true

  override fun isNumeric(): Boolean = true

  // ----------------------------------------------------------------------
  // Interface Display
  // ----------------------------------------------------------------------

  /**
   * return a list column for list
   */
  override fun getListColumn(): VListColumn = VTimestampColumn(getHeader(), null, null, getPriority() >= 0)

  /**
   * verify that text is valid (during typing)
   */
  override fun checkText(s: String): Boolean = true

  /**
   * verify that value is valid (on exit)
   * @exception    org.kopi.galite.visual.VException    an exception is raised if text is bad
   */
  override fun checkType(rec: Int, s: Any?) {
    if (s as? String == "") {
      setNull(rec)
    } else {
      parseTimestamp(rec, s as String)
    }
  }

  internal fun parseTimestamp(rec: Int, s: String) {
    val timestamp = s.split("[ T]".toRegex(), 2)
    val date = parseDate(timestamp[0])
    val time = parseTime(timestamp[1])
    setTimestamp(rec, Timestamp.valueOf("$date $time").toInstant())
  }

  private fun parseDate(s: String): String {
    var day = 0
    var month = 0
    var year = -2
    val tokens = StringTokenizer(s, "/.#-")

    if (!tokens.hasMoreTokens()) {
      throw VFieldException(this, MessageCode.getMessage("VIS-00003"))
    }
    year = VDateField.stringToInt(tokens.nextToken())
    if (tokens.hasMoreTokens()) {
      month = VDateField.stringToInt(tokens.nextToken())
    }
    if (tokens.hasMoreTokens()) {
      day = VDateField.stringToInt(tokens.nextToken())
    }
    if (tokens.hasMoreTokens() || day == -1 || month == -1 || year == -1) {
      throw VFieldException(this, MessageCode.getMessage("VIS-00003"))
    }
    when {
      month == 0 -> {
        val now = LocalDate.now()
        month = now.monthValue
        year = now.year
      }
      year == -2 -> {
        val now = LocalDate.now()
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
    }
    if (!VDateField.isDate(day, month, year)) {
      throw VFieldException(this, MessageCode.getMessage("VIS-00003"))
    }
    return String.format("%04d-%02d-%02d", year, month, day)
  }

  private fun parseTime(s: String): String? {
    var hours = -1
    var minutes = 0
    var seconds = 0
    val buffer = s + '\u0000'
    var bp = 0
    var state = 1

    while (state > 0) {
      when (state) {
        1 -> when {
          buffer[bp] in '0'..'9' -> {
            hours = buffer[bp] - '0'
            state = 2
          }
          buffer[bp] == '\u0000' -> {
            state = 0
          }
          else -> {
            state = -1
          }
        }
        2 -> when {
          buffer[bp] in '0'..'9' -> {
            hours = 10 * hours + (buffer[bp] - '0')
            state = 3
          }
          buffer[bp] == ':' -> {
            state = 4
          }
          buffer[bp] == '\u0000' -> {
            state = 0
          }
          else -> {
            state = -1
          }
        }
        3 -> state = when {
          buffer[bp] == ':' -> {
            4
          }
          buffer[bp] == '\u0000' -> {
            0
          }
          else -> {
            -1
          }
        }
        4 -> when {
          buffer[bp] in '0'..'9' -> {
            minutes = buffer[bp] - '0'
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
          minutes = 10 * minutes + (buffer[bp] - '0')
          state = 6
        } else {
          state = -1
        }
        6 -> state = when {
          buffer[bp] == ':' -> {
            7
          }
          buffer[bp] == '\u0000' -> {
            0
          }
          else -> {
            -1
          }
        }
        7 -> when {
          buffer[bp] in '0'..'9' -> {
            seconds = buffer[bp] - '0'
            state = 8
          }
          buffer[bp] == '\u0000' -> {
            state = 0
          }
          else -> {
            state = -1
          }
        }
        8 -> if (buffer[bp] in '0'..'9') {
          seconds = 10 * seconds + (buffer[bp] - '0')
          state = 9
        } else {
          state = -1
        }
        9 -> state = if (buffer[bp] == '\u0000') {
          0
        } else {
          -1
        }
      }
      bp += 1
    }
    if (state == -1) {
      throw VFieldException(this, MessageCode.getMessage("VIS-00007"))
    }
    return if (hours == -1) {
      null
    } else {
      if (!VTimeField.isTime(hours, minutes)) {
        throw VFieldException(this, MessageCode.getMessage("VIS-00007"))
      }
      String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
  }

  // ----------------------------------------------------------------------
  // Interface bd/Triggers
  // ----------------------------------------------------------------------

  /**
   * Sets the field value of given record to a null value.
   */
  override fun setNull(r: Int) {
    setTimestamp(r, null)
  }

  /**
   * Sets the field value of given record to a timestamp value.
   */
  override fun setTimestamp(r: Int, v: Instant?) {
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
   * Warning:	This method will become inaccessible to kopi users in next release
   */
  override fun setObject(r: Int, v: Any?) {
    setTimestamp(r, v as? Instant)
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param    result       the result row
   * @param    column       the column in the tuple
   */
  override fun retrieveQuery(result: ResultRow, column: Column<*>): Any? {
    return when (val tmp = result[column]) {
      is Timestamp -> tmp.toInstant()
      is LocalDateTime -> Instant.from(tmp.atZone(ZoneId.systemDefault()))
      is Instant -> tmp
      else -> null
    }
  }

  /**
   * Is the field value of given record null ?
   */
  override fun isNullImpl(r: Int): Boolean = value[r] == null

  /**
   * Returns the field value of given record as a timestamp value.
   */
  override fun getTimestamp(r: Int): Instant? = getObject(r) as? Instant

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? = value[r]

  override fun toText(o: Any?): String {
    return if (o == null) {
      VConstants.EMPTY_TEXT
    } else {
      val text = if (o is Instant) o.format() else o.toString()
      // this is workaround to display the timestamp in yyyy-MM-dd hh:mm:ss format
      text.substring(0, min(width, text.length))
    }
  }

  override fun toObject(s: String): Any? {
    return if (s == "") {
      null
    } else {
      SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(s).toInstant()
    }
  }

  /**
   * Returns the display representation of field value of given record.
   */
  override fun getTextImpl(r: Int): String {
    return if (value[r] == null) {
      VConstants.EMPTY_TEXT
    } else {
      val text = value[r]!!.format()
      // this is work around to display the timestamp in yyyy-MM-dd hh:mm:ss format
      // The proper way is to change the method Timestamp#toString(Locale) but this
      // will affect the SQL representation of the timestamp value.
      text.substring(0, min(width, text.length))
    }
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): Instant? = value[r]

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
  override fun getDataType(): KClass<*> = Instant::class

  override fun fillField(handler: PredefinedValueHandler?): Boolean {
    return if (list == null) {
      setTimestamp(block!!.activeRecord, Instant.now())
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
        setTimestamp(record, Instant.now())
      }
      else -> {
        // try to read timestamp
        try {
          checkType(getText(record))
        } catch (e: VException) {
          // not valid, get now
          setTimestamp(record, Instant.now())
        }
        setTimestamp(record, getTimestamp(record)?.plusMillis(if (desc) -1 else 1))
      }
    }
  }
}
