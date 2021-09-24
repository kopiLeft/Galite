/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

import org.jetbrains.exposed.sql.AutoIncColumnType
import org.jetbrains.exposed.sql.BooleanColumnType
import java.lang.RuntimeException

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.IDateColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.StringColumnType
import org.kopi.galite.domain.Domain
import org.kopi.galite.list.VBooleanColumn
import org.kopi.galite.list.VDateColumn
import org.kopi.galite.list.VIntegerColumn
import org.kopi.galite.list.VListColumn
import org.kopi.galite.list.VStringColumn

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
    val type = if(column.columnType is AutoIncColumnType) {
      (column.columnType as AutoIncColumnType).delegate
    } else {
      column.columnType
    }

    return when(type) {
      is IntegerColumnType, is LongColumnType -> VIntegerColumn(title, column, domain.defaultAlignment, domain.width!!, true)
      is StringColumnType -> VStringColumn(title, column, domain.defaultAlignment, domain.width!!, true)
      is BooleanColumnType -> VBooleanColumn(title, column, true)
      is IDateColumnType, ->
        VDateColumn(title, column, true)
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
