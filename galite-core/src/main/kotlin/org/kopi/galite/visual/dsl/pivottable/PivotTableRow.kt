/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2023 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.dsl.pivottable

import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.kopi.galite.type.Month
import org.kopi.galite.type.Type0
import org.kopi.galite.type.Week

/**
 * Represents a data row of a [PivotTable].
 *
 * @param pivotTableFields the fields that exists in the pivot table.
 */
class PivotTableRow(private val pivotTableFields: MutableList<PivotTableField<*>>) {
  /** A pivot table data row */
  val data = mutableMapOf<PivotTableField<*>, Any?>()

  /**
   * Returns data value of a specific [PivotTableField] in this pivot table row.
   *
   * @param field the field.
   * @return  data value for a specific [PivotTableField].
   */
  @Suppress("UNCHECKED_CAST")
  fun <T> getValueOf(field: PivotTableField<T>): T = data[field] as T

  /**
   * Gets the value of the field in this pivot table row.
   *
   * @param field the field.
   * @return  data value for a specific [PivotTableField].
   */
  operator fun <T> get(field: PivotTableField<T>): T = getValueOf(field)

  /**
   * Sets the value of the field in this pivot table row.
   *
   * @param field the field.
   * @param value the field's value.
   */
  operator fun <T> set(field: PivotTableField<T>, value: T) {
    if (field in pivotTableFields) {
      data.putIfAbsent(field, value)
    }
  }

  /**
   * Sets the value of the field in this pivot table row.
   *
   * @param field the field.
   * @param value the field's value.
   */
  @JvmName("setType0")
  operator fun <T : Type0<K>, K> set(field: PivotTableField<T>, value: K) {
    if (field in pivotTableFields) {
      data.putIfAbsent(field, field.toType0(value))
    }
  }
}

/**
 * Represents the value in sql
 */
fun <T> PivotTableField<*>.toType0(value: T): Any? {
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
