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
package org.kopi.galite.visual.dsl.common

import java.lang.RuntimeException

import org.jetbrains.exposed.sql.*

import org.kopi.galite.database.MonthColumnType
import org.kopi.galite.visual.domain.ListDomain
import org.kopi.galite.visual.list.VBooleanColumn
import org.kopi.galite.visual.list.VDateColumn
import org.kopi.galite.visual.list.VDecimalColumn
import org.kopi.galite.visual.list.VIntegerColumn
import org.kopi.galite.visual.list.VListColumn
import org.kopi.galite.visual.list.VMonthColumn
import org.kopi.galite.visual.list.VStringColumn

/**
 * The description of a list element
 *
 * @param title                 the title of the column
 * @param column                the column itself
 * @param domain                the domain of the column
 */
class ListDescription(val title: String,
                      val column: Column<*>,
                      val domain: ListDomain<*>) {

  val type = if(column.columnType is AutoIncColumnType) {
    (column.columnType as AutoIncColumnType).delegate
  } else {
    column.columnType
  }

  var width = when (type) {
    is VarCharColumnType -> type.colLength
    is CharColumnType -> type.colLength
    is CharacterColumnType -> 1
    is BinaryColumnType -> type.length
    else -> domain.width!!
  }

  fun buildModel(): VListColumn {
    return when(type) {
      is IntegerColumnType, is LongColumnType -> VIntegerColumn(title, column, domain.tableInitializer, domain.defaultAlignment, width, true)
      is StringColumnType -> VStringColumn(title, column, domain.tableInitializer, domain.defaultAlignment, width, true)
      is BooleanColumnType -> VBooleanColumn(title, column, domain.tableInitializer, true)
      is IDateColumnType, -> VDateColumn(title, column, domain.tableInitializer, true)
      is DecimalColumnType -> VDecimalColumn(title, column, domain.tableInitializer,domain.defaultAlignment, width, domain.height ?: 6, true)
      is MonthColumnType -> VMonthColumn(title, column, domain.tableInitializer, true)
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
