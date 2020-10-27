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

package org.kopi.galite.db

import org.jetbrains.exposed.sql.Table

class DBSchema {
  object references : Table() {
    val id = integer("id").uniqueIndex().autoIncrement()
    val table = varchar("table", 255)
    val column = varchar("column", 255)
    val action = char("action", 1)
    val reference  = varchar("reference"  , 255)

    init {
      uniqueIndex("REFERENCES0",table, column)
    }
    override val primaryKey = PrimaryKey(id, name = "PK_REFERENCES_ID")
  }
}
