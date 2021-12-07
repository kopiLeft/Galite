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
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Block

object FormWithBlockTrigger: Form() {

  override val locale = Locale.UK
  override val title = "form for test"
  val testPage = page("test page")
  val menu = menu("Action")
  val firstBlock = testPage.insertBlock(BlockWithTrigger1)
  val secondVlock = testPage.insertBlock(BlockWithTrigger2)
}

object BlockWithTrigger1 : Block(1, 1, "Test block") {
  val u = table(User)
  val i = index(message = "ID should be unique")

  init {
    trigger(PREBLK, INIT) {
      println("---------------works---------------")
    }
  }

  val name = visit(domain = STRING(20), position = at(1, 1)) {
    label = "name"
    help = "The user name"
    columns(u.name)
  }
}

object BlockWithTrigger2 : Block(1, 1, "Test", "Test block") {
  val u = table(User)
  val i = index(message = "ID should be unique")

  init {
    trigger(PREBLK) {
      println("---------------works---------------")
    }
  }

  val name = visit(domain = STRING(20), position = at(1, 1)) {
    label = "name"
    help = "The user name"
    columns(u.name)
  }
}

fun main() {
  runForm(FormWithBlockTrigger)
}
