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
package org.kopi.galite.tests.examples

import java.util.Locale

import org.jetbrains.exposed.sql.insert
import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.db.transaction
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.ListDomain
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key

class DocumentationForm : DictionaryForm(title = "Test Form", locale = Locale.UK) {

  //Menus Definition
  val file = menu("file")

  // Actors Definition
  val cut = actor(
    menu = file,
    label = "cut",
    help = "cut element",
  ) {
    key = Key.F2
    icon = Icon.LIST
  }

  val quit = actor(
    menu = file,
    label = "quit",
    help = "Quit",
  ) {
    key = Key.ESCAPE
    icon = Icon.QUIT
  }

  val resetForm = actor(
    menu = file,
    label = "resetForm",
    help = "Reset Form",
  ) {
    key = Key.F7
    icon = Icon.BREAK
  }

  val quitCmd = command(item = quit) {
    quitForm()
  }

  val resetFormCmd = command(item = resetForm) {
    resetForm()
  }

 // Types Definition Create CodeDomain & ListDomain Inside Form
  object Days: CodeDomain<Int>() {
    init {
      "Sunday" keyOf 1
      "Monday" keyOf 2
      "Tuesday" keyOf 3
      "Wednesday" keyOf 4
      "Thursday" keyOf 5
      "Friday" keyOf 6
      "Saturday" keyOf 7
    }
  }

  object CurrentDegree : ListDomain<String>(20) {
    override val table = TestTable

    init {
      "id" keyOf table.id
      "name" keyOf table.name
    }
  }

  // Commands Definition
  // test mode
  val cmd = command(item = cut, Mode.UPDATE, Mode.QUERY) {
    model.notice("form command")
  }

  /** Form Triggers Definition **/
  // test INIT form trigger
  val initFormTrigger = trigger(INIT) {
    formTriggers.initTriggerForm.value = "INIT Trigger"
  }

  // test PREFORM form trigger
  val preFormTrigger = trigger(PREFORM) {
    formTriggers.preFormTriggerForm.value = "PREFORM Trigger"
  }

  // test POSTFORM form trigger
  val postFormTrigger = trigger(POSTFORM) {
    transaction {
      initDocumentationData()
      TestTriggers.insert {
        it[id] = 5
        it[INS] = "POSTFORM Trigger"
      }
    }
  }

  // test QUITFORM form trigger
  val quitFormTrigger = trigger(QUITFORM) {
    // actually not available
    true
  }

  // test RESET form : click on resetForm commands and check that form keep field values
  val resetFormTrigger = trigger(RESET) {
    true
  }

  // test CHANGED form
  val changedFormTrigger = trigger(CHANGED) {
    true
  }

  /** Form Pages **/
  val p1 = page("first page")
  val p2 = page("second page")
  val p3 = page("third page")

  // insert blocks inside pages
  val formTriggers = p1.insertBlock(TriggerForm())
  val simpleBlock = p2.insertBlock(SimpleBlock())

  // simple block
  inner class SimpleBlock : Block("SimpleForm", 1, 10) {
    init {
      border = Border.LINE
    }

    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }

  // form triggers
  inner class TriggerForm : Block("Block to test: from triggers", 1, 10) {

    val initTriggerForm = visit(domain = STRING(20), position = at(1, 1)) {
      label = "INIT Trigger Form"
    }
    val preFormTriggerForm = visit(domain = STRING(20), position = at(1, 2)) {
      label = "PREFORM Trigger Form"
    }
  }
}

fun main() {
  runForm(formName = DocumentationForm())
}
