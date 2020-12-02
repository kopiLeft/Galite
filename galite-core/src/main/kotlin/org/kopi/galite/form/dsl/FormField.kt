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

import org.kopi.galite.common.Command
import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.Trigger
import org.kopi.galite.domain.Domain
import org.kopi.galite.field.Field
import org.kopi.galite.form.VBooleanField
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VDateField
import org.kopi.galite.form.VField
import org.kopi.galite.form.VIntegerField
import org.kopi.galite.form.VMonthField
import org.kopi.galite.form.VStringField
import org.kopi.galite.form.VTimeField
import org.kopi.galite.form.VTimestampField
import org.kopi.galite.form.VWeekField
import org.kopi.galite.type.Date
import org.kopi.galite.type.Month
import org.kopi.galite.type.Time
import org.kopi.galite.type.Timestamp
import org.kopi.galite.type.Week
import java.util.ArrayList

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
class FormField<T : Comparable<T>>(val block: FormBlock,
                                   override val domain: Domain<T>,
                                   private val fieldIndex: Int,
                                   initialAccess: Int,
                                   var position: FormPosition? = null): Field<T>(domain) {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  var options: Int = 0
  var columns: FormFieldColumns? = null
  var access: IntArray = IntArray(3) { initialAccess }
  var dropList: MutableList<String>? = null
  var commands: MutableList<Command>? = null
  var triggers: Array<Trigger>? = null
  var alias: String? = null
  var initialValues = mutableMapOf<Int, T?>()
  var value: T? = null
    get() {
      return if(vField.block == null) {
        initialValues[0]
      } else {
        vField.getObject() as? T
      }
    }
    set(value) {
      field = value
      if(vField.block == null) {
        initialValues[0] = value
      } else {
        vField.setObject(value)
      }
    }

  /**
   * Returns the field value of the current record number [record]
   *
   * FIXME temporary workaround
   *
   * @param record the record number
   */
  operator fun get(record: Int): T? {
    return if(vField.block == null) {
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

    if(vField.block != null) {
      vField.setObject(record, value)
    }
  }

  fun droppable(vararg droppables : String) {
    this.block.dropList?.addAll(droppables)
  }

  fun stringList(list: Array<out String>): Array<String>{
    val vect : ArrayList<String> = arrayListOf()
    list.forEach {
      vect.add(it)
    }
    return vect.toTypedArray()
  }

  /** the alignment of the text */
  var align: FieldAlignment = FieldAlignment.LEFT

  /**
   * Assigns [columns] to this field.
   *
   * @param joinColumns columns to use to make join between block tables
   * @param init        initialises the form field column properties (index, priority...)
   */
  fun columns(vararg joinColumns: Column<*>, init: (FormFieldColumns.() -> Unit)? = null) {
    val cols = joinColumns.map {
      FormFieldColumn(it, it.table.tableName, it.name, true, true) // TODO
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
   * The field model based on the field type.
   */
  var vField: VField =
          when(domain.kClass) {
            Int::class -> VIntegerField(block.buffer, domain.width ?: 0, Int.MIN_VALUE, Int.MAX_VALUE)
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
            Timestamp::class -> VTimestampField(block.buffer)
            else -> throw RuntimeException("Type ${domain.kClass!!.qualifiedName} is not supported")
          }

  fun setInfo() {
    vField.setInfo(
            getIdent(),
            fieldIndex,
            posInArray,
            options,
            access,
            null, // TODO
            null, // TODO
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

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

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
  /**
   * !!!FIX:taoufik
   */
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
      val cols: Array<FormFieldColumn?>? = columns!!.columns
      for (i in cols!!.indices) {
        if (cols[i]!!.corr == table.corr) {
          return i
        }
      }
    }
    return -1
  }
}
