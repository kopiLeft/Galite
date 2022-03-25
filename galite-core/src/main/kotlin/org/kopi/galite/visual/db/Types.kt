/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.db

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.currentDialect
import org.kopi.galite.visual.type.Month
import org.kopi.galite.visual.type.Week

/**
 * Month column type.
 *
 * @param name the column name
 */
fun Table.month(name: String) = registerColumn<Month>(name, MonthColumnType())

/**
 * Week column type.
 *
 * @param name the column name
 */
fun Table.week(name: String) = registerColumn<Week>(name, WeekColumnType())

/**
 * Week column for storing weeks.
 */
class WeekColumnType : ColumnType() {
  override fun sqlType(): String = currentDialect.dataTypeProvider.integerType()
  override fun valueFromDB(value: Any): Week = when (value) {
    is Int -> Week(value)
    is Number -> valueFromDB(value.toInt())
    is String -> valueFromDB(value.toInt())
    else -> error("Unexpected value of type Week: $value of ${value::class.qualifiedName}")
  }

  override fun valueToDB(value: Any?): Any? = when (value) {
    is Week -> value.toSql()
    is Int -> value.toInt()
    else -> value
  }
}

/**
 * Months column for storing months.
 */
class MonthColumnType : ColumnType() {
  override fun sqlType(): String = currentDialect.dataTypeProvider.integerType()
  override fun valueFromDB(value: Any): Month = when (value) {
    is Int -> Month(value)
    is Number -> valueFromDB(value.toInt())
    is String -> valueFromDB(value.toInt())
    else -> error("Unexpected value of type Month: $value of ${value::class.qualifiedName}")
  }

  override fun valueToDB(value: Any?): Any? = when (value) {
    is Month -> value.toSql()
    is Int -> value.toInt()
    else -> value
  }
}
