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

import org.kopi.galite.db.Users
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.DictionaryForm
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key

object FormWithList : DictionaryForm() {
  override val locale = Locale.FRANCE
  override val title = "form for test"

  val action = menu("Action")
  val testPage = page("test page")

  val list = actor(
    ident = "list",
    menu = action,
    label = "list",
    help = "Display List",
  ) {
    key = Key.F1   // key is optional here
    icon = "list"  // icon is optional here
  }

  val block = insertBlock(BlockSample, testPage) {
    command(item = list) {
      this.name = "list"
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery(BlockSample)
      }
    }
  }
}

object BlockSample : FormBlock(1, 1, "Test", "Test block") {
  val u = table(Users)
  val i = index(message = "ID should be unique")

  val id = hidden(domain = Domain<Int>(20)) {
    label = "id"
    help = "The user id"
    columns(u.id)
  }

  val name = visit(domain = Domain<String>(20), position = at(1, 1)) {
    label = "name"
    help = "The user name"
    columns(u.name) {
      priority = 1
    }
  }
}

fun main(){
  Application.runForm(formName = FormWithList)
}
