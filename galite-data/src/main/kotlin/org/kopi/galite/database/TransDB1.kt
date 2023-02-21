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

package org.kopi.galite.database

import java.time.Instant

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert

class TransDB1 : TransDB("galite", 1) {
  /**
   * Exécuter les requêtes SQL
   */
  override fun execute() {
    val tables = listOf(Versions,
                        Modules,
                        UserRights,
                        GroupRights,
                        GroupParties,
                        Symbols,
                        Favorites,
                        Users,
                        Groups,
                        References,
                        Dummy,
                        Dual)
    // drop tables if existing (via common/dbsSchema of Kopi)
    tables.forEach { table ->
      if (table.exists()) {
        table.deleteAll()
      }
    }
    tables.forEach { table ->
      SchemaUtils.create(table)
    }
    initTables()
  }

  /**
   * Initialisation de la BDD
   */
  private fun initTables() {
    Dummy.deleteAll() ; Dummy.insert { it[table] = "x" }
    Dual.deleteAll() ; Dual.insert { it[table] = "x" }
  }

  override val SOURCE: String
    get() = TODO("Not yet implemented")
}
