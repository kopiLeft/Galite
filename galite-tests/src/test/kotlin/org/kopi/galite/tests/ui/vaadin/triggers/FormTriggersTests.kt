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
package org.kopi.galite.tests.ui.vaadin.triggers

import java.util.Locale

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.enter
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.testing.expectConfirmNotification
import org.kopi.galite.testing.findField
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Access
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm

class FormTriggersTests : GaliteVUITestBase() {

  val form = TestTriggersForm.also { it.model }
  val report = ReportTriggersTest.also { it.model }
  val chart = ChartTriggersTest.also { it.model }

  @Before
  fun `login to the App`() {
    login()

    // Open the form
    form.model.doNotModal()
  }

  @Test
  fun `test form triggers`() {
    form.listActor.triggerCommand()

    form.deleteBlock.triggerCommand()
    expectConfirmNotification(true)

    form.block.fstnameClt.edit("Name")
    form.block.lastnameClt.edit("last Name")
    form.block.idClt.edit(2)
    form.insertMode.triggerCommand()
    form.saveBlock.triggerCommand()

    form.listActor.triggerCommand()
    form.block.fstnameClt.edit("First Name")
    form.saveBlock.triggerCommand()

    form.block.idClt.edit(2)
    form.resetBlock.triggerCommand()
    expectConfirmNotification(true)

    form.block.age.edit(1)

    form.secondBlock.enter()
    form.secondBlock.field.findField()
    form.secondBlock.field.edit(1)

    form.block.enter()

    form.resetForm.triggerCommand()
    expectConfirmNotification(true)

    report.model.doNotModal()
    report.quit.triggerCommand()

    chart.model.doNotModal()
    chart.quit.triggerCommand()

    form.quit.triggerCommand()
    expectConfirmNotification(true)

    val expectedTriggersList = listOf("ACCESS FIELD Trigger", "VALUE FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "VALUE FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "INIT FORM Trigger",
                                      "INIT BLOCK Trigger", "PREFORM FORM Trigger", "ACCESS BLOCK Trigger",
                                      "PREBLK BLOCK Trigger", "ACCESS FIELD Trigger", "ACCESS BLOCK Trigger",
                                      "VALBLK BLOCK Trigger", "PREQRY BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "VALUE FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "VALUE FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "POSTQRY BLOCK Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS BLOCK Trigger", "PREDEL BLOCK Trigger",
                                      "PREDEL FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "ACCESS FIELD Trigger", "VALUE FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "POSTDEL BLOCK Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "VALUE FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "VALUE FIELD Trigger",
                                      "ACCESS BLOCK Trigger", "ACCESS BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS BLOCK Trigger", "PREFLD FIELD Trigger", "ACCESS BLOCK Trigger",
                                      "VALUE FIELD Trigger", "ACCESS FIELD Trigger", "VALUE FIELD Trigger",
                                      "ACCESS FIELD Trigger", "VALUE FIELD Trigger", "VALUE FIELD Trigger",
                                      "VALUE FIELD Trigger", "ACCESS BLOCK Trigger", "PREVAL FIELD Trigger",
                                      "VALUE FIELD Trigger", "FORMAT FIELD Trigger", "VALUE FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALIDATE FIELD Trigger",
                                      "POSTCHG FIELD Trigger", "POSTFLD FIELD Trigger", "ACCESS BLOCK Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "DEFAULT BLOCK Trigger", "VALUE FIELD Trigger", "DEFAULT FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "ACCESS BLOCK Trigger", "VALBLK BLOCK Trigger",
                                      "CHANGED BLOCK Trigger", "PRESAVE BLOCK Trigger", "PREINS BLOCK Trigger",
                                      "PREINS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "POSTINS BLOCK Trigger", "POSTINS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "VALUE FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "VALUE FIELD Trigger",
                                      "DEFAULT BLOCK Trigger", "VALUE FIELD Trigger", "DEFAULT FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS BLOCK Trigger", "VALBLK BLOCK Trigger",
                                      "PREQRY BLOCK Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "ACCESS FIELD Trigger", "VALUE FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "POSTQRY BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS BLOCK Trigger", "ACCESS BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS BLOCK Trigger", "VALBLK BLOCK Trigger", "CHANGED BLOCK Trigger",
                                      "PRESAVE BLOCK Trigger", "PREUPD BLOCK Trigger", "PREUPD FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "POSTUPD BLOCK Trigger", "POSTUPD FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "VALUE FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "VALUE FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS BLOCK Trigger", "CHANGED BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "VALUE FIELD Trigger",
                                      "ACCESS FIELD Trigger", "VALUE FIELD Trigger", "VALUE FIELD Trigger",
                                      "VALUE FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS BLOCK Trigger",
                                      "ACCESS BLOCK Trigger", "AUTOLEAVE FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALFLD FIELD Trigger", "ACCESS BLOCK Trigger", "ACCESS BLOCK Trigger",
                                      "VALBLK BLOCK Trigger", "POSTBLK BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "PREREC FIELD Trigger", "ACCESS BLOCK Trigger", "ACCESS BLOCK Trigger",
                                      "POSTREC FIELD Trigger", "VALREC FIELD Trigger", "PREBLK BLOCK Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS BLOCK Trigger", "CHANGED FORM Trigger",
                                      "RESET FORM Trigger", "ACCESS BLOCK Trigger","PREREPORT REPORT Trigger",
                                      "POSTREPORT REPORT Trigger", "CHARTTYPE CHART Trigger",
                                      "INITCHART CHART Trigger", "CHARTTYPE CHART Trigger", "CHARTTYPE CHART Trigger",
                                      "PRECHART CHART Trigger", "POSTCHART CHART Trigger", "CHANGED FORM Trigger",
                                      "POSTFORM FORM Trigger")

    /*val expectedTriggersList = listOf("ACCESS FIELD Trigger", "INIT FORM Trigger", "INIT BLOCK Trigger",
                                      "PREFORM FORM Trigger", "VALUE FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "VALUE FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "ACCESS BLOCK Trigger",
                                      "PREBLK BLOCK Trigger", "ACCESS FIELD Trigger", "ACCESS BLOCK Trigger",
                                      "VALBLK BLOCK Trigger", "PREQRY BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "VALUE FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "VALUE FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "POSTQRY BLOCK Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS BLOCK Trigger", "PREDEL BLOCK Trigger",
                                      "PREDEL FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "ACCESS FIELD Trigger", "VALUE FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "POSTDEL BLOCK Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "VALUE FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "VALUE FIELD Trigger",
                                      "ACCESS BLOCK Trigger", "ACCESS BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS BLOCK Trigger", "PREFLD FIELD Trigger", "ACCESS BLOCK Trigger",
                                      "VALUE FIELD Trigger", "ACCESS FIELD Trigger", "VALUE FIELD Trigger",
                                      "ACCESS FIELD Trigger", "VALUE FIELD Trigger", "VALUE FIELD Trigger",
                                      "VALUE FIELD Trigger", "ACCESS BLOCK Trigger", "PREVAL FIELD Trigger",
                                      "VALUE FIELD Trigger", "FORMAT FIELD Trigger", "VALUE FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALIDATE FIELD Trigger",
                                      "POSTCHG FIELD Trigger", "POSTFLD FIELD Trigger", "ACCESS BLOCK Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "DEFAULT BLOCK Trigger", "VALUE FIELD Trigger", "DEFAULT FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "ACCESS BLOCK Trigger", "VALBLK BLOCK Trigger",
                                      "CHANGED BLOCK Trigger", "PRESAVE BLOCK Trigger", "PREINS BLOCK Trigger",
                                      "PREINS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "POSTINS BLOCK Trigger", "POSTINS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "VALUE FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "VALUE FIELD Trigger",
                                      "DEFAULT BLOCK Trigger", "VALUE FIELD Trigger", "DEFAULT FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS BLOCK Trigger", "VALBLK BLOCK Trigger",
                                      "PREQRY BLOCK Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "ACCESS FIELD Trigger", "VALUE FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "POSTQRY BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS BLOCK Trigger", "ACCESS BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS BLOCK Trigger", "VALBLK BLOCK Trigger", "CHANGED BLOCK Trigger",
                                      "PRESAVE BLOCK Trigger", "PREUPD BLOCK Trigger", "PREUPD FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS FIELD Trigger",
                                      "POSTUPD BLOCK Trigger", "POSTUPD FIELD Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "VALUE FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALUE FIELD Trigger", "VALUE FIELD Trigger", "VALUE FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS BLOCK Trigger", "CHANGED BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS FIELD Trigger", "VALUE FIELD Trigger",
                                      "ACCESS FIELD Trigger", "VALUE FIELD Trigger", "VALUE FIELD Trigger",
                                      "VALUE FIELD Trigger", "ACCESS FIELD Trigger", "ACCESS BLOCK Trigger",
                                      "ACCESS BLOCK Trigger", "AUTOLEAVE FIELD Trigger", "ACCESS FIELD Trigger",
                                      "VALFLD FIELD Trigger", "ACCESS BLOCK Trigger", "ACCESS BLOCK Trigger",
                                      "VALBLK BLOCK Trigger", "POSTBLK BLOCK Trigger", "ACCESS FIELD Trigger",
                                      "PREREC FIELD Trigger", "ACCESS BLOCK Trigger", "ACCESS BLOCK Trigger",
                                      "POSTREC FIELD Trigger", "VALREC FIELD Trigger", "PREBLK BLOCK Trigger",
                                      "ACCESS FIELD Trigger", "ACCESS BLOCK Trigger", "CHANGED FORM Trigger",
                                      "RESET FORM Trigger", "ACCESS BLOCK Trigger","PREREPORT REPORT Trigger",
                                      "POSTREPORT REPORT Trigger", "CHARTTYPE CHART Trigger",
                                      "INITCHART CHART Trigger", "CHARTTYPE CHART Trigger", "CHARTTYPE CHART Trigger",
                                      "PRECHART CHART Trigger", "POSTCHART CHART Trigger", "CHANGED FORM Trigger",
                                      "POSTFORM FORM Trigger")*/

    assertEquals(expectedTriggersList, list)
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      transaction {
        org.kopi.galite.tests.examples.initModules()
        SchemaUtils.create(Client)
        Client.insert {
          it[id] = 1
          it[firstNameClt] = "name"
        }
        SchemaUtils.createSequence(org.jetbrains.exposed.sql.Sequence("CLIENTSID"))
      }
    }
    val list = arrayListOf<String>()
  }
}

