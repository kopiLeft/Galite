/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.tests.form

import java.util.Locale

import kotlin.test.assertEquals

import org.junit.Test

import org.jetbrains.exposed.sql.Table
import org.kopi.galite.db.DBSchema

import org.kopi.galite.domain.Domain
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.dsl.DictionaryForm
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.tests.JApplicationTestBase

class DictionaryFormTests: JApplicationTestBase() {

  @Test
  fun sourceFormTest() {
    val formModel = TestForm.model
    assertEquals(TestForm::class.qualifiedName!!.replace(".", "/"), formModel.source)
  }
}

object User1: Table() {
  val id = integer("id")
  val name = varchar("name", 20)
  val age = integer("age")
}

object DictionaryFormTest: DictionaryForm() {
  override val locale = Locale.FRANCE
  override val title = "form for test"

  val action = menu("Action")

  val graph = actor (
          ident =  "graph",
          menu = TestForm.action,
          label = "Graph for test",
          help =  "show graph values" ,
  ) {
    key  =  Key.F9
    icon =  "column_chart"  // icon is optional here
  }

  val page = page("test page")

  val tb1 =  insertBlock(TestBlock1, page) {
    command(item = graph) {
      this.name = "graphe"
      mode(VConstants.MOD_UPDATE, VConstants.MOD_INSERT, VConstants.MOD_QUERY)
      action = {
        println("---------------------------------- IN TEST COMMAND ----------------------------------")
        recursiveQuery()
      }
    }
  }

}

object TestBlock1 : FormBlock(1, 1, "Test", "Test block") {
  val u = table(User)
  val i = index(message = "ID should be unique")

  val id = visit(domain = Domain<Int>(20), position = at(1, 1)) {
    label = "id"
    help = "The user id"
    columns(DBSchema.Users.id)
  }
  val name = visit(domain = Domain<String>(20), position = at(2, 2)) {
    label = "name11"
    help = "The user name"
    columns(DBSchema.Users.shortName)
  }
  val age = visit(domain = Domain<Int>(3), position = at(2, 3)) {
    label = "age"
    help = "The user age"
    columns(u.age) {
      index = i
      priority = 1
    }
  }
}
