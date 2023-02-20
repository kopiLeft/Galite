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

import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.nextIntVal
import org.jetbrains.exposed.sql.select


abstract class TransDB(val module: String, val version: Int) {
  /**
   * Run transDB queries
   */
  fun run() {
    incrementVersion()
    execute()
  }

  /**
   * Increment module version
   */
  private fun incrementVersion() {
    if (Versions.exists()) {
      Versions.insert {
        it[packageName] = module
        it[number] = version
        it[date] = Instant.now()
      }
    }
  }

  /**
   * Add menu entry to table Modules
   */
  fun addMenu(menu: String,
              parentId: Int? = null,
              parent: String? = null,
              priority: Int,
              objectName: String?= null,
              symbol: Int? = null)
  {
    Modules.insert {
      it[id] = ModulesId.nextIntVal()
      it[uc] = 0
      it[ts] = 0
      it[shortName] = menu
      it[Modules.parent] = parentId ?: Modules.slice(id).select { shortName eq parent!! }.map { module -> module[id] }.first()
      it[sourceName] = SOURCE
      it[Modules.priority] = priority
      it[Modules.objectName] = objectName
      it[Modules.symbol] = symbol
    }
  }

  /**
   * Execute SQL queries
   */
  abstract fun execute()

  abstract val SOURCE: String
}
