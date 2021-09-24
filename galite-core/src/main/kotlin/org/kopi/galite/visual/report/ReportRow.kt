/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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

import org.kopi.galite.type.Type0

/**
 * Represents a data row of a [Report].
 *
 * @param reportFields the fields that exists in the report.
 */
class ReportRow(private val reportFields: MutableList<ReportField<*>>) {
  /** A report data row */
  val data = mutableMapOf<ReportField<*>, Any?>()

  /**
   * Returns data value for a specific [ReportField].
   *
   * @param field the field.
   * @return  data value for a specific [ReportField].
   */
  fun getValueOf(field: ReportField<*>) = data[field]

  /**
   * Sets a mapping between the values that the domain can take
   * and a corresponding text to be displayed in a [ReportField].
   *
   * @param field the field.
   * @param value the field's value.
   */
  operator fun <T : Comparable<T>?> set(field: ReportField<T>, value: T) {
    if (field in reportFields) {
      data.putIfAbsent(field, value)
    }
  }

  /**
   * Sets a mapping between the values that the domain can take
   * and a corresponding text to be displayed in a [ReportField].
   *
   * @param field the field.
   * @param value the field's value.
   */
  operator fun <T : Type0<K>, K> set(field: ReportField<T>, value: K) {
    if (field in reportFields) {
      data.putIfAbsent(field, value)
    }
  }
}
