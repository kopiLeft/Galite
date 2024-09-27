/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.domain

import java.time.Instant
import java.time.LocalDateTime

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.time
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.vendors.currentDialect
import org.joda.time.DateTime
import org.kopi.galite.type.Month
import org.kopi.galite.type.Week

class DomainColumn<T>(val column: Column<T>,
                      val columnProperties: ColumnProperties,
                      val domain: Domain<T>)

class ColumnProperties {
    var title = "" // Label
    var default = "" // Default value
    var readonly = false // Read only?
    var help = "" // The help text
    var required = true // Required value?
    var unique = false // Unique?
}

/**
 * A date column to store a date.
 *
 * @param name The column name
 */
inline fun <reified T> Table.column(name: String,
                                    domain: Domain<T>,
                                    noinline init: (ColumnProperties.() -> Unit)? = null): DomainColumn<T> {
    val kClass = T::class

    val column = when (kClass) {
        Int::class -> integer(name)
        Long::class -> long(name)
        String::class -> varchar(name, domain.maxWidth ?: 0)
        java.math.BigDecimal::class -> decimal(name, domain.precision ?: 0, domain.scale ?: 0)
        Boolean::class -> bool(name)
        org.joda.time.LocalDate::class, java.time.LocalDate::class, java.sql.Date::class, java.util.Date::class ->
            date(name)
        Month::class -> month(name)
        Week::class -> week(name)
        org.joda.time.LocalTime::class, java.time.LocalTime::class -> time(name)
        Instant::class, LocalDateTime::class -> timestamp(name)
        DateTime::class -> datetime(name)
        ExposedBlob::class -> blob(name)
        else -> {
            throw RuntimeException("Type ${kClass.qualifiedName} is not supported")
        }
    } as Column<T>

    val cp = ColumnProperties()

    if (init != null) {
        cp.init()
    }

    return DomainColumn(column, cp, domain)
}

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
