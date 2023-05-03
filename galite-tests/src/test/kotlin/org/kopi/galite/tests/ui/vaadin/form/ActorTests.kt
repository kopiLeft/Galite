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
package org.kopi.galite.tests.ui.vaadin.form

import java.util.Locale

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.getNavigationItem
import org.kopi.galite.tests.examples.initDatabase
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.dsl.common.Actor
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Key

class ActorTests : GaliteVUITestBase() {

  val form = ActorsForm()

  @Before
  fun `login to the App`() {
    login()

    // Open the form
    form.model.doNotModal()
  }

  @Test
  fun `actor navigation panel contains actors with icon and acceleratorKey`() {
    form.autoFill.getNavigationItem()
    form.reset.getNavigationItem()
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      transaction {
        initDatabase()
      }
    }
  }
}

class ActorsForm : Form(title = "Commands Form", locale = Locale.UK) {
  val action = menu("Action")

  val autoFill = actor(menu = action, label = "Autofill", help = "Autofill", command = PredefinedCommand.AUTOFILL)
  val reset = actor(ResetBlock())

  init {
    insertBlock(Training())
  }

  inner class Training : Block("Training", 1, 10) {

    val ID = visit(domain = INT(25), position = at(1, 1)) {
      label = "ID"
      help = "ID"
    }

    init {
      command(item = reset) {}
    }
  }

  inner class ResetBlock: Actor(menu = action,
                                label = "break",
                                help = "Reset Block") {
    init {
      key = Key.F11
      icon = Icon.BREAK
    }
  }
}
