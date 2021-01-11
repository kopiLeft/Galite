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

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime
import org.kopi.galite.common.Action
import org.kopi.galite.common.Command
import org.kopi.galite.common.FormTrigger
import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.Trigger
import org.kopi.galite.domain.CodeDomain
import org.kopi.galite.domain.Domain
import org.kopi.galite.domain.ListDomain
import org.kopi.galite.field.Field
import org.kopi.galite.field.FieldBooleanTriggerEvent
import org.kopi.galite.field.FieldIntTriggerEvent
import org.kopi.galite.field.FieldObjectTriggerEvent
import org.kopi.galite.field.FieldProtectedTriggerEvent
import org.kopi.galite.field.FieldTriggerEvent
import org.kopi.galite.field.FieldVoidTriggerEvent
import org.kopi.galite.form.VBooleanCodeField
import org.kopi.galite.form.VBooleanField
import org.kopi.galite.form.VCodeField
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VDateField
import org.kopi.galite.form.VField
import org.kopi.galite.form.VFixnumCodeField
import org.kopi.galite.form.VIntegerCodeField
import org.kopi.galite.form.VIntegerField
import org.kopi.galite.form.VMonthField
import org.kopi.galite.form.VStringCodeField
import org.kopi.galite.form.VStringField
import org.kopi.galite.form.VTimeField
import org.kopi.galite.form.VTimestampField
import org.kopi.galite.form.VWeekField
import org.kopi.galite.type.Date
import org.kopi.galite.type.Fixed
import org.kopi.galite.type.Month
import org.kopi.galite.type.Time
import org.kopi.galite.type.Timestamp
import org.kopi.galite.type.Week

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
class FormField<T : Comparable<T>?>(val block: FormBlock,
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
  var commands: MutableList<Command>? = null
  var triggers = mutableListOf<Trigger>()
  var alias: String? = null
  var initialValues = mutableMapOf<Int, T?>()
  var value: T? = null
    get() {
      return if (vField.block == null) {
        initialValues[0]
      } else {
        vField.getObject() as? T
      }
    }
    set(value) {
      field = value
      if (vField.block == null) {
        initialValues[0] = value
      } else {
        vField.setObject(value)
      }
    }

  /** the minimum value that cannot exceed  */
  private var min: Int = Int.MIN_VALUE

  /** the maximum value that cannot exceed  */
  private var max: Int = Int.MAX_VALUE

  /**
   * Sets the minimum value of an Int field.
   */
  var <U> FormField<U>.minValue: Int where U : Comparable<U>?, U : Number?
    get() {
      return min
    }
    set(value) {
      min = value
    }

  /**
   * Sets the maximum value of an Int field.
   */
  var <U> FormField<U>.maxValue: Int where U : Comparable<U>?, U : Number?
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


  /** the alignment of the text */
  var align: FieldAlignment = FieldAlignment.LEFT

  /**
   * Assigns [columns] to this field.
   *
   * @param joinColumns columns to use to make join between block tables
   * @param init        initialises the form field column properties (index, priority...)
   */
  fun columns(vararg joinColumns: Column<T>, init: (FormFieldColumns<T>.() -> Unit)? = null) {
    val cols = joinColumns.map { column ->
      FormFieldColumn(column, column.table.tableName, column.name, this, false, false) // TODO
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
  private fun <T> trigger(fieldTriggerEvents: Array<out FieldTriggerEvent>, method: () -> T): Trigger {
    val event = fieldEventList(fieldTriggerEvents)
    val fieldAction = Action(null, method)
    val trigger = FormTrigger(event, fieldAction)

    triggers.add(trigger)
    return trigger
  }

  private fun fieldEventList(fieldTriggerEvents: Array<out FieldTriggerEvent>): Long {
    var self = 0L

    fieldTriggerEvents.forEach { trigger ->
      self = self or (1L shl trigger.event)
    }

    return self
  }

  /**
   * Adds void triggers to this field
   *
   * @param fieldTriggerEvents  the trigger event to add
   * @param method              the method to execute when trigger is called
   */
  fun trigger(vararg fieldTriggerEvents: FieldVoidTriggerEvent, method: () -> Unit): Trigger {
    return trigger(fieldTriggerEvents, method)
  }

  /**
   * Adds boolean triggers to this field
   *
   * @param fieldTriggerEvents  the trigger events to add
   * @param method              the method to execute when trigger is called
   */
  fun trigger(vararg fieldTriggerEvents: FieldBooleanTriggerEvent, method: () -> Boolean): Trigger {
    return trigger(fieldTriggerEvents, method)
  }

  /**
   * Adds protected triggers to this block.
   *
   * @param fieldTriggerEvents  the triggers to add
   * @param method              the method to execute when trigger is called
   */
  fun trigger(vararg fieldTriggerEvents: FieldProtectedTriggerEvent, method: () -> Unit): Trigger {
    return trigger(fieldTriggerEvents, method)
  }

  /**
   * Adds object triggers to this block.
   *
   * @param fieldTriggerEvents  the triggers to add
   * @param method              the method to execute when trigger is called
   */
  fun trigger(vararg fieldTriggerEvents: FieldObjectTriggerEvent, method: () -> Any): Trigger {
    return trigger(fieldTriggerEvents, method)
  }

  /**
   * Adds int triggers to this block.
   *
   * @param fieldTriggerEvents  the triggers to add
   * @param method              the method to execute when trigger is called
   */
  fun trigger(vararg fieldTriggerEvents: FieldIntTriggerEvent, method: () -> Int): Trigger {
    return trigger(fieldTriggerEvents, method)
  }

  // TODO add Fixed types
  /**
   * The field model based on the field type.
   */
  val vField: VField by lazy {
    when {
      domain is CodeDomain -> {
        val type = domain as CodeDomain<*>
        when (domain.kClass) {
          Boolean::class -> VBooleanCodeField(block.buffer,
                                              domain.ident,
                                              block.sourceFile,
                                              type.codes.map { it.ident }.toTypedArray(),
                                              type.codes.map { it.value as? Boolean }.toTypedArray())
          Fixed::class -> VFixnumCodeField(block.buffer,
                                           domain.ident,
                                           block.sourceFile,
                                           type.codes.map { it.ident }.toTypedArray(),
                                           type.codes.map { it.value as? Fixed }.toTypedArray())
          Int::class, Long::class -> VIntegerCodeField(block.buffer,
                                                       domain.ident,
                                                       block.sourceFile,
                                                       type.codes.map { it.ident }.toTypedArray(),
                                                       type.codes.map { it.value as? Int }.toTypedArray())
          String::class -> VStringCodeField(block.buffer,
                                            domain.ident,
                                            block.sourceFile,
                                            type.codes.map { it.ident }.toTypedArray(),
                                            type.codes.map { it.value as? String }.toTypedArray())
          else -> throw RuntimeException("Type ${domain.kClass!!.qualifiedName} is not supported")
        }
      }
      else -> {
        when (domain.kClass) {
          Int::class, Long::class -> VIntegerField(block.buffer, domain.width ?: 0, min, max)
          String::class -> VStringField(block.buffer,
                                        domain.width ?: 0,
                                        domain.height ?: 1,
                                        domain.visibleHeight ?: 1,
                                        0,  // TODO
                                        false) // TODO
          Boolean::class -> VBooleanField(block.buffer)
          Date::class, java.util.Date::class -> VDateField(block.buffer)
          Month::class -> VMonthField(block.buffer)
          Week::class -> VWeekField(block.buffer)
          Time::class -> VTimeField(block.buffer)
          Timestamp::class, DateTime::class -> VTimestampField(block.buffer)
          else -> throw RuntimeException("Type ${domain.kClass!!.qualifiedName} is not supported")
        }
      }
    }
  }

  fun setInfo(source: String) {
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
            null, // TODO
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

    // ACCESS
    val blockAccess: IntArray = block.access
    for (i in 0..2) {
      access[i] = access[i].coerceAtMost(blockAccess[i])
    }

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
