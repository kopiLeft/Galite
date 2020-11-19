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

import org.junit.Test
import org.kopi.galite.chart.Chart

import org.jetbrains.exposed.sql.Table

import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.tests.JApplicationTestBase
import org.kopi.galite.visual.WindowController

class FormTests: JApplicationTestBase() {
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
        val testBlock = block(1, 1, "Test block") {
          val u = table(User)

          val id = hidden(Domain<Int>(20)) {
            label = "id"
            help = "The user id"
            columns(u.id)
          }
          val name = mustFill(Domain<String>(20)) {
            label = "name"
            help = "The user name"
            columns(u.name)
          }
          val age = visit(Domain<Int>(3)) {
            label = "age"
            help = "The user age"
            columns(u.age)
          }

          command{
            item = graph
            action = {
              WindowController.windowController.doNotModal(CommandesC(id.value))
            }
          }
        }
      }
    }
  }

  class CommandesC(fournisseur: Int?): Chart() {
    override val title: String = "Fournisseur"
  }

  @Test
  fun simpleFormTest() {
    //val formModel = TestForm.model
    //assertEquals("org.kopi.galite.tests.form.FormTests", formModel.source) TODO
  }
}
