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

import org.kopi.galite.list.VColumn

import org.jetbrains.exposed.sql.Column

/**
 * A binding to database
 *
 * @param corr                the name of the table in database
 * @param ident                the name of the column
 * @param isKey                true if this column is a key in the database
 * @param nullable                true if this column is nullable.
 */
class FormFieldColumn<T : Comparable<T>>(val column: Column<T>,
                                         val corr: String,
                                         private val ident: String,
                                         private val field: FormField<T>,
                                         private val isKey: Boolean,
                                         val nullable: Boolean) {
  init {
    initialize()
  }

  private var num = 0

  /**
   * Returns the the field column model.
   */
  fun getFormFieldColumnModel(): VColumn<T> {
    return VColumn(num, ident, isKey, nullable, column)
  }

  /**
   * TODO: Do we really need his method
   */
  fun initialize() {
    val table = field.getTable(column.table)
    num = field.getTableNum(table)
  }

  /**
   * Sets the position in an array of fields
   */
  fun cloneToPos(pos: Int): FormFieldColumn<T> {
    return FormFieldColumn(column, corr, ident + pos, field, isKey, nullable)
  }
}