object TestTriggersForm : ReportSelectionForm(title = "Form to test triggers", locale = Locale.UK) {
  val action = menu("Action")

  val quit = actor(
    menu = action,
    label = "quit",
    help = "Quit",
  ) {
    key = Key.ESCAPE
    icon = Icon.QUIT
  }

  val resetForm = actor(
    menu = action,
    label = "reset Form",
    help = "Reset Form",
  ) {
    key = Key.F7
    icon = Icon.BREAK
  }

  val listActor = actor(
    menu = action,
    label = "list",
    help = "Display List",
  ) {
    key = Key.F2
    icon = Icon.LIST
  }

  val deleteBlock = actor(
    menu = action,
    label = "delete Block",
    help = " deletes block",
  ) {
    key = Key.F4
    icon = Icon.DELETE
  }

  val insertMode = actor(
    menu = action,
    label = "Insert",
    help = " Insert",
  ) {
    key = Key.F7
    icon = Icon.INSERT
  }

  val saveBlock = actor(
    menu = action,
    label = "Save Block",
    help = " Save Block",
  ) {
    key = Key.F3
    icon = Icon.SAVE
  }
  val resetBlock = actor(
    menu = action,
    label = "break",
    help = "Reset Block",
  ) {
    key = Key.F11
    icon = Icon.BREAK
  }

