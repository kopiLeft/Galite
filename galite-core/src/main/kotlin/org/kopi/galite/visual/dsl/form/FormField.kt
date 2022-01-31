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
package org.kopi.galite.visual.dsl.form

import kotlin.reflect.KProperty

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.domain.ListDomain
import org.kopi.galite.visual.dsl.common.Action
import org.kopi.galite.visual.dsl.common.Actor
import org.kopi.galite.visual.dsl.common.Command
import org.kopi.galite.visual.dsl.common.FormTrigger
import org.kopi.galite.visual.dsl.common.LocalizationWriter
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.dsl.field.Field
import org.kopi.galite.visual.form.VCodeField
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.type.Image
import org.kopi.galite.visual.visual.VColor
import org.kopi.galite.visual.visual.VException

/**
 * This class represents a form field. It represents an editable element of a block
 *
 * @param block                the form block containing this field
 * @param domain               the domain of this field
 * @param fieldIndex           the index in parent array of fields
 * @param initialAccess        the initial access mode
 * @param position             the position within the block
 */
open class FormField<T>(internal val block: Block,
                        domain: Domain<T>,
                        private val fieldIndex: Int,
                        initialAccess: Int,
                        var position: FormPosition? = null) : Field<T>(domain) {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private var options: Int = 0 // the options of the field
  var columns: FormFieldColumns<T>? = null // the column in the database
  var access: IntArray = IntArray(3) { initialAccess }
  var commands: MutableList<Command> = mutableListOf() // the commands accessible in this field
  var triggers = mutableListOf<Trigger>() // the triggers executed by this field
  var alias: String? = null // the alias of this field
  var initialValues = mutableMapOf<Int, T>()
  var value: T by this

  private operator fun setValue(any: Any, property: KProperty<*>, value : T) {
    if (!block.isModelInitialized) {
      initialValues[0] = value
    } else {
      vField.setObject(value)
    }
  }

  @Suppress("UNCHECKED_CAST")
  private operator fun getValue(any: Any, property: KProperty<*>): T {
    return if (vField.block == null) {
      initialValues[0] as T
    } else {
      vField.getObject() as T
    }
  }

  /**
   * Adds the field options. you can use one or more option from the options available for fields.
   *
   * Use [FieldOption] to see the list of these field options.
   *
   * @param fieldOptions the field options
   */
  fun options(vararg fieldOptions: FieldOption) {
    fieldOptions.forEach { fieldOption ->
      if (fieldOption == FieldOption.QUERY_LOWER || fieldOption == FieldOption.QUERY_UPPER) {
        options = options and fieldOption.value.inv()
      }

      options = options or fieldOption.value
    }
  }

  /** the minimum value that cannot exceed  */
  internal var min : T? = null

  /** the maximum value that cannot exceed  */
  internal var max : T? = null

  /**
   * Sets the minimum value of a number field.
   */
  var <U> FormField<U>.minValue : U? where U : Comparable<U>?, U : Number?
    get() {
      return min
    }
    set(value) {
      min = value
    }

  /**
   * Sets the maximum value of a number field.
   */
  var <U> FormField<U>.maxValue : U? where U : Comparable<U>?, U : Number?
    get() {
      return max
    }
    set(value) {
      max = value
    }

  /**
   * Returns the field value of the current record number [record]
   *
   * @param record the record number
   */
  operator fun get(record: Int): T? {
    return if (vField.block == null) {
      initialValues[record]
    } else {
      vField.getObject(record) as? T
    }
  }

  /**
   * Sets the field value of given record.
   *
   * @param record the record number
   * @param value  the value
   */
  operator fun set(record: Int = 0, value: T) {
    initialValues[record] = value

    if (block.isModelInitialized) {
      vField.setObject(record, value)
    }
  }

  fun droppable(vararg droppables : String) {
    if (domain.kClass != String::class
      && domain.kClass != Image::class) {
      error("The field is droppable but its type is not supported as a drop target.")
    } else {
      val flavor = block.addDropList(droppables, this)
      if (flavor != null) {
        error("The extension is already defined as a drop target for this field. ")
      }
    }
  }

  /**
   * Adds a new command to this field.
   **
   * @param item    the actor linked to the command.
   * @param modes   the modes in which the command should be executed.
   * @param action  the action function.
   */
  fun command(item: Actor, vararg modes: Mode, action: () -> Unit): Command {
    val command = Command(item)

    if (modes.isNotEmpty()) {
      command.setMode(*modes)
    }
    command.action = action
    commands.add(command)
    return command
  }

  /** the alignment of the text */
  var align: FieldAlignment = FieldAlignment.LEFT

  /** list of nullable columns */
  var nullableColumns = mutableListOf<Column<*>>()

  /** list of key columns */
  var keyColumns = mutableListOf<Column<*>>()

  /** used for LEFT OUTER JOIN */
  fun <T: Column<*>> nullable(column: T): T {
    nullableColumns.add(column)
    return column
  }

  /**
   * Call this if this column is a key in the database.
   */
  fun <T: Column<*>> key(column: T): T {
    keyColumns.add(column)
    return column
  }

  fun initColumn(vararg joinColumns: Column<*>, init: (FormFieldColumns<T>.() -> Unit)?) {
    val cols = joinColumns.map { column ->
      FormFieldColumn(column,
                      column.table.tableName,
                      column.name,
                      this,
                      keyColumns.contains(column),
                      nullableColumns.contains(column))
    }
    columns = FormFieldColumns(cols.toTypedArray())
    if (init != null) {
      columns!!.init()
    }
  }

  /** changing field visibility in mode query */
  fun onQueryHidden() {
    this.access[VConstants.MOD_QUERY] = VConstants.ACS_HIDDEN
  }

  fun onQuerySkipped() {
    this.access[VConstants.MOD_QUERY] = VConstants.ACS_SKIPPED
  }

  fun onQueryVisit() {
    this.access[VConstants.MOD_QUERY] = VConstants.ACS_VISIT
  }

  fun onQueryMustFill() {
    this.access[VConstants.MOD_QUERY] = VConstants.ACS_MUSTFILL
  }

  /** changing field visibility in mode insert */
  fun onInsertHidden() {
    this.access[VConstants.MOD_INSERT] = VConstants.ACS_HIDDEN
  }

  fun onInsertSkipped() {
    this.access[VConstants.MOD_INSERT] = VConstants.ACS_SKIPPED
  }

  fun onInsertVisit() {
    this.access[VConstants.MOD_INSERT] = VConstants.ACS_VISIT
  }

  fun onInsertMustFill() {
    this.access[VConstants.MOD_INSERT] = VConstants.ACS_MUSTFILL
  }

  /** changing field visibility in mode update */
  fun onUpdateHidden() {
    this.access[VConstants.MOD_UPDATE] = VConstants.ACS_HIDDEN
  }

  fun onUpdateSkipped() {
    this.access[VConstants.MOD_UPDATE] = VConstants.ACS_SKIPPED
  }

  fun onUpdateVisit() {
    this.access[VConstants.MOD_UPDATE] = VConstants.ACS_VISIT
  }

  fun onUpdateMustFill() {
    this.access[VConstants.MOD_UPDATE] = VConstants.ACS_MUSTFILL
  }

  // ----------------------------------------------------------------------
  // UTILS
  // ----------------------------------------------------------------------

  /**
   * Copies the fields value of a record to another
   */
  fun copyRecord(f: Int, t: Int) = vField.copyRecord(f, t)

  // ----------------------------------------------------------------------
  // FOREGROUND AND BACKGROUND COLOR MANAGEMENT
  // ----------------------------------------------------------------------

  /**
   * Sets the foreground and the background colors for the current record.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  fun setColor(foreground: VColor?, background: VColor?) {
    vField.setColor(foreground, background)
  }

  /**
   * Sets the foreground and the background colors.
   * @param r The record number.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  fun setColor(r: Int, foreground: VColor?, background: VColor?) {
    vField.setColor(r, foreground, background)
  }

  /**
   * Resets the foreground and the background colors the current record.
   */
  fun resetColor(r: Int) {
    vField.resetColor(r)
  }

  /**
   * Update the foreground and the background colors.
   * @param r The record number.
   */
  fun updateColor(r: Int) {
    vField.updateColor(r)
  }

  fun getForeground(r: Int): VColor? = vField.getForeground(r)

  fun getBackground(r: Int): VColor? = vField.getBackground(r)

  // ----------------------------------------------------------------------
  // DRAG AND DROP HANDLIN
  // ----------------------------------------------------------------------

  /**
   * Call before a drop starts on this field.
   * @throws VException Visual errors occurring.
   */
  fun onBeforeDrop() {
    vField.onBeforeDrop()
  }

  /**
   * Called after a drop ends on this field.
   * @throws VException Visual errors occurring.
   */
  fun onAfterDrop() {
    vField.onAfterDrop()
  }

  ///////////////////////////////////////////////////////////////////////////
  // FORM FIELD TRIGGERS
  ///////////////////////////////////////////////////////////////////////////

  /**
   * `access` is a special trigger that defines how a field can be accessed.
   * This trigger must return one of these values:
   * [Access.SKIPPED], [Access.HIDDEN], [Access.VISIT] or [Access.MUSTFILL].
   */
  fun access(action: () -> Access): Trigger {
    return initTrigger(arrayOf(FieldTriggerEvent<Access>(VConstants.TRG_FLDACCESS)), action)
  }

  /**
   * Adds triggers to this field
   *
   * @param fieldTriggerEvents    the trigger events to add
   * @param method                the method to execute when trigger is called
   */
  fun <T> trigger(vararg fieldTriggerEvents: FieldTriggerEvent<T>, method: () -> T): Trigger =
    initTrigger(fieldTriggerEvents, method)

  /**
   * Adds protected triggers to this block.
   *
   * @param fieldTriggerEvents  the triggers to add
   * @param method              the method to execute when trigger is called
   */
  fun trigger(vararg fieldTriggerEvents: FieldProtectedTriggerEvent, method: () -> Unit): Trigger =
    initTrigger(fieldTriggerEvents, method)

  /**
   * Adds protected triggers to this block.
   *
   * @param fieldTriggerEvents  the triggers to add
   * @param method              the method to execute when trigger is called
   */
  fun trigger(vararg fieldTriggerEvents: FieldTypeTriggerEvent, method: () -> T): Trigger =
    initTrigger(fieldTriggerEvents, method)

  private fun <T> initTrigger(fieldTriggerEvents: Array<out FieldTriggerEvent<*>>, method: () -> T) : Trigger {
    val event = fieldEventList(fieldTriggerEvents)
    val fieldAction = Action(null, method)
    val trigger = FormTrigger(event, fieldAction)

    triggers.add(trigger)
    return trigger
  }

  private fun fieldEventList(fieldTriggerEvents: Array<out FieldTriggerEvent<*>>): Long {
    var self = 0L

    fieldTriggerEvents.forEach { trigger ->
      self = self or (1L shl trigger.event)
    }

    return self
  }

  ///////////////////////////////////////////////////////////////////////////
  // FIELD MODEL
  ///////////////////////////////////////////////////////////////////////////

  /**
   * The field model based on the field type.
   */
  val vField: VField by lazy {
    domain.buildFormFieldModel(this).also {
      it.label = label
      it.toolTip = help
    }
  }

  fun setInfo(source: String, form: VForm) {
    val list = if (domain is ListDomain) {
      (domain as ListDomain).list.buildListModel(source)
    } else {
      null
    }

    if (domain is CodeDomain) {
      (vField as VCodeField).source = source
    }

    vField.setInfo(
      ident,
      fieldIndex,
      posInArray,
      options,
      access,
      list, // TODO
      columns?.getColumnsModels()?.toTypedArray(), // TODO
      columns?.index?.indexNumber ?: 0,
      columns?.priority ?: 0,
      commands.map { it.buildModel(block.block, form.actors) }.toTypedArray(),
      position?.getPositionModel(),
      align.value,
      null // TODO
    )
  }

  /**
   * Initializes form field properties
   *
   * @param block        the actual form block
   */
  open fun initialize(block: Block) {
    // TRANSIENT MODE
    if (columns == null && isNeverAccessible) {
      options = options or VConstants.FDO_TRANSIENT
    }

    // POSITION
    if (!_isInternal && !block.isSingle()) {
      // with NO DETAIL the position must be null
      if (hasOption(VConstants.FDO_NODETAIL) || block.hasOption(VConstants.BKO_NODETAIL)) {

        // Get a position for the chart view.
        position = block.positionField(this)
      }
      if (!(hasOption(VConstants.FDO_NODETAIL)
                      || block.hasOption(VConstants.BKO_NODETAIL)
                      || hasOption(VConstants.FDO_NOCHART)
                      || block.hasOption(VConstants.BKO_NOCHART))) {
        block.positionField(position)
      }
    }
  }

  val parentBlock: Block get() = block

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * return table num
   */
  fun getTable(name: Table): FormBlockTable {
    return block.getTable(name)
  }

  /**
   * return table num
   */
  fun getTableNum(table: FormBlockTable): Int {
    return block.getTableNum(table)
  }

  /**
   * Returns true if the field is never displayed
   */
  val isInternal: Boolean
    get() = vField.isInternal()

  private val _isInternal = access[0] == VConstants.ACS_HIDDEN
          && access[1] == VConstants.ACS_HIDDEN
          && access[2] == VConstants.ACS_HIDDEN

  override var ident: String = if (_isInternal) "ANONYMOUS!@#$%^&*()" else super.ident

  /**
   * Returns true if it is certain that the field will never be entered
   */
  val isNeverAccessible: Boolean
    get() {
      for (i in 0..2) {
        if (access[i] == VConstants.ACS_VISIT) {
          return false
        }
        if (access[i] == VConstants.ACS_MUSTFILL) {
          return false
        }
      }
      return true
    }

  /**
   * Returns the position in the array of fields
   */
  open val posInArray: Int
    get() = -1

  ///////////////////////////////////////////////////////////////////////////
  // FORM FIELD TRIGGERS EVENTS
  ///////////////////////////////////////////////////////////////////////////

  /**
   * Not defined actually
   */
  val FORMAT = FieldTriggerEvent<Unit>(VConstants.TRG_FORMAT)

  /**
   * Must return a boolean value, if "true" the cursor will move to the next field
   */
  val AUTOLEAVE = FieldTriggerEvent<Boolean>(VConstants.TRG_AUTOLEAVE)

  /**
   * Defines the default value of the field to be set if the setDefault() method is called
   * (this method is automatically called when the user choose the insert command)
   */
  val DEFAULT = FieldTriggerEvent<Unit>(VConstants.TRG_DEFAULT)

  /**
   * Executed on field content change
   */
  val POSTCHG = FieldTriggerEvent<Unit>(VConstants.TRG_POSTCHG)

  /**
   * Executed before dropping file
   */
  val PREDROP = FieldTriggerEvent<Unit>(VConstants.TRG_PREDROP)

  /**
   * Executed after dropping file
   */
  val POSTDROP = FieldTriggerEvent<Unit>(VConstants.TRG_POSTDROP)

  /**
   * Executed upon exit of field
   */
  val POSTFLD = FieldTriggerEvent<Unit>(VConstants.TRG_POSTFLD)

  /**
   * Executed after inserting a row of the database
   */
  val POSTINS = FieldProtectedTriggerEvent(VConstants.TRG_POSTINS)

  /**
   * Executed after updating a row of the database
   */
  val POSTUPD = FieldProtectedTriggerEvent(VConstants.TRG_POSTUPD)

  /**
   * Executed before deleting a row of the database
   */
  val PREDEL = FieldProtectedTriggerEvent(VConstants.TRG_PREDEL)

  /**
   * Executed upon entry of field
   */
  val PREFLD = FieldTriggerEvent<Unit>(VConstants.TRG_PREFLD)

  /**
   * Executed before inserting a row of the database
   */
  val PREINS = FieldProtectedTriggerEvent(VConstants.TRG_PREINS)

  /**
   * Executed before updating a row of the database
   */
  val PREUPD = FieldProtectedTriggerEvent(VConstants.TRG_PREUPD)

  /**
   * Executed before validating any new entry
   */
  val PREVAL = FieldTriggerEvent<Unit>(VConstants.TRG_PREVAL)

  /**
   * Executed after field change and validation
   */
  val VALFLD = FieldTriggerEvent<Unit>(VConstants.TRG_VALFLD)

  /**
   * This is the same trigger as VALFLD
   */
  val VALIDATE = FieldTriggerEvent<Unit>(VConstants.TRG_VALFLD)

  /**
   * Equates the value of two fields
   */
  val VALUE = FieldTypeTriggerEvent(VConstants.TRG_VALUE)

  /**
   * Make field clickable and execute an action
   */
  val ACTION = FieldTriggerEvent<Unit>(VConstants.TRG_ACTION)

  /**
   * Field Triggers
   *
   * @param event the event of the trigger
   */
  open class FieldTriggerEvent<T>(val event: Int)

  /**
   * Field protected Triggers
   *
   * @param event the event of the trigger
   */
  open class FieldProtectedTriggerEvent(event: Int) : FieldTriggerEvent<Unit>(event)

  /**
   * Field object Triggers
   *
   * @param event the event of the trigger
   */
  open class FieldTypeTriggerEvent(event: Int) : FieldTriggerEvent<Any>(event)

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  override fun genLocalization(writer: LocalizationWriter) {
    if (!_isInternal) {
      (writer as FormLocalizationWriter).genField(ident, label, help)
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  fun hasOption(option: Int): Boolean {
    return options and option == option
  }

  fun fetchColumn(table: FormBlockTable): Int {
    if (columns != null) {
      val cols = columns!!.columns
      for (i in cols.indices) {
        if (cols[i].corr == table.corr) {
          return i
        }
      }
    }
    return -1
  }
}
