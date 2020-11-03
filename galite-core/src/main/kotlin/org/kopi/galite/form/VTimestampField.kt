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

import kotlin.math.min
import kotlin.reflect.KClass

import org.kopi.galite.db.Query
import org.kopi.galite.list.VListColumn
import org.kopi.galite.list.VTimestampColumn
import org.kopi.galite.type.Timestamp
import org.kopi.galite.db.Utils
import org.kopi.galite.visual.Message
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VlibProperties

class VTimestampField : VField(10 + 1 + 8, 1) {

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
  fun hasNextPreviousEntry(): Boolean = true

  /**
   * just after loading, construct record
   */
  override fun build() {
    super.build()
    value = arrayOfNulls(2 * block.bufferSize)
  }

  fun isNumeric(): Boolean = true

  // ----------------------------------------------------------------------
  // Interface Display
  // ----------------------------------------------------------------------

  /**
   * return a list column for list
   */
  override fun getListColumn(): VListColumn = VTimestampColumn(getHeader(), null, getPriority() >= 0)

  /**
   * verify that text is valid (during typing)
   */
  override fun checkText(s: String): Boolean = true

  /**
   * verify that value is valid (on exit)
   * @exception    org.kopi.galite.visual.VException    an exception is raised if text is bad
   */
  override fun checkType(rec: Int, o: Any?) {
    if (o as String == "") {
      setNull(rec)
    } else {
      setTimestamp(rec, Timestamp.now())
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
  fun setTimestamp(r: Int, v: Timestamp?) {
    if (changedUI
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
    setTimestamp(r, v as? Timestamp)
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
      query.getTimestamp(column)
    }
  }

  /**
   * Is the field value of given record null ?
   */
  override fun isNullImpl(r: Int): Boolean = value[r] == null

  /**
   * Returns the field value of given record as a timestamp value.
   */
  fun getTimestamp(r: Int): Timestamp = getObject(r) as Timestamp

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? = value[r]

  override fun toText(o: Any?): String {
    return if (o == null) {
      VConstants.EMPTY_TEXT
    } else {
      val text = o.toString()
      // this is work around to display the timestamp in yyyy-MM-dd hh:mm:ss format
      // The proper way is to change the method Timestamp#toString(Locale) but this
      // will affect the SQL representation of the timestamp value.
      text.substring(0, min(width, text.length))
    }
  }

  override fun toObject(s: String): Any? {
    return if (s == "") {
      null
    } else {
      Timestamp.parse(s, "yyyy-MM-dd HH:mm:ss")
    }
  }

  /**
   * Returns the display representation of field value of given record.
   */
  override fun getTextImpl(r: Int): String {
    return if (value[r] == null) {
      VConstants.EMPTY_TEXT
    } else {
      val text = value[r].toString()
      // this is work around to display the timestamp in yyyy-MM-dd hh:mm:ss format
      // The proper way is to change the method Timestamp#toString(Locale) but this
      // will affect the SQL representation of the timestamp value.
      text.substring(0, min(width, text.length))
    }
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): String = Utils.toSql(value[r])

  /**
   * Copies the value of a record to another
   */
  override fun copyRecord(f: Int, t: Int) {
    val oldValue = value[t]

    value[t] = value[f]
    // inform that value has changed for non backup records
    // only when the value has really changed.
    if (t < block.bufferSize
        && (oldValue != null && value[t] == null
            || oldValue == null && value[t] != null
            || oldValue != null && oldValue != value[t])) {
      fireValueChanged(t)
    }
  }

  /**
   * Returns the data type handled by this field.
   */
  override fun getDataType(): KClass<*> = Timestamp::class

  override fun fillField(handler: PredefinedValueHandler?): Boolean {
    return if (list == null) {
      setTimestamp(block.activeRecord, Timestamp.now())
      true
    } else {
      super.fillField(handler)
    }
  }

  /**
   * Checks that field value exists in list
   */
  override fun enumerateValue(desc: Boolean) {
    val record: Int = block.activeRecord

    when {
      list != null -> {
        super.enumerateValue(desc)
      }
      isNull(record) -> {
        setTimestamp(record, Timestamp.now())
      }
      else -> {
        // try to read timestamp
        try {
          checkType(getText(record))
        } catch (e: VException) {
          // not valid, get now
          setTimestamp(record, Timestamp.now())
        }
        setTimestamp(record, getTimestamp(record).add(if (desc) -1 else 1))
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private lateinit var value: Array<Timestamp?>
}
