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
package org.kopi.galite.database

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.currentDialect
import org.kopi.galite.type.Color
import org.kopi.galite.type.Month
import org.kopi.galite.type.Week

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
 * Color column type.
 *
 * @param name the column name
 */
fun Table.color(name: String) = registerColumn<Color>(name, ColorColumnType())

/**
 * Week column for storing weeks.
 */
class WeekColumnType : ColumnType<Week>() {
  override fun sqlType(): String = currentDialect.dataTypeProvider.integerType()
  override fun valueFromDB(value: Any): Week = when (value) {
    is Int -> Week(value / 100, value % 100)
    is Number -> valueFromDB(value.toInt())
    is String -> valueFromDB(value.toInt())
    else -> error("Unexpected value of type Week: $value of ${value::class.qualifiedName}")
  }

  override fun valueToDB(value: Week?): Any? = when (value) {
    is Week -> value.toSql()
    else -> value
  }
}

/**
 * Months column for storing months.
 */
class MonthColumnType : ColumnType<Month>() {
  override fun sqlType(): String = currentDialect.dataTypeProvider.integerType()
  override fun valueFromDB(value: Any): Month = when (value) {
    is Int -> Month(value / 100, value % 100)
    is Number -> valueFromDB(value.toInt())
    is String -> valueFromDB(value.toInt())
    else -> error("Unexpected value of type Month: $value of ${value::class.qualifiedName}")
  }

  override fun valueToDB(value: Month?): Any? = when (value) {
    is Month -> value.toSql()
    else -> value
  }
}

/**
 * Color column for storing colors.
 */
class ColorColumnType : ColumnType<Color>() {
  override fun sqlType(): String = currentDialect.dataTypeProvider.integerType()
  override fun valueFromDB(value: Any): Color {
    return when (value) {
      is Int -> Color(value)
      is Number -> valueFromDB(value.toInt())
      is String -> valueFromDB(value.toInt())
      else -> error("Unexpected value of type Color: $value of ${value::class.qualifiedName}")
    }
  }

  override fun valueToDB(value: Color?): Any? {
    return  when (value) {
      is Color -> value.toSql()
      else -> value
    }
  }
}
