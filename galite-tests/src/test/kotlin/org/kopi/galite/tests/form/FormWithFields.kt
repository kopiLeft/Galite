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

package org.kopi.galite.tests.form

import java.util.Locale

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.form.Block

class FormWithFields: Form(title = "form with fields", locale = Locale.UK) {

  val testPage = page("test page")
  val testPagse = page("test page")
  val testBlock = testPage.insertBlock(BlockWithFields())
  val testBlocsk = testPagse.insertBlock(BlockWithFields())
}

class BlockWithFields : Block("Test block", 1, 1) {
  val u = table(User)
  val i = index(message = "ID should be unique")

  val id = hidden(domain = INT(20)) {
    label = "id"
    help = "The user id"
    columns(u.id)
  }
  val name = visit(domain = STRING(20), position = at(1, 1)) {
    label = "name"
    help = "The user name"
    columns(u.name)
    trigger(VALUE) {
      "example"
    }
  }
  val age = skipped(domain = INT(3), position = follow(name)) {
    label = "age"
    help = "The user age"
    columns(u.age) {
      index = i
      priority = 1
    }
  }
}

fun main() {
  runForm(formName = FormWithFields())
}
