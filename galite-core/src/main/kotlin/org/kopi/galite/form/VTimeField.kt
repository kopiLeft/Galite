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

import kotlin.reflect.KClass

import org.kopi.galite.db.Query
import org.kopi.galite.list.VListColumn
import org.kopi.galite.list.VTimeColumn
import org.kopi.galite.type.Time
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VlibProperties

class VTimeField(val bufferSize: Int) : VField(5, 1) {

  override fun hasAutofill(): Boolean = true

  /**
   * return the name of this field
   */
  override fun getTypeInformation(): String = VlibProperties.getString("time-type-field")

  /**
   * return the name of this field
   */
  override fun getTypeName(): String = VlibProperties.getString("Time")

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
  override fun getListColumn(): VListColumn = VTimeColumn(getHeader(), null, getPriority() >= 0)

  /**
   * verify that text is valid (during typing)
   */
  override fun checkText(s: String): Boolean {
    if (s.length > 5) {
      return false
    }
    for (element in s) {
      if (!isTimeChar(element)) {
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
    if (s as? String == "") {
      setNull(rec)
    } else {
      var hours = -1
      var minutes = 0
      val buffer = (s as? String) + '\u0000'
      var bp = 0
      var state = 1

      while (state > 0) {
        when (state) {
          1 -> when { /* The first hours' digit */
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
          2 -> when { /* The second hours' digit */
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
          3 -> state = when { /* The point between hours and minutes */
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
          4 -> when { /* The first minutes' digit */
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
          5 -> if (buffer[bp] in '0'..'9') { /* The second minutes' digit */
            minutes = 10 * minutes + (buffer[bp] - '0')
            state = 6
          } else {
            state = -1
          }
          6 -> state = if (buffer[bp] == '\u0000') { /* The end */
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
      if (hours == -1) {
        setNull(rec)
      } else {
        if (!isTime(hours, minutes)) {
          throw VFieldException(this, MessageCode.getMessage("VIS-00007"))
        }
        setTime(rec, Time(hours, minutes))
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
    setTime(r, null)
  }

  /**
   * Sets the field value of given record to a time value.
   */
  override fun setTime(r: Int, v: Time?) {
    if (isChangedUI
            || value[r] == null && v != null
            || value[r] != null && !value[r]?.equals(v)!!) {
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
    setTime(r, v as? Time)
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
      query.getTime(column)
    }
  }

  /**
   * Is the field value of given record null ?
   */
  override fun isNullImpl(r: Int): Boolean = value[r] == null

  /**
   * Returns the field value of given record as a time value.
   */
  override fun getTime(r: Int): Time = getObject(r) as Time

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? = value[r]

  override fun toText(o: Any?): String = if (o == null) "" else (o as Time).toString()

  override fun toObject(s: String): Any? {
    return if (s == "") {
      null
    } else {
      var hours = -1
      var minutes = 0
      val buffer = s + '\u0000'
      var bp = 0
      var state = 1

      while (state > 0) {
        when (state) {
          1 -> when { /* The first hours' digit */
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
          2 -> when { /* The second hours' digit */
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
          3 -> state = when { /* The point between hours and minutes */
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
          4 -> when {  /* The first minutes' digit */
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
          5 -> if (buffer[bp] in '0'..'9') { /* The second minutes' digit */
            minutes = 10 * minutes + (buffer[bp] - '0')
            state = 6
          } else {
            state = -1
          }
          6 -> state = if (buffer[bp] == '\u0000') { /* The end */
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
      if (hours == -1) {
        null
      } else {
        if (!isTime(hours, minutes)) {
          throw VFieldException(this, MessageCode.getMessage("VIS-00007"))
        }
        Time(hours, minutes)
      }
    }
  }

  /**
   * Returns the display representation of field value of given record.
   */
  override fun getTextImpl(r: Int): String = if (value[r] == null) VConstants.EMPTY_TEXT else value[r].toString()

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): String? = if (value[r] == null) null else value[r]!!.toSql()  // TODO("NOT SUPPORTED YET")

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
  override fun getDataType(): KClass<*> = Time::class

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  private fun isTimeChar(c: Char): Boolean = c in '0'..'9' || c == ':'

  override fun fillField(handler: PredefinedValueHandler?): Boolean {
    return if (list == null) {
      setTime(block!!.activeRecord, Time.now())
      true
    } else {
      super.fillField(handler)
    }
  }

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
        setTime(record, Time.now())
      }
      else -> {
        // try to read time
        try {
          checkType(getText(record))
        } catch (e: VException) {
          // not valid, get now
          setTime(record, Time.now())
        }
        setTime(record, getTime(record).add(if (desc) -1 else 1))
      }
    }
  }

  companion object {
    internal fun isTime(h: Int, m: Int): Boolean = h in 0..23 && m in 0..59
  }

  private var value: Array<Time?> = arrayOfNulls(2 * bufferSize)
}
