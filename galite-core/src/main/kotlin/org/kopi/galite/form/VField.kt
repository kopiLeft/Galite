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

import java.awt.Color
import java.io.InputStream

import javax.swing.event.EventListenerList

import kotlin.reflect.KClass

import org.kopi.galite.db.Query
import org.kopi.galite.base.UComponent
import org.kopi.galite.l10n.BlockLocalizer
import org.kopi.galite.l10n.FieldLocalizer
import org.kopi.galite.list.VColumn
import org.kopi.galite.list.VList
import org.kopi.galite.list.VListColumn
import org.kopi.galite.type.Time
import org.kopi.galite.type.Fixed
import org.kopi.galite.type.Week
import org.kopi.galite.type.Month
import org.kopi.galite.type.Timestamp
import org.kopi.galite.type.Date
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VColor
import org.kopi.galite.visual.VRuntimeException
import org.kopi.galite.visual.VModel

/**
 * A field is a column in the the database (a list of rows)
 * it provides an access to data both programmatically or via a UI
 * (DForm)
 */
abstract class VField protected constructor(width: Int, height: Int) : VConstants, VModel {
  /**
   * Sets the dimensions
   */
  fun setDimension(width: Int, height: Int) {
    TODO()
  }

  /**
   * set information on the field.
   */
  fun setInfo(
          name: String,
          index: Int,
          posInArray: Int,
          options: Int,
          access: IntArray,
          list: VList?,
          columns: Array<VColumn?>?,
          indices: Int,
          priority: Int,
          commands: Array<VCommand>,
          pos: VPosition,
          align: Int,
          alias: VField,
  ) {
    TODO()
  }

  fun fetchColumn(table: Int): Int {
    TODO()
  }

  /**
   * The position of the label (left / top)
   */
  fun setLabelPos(pos: Int) {
    TODO()
  }

  open fun isNoEdit(): Boolean = options and VConstants.FDO_NOEDIT != 0

  open fun isTransient(): Boolean = options and VConstants.FDO_TRANSIENT != 0

  open fun noDetail(): Boolean = options and VConstants.FDO_NODETAIL != 0 || block!!.noDetail()

  fun noChart(): Boolean = options and VConstants.FDO_NOCHART != 0 || block!!.noChart()

  open fun isSortable(): Boolean = options and VConstants.FDO_SORT != 0

  fun eraseOnLookup(): Boolean = options and VConstants.FDO_DO_NOT_ERASE_ON_LOOKUP == 0

  /**
   * Returns `true` if the field has the auto fill feature.
   */
  open fun hasAutofill(): Boolean = list != null

  /**
   * Returns true if the field has an action trigger.
   * @return True if the field has an action trigger.
   */
  fun hasAction(): Boolean = hasTrigger(VConstants.TRG_ACTION)

  /**
   * Returns `true` if the field has the auto complete feature.
   */
  open fun hasAutocomplete(): Boolean = list != null && list!!.hasAutocomplete()

  /**
   * Returns the auto complete length.
   * @return The auto complete length.
   */
  open fun getAutocompleteLength(): Int {
    TODO()
  }

  /**
   * Returns the auto complete type.
   * @return The auto complete type.
   */
  open fun getAutocompleteType(): Int {
    TODO()
  }

  /**
   * return true if this field implements "enumerateValue"
   */
  open fun hasNextPreviousEntry(): Boolean = list != null

  fun hasNullableCols(): Boolean  {
    TODO()
  }

