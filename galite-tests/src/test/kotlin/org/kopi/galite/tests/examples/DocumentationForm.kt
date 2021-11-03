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

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.ListDomain
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.form.Key

class DocumentationForm : DictionaryForm() {

  //test Form Localization
  override val locale = Locale.UK
  // test Form Title
  override val title = "Test Form"

  //Menus Definition
  val file = menu("file")

  // Actors Definition
  val cut = actor(
    ident = "cut",
    menu = file,
    label = "cut",
    help = "cut element",
  ) {
    key = Key.F2          // key is optional here
    icon = "list"  // icon is optional here
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
 val cmd = command(item = cut) {
   // test mode !! can we change mode of commands in form??!!
   mode(Mode.UPDATE, Mode.QUERY)
    action = {
      println("Form Commands")
    }
  }

  /** Form Triggers Definition **/
  // test INIT form
  val initForm = trigger(INIT) {
    println("INIT trigger !!")
  }

  // test PREFORM form
  val preForm = trigger(PREFORM) {
    println("PREFORM trigger !!")
  }

  // test POSTFORM form
  val postForm = trigger(POSTFORM) {
    println("POSTFORM trigger !!")
  }

  // test QUITFORM form
  val quitForm = trigger(QUITFORM) {
    // actually not available
    true
  }

  // test RESET form !!
  val resetForm = trigger(RESET) {
    true
  }

  // test CHANGED form !!
  val changedForm = trigger(CHANGED) {
    true
  }
  /** Form Pages **/
  val p1 = page("Page1")
  val p2 = page("Page2")
  // insert blocks inside pages
  val block1 = p1.insertBlock(Block1())
  val block2 = p1.insertBlock(Block1())
  val block3 = p2.insertBlock(Block1())

  //simple block
  inner class Block1 : FormBlock(1, 10, "Block1") {

    val field = visit(domain = INT(20), position = at(1, 1)) {
      label = "field"
    }
  }
}

fun main() {
  runForm(formName = DocumentationForm())
}
