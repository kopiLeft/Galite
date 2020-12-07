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

import org.kopi.galite.demo.desktop.Application
import java.util.Locale

import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.form.dsl.ReportSelectionForm
import org.kopi.galite.report.Report
import org.kopi.galite.tests.report.SimpleReport

object FormWithReport : ReportSelectionForm() {
  override val locale = Locale.FRANCE

  override val title = "form for test"

  val action = menu("Action")
  val testPage = page("test page")
  val report = actor(
          ident = "report",
          menu = action,
          label = "CreateReport",
          help = "Create report",
  ) {
    key = Key.F8          // key is optional here
    icon = "preview"  // icon is optional here
  }

  val block = insertBlock(BlockSample, testPage) {
    command(item = report) {
      this.name = "report"
      action = {
        println("-----------Generating report-----------------")
        createReport(BlockSample)
      }
    }
  }

  override fun createReport(): Report {
    return SimpleReport
  }
}

object BlockSample : FormBlock(1, 1, "Test", "Test block") {
  val u = table(User)
  val i = index(message = "ID should be unique")

  val name = visit(domain = Domain<String>(20), position = at(1, 1)) {
    label = "name"
    help = "The user name"
    columns(u.name)
  }
}

fun main(){
  Application.runForm(FormWithReport)
}