  val report = actor(
    menu = action,
    label = "CreateReport",
    help = "Create report",
  ) {
    key = Key.F8
    icon = Icon.REPORT
  }

  val graph = actor (
    menu =   action,
    label =  "Graph",
    help =   "show graph values",
  ) {
    key  =  Key.F9
    icon =  Icon.COLUMN_CHART
  }

  val quitCmd = command(item = quit) {
    quitForm()
  }

  val resetFormCmd = command(item = resetForm) {
    resetForm()
  }

  /** Form Triggers Definition **/
  // test INIT form trigger
  val initFormTrigger = trigger(INIT) {
    FormTriggersTests.list.add("INIT FORM Trigger")
  }

  // test PREFORM form trigger
  val preFormTrigger = trigger(PREFORM) {
    FormTriggersTests.list.add("PREFORM FORM Trigger")
  }

  // test POSTFORM form trigger
  val postFormTrigger = trigger(POSTFORM) {
    FormTriggersTests.list.add("POSTFORM FORM Trigger")
  }

  // test QUITFORM form trigger
  val quitFormTrigger = trigger(QUITFORM) {
    //actually not available
    FormTriggersTests.list.add("QUITFORM FORM Trigger")
  }

  // test RESET form trigger
  val resetFormTrigger = trigger(RESET) {
    FormTriggersTests.list.add("RESET FORM Trigger")
  }

