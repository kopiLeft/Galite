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
import java.sql.SQLException

import javax.swing.event.EventListenerList

import kotlin.reflect.KClass

import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.EqOp
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.GreaterEqOp
import org.jetbrains.exposed.sql.GreaterOp
import org.jetbrains.exposed.sql.LessEqOp
import org.jetbrains.exposed.sql.LessOp
import org.jetbrains.exposed.sql.LikeOp
import org.jetbrains.exposed.sql.NeqOp
import org.jetbrains.exposed.sql.NotLikeOp
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.QueryAlias
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.intLiteral
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.stringLiteral
import org.jetbrains.exposed.sql.substring
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upperCase
import org.kopi.galite.base.UComponent
import org.kopi.galite.db.Query
import org.kopi.galite.db.Utils
import org.kopi.galite.form.dsl.Access
import org.kopi.galite.l10n.BlockLocalizer
import org.kopi.galite.l10n.FieldLocalizer
import org.kopi.galite.list.VColumn
import org.kopi.galite.list.VList
import org.kopi.galite.list.VListColumn
import org.kopi.galite.type.Date
import org.kopi.galite.type.Decimal
import org.kopi.galite.type.Month
import org.kopi.galite.type.Time
import org.kopi.galite.type.Timestamp
import org.kopi.galite.type.Week
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.Module
import org.kopi.galite.visual.VColor
import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VExecFailedException
import org.kopi.galite.visual.VModel
import org.kopi.galite.visual.VRuntimeException
import org.kopi.galite.visual.VlibProperties

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
    this.width = width
    this.height = height
  }

  /**
   * set information on the field.
   */
  fun setInfo(name: String,
              index: Int,
              posInArray: Int,
              options: Int,
              access: IntArray,
              list: VList?,
              columns: Array<VColumn?>?,
              indices: Int,
              priority: Int,
              commands: Array<VCommand>?,
              pos: VPosition?,
              align: Int,
              alias: VField?) {
    this.name = name
    this.index = index
    this.posInArray = posInArray
    this.options = options
    this.access = access
    this.list = list
    this.columns = columns
    if (columns == null) {
      this.columns = arrayOfNulls(0)
    }
    this.indices = indices
    this.priority = priority
    this.align = align
    if (this is VFixnumField || this is VIntegerField) {
      // move it to compiler !!!
      this.align = VConstants.ALG_RIGHT
    }
    position = pos
    command = commands
    this.alias = alias
    alias?.addFieldChangeListener(object : FieldChangeListener {
      override fun labelChanged() {}
      override fun searchOperatorChanged() {}
      override fun valueChanged(r: Int) {
        fireValueChanged(r)
      }

      override fun accessChanged(r: Int) {}
      override fun colorChanged(r: Int) {}
    })
  }

  @Deprecated("use fetchColumn(table: Table)")
  fun fetchColumn(table: Int): Int {
    if (columns != null) {
      for (i in columns!!.indices) {
        if (columns!![i]!!.getTable() == table) {
          return i
        }
      }
    }
    return -1
  }

  fun fetchColumn(table: Table): Int {
    if (columns != null) {
      for (i in columns!!.indices) {
        if (columns!![i]!!.getTable_() == table) {
          return i
        }
      }
    }
    return -1
  }

  /**
   * The position of the label (left / top)
   */
  fun setLabelPos(pos: Int) {
    fireLabelChanged()
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
  open fun getAutocompleteLength(): Int = if (list != null) list!!.autocompleteLength else 0


  /**
   * Returns the auto complete type.
   * @return The auto complete type.
   */
  open fun getAutocompleteType(): Int = if (list != null) list!!.autocompleteType else VList.AUTOCOMPLETE_NONE


  /**
   * return true if this field implements "enumerateValue"
   */
  open fun hasNextPreviousEntry(): Boolean = list != null

  fun hasNullableCols(): Boolean = columns!!.find { it!!.nullable } != null

  /**
   * Returns true if it is a numeric field.
   */
  open fun isNumeric(): Boolean = false

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------

  /**
   * Localizes this field
   *
   * @param     parent         the caller localizer
   */
  fun localize(parent: BlockLocalizer) {
    val loc = parent.getFieldLocalizer(name)

    label = loc.getLabel()
    toolTip = loc.getHelp()
    if (list != null) {
      list?.localize(loc.manager)
    }

    // field type specific localizations
    localize(loc)
  }

  /**
   * Localizes this field
   *
   * @param     loc         the caller localizer
   */
  internal open fun localize(loc: FieldLocalizer) {
    // by default nothing to do
  }

  // ----------------------------------------------------------------------
  // PUBLIC COMMANDS
  // ----------------------------------------------------------------------

  // called if the in a chart the line changes, but it is still visible
  fun updateText() {
    if (isChangedUI) {
      modelNeedUpdate()
    }
  }

  /**
   * Validate the field, ie: get the last displayed value, check it and check mustfill
   */
  fun validate() {
    if (isChanged) {
      if (isChangedUI) {
        modelNeedUpdate()
      }
      callTrigger(VConstants.TRG_PREVAL)
      if (!isNull(block!!.activeRecord)) {
        callTrigger(VConstants.TRG_FORMAT)
      }
      checkList()
      try {
        if (!isNull(block!!.activeRecord)) {
          callTrigger(VConstants.TRG_VALFLD)
        }
        callTrigger(VConstants.TRG_POSTCHG)
      } catch (e: VFieldException) {
        e.resetValue()
        throw e
      }
      isChanged = false // !!! check
      isChangedUI = false
    }
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
  abstract fun checkType(rec: Int, s: Any?)

  /**
   * verify that value is valid (on exit)
   *
   * @param     s               the object to check
   * @exception VException      an exception is raised if text is bad
   */
  open fun checkType(s: Any?) {
    checkType(block!!.activeRecord, s!!)
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
    isChanged = true
    isChangedUI = true
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

  open fun fillField(handler: PredefinedValueHandler?): Boolean = handler?.selectDefaultValue() ?: false

  // ----------------------------------------------------------------------
  // PROTECTED ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * @return a list column for list
   */
  internal abstract fun getListColumn(): VListColumn?

  // ----------------------------------------------------------------------
  // NAVIGATING
  // ----------------------------------------------------------------------

  /**
   * enter a field
   */
  fun enter() {
    assert(block === getForm().getActiveBlock()) {
      threadInfo() + "field : " + name + " block : " + block!!.name +
              " active block : " + getForm().getActiveBlock()!!.name
    }
    assert(block!!.activeRecord != -1) { threadInfo() + "current record = " + block!!.activeRecord }
    assert(block!!.activeField == null) { threadInfo() + "current field: " + block!!.activeField }
    block!!.activeField = this
    isChanged = false
    fireEntered()
    try {
      callTrigger(VConstants.TRG_PREFLD)
    } catch (e: VException) {
      throw InconsistencyException(e)
    }
  }

  /**
   * when leaving field, if text is okay, set value in record
   * @exception VException      an exception is raised if text is bad
   */
  fun leave(check: Boolean) {
    assert(this === block!!.activeField) { threadInfo() + "current field: " + block!!.activeField }
    try {
      if (check && isChanged) {
        if (isChangedUI && hasListener) {
          checkType(getDisplayedValue(true))
        }
        callTrigger(VConstants.TRG_PREVAL)
        if (!isNull(block!!.activeRecord)) {
          callTrigger(VConstants.TRG_FORMAT)
        }
        checkList()
        try {
          if (!isNull(block!!.activeRecord)) {
            callTrigger(VConstants.TRG_VALFLD)
          }
          callTrigger(VConstants.TRG_POSTCHG)
        } catch (e: VFieldException) {
          e.resetValue()
          throw e
        }
      } else if (getForm().setTextOnFieldLeave()) {
        if (isChanged && isChangedUI && hasListener) {
          checkType(getDisplayedValue(true))
        }
      }
    } catch (e: VException) {
      throw e
    }
    isChanged = false
    isChangedUI = false
    callTrigger(VConstants.TRG_POSTFLD)
    block!!.activeField = null
    fireLeaved()
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
    for (i in 0 until block!!.bufferSize) {
      setAccess(i, value)
    }
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
  fun getDefaultAccess(): Int = access[block!!.getMode()]

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
    updateAccess(block!!.activeRecord)
  }

  fun updateAccess(current: Int = block!!.currentRecord) {
    if (isInternal()) {
      // internal fields are always hidden
      // there no need to update the field
      // (also necessary for performance)
      return
    }

    val defaultAccess = getDefaultAccess()
    var accessTemp = defaultAccess //dynAccess;

    if (current != -1) {
      if (!block!!.isRecordInsertAllowed(current)) {
        accessTemp = VConstants.ACS_SKIPPED
      } else if (hasTrigger(VConstants.TRG_FLDACCESS)) {
        // evaluate ACCESS-Trigger
        val oldRow = block!!.activeRecord
        val old = block!!.activeField

        // used by callTrigger
        block!!.activeRecord = current
        try {
          block!!.activeField = this
          accessTemp = (callTrigger(VConstants.TRG_FLDACCESS) as Access).value
          block!!.activeField = old
        } catch (e: Exception) {
          e.printStackTrace()
          block!!.activeField = old
        }
        block!!.activeRecord = oldRow
      }
    }
    if (defaultAccess < accessTemp) {
      accessTemp = defaultAccess
    }
    if (current == -1) {
      setAccess(accessTemp)
    } else {
      setAccess(current, accessTemp)
    }
  }

  fun updateModeAccess() {
    // TOO SIMPLE (ACCESS TRIGGER IGNORED)
    for (i in 0 until block!!.bufferSize) {
      updateAccess(i)
    }
  }

  // ----------------------------------------------------------------------
  // RESET TO DEFAULT
  // ----------------------------------------------------------------------

  /**
   * Sets default values
   */
  fun setDefault() {
    if (isNull(block!!.activeRecord)) {
      try {
        callTrigger(VConstants.TRG_DEFAULT)
      } catch (e: VException) {
        throw InconsistencyException() // !!! NO, Just a VExc...
      }
    }
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
  @Deprecated("use lookupColumn(corr: Table)")
  fun lookupColumn(corr: Int): Column<*>? = columns!!.find { corr == it!!.getTable() }?.column

  /**
   * Returns the column name in the table with specified correlation.
   * returns null if the field has no access to this table.
   */
  fun lookupColumn(corr: Table): Column<*>? = columns!!.find { corr == it!!.getTable_() }?.column

  /**
   * Returns true if the column is a key of the table with specified correlation.
   */
  fun isLookupKey(corr: Int): Boolean = columns!!.find { corr == it!!.getTable() }?.key ?: false

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
    return if (isNull(block!!.activeRecord)) {
      when (getSearchOperator()) {
        VConstants.SOP_EQ -> VConstants.STY_NO_COND
        VConstants.SOP_NE -> VConstants.STY_MANY
        else -> VConstants.STY_EXACT
      }
    } else {
      val buffer = getSql(block!!.activeRecord)

      if (buffer.toString().indexOf('*') == -1) {
        if (getSearchOperator() == VConstants.SOP_EQ) VConstants.STY_EXACT else VConstants.STY_MANY
      } else {
        VConstants.STY_MANY
      }
    }
  }

  open fun <T> getSearchCondition(column: ExpressionWithColumnType<T>): Op<Boolean>? {
    if (isNull(block!!.activeRecord)) {
      return when (getSearchOperator()) {
        VConstants.SOP_EQ -> null
        VConstants.SOP_NE -> Op.build { column.isNotNull() }
        else -> Op.build { column.isNull() }
      }
    } else {
      val operand = getSql(block!!.activeRecord)

      if (operand is String && operand.indexOf('*') != -1) {
        val stringOperand = when (getSearchOperator()) {
          VConstants.SOP_EQ, VConstants.SOP_NE -> operand.replace('*', '%')
          VConstants.SOP_GE, VConstants.SOP_GT -> {
            // remove everything after at '*'
            operand.substring(0, operand.indexOf('*'))
          }
          VConstants.SOP_LE, VConstants.SOP_LT -> {
            // replace substring starting at '*' by highest (ascii) char
            operand.substring(0, operand.indexOf('*')) + "\u00ff"
          }
          else -> throw InconsistencyException()
        }

        val stringOperandLiteral = when (options and VConstants.FDO_SEARCH_MASK) {
          VConstants.FDO_SEARCH_NONE -> stringLiteral(stringOperand)
          VConstants.FDO_SEARCH_UPPER -> stringLiteral(stringOperand).upperCase()
          VConstants.FDO_SEARCH_LOWER -> stringLiteral(stringOperand).lowerCase()
          else -> throw InconsistencyException("FATAL ERROR: bad search code: $options")
        }

        return when (getSearchOperator()) {
          VConstants.SOP_EQ -> Op.build {
            LikeOp(column, stringOperandLiteral)
          }
          VConstants.SOP_NE -> Op.build {
            NotLikeOp(column, stringOperandLiteral)
          }
          VConstants.SOP_GE -> Op.build {
            GreaterEqOp(column, stringOperandLiteral)
          }
          VConstants.SOP_GT -> Op.build {
            GreaterOp(column, stringOperandLiteral)
          }
          VConstants.SOP_LE -> Op.build {
            LessEqOp(column, stringOperandLiteral)
          }
          VConstants.SOP_LT -> Op.build {
            LessOp(column, stringOperandLiteral)
          }
          else -> throw InconsistencyException()
        }
      } else {
        return when (getSearchOperator()) {
          VConstants.SOP_EQ -> Op.build {
            EqOp(column, column.wrap(operand))
          }
          VConstants.SOP_NE -> Op.build {
            NeqOp(column, column.wrap(operand))
          }
          VConstants.SOP_GE -> Op.build {
            GreaterEqOp(column, column.wrap(operand))
          }
          VConstants.SOP_GT -> Op.build {
            GreaterOp(column, column.wrap(operand))
          }
          VConstants.SOP_LE -> Op.build {
            LessEqOp(column, column.wrap(operand))
          }
          VConstants.SOP_LT -> Op.build {
            LessOp(column, column.wrap(operand))
          }
          else -> throw InconsistencyException()
        }
      }
    }
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
  open fun clear(r: Int) {
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
  fun setDecimal(v: Decimal?) {
    setDecimal(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a boolean value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setBoolean(v: Boolean?) {
    setBoolean(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a date value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setDate(v: Date?) {
    setDate(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a month value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setMonth(v: Month?) {
    setMonth(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a int value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setInt(v: Int?) {
    setInt(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setObject(v: Any?) {
    setObject(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a string value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setString(v: String?) {
    setString(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of given record to a date value.
   */
  fun setImage(v: ByteArray?) {
    setImage(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a time value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setTime(v: Time?) {
    setTime(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a week value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setWeek(v: Week?) {
    setWeek(block!!.currentRecord, v)
  }

  /**
   * Sets the field value of the current record to a timestamp value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun setTimestamp(v: Timestamp?) {
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
  open fun setDecimal(r: Int, v: Decimal?) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record to a boolean value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun setBoolean(r: Int, v: Boolean?) {
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
  open fun setMonth(r: Int, v: Month?) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record to a week value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun setWeek(r: Int, v: Week?) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record to a int value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun setInt(r: Int, v: Int?) {
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
  open fun setString(r: Int, v: String?) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record to a date value.
   */
  open fun setImage(r: Int, v: ByteArray?) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record to a time value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun setTime(r: Int, v: Time?) {
    throw InconsistencyException()
  }

  /**
   * Sets the field value of given record to a timestamp value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun setTimestamp(r: Int, v: Timestamp?) {
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
   * @param     result          the result row of the query holding the tuple
   * @param     column          the index of the column in the tuple
   */
  fun setQuery(result: ResultRow, column: Column<*>) {
    setQuery(block!!.currentRecord, result, column)
  }

  /**
   * Sets the field value of given record from a query tuple.
   * @param     record          the index of the record
   * @param     result          the result row of the query holding the tuple
   * @param     column          the index of the column in the tuple
   */
  fun setQuery(record: Int, result: ResultRow, column: Column<*>) {
    setObject(record, retrieveQuery_(result, column))
  }

  /**
   * Returns the specified tuple column as object of correct type for the field.
   * @param     query           the query holding the tuple
   * @param     column          the index of the column in the tuple
   */
  @Deprecated("use retrieveQuery_(result: ResultRow, column: Column<*>)")
  abstract fun retrieveQuery(query: Query, column: Int): Any?


  /**
   * TODO document! and add needed implementations
   */
  open fun retrieveQuery_(result: ResultRow, column: Column<*>): Any? = result[column]

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
  fun getDecimal(): Decimal = getDecimal(block!!.currentRecord)

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
  fun getInt(): Int? = getInt(block!!.currentRecord)

  /**
   * Returns the field value of given record as a date value.
   */
  fun getImage(): ByteArray? = getImage(block!!.currentRecord)

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
  fun getSql(): Any? = getSql(block!!.currentRecord)

  /**
   * Is the field value of given record null ?
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun isNull(r: Int): Boolean =
          alias?.isNull(0)
                  ?: if (hasTrigger(VConstants.TRG_VALUE)) {
                    callSafeTrigger(VConstants.TRG_VALUE) == null
                  } else {
                    isNullImpl(r)
                  }

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
  fun getObject(r: Int): Any? =
    alias?.getObject(0)
      ?: if (hasTrigger(VConstants.TRG_VALUE)) {
        callSafeTrigger(VConstants.TRG_VALUE)
      } else {
        getObjectImpl(r)
      }

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
  open fun getDecimal(r: Int): Decimal {
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
  open fun getMonth(r: Int): Month {
    throw InconsistencyException()
  }

  /**
   * Returns the field value of given record as a week value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun getWeek(r: Int): Week {
    throw InconsistencyException()
  }

  /**
   * Returns the field value of given record as a int value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun getInt(r: Int): Int? {
    throw InconsistencyException()
  }

  /**
   * Returns the field value of given record as a date value.
   */
  open fun getImage(r: Int): ByteArray? {
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
  open fun getTime(r: Int): Time {
    throw InconsistencyException()
  }

  /**
   * Returns the field value of given record as a timestamp value.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  open fun getTimestamp(r: Int): Timestamp {
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

  abstract fun toText(o: Any?): String?

  abstract fun toObject(s: String): Any?

  /**
   * Returns the display representation of field value of given record.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  abstract fun getTextImpl(r: Int): String?

  /**
   * Returns the SQL representation of field value of given record.
   * Warning:   This method will become inaccessible to users in next release
   *
   */
  fun getSql(r: Int): Any? {
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
  abstract fun getSqlImpl(r: Int): Any?

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
  open fun hasBinaryLargeObject(r: Int): Boolean {
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
            || this.foreground[r] != null && this.foreground[r]!! != foreground) {
      this.foreground[r] = foreground
      fireColorChanged = true
    }
    if (this.background[r] == null && background != null
            || this.background[r] != null && this.background[r]!! != foreground) {
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

  fun getForeground(r: Int): VColor? = foreground[r]

  fun getBackground(r: Int): VColor? = background[r]

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
    if (!isTransient() && !hasTrigger(VConstants.TRG_VALUE) && alias == null) {
      block!!.trailRecord(r)
    }
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
    isChanged = true
    isChangedUI = false
    fireValueChanged(r)
  }

  /**
   * Marks the field changed, trails the record if necessary
   */
  fun setChanged(changed: Boolean) {
    if (changed && block!!.activeRecord != -1) {
      block!!.setRecordChanged(block!!.activeRecord, true)
    }
    this.isChanged = changed
  }

  /**
   * Checks that field value exists in list
   */
  private fun checkList() {
    if (!getForm().forceCheckList()) {
      // Oracle doesn't force the value to be in the list
      return
    }

    if (isNull(block!!.activeRecord) || list == null) {
      return
    }
    val alreadyProtected: Boolean = getForm().inTransaction()

    if (this !is VStringField) {
      var exists = false

      try {
        try {
          val table = evalListTable()
          val column = table.resolveColumn(list!!.getColumn(0).column!!) as Column<Any?>

          val query = table.slice(intLiteral(1)).select { column eq getSql(block!!.activeRecord) }

          val transaction = TransactionManager.currentOrNull()
          if (transaction != null) {
            exists = !query.empty()
          } else {
            transaction {
              exists = !query.empty()
            }
          }

        } catch (e: SQLException) {
          if (alreadyProtected) {
            throw e
          }
        } catch (error: Error) {
          if (alreadyProtected) {
            throw error
          }
        } catch (rte: RuntimeException) {
          if (alreadyProtected) {
            throw rte
          }
        }
      } catch (e: Throwable) {
        throw VExecFailedException(e)
      }
      if (!exists) {
        throw VFieldException(this, MessageCode.getMessage("VIS-00001"))
      }
      return
    } else {
      var count = 0
      var result: String? = null
      val condition = getSql(block!!.activeRecord)

      if (condition.toString().indexOf('*') > 0) {
        return
      }
      try {
        try {
          val column = list!!.getColumn(0).column as Column<String>
          val query = evalListTable().slice(column).select {
            column.substring(1, getString(block!!.activeRecord).length) eq getString(block!!.activeRecord)
          }.orderBy(column)

          val transaction = TransactionManager.currentOrNull()
          if (transaction != null) {
            count = query.count().toInt()
            if (count > 0) result = query.first()[column]
            if (count > 2) count = 2
          } else {
            transaction {
              count = query.count().toInt()
              if (count > 0) result = query.first()[column]
              if (count > 2) count = 2
            }
          }
        } catch (e: SQLException) {
          if (alreadyProtected) {
            throw e
          }
        } catch (error: Error) {
          if (alreadyProtected) {
            throw error
          }
        } catch (rte: RuntimeException) {
          if (alreadyProtected) {
            throw rte
          }
        }
      } catch (e: Throwable) {
        throw VExecFailedException(e)
      }
      when (count) {
        0 -> throw VFieldException(this, MessageCode.getMessage("VIS-00001"))
        1 -> {
          if (result != getString(block!!.activeRecord)) {
            setString(block!!.activeRecord, result!!)
          }
          return
        }
        2 -> if (result == getString(block!!.activeRecord)) {
          return
        } else {
          var i = 0
          val columns = mutableListOf<Column<*>>()

          while (i < list!!.columnCount()) {
            val column = list!!.getColumn(i).column!!

            columns.add(column)
            i++
          }

          val column = list!!.getColumn(0).column as Column<String>
          val query = evalListTable().slice(columns).select {
            column.substring(1, condition.toString().length) eq condition.toString()
          }.orderBy(columns[0])

          result = displayQueryList(query, list!!.columns) as String?
          if (result == null) {
            throw VExecFailedException() // no message to display
          } else {
            setString(block!!.activeRecord, result)
            return
          }
        }
        else -> throw InconsistencyException(threadInfo() + "count = " + count)
      }
    }
  }

  /**
   * Checks that field value exists in list
   * !!! TODO: TRY TO MERGE WITH checkList ???
   */
  open fun getListID(): Int {
    val table = evalListTable()
    val column = table.resolveColumn(list!!.getColumn(0).column!!) as Column<Any?>
    val idColumn = table.columns.find { it.name == "ID" } as Column<Int>

    assert(!isNull(block!!.activeRecord)) { threadInfo() + " is null" }
    assert(list != null) { threadInfo() + "list ist not null" }
    var id = -1

    try {
      try {
        transaction {
          val query = table.slice(idColumn).select { column eq getSql(block!!.activeRecord) }

          if (!query.empty()) {
            id = query.first()[idColumn]
          }
        }
      } catch (e: SQLException) {
      }
    } catch (e: Throwable) {
      throw VExecFailedException(e)
    }
    if (id == -1) {
      throw VFieldException(this, MessageCode.getMessage("VIS-00001"))
    }
    return id
  }

  private fun displayQueryList(query: org.jetbrains.exposed.sql.Query, columns: Array<VListColumn?>): Any? {
    val columnsList = columns.map { vListColumn ->
      vListColumn!!.column
    }
    val MAX_LINE_COUNT = 1024
    val SKIP_FIRST_COLUMN = false
    val SHOW_SINGLE_ENTRY: Boolean
    val lines = Array(columns.size - if (SKIP_FIRST_COLUMN) 1 else 0) {
      arrayOfNulls<Any>(MAX_LINE_COUNT)
    }
    var lineCount = 0
    val newForm: VDictionary? = when {
      list!!.newForm != null -> {
        // OLD SYNTAX
        Module.getExecutable(list!!.newForm) as VDictionary?
      }
      list!!.action != null -> {
        // NEW SYNTAX
        list!!.action!!()
      }
      else -> {
        null // should never happen.
      }
    }

    SHOW_SINGLE_ENTRY = newForm != null
    try {
      try {
        transaction {
          lineCount = 0
          for (result in query) {
            if (lineCount >= MAX_LINE_COUNT - 1) {
              break
            }
            if (result[columnsList[0]!!] == null) {
              continue
            }
            var i = 0

            while (i < lines.size) {
              lines[i][lineCount] = result[columnsList[i + if (SKIP_FIRST_COLUMN) 1 else 0]!!]
              i += 1
            }
            lineCount += 1
          }
        }
      } catch (e: SQLException) {
      }
    } catch (e: Throwable) {
      throw VRuntimeException(e)
    }
    return if (lineCount == 0 && (newForm == null || !isNull(block!!.activeRecord))) {
      throw VFieldException(this, MessageCode.getMessage("VIS-00001"))
    } else {
      val selected = if (lineCount == 0 && newForm != null && isNull(block!!.activeRecord)) {
        newForm.add(getForm())
      } else {
        if (lineCount == MAX_LINE_COUNT - 1) {
          getForm().notice(MessageCode.getMessage("VIS-00028"))
        }
        if (lineCount == 1 && !SHOW_SINGLE_ENTRY) {
          0
        } else {
          val ld = VListDialog(columns, lines, lineCount, newForm)

          ld.selectFromDialog(getForm(), null, this)
        }
      }

      if (selected == -1) {
        throw VExecFailedException() // no message needed
      } else if (selected >= lineCount) {
        // new, retrieve it
        var result: Any?

        try {
          result = try {
            transaction {
              val table = evalListTable()
              val column = list!!.getColumn(0).column!!
              val idColumn = table.columns.find { it.name == "ID" } as Column<Int>

              table.slice(column).select { idColumn eq selected }.first()[column]
            }
          } catch (e: SQLException) {
          } catch (error: java.lang.Error) {
          } catch (rte: RuntimeException) {
          }
        } catch (e: Throwable) {
          throw VRuntimeException(e)
        }
        result
      } else {
        lines[0][selected]
      }
    }
  }

  /**
   * Checks that field value exists in list
   */
  @Suppress("UNCHECKED_CAST")
  internal fun selectFromList(gotoNextField: Boolean) {
    val columns = mutableListOf<Column<*>>()

    list!!.columns.forEach {
      columns.add(it!!.column!!)
    }

    val searchCondition = if (getSearchType() == VConstants.STY_MANY) {
      val expression = when (options and VConstants.FDO_SEARCH_MASK) {
        VConstants.FDO_SEARCH_NONE -> {
          list!!.getColumn(0).column as Column<Any?>
        }

        VConstants.FDO_SEARCH_UPPER -> {
          (list!!.getColumn(0).column as Column<String>).upperCase()
        }

        VConstants.FDO_SEARCH_LOWER -> {
          (list!!.getColumn(0).column as Column<String>).lowerCase()
        }

        else -> throw InconsistencyException("FATAL ERROR: bad search code: $options")
      }
      getSearchCondition(expression)
    } else {
      null
    }

    val query = if (searchCondition == null) {
      evalListTable().slice(columns).selectAll().orderBy(list!!.getColumn(0).column!!)
    } else {
      evalListTable().slice(columns).select(searchCondition).orderBy(list!!.getColumn(0).column!!)
    }

    val result = displayQueryList(query, list!!.columns)

    if (result == null) {
      throw VExecFailedException() // no message to display
    } else {
      setObject(block!!.activeRecord, result)
      if (gotoNextField) {
        block!!.gotoNextField()
      }
    }
  }

  /**
   * Checks that field value exists in list
   */
  internal open fun enumerateValue(desc: Boolean) {
    var value: Any? = null
    val table = evalListTable()
    val column = list!!.getColumn(0).column

    val condition = if (desc) {
      Op.build { list!!.getColumn(0).column!! greater getSql(block!!.activeRecord).toString() }
    } else {
      Op.build { list!!.getColumn(0).column!! less getSql(block!!.activeRecord).toString() }
    }

    val orderBy = if (desc) SortOrder.ASC else  SortOrder.DESC

    val query = if (isNull(block!!.activeRecord)) {
      table.slice(column!!).selectAll()
    } else {
      table.slice(column!!).select(condition).orderBy(column to orderBy)
    }

    while (true) {
      try {

        transaction {
          query.forEach {
            if (value == null) {
              value = it[column]
            }
          }
        }
        break
      } catch (e: SQLException) {
        try {
        } catch (abortEx: SQLException) {
          throw VExecFailedException(abortEx)
        }
      } catch (error: Error) {
        try {
        } catch (abortEx: Error) {
          throw VExecFailedException(abortEx)
        }
      } catch (rte: RuntimeException) {
        try {
        } catch (abortEx: RuntimeException) {
          throw VExecFailedException(abortEx)
        }
      }
    }
    if (value == null) {
      throw VExecFailedException() // no message to display
    } else {
      setObject(block!!.activeRecord, value)
    }
  }

  /**
   * Returns the suggestion list of this field.
   * @param query The field content to be taken into consideration when looking for suggestions.
   * @return An object array that contains two arrays : The displayed values of the suggestions
   * and the object values of the suggestions.
   * @throws VException Visual exceptions related to database errors.
   */
  open fun getSuggestions(query: String?): Array<Array<String?>>? {
    return if (query == null || getAutocompleteType() == VList.AUTOCOMPLETE_NONE) {
      null
    } else {
      val table = evalListTable()
      val columns = mutableListOf<Column<*>>()
      val suggestions= mutableListOf<Array<String?>>()
      val condition = (list!!.getColumn(0).column as Column<String>).lowerCase()

      list!!.columns.forEach {
        columns.add(it!!.column!!)
      }

      val cond = when (getAutocompleteType()) {
        VList.AUTOCOMPLETE_CONTAINS -> {
          Op.build { condition like Utils.toSql("%" + query.toLowerCase() + "%") }
        }
        VList.AUTOCOMPLETE_STARTSWITH -> {
          Op.build { condition like Utils.toSql(query.toLowerCase() + "%") }
        }
        else -> {
          Op.build { condition eq Utils.toSql(query.toString()) }
        }
      }

      val query = table.slice(columns).select(cond).orderBy(columns[0])

      while (true) {
        try {
          transaction {
              query.forEach {
                val columnsList = mutableListOf<String>()

                list!!.columns.forEach { column ->
                  columnsList.add(column!!.formatObject(it[column.column!!]) as String)
                }
                suggestions.add(columnsList.toTypedArray())
              }
          }
          break
        } catch (e: SQLException) {
          try {
          } catch (abortEx: SQLException) {
            throw VExecFailedException(abortEx)
          }
        } catch (error: Error) {
          try {
          } catch (abortEx: Error) {
            throw VExecFailedException(abortEx)
          }
        } catch (rte: RuntimeException) {
          try {
          } catch (abortEx: RuntimeException) {
            throw VExecFailedException(abortEx)
          }
        }
      }
      suggestions.toTypedArray()
    }
  }

  // ---------------------------------------------------------------------
  // IMPLEMENTATION
  // ---------------------------------------------------------------------
  /**
   * Returns the list table.
   */
  private fun evalListTable(): ColumnSet {
    return try {
      list!!.table
    } catch (e: VException) {
      throw InconsistencyException()
    }
  }

  /**
   * Calls trigger for given event.
   */
  fun callTrigger(event: Int): Any? = block!!.callTrigger(event, index + 1)

  /**
   * Calls trigger for given event.
   */
  fun callProtectedTrigger(event: Int): Any? = block!!.callProtectedTrigger(event, index + 1)

  /**
   * return if there is trigger associated with event
   */
  fun hasTrigger(event: Int): Boolean = block!!.hasTrigger(event, index + 1)

  /**
   * Calls trigger for given event.
   */
  private fun callSafeTrigger(event: Int): Any? {
    return try {
      callTrigger(event)
    } catch (ve: VException) {
      throw VRuntimeException(ve)
    }
  }

  @Suppress("UNCHECKED_CAST")
  fun setValueID(id: Int) {
    val result = try {
      transaction {
        val table = evalListTable()
        val idColumn = table.columns.find { it.name == "ID" } as Column<Int>
        val firstRecord = table.slice(table.resolveColumn(list!!.getColumn(0).column!!)).select {
          idColumn eq id
        }.firstOrNull()

        firstRecord?.get(idColumn)
      }
    } catch (e: Throwable) {
      throw VRuntimeException(e)
    }
    setObject(block!!.activeRecord, result)
    isChanged = true // if you edit the value it's like if you change it
  }

  /**
   * Finds and returns the column in this [ColumnSet] corresponding to the [column] from the original table
   *
   * @param column The column in the original table
   */
  private fun ColumnSet.resolveColumn(column: Column<*>): Column<*> {
    return when (this) {
      is Table -> {
        column
      }
      is QueryAlias -> {
        get(column)
      }
      is Alias<*> -> {
        get(column)
      }
      else -> {
        columns.single { it.name == column.name }
      }
    }
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------

  fun helpOnField(help: VHelpGenerator) {
    var lab = label

    if (lab != null) {
      lab = lab.replace(' ', '_')
      help.helpOnField(block!!.title,
                       block!!.getFieldPos(this),
                       label,
                       lab ?: name,
                       toolTip)
      if (access[VConstants.MOD_UPDATE] != VConstants.ACS_SKIPPED
              || access[VConstants.MOD_INSERT] != VConstants.ACS_SKIPPED
              || access[VConstants.MOD_QUERY] != VConstants.ACS_SKIPPED) {
        helpOnType(help)
        help.helpOnFieldCommand(command)
      }
    }
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
    val modeName: String?
    val modeDesc: String?

    if (access[VConstants.MOD_UPDATE] == VConstants.ACS_MUSTFILL
            || access[VConstants.MOD_INSERT] == VConstants.ACS_MUSTFILL
            || access[VConstants.MOD_QUERY] == VConstants.ACS_MUSTFILL) {
      modeName = VlibProperties.getString("mustfill")
      modeDesc = VlibProperties.getString("mustfill-long")
    } else if (access[VConstants.MOD_UPDATE] == VConstants.ACS_MUSTFILL
            || access[VConstants.MOD_INSERT] == VConstants.ACS_VISIT
            || access[VConstants.MOD_QUERY] == VConstants.ACS_VISIT) {
      modeName = VlibProperties.getString("visit")
      modeDesc = VlibProperties.getString("visit-long")
    } else if (access[VConstants.MOD_UPDATE] == VConstants.ACS_MUSTFILL
            || access[VConstants.MOD_INSERT] == VConstants.ACS_SKIPPED
            || access[VConstants.MOD_QUERY] == VConstants.ACS_SKIPPED) {
      modeName = VlibProperties.getString("skipped")
      modeDesc = VlibProperties.getString("skipped-long")
    } else {
      modeName = VlibProperties.getString("skipped")
      modeDesc = VlibProperties.getString("skipped-long")
    }
    help.helpOnType(modeName,
                    modeDesc,
                    getTypeName(),
                    getTypeInformation(),
                    names)
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
    return buildString {
      try {
        append("\nFIELD ")
        append(name)
        append(" label: ")
        append(label)
        append("\n")
        try {
          val value = getObject(block!!.activeRecord)

          if (value == null) {
            append("    value: null")
          } else {
            append("    value: \"")
            append(value)
            append("\"")
          }
        } catch (e: Exception) {
          append("value information exception ")
        }
        append("\n")
        try {
          append("    type name: ")
          append(getTypeName())
        } catch (e: Exception) {
          append("type information exception")
        }
        append("\n")
        append("    changed: ")
        append(isChanged)
        append("\n")
      } catch (e: Exception) {
        append("exception while retrieving field information\n")
      }
    }
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
    fun getDisplayedValue(trim: Boolean): Any? = this@VField.getDisplayedValue(trim)

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
  var width = 0
    // max # of chars per line
    protected set

  /**
   * The height of a field is the max number of line needed to display
   * any value
   * @return    the width of this field
   */
  var height = 0 // max # of lines
    protected set

  private lateinit var access: IntArray // access in each mode

  private var priority = 0  // order in select results

  private var indices = 0  // bitset of unique indices

  /**
   * The name of the field is the ident.
   *
   * @return    the name of this field.
   */
  lateinit var name: String   // field name (for dumps)
    private set

  var label: String? = null // field label
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
  var toolTip: String? = null // help text
    internal set

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
      field = block
      dynAccess = IntArray(block!!.bufferSize)
      foreground = arrayOfNulls(block.bufferSize)
      background = arrayOfNulls(block.bufferSize)
      setAccess(-1)
    }

  private var columns: Array<VColumn?>? = null // columns in block's tables

  //  private   VFieldUI        ui;             // The UI manager
  private var alias: VField? = null // The alias field

  // changed?
  var isChanged = false // changed by user / changes are done in the model
    private set

  var isChangedUI = false // changed by user / changes are in the ui -> update model

  // UPDATE model before doing anything
  var border = 0

  // dynamic data
  private var searchOperator = 0  // search operator

  private lateinit var dynAccess: IntArray // dynamic access

  // ####
  private var fieldListener: EventListenerList? = null

  // if there is only the model and no gui
  // all the job use less memory and are faster
  private var hasListener = false

  var position: VPosition? = null
    private set

  var command: Array<VCommand>? = null

  private lateinit var foreground: Array<VColor?> // foreground colors for this field.

  private lateinit var background: Array<VColor?> // background colors for this field.

  companion object {
    /**
     * @return a String with the current thread information for debugging
     */
    private fun threadInfo(): String = "Thread: ${Thread.currentThread()}".trimIndent()

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
