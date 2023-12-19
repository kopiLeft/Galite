/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2023 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.tests.examples

import org.jetbrains.exposed.sql.insert
import java.util.Locale

import org.jetbrains.exposed.sql.selectAll

import org.kopi.galite.visual.database.transaction
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.pivottable.Dimension
import org.kopi.galite.visual.dsl.pivottable.PivotTable

/**
 *  Pivot table Tests :
 *    test title
 *    test locale
 *    test menu
 *    test actors
 */
class DocumentationPivotTableP : PivotTable(title = "Test Pivot table", locale = Locale.UK) {

  //test Menu
  val action = menu("Action")

  val helpForm = actor(
    menu = action,
    label = "Help",
    help = " Help"
  ) {
    key = Key.F1
    icon = Icon.HELP
  }

  val quit = actor(
    menu = action,
    label = "Quit",
    help = "Quit",
  ) {
    key = Key.ESCAPE
    icon = Icon.QUIT
  }

  val quitCmd = command(item = quit) {
    model.close()
  }

  val helpCmd = command(item = helpForm) {
    model.notice("Pivot table command")
    model.showHelp()
  }

  val name = dimension(STRING(25), Dimension.Position.COLUMN) {
    label = "Name"
    help = "The name"
  }

  val lastName = dimension(STRING(25), Dimension.Position.COLUMN) {
    label = "last Name "
    help = "The last name"
  }

  val middleName = dimension(STRING(25), Dimension.Position.COLUMN) {
    label = "middleName"
    help = "The middle Name"
  }

  val age = measure(INT(25)) {
    label = "age"
    help = "age"
  }

  val testTable = TestTable.selectAll()

  /** Pivot table Triggers Declaration **/
  // trigger INIT
  val init = trigger(INIT) {
    transaction {
      TestTriggers.insert {
        it[id] = 5
        it[INS] = "INIT Trigger"
      }
    }
  }

  init {
    transaction {
      testTable.forEach { result ->
        add {
          this[name] = result[TestTable.name]
          this[lastName] = result[TestTable.lastName] ?: ""
          this[middleName] = result[TestTable.lastName] ?: ""
          this[age] = result[TestTable.age] ?: 0
        }
      }
    }
  }
}
