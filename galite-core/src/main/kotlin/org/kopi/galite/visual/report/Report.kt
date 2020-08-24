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

package org.kopi.galite.visual.report

import org.kopi.galite.domain.Domain
import org.kopi.galite.visual.field.Field

/**
 * Represents a report that contains fields [fields] and displays a table of [lines].
 */
open class Report {
  /** Report's fields */
  val fields = mutableListOf<Field<*>>()
  /** Report's data lines */
  val lines = mutableListOf<MutableMap<Field<*>, Any>>()

  /**
   * creates and returns a field. It uses [init] method to initialize the field.
   *
   * @param domain  the domain of the field
   * @param init    initialization method
   * @return a field.
   */
  fun <T: Comparable<T>> field(domain: Domain<T>, init: Field<T>.() -> Unit) : Field<T> {
    val field = Field(domain)
    field.init()
    fields.add(field)
    return field
  }

  /**
   * Adds a line to the report.
   *
   * @param init initializes the line with values
   */
  fun add(init: Report.() -> Unit) {
    val map = mutableMapOf<Field<*>, Any>()
    lines.add(map)
    init()
  }

  /**
   * Returns line number [lineNumber].
   *
   * @param lineNumber line's number
   */
  fun getLine(lineNumber: Int): MutableMap<Field<*>, Any> = lines[lineNumber]

  /**
   * Returns lines of data for a specific [field].
   *
   * @param field the field
   */
  fun getLinesForField(field: Field<*>) = lines.map { it[field] }
}
