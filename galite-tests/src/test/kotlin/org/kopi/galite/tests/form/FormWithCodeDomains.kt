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

import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.CodeDomain
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock

object FormWithDomains: Form() {
  override val locale = Locale.FRANCE
  override val title = "form to test domains"
  val testBlock = insertBlock(DaysBlock)
}

object DaysBlock : FormBlock(1, 1, "DaysBlock", "Days block") {
  val day = mustFill(domain = Days, position = at(1, 1)) {
    label = "day"
    help = "The day"
  }
}

object Days: CodeDomain<Int>() {
  init {
    this["Sunday"] = 1
    this["Monday"] = 2
    this["Tuesday"] = 3
    this["Wednesday"] = 4
    this["Thursday"] = 5
    this["Friday"] = 6
    this["Saturday"] = 7
  }
}

fun main() {
  Application.runForm(formName = FormWithDomains)
}