  // test CHANGED form trigger
  val changedFormTrigger = trigger(CHANGED) {
    FormTriggersTests.list.add("CHANGED FORM Trigger")
  }

  val block = insertBlock(TestTriggersBlock()) {

    command(item = listActor) {
      serialQuery()
    }

    command(item = deleteBlock) {
      deleteBlock()
    }

    command(item = insertMode) {
      insertMode()
    }

    command(item = saveBlock) {
      saveBlock()
    }

    command(item = resetBlock) {
      resetBlock()
    }

    command(item = report) {
      createReport {
        ReportTriggersTest
      }
    }

    command(item = graph) {
      showChart(ChartTriggersTest)
    }

    // test PREQRY block trigger
    trigger(PREQRY) {
      FormTriggersTests.list.add("PREQRY BLOCK Trigger")
    }

    // test POSTQRY block trigger
    trigger(POSTQRY) {
      FormTriggersTests.list.add("POSTQRY BLOCK Trigger")
    }

    // test PREDEL block trigger
    trigger(PREDEL) {
      FormTriggersTests.list.add("PREDEL BLOCK Trigger")
    }

    // test POSTDEL block trigger
    trigger(POSTDEL) {
      FormTriggersTests.list.add("POSTDEL BLOCK Trigger")
    }

    // test PREINS block trigger
    trigger(PREINS) {
      FormTriggersTests.list.add("PREINS BLOCK Trigger")
    }

    // test POSTINS block trigger
    trigger(POSTINS) {
      FormTriggersTests.list.add("POSTINS BLOCK Trigger")
    }

    // test PREUPD block trigger
    trigger(PREUPD) {
      FormTriggersTests.list.add("PREUPD BLOCK Trigger")
    }

    // test POSTUPD block trigger
    trigger(POSTUPD) {
      FormTriggersTests.list.add("POSTUPD BLOCK Trigger")
    }

    // test PRESAVE block trigger
    trigger(PRESAVE) {
      FormTriggersTests.list.add("PRESAVE BLOCK Trigger")
    }

    // test PREREC block trigger
    trigger(PREREC) {
      FormTriggersTests.list.add("PREREC BLOCK Trigger")
    }

    // test POSTREC block trigger
    trigger(POSTREC) {
      FormTriggersTests.list.add("POSTREC BLOCK Trigger")
    }

    // test PREBLK block trigger
    trigger(PREBLK) {
      FormTriggersTests.list.add("PREBLK BLOCK Trigger")
    }

    // test POSTBLK block trigger
    trigger(POSTBLK) {
      FormTriggersTests.list.add("POSTBLK BLOCK Trigger")
    }

    // test VALBLK block trigger
    trigger(VALBLK) {
      FormTriggersTests.list.add("VALBLK BLOCK Trigger")
    }

    // test VALREC block trigger : need to test this on multi block
    trigger(VALREC) {
      FormTriggersTests.list.add("VALREC BLOCK Trigger")
    }

    // test DEFAULT block trigger
    trigger(DEFAULT) {
      FormTriggersTests.list.add("DEFAULT BLOCK Trigger")
    }

    // test INIT block trigger
    trigger(INIT) {
      FormTriggersTests.list.add("INIT BLOCK Trigger")
    }

    // test RESET block trigger : check !!
    trigger(RESET) {
      FormTriggersTests.list.add("RESET BLOCK Trigger")
    }

    // test CHANGED block trigger
    trigger(CHANGED) {
      FormTriggersTests.list.add("CHANGED BLOCK Trigger")
    }

    // test ACCESS block trigger
    trigger(ACCESS) {
      FormTriggersTests.list.add("ACCESS BLOCK Trigger")
    }
  }

