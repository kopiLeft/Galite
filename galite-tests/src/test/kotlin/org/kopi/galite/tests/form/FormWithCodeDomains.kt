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
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Block

class FormWithCodeDomains: Form(title = "form to test domains",  locale = Locale.UK) {
  val autoFill = actor(
    menu = edit,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  )
  val testBlock = insertBlock(DaysBlock)
}

object DaysBlock : Block("DaysBlock", 1, 1) {
  val day = mustFill(domain = Days, position = at(1, 1)) {
    label = "day"
    help = "The day"
  }
}

object Days: CodeDomain<Int>() {
  init {
    "Sunday" keyOf 1
    "Monday" keyOf 2
    "Tuesday" keyOf 3
    "Wednesday" keyOf 4
    "Thursday" keyOf 5
    "Friday" keyOf 6
    "Saturday" keyOf 7
  }
}

fun main() {
  runForm(formName = FormWithCodeDomains())
}
