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
import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.Command
import org.kopi.galite.common.Trigger
import org.kopi.galite.domain.Domain
import org.kopi.galite.field.Field
import org.kopi.galite.form.VConstants

/**
 * This class represents a form field. It represents an editable element of a block
 *
 * @param ident                the ident of this field
 * @param detailedPosition     the position within the block
 * @param label                the label (text on the left)
 * @param help                 the help text
 * @param align                the alignment of the text
 * @param options              the options of the field
 * @param columns              the column in the database
 * @param access               the access mode
 * @param commands             the commands accessible in this field
 * @param triggers             the triggers executed by this field
 * @param alias                the e alias of this field
 */
open class FormField<T : Comparable<T>>(override val domain: Domain<T>? = null): Field<T>(domain) {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  var detailedPosition: FormPosition? = null
  var options: Int = 0
  var columns: FormFieldColumns? = null
  lateinit var access: IntArray
  var commands: Array<Command>? = null
  var triggers: Array<Trigger>? = null
  var alias: String? = null

  var block: FormBlock? = null
    private set

  /**
   * Returns the index in parent array of fields
   */
  var index = 0
    private set

  /**
   * Assigns [columns] to this field.
   */
  fun columns(fieldColumn: Column<T>, vararg joinColumns: Column<*>) {
    val cols = joinColumns.map {
      FormFieldColumn(it, it.table.tableName, it.name, true, true) // TODO
    }
    columns = FormFieldColumns(cols.toTypedArray(), index, 0) // TODO
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
