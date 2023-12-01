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
package org.kopi.galite.tests.ui.vaadin.triggers

import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.report.Report
import java.util.*

object ReportTriggersTest : Report(title = "Report to test triggers", locale = Locale.UK) {
  val action = menu("Action")

  val quit = actor(
    menu = action,
    label = "Quit",
    help = "Quit",
  ) {
    key = Key.ESCAPE
    icon = Icon.QUIT
  }

  val quitCmd = command(item = quit) {
    model.getDisplay()!!.closeWindow()
  }

  val preReport = trigger(PREREPORT) {
    FormTriggersTests.list.add("PREREPORT REPORT Trigger")
  }

  val postReport = trigger(POSTREPORT) {
    FormTriggersTests.list.add("POSTREPORT REPORT Trigger")
  }

  val firstName = field(STRING(25)) {
    label = "First Name"
    help = "The client first name"
  }

  val lastName = field(STRING(25)) {
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
