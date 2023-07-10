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
package org.kopi.galite.demo.client

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Client

//@Route
class ClientR3 : DSLPivotTable("ClientR")  {

  val columns = mapOf("firstName" to String::class.java,
      "lastName" to String::class.java,
      "addressClt" to String::class.java,
      "ageClt" to Int::class.java,
      "countryClt" to String::class.java,
      "cityClt" to String::class.java,
      "zipCodeClt" to Int::class.java,
      "activeClt" to String::class.java)

  fun fillRows () : List<List<Any>> {
      val clients = Client.selectAll()
      val rows = mutableListOf(listOf<Any>())

      transaction {
          clients.forEach {
              val row = listOf(Client.firstNameClt, Client.lastNameClt, Client.addressClt,
                  Client.ageClt, Client.countryClt, Client.cityClt, Client.zipCodeClt, Client.activeClt)
              rows.add(row)
          }
      }

      return rows
  }

    val rowOptions = listOf("countryClt", "activeClt")
    val colOptions = listOf("ageClt")

    init {
        setColumns(columns)
        setRows(fillRows())
        setOptions(colOptions, rowOptions)
    }
}
