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

package org.kopi.galite.tests.form

import java.util.Locale

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Block

object FormToCheckFieldVisibility: Form(title = "form for test fields visibility", locale = Locale.UK) {
  val testPage = page("test page")
  val testBlock = testPage.insertBlock(blockToCheckFieldVisibility)
}

object blockToCheckFieldVisibility : Block("Test block", 1, 1) {
  val u = table(User)

  val id = hidden(domain = INT(20)) {
    label = "id"
    help = "The user id"
    columns(u.id)
  }
  val name = visit(domain = STRING(20), position = at(1, 1)) {
    label = "name"
    help = "The user name"
    onQuerySkipped()
    onInsertHidden()
    onUpdateSkipped()
    columns(u.name)
  }
  val age = skipped(domain = INT(3), position = follow(name)) {
    label = "age"
    help = "The user age"
    onQueryMustFill()
    onInsertMustFill()
    onUpdateVisit()
    columns(u.age) {
      priority = 1
    }
  }
  val lastName = mustFill(domain = STRING(20), position = at(2, 1)) {
    label = "lastName"
    help = "The user last name"
  }
  val gender = visit(domain = STRING(20), position = at(2, 2)) {
    label = "gender"
    help = "The user gender"
    onQueryHidden()
    onInsertSkipped()
    onUpdateMustFill()
  }
  val country = skipped(domain = STRING(20), position = at(3, 1)) {
    label = "country"
    help = "The country"
    onQueryVisit()
    onInsertVisit()
    onUpdateHidden()
  }
}

fun main() {
  runForm(formName = FormToCheckFieldVisibility)
}