  /**
   * Returns true if it is a numeric field.
   */
  open fun isNumeric(): Boolean {
    TODO()
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  /**
   * Localizes this field
   *
   * @param     parent         the caller localizer
   */
  fun localize(parent: BlockLocalizer) {
    TODO()
  }

  /**
   * Localizes this field
   *
   * @param     loc         the caller localizer
   */
  protected open fun localize(loc: FieldLocalizer) {
    // by default nothing to do
  }

  // ----------------------------------------------------------------------
  // PUBLIC COMMANDS
  // ----------------------------------------------------------------------
  // called if the in a chart the line changes, but it is still visible
  fun updateText() {
    if (changedUI) {
      modelNeedUpdate()
    }
  }

  /**
   * Validate the field, ie: get the last displayed value, check it and check mustfill
   */
  fun validate() {
    TODO()
  }

  /**
   * Verify that text is valid (during typing)
   *
   * @param     s               the text to check
   * @return    true if the text is valid
   */
  abstract fun checkText(s: String): Boolean

  /**
   * verify that value is valid (on exit)
   *
   * @param     rec             the concerned record.
   * @param     s               the object to check
   * @exception VException      an exception is raised if text is bad
   */
  abstract fun checkType(rec: Int, s: Any)

  /**
   * verify that value is valid (on exit)
   *
   * @param     s               the object to check
   * @exception VException      an exception is raised if text is bad
   */
  fun checkType(s: Any?) {
    TODO()
  }

  /**
   * Returns the data type handled by this field.
   * @return The data type handled by this field.
   */
  abstract fun getDataType(): KClass<*>

  /**
   * text has changed (key typed on a display)
   */
  fun onTextChange(text: String) {
    changed = true
    changedUI = true
    autoLeave()
  }

  private fun autoLeave() {
    assert(this == block!!.activeField) { threadInfo() + "current field: " + block!!.activeField }
    if (!hasTrigger(VConstants.TRG_AUTOLEAVE)) {
      return
    }
    val autoleave: Boolean
    try {
      autoleave = (callTrigger(VConstants.TRG_AUTOLEAVE) as Boolean)
    } catch (e: VException) {
      throw InconsistencyException("autoleave can not throw a VException", e)
    }
    if (autoleave) {
      val action: Action = object : Action("autoleave") {
        override fun execute() {
          block!!.form.getActiveBlock()!!.gotoNextField()
        }
      }
      (getDisplay() as UField).getBlockView().getFormView().performAsyncAction(action)
    }
  }

  override fun setDisplay(display: UComponent) {
    // not used
  }

  /**
   * Return the display
   */
  override fun getDisplay(): UField? {
    var value: UField? = null
    if (hasListener) {
      val listeners = fieldListener!!.listenerList
      var i = listeners.size - 2
      while (i >= 0 && value == null) {
        if (listeners[i] === FieldListener::class.java) {
          value = (listeners[i + 1] as FieldListener).getCurrentDisplay()
        }
        i -= 2
      }
    }
    return value
  }

  open fun getType(): Int = MDL_FLD_TEXT


  open fun build() {
    setAccess(access[VConstants.MOD_QUERY])
  }

  /**
   * Display error
   */
  fun displayFieldError(message: String?) {
    if (hasListener) {
      val listeners = fieldListener!!.listenerList
      var i = listeners.size - 2

      while (i >= 0) {
        if (listeners[i] == FieldListener::class.java) {
          (listeners[i + 1] as FieldListener).fieldError(message!!)
        }
        i -= 2
      }
    }
  }

  /**
   * Fill this field with an appropriate value according to present text
   * and ask the user if there is multiple choice
   * @exception VException      an exception may occur in gotoNextField
   */
  fun autofill() {
    // programatic autofill => no UI
    fillField(null) // no Handler
  }

  fun autofill(showDialog: Boolean, gotoNextField: Boolean) {
    autofill()
  }

  /**
   * Fill this field with an appropriate value according to present text
   * and ask the user if there is multiple choice
   * @exception VException      an exception may occur in gotoNextField
   */
  fun predefinedFill() {
    if (hasListener) {
      var filled = false
      val listeners = fieldListener!!.listenerList
      var i = listeners.size - 2

      while (i >= 0 && !filled) {
        if (listeners[i] === FieldListener::class.java) {
          filled = (listeners[i + 1] as FieldListener).predefinedFill()
        }
        i -= 2
      }
    }
  }

  open fun fillField(handler: PredefinedValueHandler?): Boolean {
    return handler?.selectDefaultValue() ?: false
  }

  // ----------------------------------------------------------------------
  // PROTECTED ACCESSORS
  // ----------------------------------------------------------------------
  /**
   * @return a list column for list
   */
  abstract fun getListColumn(): VListColumn?

  // ----------------------------------------------------------------------
  // NAVIGATING
  // ----------------------------------------------------------------------
  /**
   * enter a field
   */
  fun enter() {
    TODO()
  }

  /**
   * when leaving field, if text is okay, set value in record
   * @exception VException      an exception is raised if text is bad
   */
  fun leave(check: Boolean) {
    TODO()
  }

  fun hasFocus(): Boolean = block!!.activeField == this

  /**
   * Changes access dynamically, overriding mode access
   */
  fun setAccess(at: Int, value: Int) {
    var value = value

    if (getDefaultAccess() < value) {
      // access can never be higher than the default access
      value = getDefaultAccess()
    }
    if (value != dynAccess[at]) {
      dynAccess[at] = value
      fireAccessChanged(at)
    }
  }

  fun setAccess(value: Int) {
    TODO()
  }

  /**
   * Changes access dynamically, overriding mode access
   */
  fun setAccess(access: IntArray) {
    assert(access.size == this.access.size) { threadInfo() + "new access length: " + access.size + " old: " + this.access.size }
    this.access = access
  }

  /**
   * return access of this field in current mode
   */
  fun getDefaultAccess(): Int  {
    TODO()
  }

  fun getAccess(i: Int): Int {
    return if (i == -1) {
      getDefaultAccess()
    } else {
      if (isInternal()) {
        VConstants.ACS_HIDDEN
      } else {
        dynAccess[i]
      }
    }
  }

  @Deprecated("use the method <code>updateAccess()<code> instead.")
  fun getAccess() {
    TODO()
  }

  fun updateAccess(current: Int = block!!.currentRecord) {
    TODO()
  }

  fun updateModeAccess() {
    TODO()
  }

  // ----------------------------------------------------------------------
  // RESET TO DEFAULT
  // ----------------------------------------------------------------------
  /**
   * Sets default values
   */
  fun setDefault() {
    TODO()
  }

  // ----------------------------------------------------------------------
  // QUERY BUILD
  // ----------------------------------------------------------------------
  /**
   * Returns the number of database columns associated to the field.
   * !!! change name
   */
  fun getColumnCount(): Int = columns!!.size

  /**
   * Returns the database column at given position.
   */
  fun getColumn(n: Int): VColumn? = columns!![n]

  /**
   * Returns the column name in the table with specified correlation.
   * returns null if the field has no access to this table.
   */
  fun lookupColumn(corr: Int): String? = columns!!.find { corr == it!!.getTable() }?.name

  /**
   * Returns true if the column is a key of the table with specified correlation.
   */
  fun isLookupKey(corr: Int): Boolean = columns!!.find { corr == it!!.getTable()}?.key ?: false

  /**
   * Is the field part of given index ?
   */
  fun hasIndex(idx: Int): Boolean = indices and (1 shl idx) != 0

  /**
   * Returns the position in select results.
   */
  fun getPriority(): Int = priority

  /**
   * @return the type of search condition for this field.
   *
   * @see VConstants
   */
  open fun getSearchType(): Int {
    TODO()
  }

  /**
   * Returns the search conditions for this field.
   */
  fun getSearchCondition(): String? {
    TODO()
  }

  // ----------------------------------------------------------------------
  // FORMATTING VALUES WRT FIELD TYPE
  // ----------------------------------------------------------------------
  /**
   * Returns the field label.
   *
   */
  fun getHeader(): String = label ?: ""

  // ----------------------------------------------------------------------
  // MANAGING FIELD VALUES
  // ----------------------------------------------------------------------
  /**
   * return the name of this field
   */
  fun getTypeOptions(): Int = 0

  /**
   * Sets the search operator for the field
   * @see VConstants
   */
  fun setSearchOperator(value: Int) {
    if (value >= VConstants.OPERATOR_NAMES.size) {
      throw InconsistencyException("Value $value is not a valid operator")
    }
    if (searchOperator != value) {
      searchOperator = value
      fireSearchOperatorChanged()
    }
  }

  /**
   * @return the search operator for the field
   * @see VConstants
   */
  fun getSearchOperator(): Int = searchOperator

  /**
   * Clears the field.
   *
   * @param     r       the recorde number.
   */
  fun clear(r: Int) {
    setSearchOperator(VConstants.SOP_EQ)
    setNull(r)
    resetColor(r)
  }

  /**
   * Sets the field value of the current record to a null value.
   */
  fun setNull() {
    setNull(block!!.currentRecord)
  }

  /**
   * Sets the field value of the current record to a bigdecimal value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setFixed(v: Fixed) {
    setFixed(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a boolean value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setBoolean(v: Boolean) {
    setBoolean(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a date value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setDate(v: Date) {
    setDate(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a month value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setMonth(v: Month) {
    setMonth(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a int value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setInt(v: Int) {
    setInt(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setObject(v: Any) {
    setObject(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a string value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setString(v: String) {
    setString(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of given record to a date value.
   */
  fun setImage(v: ByteArray) {
    setImage(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a time value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setTime(v: Time) {
    setTime(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a week value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setWeek(v: Week) {
    setWeek(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a timestamp value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setTimestamp(v: Timestamp) {
    setTimestamp(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a color value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setColor(v: Color?) {
    setColor(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of given record to a null value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  abstract fun setNull(r: Int)

  /**
   * Sets the field value of given record to a bigdecimal value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setFixed(r: Int, v: Fixed) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record to a boolean value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setBoolean(r: Int, v: Boolean) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record to a date value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun setDate(r: Int, v: Date?) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record to a month value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setMonth(r: Int, v: Month) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record to a week value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setWeek(r: Int, v: Week) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record to a int value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setInt(r: Int, v: Int) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  abstract fun setObject(r: Int, v: Any?)

  /**
   * Sets the field value of given record to a string value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setString(r: Int, v: String) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record to a date value.
   */
  fun setImage(r: Int, v: ByteArray) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record to a time value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setTime(r: Int, v: Time) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record to a timestamp value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setTimestamp(r: Int, v: Timestamp) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record to a color value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun setColor(r: Int, v: Color?) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of the current record from a query tuple.
   * @param     query           the query holding the tuple
   * @param     column          the index of the column in the tuple
   */
  fun setQuery(query: Query, column: Int) {
    setQuery(block!!.currentRecord, query, column)
  }

  /**
   * Sets the field value of given record from a query tuple.
   * @param     record          the index of the record
   * @param     query           the query holding the tuple
   * @param     column          the index of the column in the tuple
   */
  fun setQuery(record: Int, query: Query, column: Int) {
    setObject(record, retrieveQuery(query, column))
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param     query           the query holding the tuple
   * @param     column          the index of the column in the tuple
   */
  abstract fun retrieveQuery(query: Query, column: Int): Any?

  // ----------------------------------------------------------------------
  // FIELD VALUE ACCESS
  // ----------------------------------------------------------------------
  /**
   * Is the field value of the current record null ?
   */
  fun isNull(): Boolean = isNull(block!!.currentRecord)

  /**
   * Returns the field value of the current record as an object
   */
  fun getObject(): Any? = getObject(block!!.currentRecord)

  /**
   * Returns the field value of the current record as a bigdecimal value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getFixed(): Fixed = getFixed(block!!.currentRecord)

  /**
   * Returns the field value of the current record as a boolean value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getBoolean(): Boolean? = getBoolean(block!!.currentRecord)

  /**
   * Returns the field value of the current record as a date value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getDate(): Date = getDate(block!!.currentRecord)

  /**
   * Returns the field value of the current record as a int value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getInt(): Int = getInt(block!!.currentRecord)

  /**
   * Returns the field value of given record as a date value.
   */
  fun getImage(): ByteArray = getImage(block!!.currentRecord)

  /**
   * Returns the field value of the current record as a month value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getMonth(): Month = getMonth(block!!.currentRecord)

  /**
   * Returns the field value of the current record as a string value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getString(): String = getString(block!!.currentRecord)

  /**
   * Returns the field value of the current record as a time value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getTime(): Time = getTime(block!!.currentRecord)

  /**
   * Returns the field value of the current record as a week value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getWeek(): Week = getWeek(block!!.currentRecord)

  /**
   * Returns the field value of the current record as a timestamp value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getTimestamp(): Timestamp = getTimestamp(block!!.currentRecord)

  /**
   * Returns the field value of the current record as a time value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getColor(): Color = getColor(block!!.currentRecord)

  /**
   * Returns the display representation of field value of the current record.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getText(): String? = getText(block!!.currentRecord)

  /**
   * Returns the SQL representation of field value of the current record.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getSql(): String? = getSql(block!!.currentRecord)

  /**
   * Is the field value of given record null ?
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun isNull(r: Int): Boolean  = alias?.isNull(0) ?: if (hasTrigger(VConstants.TRG_VALUE)) {
    callSafeTrigger(VConstants.TRG_VALUE) == null
  } else isNullImpl(r)

  /**
   * Is the field value of given record null ?
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  abstract fun isNullImpl(r: Int): Boolean

  /**
   * Returns the field value of the current record as an object
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getObject(r: Int): Any? = alias?.getObject(0) ?: if (hasTrigger(VConstants.TRG_VALUE)) {
    callSafeTrigger(VConstants.TRG_VALUE)
  } else getObjectImpl(r)

  /**
   * Returns the field value of the current record as an object
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  abstract fun getObjectImpl(r: Int): Any?

  /**
   * Returns the field value of given record as a bigdecimal value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun getFixed(r: Int): Fixed {
    throw InconsistencyException()
  }

  /**
   * Returns the field value of given record as a boolean value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun getBoolean(r: Int): Boolean? {
    throw InconsistencyException()
  }

  /**
   * Returns the field value of given record as a date value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun getDate(r: Int): Date {
    throw InconsistencyException()
  }

  /**
   * Returns the field value of given record as a month value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getMonth(r: Int): Month {
    throw InconsistencyException()
  }

  /**
   * Returns the field value of given record as a week value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getWeek(r: Int): Week {
    throw InconsistencyException()
  }

  /**
   * Returns the field value of given record as a int value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun getInt(r: Int): Int {
    throw InconsistencyException()
  }

  /**
   * Returns the field value of given record as a date value.
   */
  fun getImage(r: Int): ByteArray {
    throw InconsistencyException()
  }

  /**
   * Returns the field value of given record as a string value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun getString(r: Int): String {
    throw InconsistencyException()
  }

  /**
   * Returns the field value of given record as a time value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getTime(r: Int): Time {
    throw InconsistencyException()
  }

  /**
   * Returns the field value of given record as a timestamp value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getTimestamp(r: Int): Timestamp {
    throw InconsistencyException()
  }

  /**
   * Returns the field value of given record as a color value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun getColor(r: Int): Color {
    throw InconsistencyException()
  }

  /**
   * Returns the display representation of field value of given record.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getText(r: Int): String? {
    if (alias != null) {
      return alias!!.getText(0)
    }
    if (hasTrigger(VConstants.TRG_VALUE)) {
      val value = callSafeTrigger(VConstants.TRG_VALUE)
      val currentValue = getObjectImpl(r)
      if (value != currentValue) {
        // set Value only if necessary otherwise an endless loop
        // alternative solution: do this check in setChanged
        setObject(r, value)
      }
    }
    return getTextImpl(r)
  }

  abstract fun toText(o: Any): String?

  abstract fun toObject(s: String): Any?

  /**
   * Returns the display representation of field value of given record.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  abstract fun getTextImpl(r: Int?): String?

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getSql(r: Int): String? {
    if (alias != null) {
      return alias!!.getSql(0)
    }
    if (hasTrigger(VConstants.TRG_VALUE)) {
      setObject(r, callSafeTrigger(VConstants.TRG_VALUE))
    }
    return getSqlImpl(r)
  }

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  abstract fun getSqlImpl(r: Int): String

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun hasLargeObject(r: Int): Boolean = false

  /**
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun hasBinaryLargeObject(r: Int): Boolean {
    throw InconsistencyException("NO LOB WITH THIS FIELD $this")
  }

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun getLargeObject(r: Int): InputStream? {
    throw InconsistencyException("NO BLOB WITH THIS FIELD $this")
  }
  // ----------------------------------------------------------------------
  // FOREGROUND AND BACKGROUND COLOR MANAGEMENT
  // ----------------------------------------------------------------------
  /**
   * Sets the foreground and the background colors for the current record.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  fun setColor(foreground: VColor?, background: VColor?) {
    setColor(block!!.currentRecord, foreground, background)
  }

  /**
   * Sets the foreground and the background colors.
   * @param r The record number.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  fun setColor(r: Int, foreground: VColor?, background: VColor?) {
    var fireColorChanged: Boolean

    fireColorChanged = false
    if (this.foreground[r] == null && foreground != null
            || this.foreground[r] != null && !(this.foreground[r]!! == foreground)) {
      this.foreground[r] = foreground
      fireColorChanged = true
    }
    if (this.background[r] == null && background != null
            || this.background[r] != null && !(this.background[r]!! == foreground)) {
      this.background[r] = background
      fireColorChanged = true
    }
    if (fireColorChanged) {
      fireColorChanged(r)
    }
  }

  /**
   * Resets the foreground and the background colors the current record.
   */
  fun resetColor(r: Int = block!!.currentRecord) {
    setColor(r, null, null)
  }

  /**
   * Update the foreground and the background colors.
   * @param r The record number.
   */
  fun updateColor(r: Int) {
    setColor(r, getForeground(r), getBackground(r))
  }

  fun getForeground(r: Int): VColor = foreground[r]!!

  fun getBackground(r: Int): VColor = background[r]!!

  // ----------------------------------------------------------------------
  // DRAG AND DROP HANDLIN
  // ----------------------------------------------------------------------
  /**
   * Call before a drop starts on this field.
   * @throws VException Visual errors occurring.
   */
  fun onBeforeDrop() {
    if (hasTrigger(VConstants.TRG_PREDROP)) {
      callTrigger(VConstants.TRG_PREDROP)
    }
  }

  /**
   * Called after a drop ends on this field.
   * @throws VException Visual errors occurring.
   */
  fun onAfterDrop() {
    if (hasTrigger(VConstants.TRG_POSTDROP)) {
      callTrigger(VConstants.TRG_POSTDROP)
    }
  }

  // ----------------------------------------------------------------------
  // UTILS
  // ----------------------------------------------------------------------
  /**
   * Copies the fields value of a record to another
   */
  abstract fun copyRecord(f: Int, t: Int)

  /**
   * Returns the containing block.
   */
  fun getForm(): VForm = block!!.form

  /**
   * Returns true if field is never displayed.
   */
  fun isInternal(): Boolean {
    return access[VConstants.MOD_QUERY] == VConstants.ACS_HIDDEN
            && access[VConstants.MOD_INSERT] == VConstants.ACS_HIDDEN
            && access[VConstants.MOD_UPDATE] == VConstants.ACS_HIDDEN
  }

  // ----------------------------------------------------------------------
  // PROTECTED UTILS
  // ----------------------------------------------------------------------
  /**
   * trails (backups) the record if called in a transaction and restore it
   * if the transaction is aborted.
   */
  protected fun trail(r: Int) {
    TODO()
  }

  /**
   * Marks the field changed, trails the record if necessary
   */
  protected fun setChanged(r: Int) {
    if (!isTransient() && !hasTrigger(VConstants.TRG_VALUE) && alias == null) {
      block!!.setRecordChanged(r, true)
    } else {
      block!!.updateAccess(r)
    }
    changed = true
    changedUI = false
    fireValueChanged(r)
  }

  /**
   * Marks the field changed, trails the record if necessary
   */
  fun setChanged(changed: Boolean) {
    TODO()
  }

  /**
   * Checks that field value exists in list
   */
  private fun checkList() {
    TODO()
  }

  /**
   * Checks that field value exists in list
   * !!! TRY TO MERGE WITH checkList ???
   */
  fun getListID(): Int {
    TODO()
  }

  private fun displayQueryList(queryText: String, columns: Array<VListColumn>): Any? {
    TODO()
  }

  /**
   * Checks that field value exists in list
   */
  protected fun selectFromList(gotoNextField: Boolean) {
    TODO()
  }

  /**
   * Checks that field value exists in list
   */
  protected open fun enumerateValue(desc: Boolean) {
    TODO()
  }

  /**
   * Returns the suggestion list of this field.
   * @param query The field content to be taken into consideration when looking for suggestions.
   * @return An object array that contains two arrays : The displayed values of the suggestions
   * and the object values of the suggestions.
   * @throws VException Visual exceptions related to database errors.
   */
  open fun getSuggestions(query: String?): Array<Array<String?>>? {
    TODO()
  }

  // ---------------------------------------------------------------------
  // IMPLEMENTATION
  // ---------------------------------------------------------------------
  /**
   * Returns the list table.
   */
  private fun evalListTable(): String {
    return try {
      block!!.executeObjectTrigger(list!!.table) as String
    } catch (e: VException) {
      throw InconsistencyException()
    }
  }

  /**
   * Calls trigger for given event.
   */
  fun callTrigger(event: Int): Any = {
    TODO()
  }

  /**
   * Calls trigger for given event.
   */
  fun callProtectedTrigger(event: Int): Any  {
    TODO()
  }

  /**
   * return if there is trigger associated with event
   */
  fun hasTrigger(event: Int): Boolean  {
    TODO()
  }

  /**
   * Calls trigger for given event.
   */
  private fun callSafeTrigger(event: Int): Any {
    return try {
      callTrigger(event)
    } catch (ve: VException) {
      throw VRuntimeException(ve)
    }
  }

  // ----------------------------------------------------------------------
  // F2
  // ----------------------------------------------------------------------
  /**
   * // TRY TO MERGE WITH queryList !!!!
   * !!!graf 030729: was ist das ???
   */
  fun setValueID(id: Int) {
    TODO()
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------
  fun helpOnField(help: VHelpGenerator) {
    TODO()
  }

  /**
   * return the name of this field
   */
  abstract fun getTypeInformation(): String

  /**
   * return the name of this field
   */
  abstract fun getTypeName(): String

  /**
   *
   */
  protected open fun helpOnType(help: VHelpGenerator) {
    helpOnType(help, null)
  }

  /**
   *
   */
  protected fun helpOnType(help: VHelpGenerator, names: Array<String>?) {
    TODO()
  }

  /**
   * prepare a snapshot
   *
   * @param     fieldPos        position of this field within block visible fields
   */
  fun prepareSnapshot(fieldPos: Int, active: Boolean) {
    // !!! TO DO
  }

  override fun toString(): String {
    TODO()
  }

  // ----------------------------------------------------------------------
  // LISTENER
  // ----------------------------------------------------------------------
  fun addFieldListener(fl: FieldListener) {
    if (!hasListener) {
      hasListener = true
      if (fieldListener == null) {
        fieldListener = EventListenerList()
      }
    }
    fieldListener!!.add(FieldListener::class.java, fl)
  }

  fun removeFieldListener(fl: FieldListener?) {
    fieldListener!!.remove(FieldListener::class.java, fl)
    if (fieldListener!!.listenerCount == 0) {
      hasListener = false
    }
  }

  fun addFieldChangeListener(fl: FieldChangeListener) {
    if (!hasListener) {
      hasListener = true
      if (fieldListener == null) {
        fieldListener = EventListenerList()
      }
    }
    fieldListener!!.add(FieldChangeListener::class.java, fl)
  }

  fun removeFieldChangeListener(fl: FieldChangeListener) {
    fieldListener!!.remove(FieldChangeListener::class.java, fl)
    if (fieldListener!!.listenerCount == 0) {
      hasListener = false
    }
  }

  fun fireValueChanged(r: Int) {
    if (hasListener) {
      val listeners = fieldListener!!.listenerList
      var i = listeners.size - 2

      while (i >= 0) {
        if (listeners[i] === FieldChangeListener::class.java) {
          (listeners[i + 1] as FieldChangeListener).valueChanged(r)
        }
        i -= 2
      }
    }
  }

  fun fireSearchOperatorChanged() {
    if (hasListener) {
      val listeners = fieldListener!!.listenerList
      var i = listeners.size - 2

      while (i >= 0) {
        if (listeners[i] === FieldChangeListener::class.java) {
          (listeners[i + 1] as FieldChangeListener).searchOperatorChanged()
        }
        i -= 2
      }
    }
  }

  fun fireLabelChanged() {
    if (hasListener) {
      val listeners = fieldListener!!.listenerList
      var i = listeners.size - 2

      while (i >= 0) {
        if (listeners[i] === FieldChangeListener::class.java) {
          (listeners[i + 1] as FieldChangeListener).labelChanged()
        }
        i -= 2
      }
    }
  }

  fun fireAccessChanged(r: Int) {
    if (hasListener) {
      val listeners = fieldListener!!.listenerList
      var i = listeners.size - 2

      while (i >= 0) {
        if (listeners[i] === FieldChangeListener::class.java) {
          (listeners[i + 1] as FieldChangeListener).accessChanged(r)
        }
        i -= 2
      }
    }
  }

  fun fireColorChanged(r: Int) {
    if (hasListener) {
      val listeners = fieldListener!!.listenerList
      var i = listeners.size - 2

      while (i >= 0) {
        if (listeners[i] === FieldChangeListener::class.java) {
          (listeners[i + 1] as FieldChangeListener).colorChanged(r)
        }
        i -= 2
      }
    }
  }

  fun fireEntered() {
    if (hasListener) {
      val listeners = fieldListener!!.listenerList
      var i = listeners.size - 2

      while (i >= 0) {
        if (listeners[i] === FieldListener::class.java) {
          (listeners[i + 1] as FieldListener).enter()
        }
        i -= 2
      }
    }
  }

  fun fireLeaved() {
    if (hasListener) {
      val listeners = fieldListener!!.listenerList
      var i = listeners.size - 2

      while (i >= 0) {
        if (listeners[i] === FieldListener::class.java) {
          (listeners[i + 1] as FieldListener).leave()
        }
        i -= 2
      }
    }
  }

  fun requestFocus() {
    if (hasListener) {
      var consumed = false
      val listeners = fieldListener!!.listenerList
      var i = listeners.size - 2

      while (i >= 0 && !consumed) {
        if (listeners[i] === FieldListener::class.java) {
          consumed = (listeners[i + 1] as FieldListener).requestFocus()
        }
        i -= 2
      }
    }
  }

  fun getDisplayedValue(trim: Boolean): Any? {
    var value: Any? = null
    if (hasListener) {
      val listeners = fieldListener!!.listenerList
      var i = listeners.size - 2

      while (i >= 0 && value == null) {
        if (listeners[i] === FieldListener::class.java) {
          value = (listeners[i + 1] as FieldListener).getDisplayedValue(trim)
        }
        i -= 2
      }
    }
    return value
  }

  fun loadItem(item: Int) {
    if (hasListener) {
      var loaded = false
      val listeners = fieldListener!!.listenerList
      var i = listeners.size - 2

      while (i >= 0 && !loaded) {
        if (listeners[i] === FieldListener::class.java) {
          loaded = (listeners[i + 1] as FieldListener).loadItem(item)
        }
        i -= 2
      }
    }
  }

  fun modelNeedUpdate() {
    if (hasListener) {
      val listeners = fieldListener!!.listenerList
      var i = listeners.size - 2

      while (i >= 0) {
        if (listeners[i] === FieldListener::class.java) {
          (listeners[i + 1] as FieldListener).updateModel()
        }
        i -= 2
      }
    }
  }

  // ----------------------------------------------------------------------
  // !!! Remove after merging the new MVC
  // ----------------------------------------------------------------------

  @Deprecated("")
  inner class Compatible {
    fun getDisplayedValue(trim: Boolean): Any? {
      return this@VField.getDisplayedValue(trim)
    }
  }

  @Deprecated("")
  fun getUI(): Compatible {
    return Compatible()
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  /**
   * The width of a field is the max number of character needed to display
   * any value
   * @return    the width of this field
   */
  var width = 0// max # of chars per line
  protected set

  /**
   * The height of a field is the max number of line needed to display
   * any value
   * @return    the width of this field
   */
  var height  = 0 // max # of lines
    protected set

  private lateinit var access : IntArray // access in each mode

  private var priority = 0  // order in select results

  private var indices = 0  // bitset of unique indices

  /**
   * The name of the field is the ident in the galite language
   * @return    the name of this field
   */
  var name: String? = null    // field name (for dumps)
    private set
  var label : String? = null // field label
    set(label) {
      field = label
      fireLabelChanged()
    }

  /**
   * Returns the option of this field
   */
  var options = 0 // options


  /**
   * The tooltip of the field is a small sentence that describe usage of the field
   * It is the first line of the field help
   * @return    the help of this field
   */
  var toolTip : String? = null // help text
    private set

  private var index = 0 // The position in parent field array

  /**
   * Returns the alignment
   */
  var align = 0   // field alignment
    private set

  var posInArray = 0   // position in array of fields
    private set

  var list: VList? = null   // list
    private set

  /**
   * Returns the containing block.
   */
  var block: VBlock? = null // containing block
    set(block) {
      TODO()
    }

  private var columns: Array<VColumn?>? = null // columns in block's tables

  //  private   VFieldUI        ui;             // The UI manager
  private var alias: VField? = null // The alias field

  // changed?
  var changed = false // changed by user / changes are done in the model
    private set

  var changedUI = false // changed by user / changes are in the ui -> update model

  // UPDATE model before doing anything
  var border = 0

  // dynamic data
  private var searchOperator // search operator
          = 0
  private lateinit var dynAccess: IntArray // dynamic access

  // ####
  private var fieldListener: EventListenerList? = null

  // if there is only the model and no gui
  // all the job use less memory and are faster
  private var hasListener = false
  var position: VPosition? = null
    private set
  lateinit var cmd: Array<VCommand>
  private lateinit var foreground: Array<VColor?> // foreground colors for this field.
  private lateinit var background: Array<VColor?> // background colors for this field.

  companion object {

    /**
     * @return a String with the current thread information for debugging
     */
    private fun threadInfo(): String {
      return """
     Thread: ${Thread.currentThread()}
     
     """.trimIndent()
    }

    const val MDL_FLD_COLOR = 1
    const val MDL_FLD_IMAGE = 2
    const val MDL_FLD_EDITOR = 3
    const val MDL_FLD_TEXT = 4
    const val MDL_FLD_ACTOR = 5
  }

  init {
    setDimension(width, height)
  }
}
