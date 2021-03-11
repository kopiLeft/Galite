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

import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.list.VListColumn
import org.kopi.galite.list.VStringColumn
import org.kopi.galite.util.LineBreaker
import org.kopi.galite.visual.VExecFailedException
import org.kopi.galite.visual.VlibProperties
import org.kopi.galite.db.Query

open class VStringField(val bufferSize: Int,
                        width: Int,
                        height: Int,
                        private val visibleHeight: Int,
                        val convert: Int,
                        styled: Boolean)
               : VField(width, height) {

  constructor(bufferSize: Int,
              width: Int,
              height: Int,
              convert: Int,
              styled: Boolean)
       : this(bufferSize,
              width,
              height,
              0,
              convert,
              styled)

  /**
   * return the name of this field
   */
  override fun getTypeInformation(): String {
    return if (height > 1) {
      VlibProperties.getString("string-type-area", arrayOf(width, height))
    } else {
      VlibProperties.getString("string-type-field", arrayOf(width))
    }
  }

  /**
   * return the name of this field
   */
  override fun getTypeName(): String = VlibProperties.getString(if (height == 1) "String" else "StringArea")

  /**
   * Return the visible height
   */
  fun getVisibleHeight(): Int = if (visibleHeight == 0) height else visibleHeight

  // ----------------------------------------------------------------------
  // Interface Display
  // ----------------------------------------------------------------------
  /**
   * return a list column for list
   */
  override fun getListColumn(): VListColumn =
          VStringColumn(getHeader(), null, align, width, getPriority() >= 0)

  /**
   * verify that text is valid (during typing)
   */
  override fun checkText(s: String): Boolean {
    val end = textToModel(s, width, Int.MAX_VALUE).length

    return end <= width * height
  }

  /**
   * verify that value is valid (on exit)
   * @exception    org.kopi.galite.visual.VException    an exception may be raised if text is bad
   */
  override fun checkType(rec: Int, s: Any?) {
    var s = s as? String

    if (s == null || s == "") {
      setNull(rec)
    } else {
      when (convert and VConstants.FDO_CONVERT_MASK) {
        VConstants.FDO_CONVERT_NONE -> {
        }
        VConstants.FDO_CONVERT_UPPER -> s = s.toUpperCase()
        VConstants.FDO_CONVERT_LOWER -> s = s.toLowerCase()
        VConstants.FDO_CONVERT_NAME -> s = convertName(s)
        else -> throw InconsistencyException()
      }
      if (!checkText(s)) {
        throw VExecFailedException()
      }
      setString(rec, s)
    }
  }

  /**
   * Convert the first letter in each word in the source text into upper case.
   *
   * @param    source        the source text.
   */
  private fun convertName(source: String): String {
    val chars = source.toLowerCase().toCharArray()
    var found = false

    for (i in chars.indices) {
      if (!found && chars[i].isLetter()) {
        chars[i] = chars[i].toUpperCase()
        found = true
      } else if (chars[i].isWhitespace()) {
        found = false
      }
    }
    return String(chars)
  }

  // ----------------------------------------------------------------------
  // INTERFACE BD/TRIGGERS
  // ----------------------------------------------------------------------
  /**
   * Sets the field value of given record to a null value.
   */
  override fun setNull(r: Int) {
    setString(r, null)
  }

  /**
   * Sets the field value of given record to a string value.
   */
  override fun setString(r: Int, v: String?) {
    val modelVal = if (v == null || v == "") null else v

    if (isChangedUI || value[r] == null && modelVal != null || value[r] != null && value[r] != modelVal) {
      // trails (backup) the record if necessary
      trail(r)
      // set value in the defined row
      value[r] = modelVal
      // inform that value has changed
      setChanged(r)
    }
  }

  /**
   * Sets the field value of given record.
   * Warning:	This method will become inaccessible to users in next release
   */
  override fun setObject(r: Int, v: Any?) {
    setString(r, v as? String)
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param    query        the query holding the tuple
   * @param    column        the index of the column in the tuple
   */
  override fun retrieveQuery(query: Query, column: Int): Any = query.getString(column)

  /**
   * Is the field value of given record null ?
   */
  override fun isNullImpl(r: Int): Boolean = value[r] == null

  /**
   * Returns the field value of given record as a string value.
   */
  override fun getString(r: Int): String = getObject(r) as String

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? = value[r]

  override fun toText(o: Any?): String = (o as? String).orEmpty()

  override fun toObject(s: String): Any? = if (s == "") null else s

  /**
   * Returns the display representation of field value of given record.
   */
  override fun getTextImpl(r: Int): String = if (value[r] == null) "" else value[r]!!

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): String? = value[r]

  /**
   * Copies the value of a record to another
   */
  override fun copyRecord(f: Int, t: Int) {
    val oldValue: String? = value[t]

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
  override fun getDataType(): KClass<*> = String::class

  // ----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  // ----------------------------------------------------------------------
  /**
   * Returns a string representation of a string value wrt the field type.
   */
  protected fun formatString(value: String): String = value

  /**
   * Replaces blanks by new-lines
   *
   * @param    record        the index of the record
   */
  fun modelToText(record: Int): String = modelToText(getString(record), width)

  /**
   * Returns true if the text field supports styled content.
   */
  val isStyled: Boolean = styled

  protected var value: Array<String?> = arrayOfNulls(2 * bufferSize)

  companion object {
    /**
     * Replaces new-lines by blanks
     *
     * @param    source    the source text with carriage return
     * @param    col       the width of the text
     */

    fun textToModel(source: String, col: Int, lin: Int = Int.MAX_VALUE): String =
            // depending on the value of FDO_DYNAMIC_NL (ie FIXED ON/OFF)
            LineBreaker.textToModel(source, col, lin)

    /**
     * Replaces blanks by new-lines
     *
     * @param    source        the source text with white space
     * @param    col           the width of the text area
     */
    fun modelToText(source: String, col: Int): String =
            // depending on the value of FDO_DYNAMIC_NL (ie FIXED ON/OFF)
            LineBreaker.modelToText(source, col)
  }
}
