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
package org.kopi.galite.common

import java.lang.RuntimeException

import org.jetbrains.exposed.sql.Column
import org.kopi.galite.domain.Domain
import org.kopi.galite.list.VBooleanColumn
import org.kopi.galite.list.VDateColumn
import org.kopi.galite.list.VIntegerColumn
import org.kopi.galite.list.VListColumn
import org.kopi.galite.list.VMonthColumn
import org.kopi.galite.list.VStringColumn
import org.kopi.galite.list.VTimeColumn
import org.kopi.galite.list.VTimestampColumn
import org.kopi.galite.list.VWeekColumn
import org.kopi.galite.type.Date
import org.kopi.galite.type.Month
import org.kopi.galite.type.Time
import org.kopi.galite.type.Timestamp
import org.kopi.galite.type.Week

/**
 * The description of a list element
 *
 * @param title                 the title of the column
 * @param column                the column itself
 * @param domain                the domain of the column
 */
class ListDescription(val title: String,
                      val column: Column<*>,
                      val domain: Domain<*>) {

  fun buildModel(): VListColumn {
    return when(domain.kClass) {
      Int::class, Long::class -> VIntegerColumn(title, column, domain.defaultAlignment, domain.width!!, true)
      String::class -> VStringColumn(title, column, domain.defaultAlignment, domain.width!!, true)
      Boolean::class -> VBooleanColumn(title, column, true)
      Date::class, java.util.Date::class ->
        VDateColumn(title, column, true)
      Month::class ->
        VMonthColumn(title, column, true)
      Week::class ->
        VWeekColumn(title, column, true)
      Time::class ->
        VTimeColumn(title, column, true)
      Timestamp::class ->
        VTimestampColumn(title, column, true)
      else -> throw RuntimeException("Type ${domain.kClass!!.qualifiedName} is not supported")
    }
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------
  /*
   * Generates localization.
   */
  fun genLocalization(writer: LocalizationWriter) {
    writer.genListDesc(column, title)
  }
}
