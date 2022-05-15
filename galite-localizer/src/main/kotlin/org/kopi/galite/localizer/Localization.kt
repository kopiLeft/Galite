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
package org.kopi.galite.localizer

import java.io.File
import java.util.Locale

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.database.Connection
import org.kopi.galite.database.Modules
import org.kopi.galite.database.Symbols
import org.kopi.galite.visual.dsl.common.Window
import org.kopi.galite.visual.Module

fun localizeWindows(url: String,
                    driver: String,
                    userName: String,
                    password: String,
                    locales: List<Locale>,
                    schema: String? = null,
                    output: String? = null) {
  locales.forEach {
    localizeWindows(url, driver, userName, password, it, schema, output)
  }
}

fun localizeWindows(url: String,
                    driver: String,
                    userName: String,
                    password: String,
                    locale: Locale,
                    schema: String? = null,
                    output: String? = null) {
  val dBConnection = Connection.createConnection(url, driver, userName, password, false, schema)
  val modules = fetchModules()
  modules.forEach {
    if (it.objectName != null) {
      val module = Class.forName(it.objectName).kotlin.objectInstance ?: Class.forName(it.objectName).newInstance()

      if (module is Window) {
        localizeWindow(module, locale, output)
      }
    }
  }
}

fun localizeWindow(module: Window, locale: Locale, output: String? = null) {
  val destination = if (output != null) {
    output + File.separator + module.javaClass.`package`.name.replace(".", "/")
  } else {
    "src/main/resources" + File.separator + module.javaClass.`package`.name.replace(".", "/")
  }
  module.genLocalization(destination, locale)
}

/**
 * Fetches the modules from the database.
 */
private fun fetchModules(): MutableList<Module> {
  val localModules: ArrayList<Module> = ArrayList()
  var icon: String? = null
  val modulesQuery =
    Modules.slice(Modules.id,
                  Modules.parent,
                  Modules.shortName,
                  Modules.sourceName,
                  Modules.objectName,
                  Modules.priority,
                  Modules.symbol)
      .selectAll()
      .orderBy(Modules.priority to SortOrder.DESC)

  transaction {
    modulesQuery.forEach {
      if (it[Modules.symbol] != null && it[Modules.symbol] != 0) {
        val symbol = it[Modules.symbol] as Int

        Symbols.select { Symbols.id eq symbol }.forEach { res ->
          icon = res[Symbols.objectName]
        }
      }

      val module = Module(it[Modules.id],
                          it[Modules.parent],
                          it[Modules.shortName],
                          it[Modules.sourceName],
                          it[Modules.objectName],
                          Module.ACS_PARENT,
                          it[Modules.priority],
                          icon)

      localModules.add(module)
    }
  }
  return localModules
}
