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
import org.kopi.galite.tests.report.SimpleReport
import org.kopi.galite.visual.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm

class FormWithReport : ReportSelectionForm(title = "form for test", locale = Locale.UK) {

  val action = menu("Action")
  val testPage = page("test page")
  val report = actor(
          menu = action,
          label = "CreateReport",
          help = "Create report",
  ) {
    key = Key.F8
    icon = Icon.REPORT
  }

  val block = testPage.insertBlock(BlockSample) {
    command(item = report) {
      createReport {
        SimpleReport()
      }
    }
  }
}

fun main() {
  runForm(FormWithReport())
}
