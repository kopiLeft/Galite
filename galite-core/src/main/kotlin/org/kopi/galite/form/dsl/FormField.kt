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
package org.kopi.galite.form.dsl

import kotlin.reflect.KProperty

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.kopi.galite.common.Action
import org.kopi.galite.common.Actor
import org.kopi.galite.common.Command
import org.kopi.galite.common.FormTrigger
import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.Trigger
import org.kopi.galite.domain.CodeDomain
import org.kopi.galite.domain.Domain
import org.kopi.galite.domain.ListDomain
import org.kopi.galite.field.Field
import org.kopi.galite.form.VCodeField
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VField
import org.kopi.galite.form.VForm

/**
 * This class represents a form field. It represents an editable element of a block
 *
 * @param ident                the ident of this field
 * @param fieldIndex           the index in parent array of fields
 * @param detailedPosition     the position within the block
 * @param label                the label (text on the left)
 * @param help                 the help text
 * @param align                the alignment of the text
 * @param options              the options of the field
 * @param columns              the column in the database
 * @param initialAccess        the initial access mode
 * @param commands             the commands accessible in this field
 * @param triggers             the triggers executed by this field
 * @param alias                the alias of this field
 */
open class FormField<T>(val block: FormBlock,
                        domain: Domain<T>,
                        private val fieldIndex: Int,
                        initialAccess: Int,
                        var position: FormPosition? = null) : Field<T>(domain) {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private var options: Int = 0
  var columns: FormFieldColumns<T>? = null
  var access: IntArray = IntArray(3) { initialAccess }
  var dropList: MutableList<String>? = null
  var commands: MutableList<Command> = mutableListOf()
  var triggers = mutableListOf<Trigger>()
  var alias: String? = null
  var initialValues = mutableMapOf<Int, T>()
  var value: T by this

  private operator fun setValue(any: Any, property: KProperty<*>, value : T) {
    if (vField.block == null) {
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
      val g = vField.getObject()
      vField.getObject() as T
    }
  }

  /** the minimum value that cannot exceed  */
  internal var min : T? = null

  /** the maximum value that cannot exceed  */
  internal var max : T? = null

  /**
   * Sets the minimum value of an Int field.
   */
  var <U> FormField<U>.minValue : U? where U : Comparable<U>?, U : Number?
    get() {
      return min
    }
    set(value) {
      min = value
    }

  /**
   * Sets the maximum value of an Int field.
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
   * FIXME temporary workaround
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
   * FIXME temporary workaround
   *
   * @param record the record number
   * @param value  the value
   */
  operator fun set(record: Int = 0, value: T) {
    initialValues[record] = value

    if (vField.block != null) {
      vField.setObject(record, value)
    }
  }

  fun droppable(vararg droppables : String) {
    this.block.dropList?.addAll(droppables)
  }

  /**
   * Adds a new command to this field.
   **
   * @param item    the actor linked to the command.
   * @param init    initialization method.
   */
  fun command(item: Actor, init: Command.() -> Unit): Command {
    val command = Command(item)
    command.init()
    commands.add(command)
    return command
  }

  /** the alignment of the text */
  var align: FieldAlignment = FieldAlignment.LEFT

  /** list of nullable columns */
  var nullableColumns = mutableListOf<Column<*>>()

  /** used for LEFT OUTER JOIN */
  fun <T: Column<*>> nullable(column: T): T {
    nullableColumns.add(column)
    return column
  }

  fun initColumn(vararg joinColumns: Column<*>, init: (FormFieldColumns<T>.() -> Unit)?) {
    val cols = joinColumns.map { column ->
      FormFieldColumn(column, column.table.tableName, column.name, this, false, nullableColumns.contains(column))
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

  fun <T> initTrigger(fieldTriggerEvents: Array<out FieldTriggerEvent<*>>, method: () -> T) : Trigger {
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

  /**
   * The field model based on the field type.
   */
  val vField: VField by lazy {
    domain.buildFieldModel(this).also {
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
            getIdent(),
            fieldIndex,
            posInArray,
            options,
            access,
            list, // TODO
            columns?.getColumnsModels()?.toTypedArray(), // TODO
            columns?.index?.indexNumber ?: 0,
            columns?.priority ?: 0,
            commands.map { it.buildModel(block.vBlock, form.actors) }.toTypedArray(),
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
  open fun initialize(block: FormBlock) {
    // TRANSIENT MODE
    if (columns == null && isNeverAccessible) {
      options = options or VConstants.FDO_TRANSIENT
    }

    // POSITION
    if (!isInternal && !block.isSingle()) {
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
    get() = access[0] == VConstants.ACS_HIDDEN && access[1] == VConstants.ACS_HIDDEN && access[2] == VConstants.ACS_HIDDEN

  fun getIdent() = label ?: "ANONYMOUS!@#$%^&*()"

  /**
   * Returns true iff it is certain that the field will never be entered
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
   * ACCESS is a special trigger that defines how a field can be accessed.
   * This trigger must return one of these values:
   * [Access.SKIPPED], [Access.HIDDEN], [Access.VISIT] or [Access.MUSTFILL].
   */
  val ACCESS = FieldTriggerEvent<Access>(VConstants.TRG_FLDACCESS)

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
    if (!isInternal) {
      (writer as FormLocalizationWriter).genField(label, label, help)
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
