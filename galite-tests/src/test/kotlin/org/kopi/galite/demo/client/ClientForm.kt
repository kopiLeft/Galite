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
package org.kopi.galite.demo.client

import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.form.dsl.ReportSelectionForm
import org.kopi.galite.report.Report
import java.util.Locale

object ClientForm : ReportSelectionForm() {
  override val locale = Locale.FRANCE
  override val title = "client form"
  val page = page("page")
  val action = menu("act")
  val report = actor(
          ident = "report",
          menu = action,
          label = "CreateReport",
          help = "Create report",
  ) {
    key = Key.F8          // key is optional here
    icon = "preview"  // icon is optional here
  }

  val block = insertBlock(BlockClient, page) {
    command(item = report) {
      action = {
        createReport(BlockClient)
      }
    }
  }

  override fun createReport(): Report {
    return ClientR
  }
}

object BlockClient : FormBlock(1, 1, "Test block") {
  val u = table(org.kopi.galite.demo.Client)

  val idClt = hidden(domain = Domain<Int>(20)) {
    label = "client id"
    help = "The client id"
    columns(u.idClt)
  }
  val nameClt = mustFill(domain = Domain<String>(25), position = at(1, 1)) {
    label = "client name"
    help = "The client name"
    columns(u.nameClt)
  }
  val fstnameClt = mustFill(domain = Domain<String>(25), position = at(2, 1)) {
    label = "client firstname"
    help = "The client firstname"
    columns(u.fstnameClt)
  }
  val addressClt = visit(domain = Domain<String>(50), position = at(3, 1)) {
    label = "client address"
    help = "The client address"
    columns(u.addressClt)
  }
  val ageClt = visit(domain = Domain<Int>(2), position = at(4, 1)) {
    label = "client age"
    help = "The client age"
    columns(u.ageClt)
  }
  val cityClt = visit(domain = Domain<String>(30), position = at(5, 1)) {
    label = "client city"
    help = "The client city"
    columns(u.cityClt)
  }

  val postalCodeClt = visit(domain = Domain<Int>(20), position = at(6, 1)) {
    label = "client postal code"
    help = "The client postal code"
    columns(u.postalCodeClt)
  }
}

fun main() {
  Application.runForm(formName = ClientForm)
}
