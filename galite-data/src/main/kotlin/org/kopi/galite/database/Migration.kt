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

package org.kopi.galite.database

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.util.base.InconsistencyException

abstract class Migration {
  /**
   * Execute command line
   */
  fun run(args: Array<String>) : Boolean {
    val processCommandLine = processCommandLine(args, options)

    try {
      run(processCommandLine)
    } catch (e: Exception) {
      e.printStackTrace()
      return false
    }
    return true
  }

  /**
   * Run database update
   */
  fun run(processCommandLine: Boolean) {
    connection(processCommandLine)
    var currentVersion: Int = -1
    var currentModule: String = ""

    // Execute transDB
    for (transDB in TRANSDBS) {
      if (transDB.module != currentModule) {
        currentModule = transDB.module
        transaction {
          currentVersion = loadModuleVersion(currentModule)
        }
        println("Current version of module \"${transDB.module}\" = $currentVersion")
      }

      if (transDB.version > currentVersion) {
        println("Executing transDB ${transDB.version} of module \"${transDB.module}\"")
        transaction {
          transDB.run()
        }
      }
    }
  }

  /**
   * Process commande line
   */
  private fun processCommandLine(args: Array<String>, options: ConnectionOptions): Boolean {
    return options.parseCommandLine(args) && checkOptions()
  }

  /**
   * Check ConnectionOptions
   */
  fun checkOptions(): Boolean {
    return options.database != null && options.driver != null && options.username != null && options.password != null
  }

  /**
   * Database connection
   */
  fun connection(processCommandLine: Boolean) {
    if (processCommandLine) {
      Connection.createConnection(options.database!!,
                                  options.driver!!,
                                  options.username!!,
                                  options.password!!,
                                  false,
                                  options.schema,
                                  options.trace)
    } else {
      Connection.createConnection(Configuration.getString("database")!!,
                                  Configuration.getString("driver")!!,
                                  Configuration.getString("username")!!,
                                  Configuration.getString("password")!!,
                                  false,
                                  Configuration.getString("schema"),
                                  Configuration.getString("trace")?.toInt())
    }
  }

  /**
   * Get module version
   */
  private fun loadModuleVersion(module: String): Int {
    return if (Versions.exists()) {
      Versions.slice(Versions.number).select { Versions.packageName eq module }.orderBy(Versions.date, SortOrder.DESC).map {
        it[Versions.number]
      }.firstOrNull() ?: -1
    } else if (module == "galite") {
      -1
    } else {
      throw InconsistencyException("Can not find table Versionen. Please run Galite TransDB")
    }
  }


  // ------------------------------------------------------------------
  // DATA MEMBERS
  // ------------------------------------------------------------------

  open var              options: ConnectionOptions = ConnectionOptions()
  abstract val          TRANSDBS : Array<TransDB>
}