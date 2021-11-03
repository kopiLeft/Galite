/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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

import java.util.Locale

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.tests.db.connectToDatabase
import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.visual.WindowController

class DocumentationReportTriggers : Form() {

  override val locale = Locale.UK

  override val title = "Test Report Form"

  //Menus Definition
  val file = menu("file")

  val report = actor(
    ident = "report",
    menu = file,
    label = "CreateReport",
    help = "Create report",
  ) {
    key = Key.F8
    icon = "report"
  }

    /** Calling reports **/
  // call report with  WindowController.windowController.doNotModal
  val block2 = insertBlock(Block1())

  //simple block
  inner class Block1 : FormBlock(1, 10, "Block1") {
    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }

    init {
      command(item = report) {
        action = {
          WindowController.windowController.doNotModal(DocumentationReportTriggersR())
        }
      }
    }
  }
}

fun main() {
  connectToDatabase()
  transaction {
    SchemaUtils.create(TestTable, TestTable2, TestTriggers)
    SchemaUtils.createSequence(org.jetbrains.exposed.sql.Sequence("TESTTABLE1ID"))
    SchemaUtils.createSequence(org.jetbrains.exposed.sql.Sequence("TRIGGERSID"))
    TestTable.insert {
      it[id] = 1
      it[name] = "Test-1"
      it[age] = 40
    }
    TestTable.insert {
      it[id] = 2
      it[name] = "Test-1"
      it[age] = 30
    }
    TestTable.insert {
      it[id] = 3
      it[name] = "TEST-2"
      it[age] = 30
    }
  }

  runForm(formName = DocumentationReportTriggers())
}
