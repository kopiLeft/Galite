/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

import org.jetbrains.exposed.sql.Table

import org.kopi.galite.chart.Chart
import org.kopi.galite.common.INIT
import org.kopi.galite.common.INITFORM
import org.kopi.galite.common.POSTFORM
import org.kopi.galite.common.PREFORM
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.dsl.FieldOption
import org.kopi.galite.form.dsl.BlockOption
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key

object User: Table() {
  val id = integer("ID")
  val name = varchar("NAME", 20)
  val age = integer("AGE")
}

object FormSample: Form() {
  override val locale = Locale.FRANCE
  override val title = "form for test"

  val action = menu("Action")

  val graph = actor (
          ident =  "graph",
          menu =   action,
          label =  "Graph for test",
          help =   "show graph values" ,
  ) {
    key  =  Key.F9          // key is optional here
    icon =  "column_chart"  // icon is optional here
  }

  val p1 = page("test page")
  val p2 = page("test page2")

  val tb1 = insertBlock(TestBlock(), p1) {
    command(item = graph) {
      this.name = "graphe"
      mode(VConstants.MOD_UPDATE, VConstants.MOD_INSERT, VConstants.MOD_QUERY)
      action = {
        println("---------------------------------- IN TEST COMMAND ----------------------------------" + tb2.age.value)
      }
    }
  }

  val tb2 = insertBlock(TestBlock(), p2) {
    command(item = graph) {
      this.name = "graphe"
      mode(VConstants.MOD_UPDATE, VConstants.MOD_INSERT, VConstants.MOD_QUERY)
      action = {
        println("---------------------------------- IN TEST COMMAND ----------------------------------")
      }
    }
  }

  val tb3ToTestBlockOptions = insertBlock(TestBlock(), p1) {
    options(BlockOption.NOINSERT)
  }

  val preform = trigger(INITFORM) {
    println("init form trigger works")
  }

  val postform = trigger(POSTFORM) {
    println("post form trigger works")
  }

  init {
    tb1.age[0] = 5
    tb1.age.value = 6
  }
}

class TestBlock : FormBlock(1, 1, "Test", "Test block") {
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
  val password = mustFill(domain = Domain<String>(20), position = at(2, 1)) {
    label = "password"
    help = "The user password"
    options(FieldOption.NOECHO)
  }
  val age = visit(domain = Domain<Int>(3), position = follow(name)) {
    label = "age"
    help = "The user age"
    columns(u.age) {
      index = i
      priority = 1
    }
  }
}

class CommandesC(fournisseur: Int?): Chart() {
  override val title: String = "Fournisseur"
}

fun main(){
  Application.runForm(formName = FormSample)
}
