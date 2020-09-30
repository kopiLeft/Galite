/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.report

import org.kopi.galite.domain.Domain
import org.kopi.galite.field.Field

/**
 * Represents a report that contains fields [fields] and displays a table of [reportRows].
 */
open class Report : VReport() {
  /** Report's fields. */
  val fields = mutableListOf<Field<*>>()

  /** Report's data rows. */
  val reportRows = mutableListOf<ReportRow>()

  /**
   * creates and returns a field. It uses [init] method to initialize the field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method.
   * @return a field.
   */
  fun <T : Comparable<T>> field(domain: Domain<T>? = null, init: Field<T>.() -> Unit): Field<T> {
    val field = Field(domain)
    field.init()
    fields.add(field)
    return field
  }

  /**
   * Adds a row to the report.
   *
   * @param init initializes the row with values.
   */
  fun add(init: ReportRow.() -> Unit) {
    val row = ReportRow(fields)
    row.init()
    reportRows.add(row)
  }

  /**
   * Returns the row's data.
   *
   * @param rowNumber the index of the desired row.
   */
  fun getRow(rowNumber: Int): MutableMap<Field<*>, Any> = reportRows[rowNumber].data

  /**
   * Returns rows of data for a specific [field].
   *
   * @param field the field.
   */
  fun getRowsForField(field: Field<*>) = reportRows.map { it.data[field] }

  // --------------------------------------------------------------------
  // VISUAL REPORT IMPLEMENTATION
  // --------------------------------------------------------------------

  /**
   * initializes the report
   */
  override fun init() {
    // TODO
    setSource("User")
    reportRows.map {
      add(it.data.values.toTypedArray())
    }
    super.model.columns = fields.map {
      VStringColumn(it.label, 0, 0, -1, null, 32, 1, null)
    }
  }

  /**
   * Adds a row to the report.
   *
   * @param data the row data.
   */
  override fun add(data: Array<Any?>) {
    val data = data.toMutableList()

    data.add(null)

    super.model.addLine(data.toTypedArray())
  }

  /**
   * Resets the report.
   */
  override fun reset() {
    // TODO
  }
}
