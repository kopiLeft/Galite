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
import org.kopi.galite.base.Query

open class VStringField(width: Int, height: Int, private val visibleHeight: Int, val typeOptions: Int, styled: Boolean)
  : VField(width, height) {

  constructor(width: Int, height: Int, convert: Int, styled: Boolean) : this(width, height, 0, convert, styled) {}

  /**
   * just after loading, construct record
   */
  override fun build() {
    super.build()
    value = arrayOfNulls(2 * block.bufferSize)
  }

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
  override fun getTypeName(): String {
    return VlibProperties.getString(if (height == 1) "String" else "StringArea")
  }

  /**
   * Return the visible height
   */
  fun getVisibleHeight(): Int {
    return if (visibleHeight == 0) height else visibleHeight
  }
  // ----------------------------------------------------------------------
  // Interface Display
  // ----------------------------------------------------------------------
  /**
   * return a list column for list
   */
  override fun getListColumn(): VListColumn {
    return VStringColumn(getHeader(), null, align, width, getPriority() >= 0)
  }

  /**
   * verify that text is valid (during typing)
   */
  override fun checkText(s: String): Boolean {
    var end = 0
    end = textToModel(s, width, Int.MAX_VALUE).length
    return end <= width * height
  }

  /**
   * verify that value is valid (on exit)
   * @exception    org.kopi.galite.visual.VException    an exception may be raised if text is bad
   */
  override fun checkType(rec: Int, o: Any?) {
    var s = o as String?

    if (s == null || s == "") {
      setNull(rec)
    } else {
      when (typeOptions and VConstants.FDO_CONVERT_MASK) {
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
      if (!found && Character.isLetter(chars[i])) {
        chars[i] = Character.toUpperCase(chars[i])
        found = true
      } else if (Character.isWhitespace(chars[i])) {
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
  fun setString(r: Int, v: String?) {
    val modelVal = if (v == null || v == "") null else v

    if (changedUI || value[r] == null && modelVal != null || value[r] != null && value[r] != modelVal) {
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
    setString(r, v as String?)
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param    query        the query holding the tuple
   * @param    column        the index of the column in the tuple
   */
  override fun retrieveQuery(query: Query, column: Int): Any {
    return query.getString(column)
  }

  /**
   * Is the field value of given record null ?
   */
  override fun isNullImpl(r: Int): Boolean {
    return value[r] == null
  }

  /**
   * Returns the field value of given record as a string value.
   */
  fun getString(r: Int): String {
    return getObject(r) as String
  }

  /**
   * Returns the field value of the current record as an object
   */
  override fun getObjectImpl(r: Int): Any? {
    return value[r]
  }

  override fun toText(o: Any?): String {
    // TODO Auto-generated method stub
    return if (o == null) "" else (o as String?)!!
  }

  override fun toObject(s: String): Any? {
    return if (s == "") null else s
  }

  /**
   * Returns the display representation of field value of given record.
   */
  override fun getTextImpl(r: Int): String {
    return if (value[r] == null) "" else value[r]!!
  }

  /**
   * Returns the SQL representation of field value of given record.
   */
  override fun getSqlImpl(r: Int): String {
    return org.kopi.galite.type.Utils.toSql(value[r]!!)
  }

  /**
   * Copies the value of a record to another
   */
  override fun copyRecord(f: Int, t: Int) {
    val oldValue: String? = value[t]

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
  override fun getDataType(): KClass<*> {
    return String::class
  }

  // ----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  // ----------------------------------------------------------------------
  /**
   * Returns a string representation of a string value wrt the field type.
   */
  protected fun formatString(value: String): String {
    return value
  }

  /**
   * Replaces blanks by new-lines
   *
   * @param    record        the index of the record
   */
  fun modelToText(record: Int): String {
    return modelToText(getString(record), width)
  }

  /**
   * Returns true if the text field supports styled content.
   */
  val isStyled: Boolean = styled
  protected lateinit var value: Array<String?>

  companion object {
    /**
     * Replaces new-lines by blanks
     *
     * @param    source    the source text with carriage return
     * @param    col    the width of the text
     */
    fun textToModel(source: String, col: Int): String {
      return textToModel(source, col, Int.MAX_VALUE)
    }
    /**
     * Replaces new-lines by blanks
     *
     * @param    source    the source text with carriage return
     * @param    col    the width of the text
     */

    fun textToModel(source: String, col: Int, lin: Int): String {
      // depending on the value of FDO_DYNAMIC_NL (ie FIXED ON/OFF)
      return LineBreaker.textToModel(source, col, lin)
    }

    /**
     * Replaces blanks by new-lines
     *
     * @param    source        the source text with white space
     * @param    col        the width of the text area
     */
    fun modelToText(source: String, col: Int): String {
      // depending on the value of FDO_DYNAMIC_NL (ie FIXED ON/OFF)
      return LineBreaker.modelToText(source, col)
    }
  }
}
