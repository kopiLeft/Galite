/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.visual.dsl.report

import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.kopi.galite.type.Month
import org.kopi.galite.type.Type0
import org.kopi.galite.type.Week

/**
 * Represents a data row of a [Report].
 *
 * @param reportFields the fields that exists in the report.
 */
class ReportRow(private val reportFields: MutableList<ReportField<*>>) {
  /** A report data row */
  val data = mutableMapOf<ReportField<*>, Any?>()

  /**
   * Returns data value of a specific [ReportField] in this report row.
   *
   * @param field the field.
   * @return  data value for a specific [ReportField].
   */
  @Suppress("UNCHECKED_CAST")
  fun <T> getValueOf(field: ReportField<T>): T = data[field] as T

  /**
   * Gets the value of the field in this report row.
   *
   * @param field the field.
   * @return  data value for a specific [ReportField].
   */
  operator fun <T> get(field: ReportField<T>): T = getValueOf(field)

  /**
   * Sets the value of the field in this report row.
   *
   * @param field the field.
   * @param value the field's value.
   */
  operator fun <T> set(field: ReportField<T>, value: T) {
    if (field in reportFields) {
      data.putIfAbsent(field, value)
    }
  }

  /**
   * Sets the value of the field in this report row.
   *
   * @param field the field.
   * @param value the field's value.
   */
  @JvmName("setType0")
  operator fun <T : Type0<K>, K> set(field: ReportField<T>, value: K) {
    if (field in reportFields) {
      data.putIfAbsent(field, field.toType0(value))
    }
  }
}

/**
 * Represents the value in sql
 */
fun <T> ReportField<*>.toType0(value: T): Any? {
  return when(value) {
    is ExposedBlob -> value
    is Int -> {
      when (domain.kClass) {
        Month::class -> Month(value / 100, value % 100)
        Week::class -> Week(value / 100, value % 100)
        else -> null
      }
    }
    else -> null
  }
}
