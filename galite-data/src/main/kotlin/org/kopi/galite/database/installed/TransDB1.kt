/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.database.installed

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert

import org.kopi.galite.database.Favorites
import org.kopi.galite.database.GroupParties
import org.kopi.galite.database.GroupRights
import org.kopi.galite.database.Groups
import org.kopi.galite.database.Modules
import org.kopi.galite.database.References
import org.kopi.galite.database.Symbols
import org.kopi.galite.database.UserRights
import org.kopi.galite.database.Users
import org.kopi.galite.database.Versions

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
                        References)
    // drop tables if existing (via common/dbsSchema of Kopi)
    tables.forEach { table ->
      if (table.exists()) {
        table.deleteAll()
      }
    }
    tables.forEach { table ->
      SchemaUtils.create(table)
    }
  }

  override val SOURCE: String
    get() = TODO("Not yet implemented")
}
