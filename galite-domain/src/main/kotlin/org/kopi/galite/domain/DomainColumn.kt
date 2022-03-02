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

package org.kopi.galite.domain

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.time
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.joda.time.DateTime
import org.kopi.galite.domain.type.Month
import org.kopi.galite.domain.type.Timestamp
import org.kopi.galite.domain.type.Week

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
        Timestamp::class -> timestamp(name)
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
fun Table.month(name: String) = integer(name)

/**
 * Week column type.
 *
 * @param name the column name
 */
fun Table.week(name: String) = integer(name)