  val secondBlock = insertBlock(SecondBlock())

  class TestTriggersBlock : Block("Test Block", 1, 10) {
    val c = table(Client)

    val id = hidden(domain = INT(20)) { columns(c.id) }
    val uc = hidden(domain = INT(20)) { columns(c.uc) }
    val ts = hidden(domain = INT(20)) { columns(c.ts) }

    val idClt = visit(domain = INT(30), position = at(1, 1..2)) {
      label = "ID"
      help = "The client id"
      columns(c.id)
      value = 1
    }

    val fstnameClt = visit(domain = STRING(25), position = at(2, 1)) {
      label = "First Name"
      help = "The client first name"
      columns(c.firstNameClt) {
        priority = 9
      }
    }

    val lastnameClt = visit(domain = STRING(25), position = at(3, 1)) {
      label = "Last Name"
      help = "The client Last name"

      trigger(PREFLD) {
        FormTriggersTests.list.add("PREFLD FIELD Trigger")
      }

      trigger(POSTFLD) {
        FormTriggersTests.list.add("POSTFLD FIELD Trigger")
      }

      trigger(POSTCHG) {
        FormTriggersTests.list.add("POSTCHG FIELD Trigger")
      }

      trigger(PREVAL) {
        FormTriggersTests.list.add("PREVAL FIELD Trigger")
      }

      trigger(VALIDATE) {
        FormTriggersTests.list.add("VALIDATE FIELD Trigger")
      }

      //Not defined actually
      trigger(FORMAT) {
        FormTriggersTests.list.add("FORMAT FIELD Trigger")
      }

      access {
        FormTriggersTests.list.add("ACCESS FIELD Trigger")
        Access.VISIT
      }

      trigger(VALUE) {
        FormTriggersTests.list.add("VALUE FIELD Trigger")
        "VALUE"
      }

      trigger(PREINS) {
        FormTriggersTests.list.add("PREINS FIELD Trigger")
      }

      trigger(PREUPD) {
        FormTriggersTests.list.add("PREUPD FIELD Trigger")
      }

      trigger(PREDEL) {
        FormTriggersTests.list.add("PREDEL FIELD Trigger")
      }

      trigger(POSTINS) {
        FormTriggersTests.list.add("POSTINS FIELD Trigger")
      }

      trigger(POSTUPD) {
        FormTriggersTests.list.add("POSTUPD FIELD Trigger")
      }
    }

    val age = visit(domain = INT(30), position = at(4, 1)) {
      label = "age"

      trigger(AUTOLEAVE) {
        FormTriggersTests.list.add("AUTOLEAVE FIELD Trigger")
      }
      trigger(VALFLD) {
        FormTriggersTests.list.add("VALFLD FIELD Trigger")
      }
    }

    val city = visit(domain = STRING(30), position = at(5, 1)) {
      label = "city"

      trigger(DEFAULT) {
        FormTriggersTests.list.add("DEFAULT FIELD Trigger")
      }
    }
  }

  class SecondBlock : Block("Second Block", 10, 10) {
    val field = visit(domain = INT(30), position = at(1, 1)) {
      label = "field"

      trigger(PREREC) {
        FormTriggersTests.list.add("PREREC FIELD Trigger")
      }

      trigger(POSTREC) {
        FormTriggersTests.list.add("POSTREC FIELD Trigger")
      }

      trigger(VALREC) {
        FormTriggersTests.list.add("VALREC FIELD Trigger")
      }
    }
  }
}

object Client : Table("CLIENTS") {
  val id = integer("ID").autoIncrement()
  val uc = integer("UC").default(0)
  val ts = integer("TS").default(0)
  val firstNameClt = varchar("FIRSTNAME", 25)
}
