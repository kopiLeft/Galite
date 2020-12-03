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

import java.awt.event.KeyEvent
import java.util.Locale

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.chart.Chart

import org.jetbrains.exposed.sql.Table

import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.visual.WindowController

class FormTests: JApplicationTestBase() {

  @Test
  fun sourceFormTest() {
    val formModel = TestForm.model
    assertEquals(TestForm::class.qualifiedName!!.replace(".", "/"), formModel.source)
  }
}

object User: Table() {
  val id = integer("id")
  val name = varchar("name", 20)
  val age = integer("age")
}

object TestForm: Form() {
  override val locale = Locale.FRANCE
  override val title = "form for test"

  val graph = actor (
          menu =  "Action",
          label = "Graphe",
          help =  "Representer les valeurs en graphe"
  ) {
    key  =  KeyEvent.VK_F9  // key is optional here
    icon =  "column_chart"  // icon is optional here
  }

  init {
    page("test page") {
      val testBlock = block(1, 1, "Test", "Test block") {
        val u = table(User)
        val i = index(message = "ID should be unique")

        val id = hidden(domain = Domain<Int>(20)) {
          label = "id"
          help = "The user id"
          columns(u.id)
        }
        val name = mustFill(domain = Domain<String>(20), position = at(1, 1)) {
          label = "name"
          help = "The user name"
          columns(u.name)
        }
        val age = visit(domain = Domain<Int>(3), position = follow(name)) {
          label = "age"
          help = "The user age"
          columns(u.age) {
            index = i
            priority = 1
          }
        }

        command(item = graph) {
          action = {
            WindowController.windowController.doNotModal(CommandesC(id.value))
          }
        }
      }
      val testBlock2 = block(1, 1, "Test2", "Test block2") {

        val totalAge = visit(Domain<Int>(3), position = at(1, 1)) {
          label = "Total"
          help = "total user age"
        }

        align(testBlock, 1 to 3)
      }
    }
  }
}

class CommandesC(fournisseur: Int?): Chart() {
  override val title: String = "Fournisseur"
}
