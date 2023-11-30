/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2023 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.tests.ui.vaadin.triggers

import java.util.Locale

import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.pivottable.Dimension.Position
import org.kopi.galite.visual.dsl.pivottable.PivotTable

object PivotTableTriggersTest : PivotTable(title = "Pivot table to test triggers", locale = Locale.UK) {
  val action = menu("Action")
  val quit = actor(menu = action, label = "Quit", help = "Quit", ) {
    key = Key.ESCAPE
    icon = Icon.QUIT
  }
  val quitCmd = command(item = quit) {
    model.close()
  }
  val init = trigger(INIT) {
    FormTriggersTests.list.add("INIT Trigger")
  }

  val firstName = dimension(STRING(25), Position.NONE) {
    label = "First Name"
    help = "The client first name"
  }

  val lastName = dimension(STRING(25), Position.NONE) {
    label = "Last Name"
    help = "The client last name"
  }

  init {
    add {
      this[firstName] = "firstName"
      this[lastName] = "lastName"
    }
  }
}
