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

import java.util.Locale

import org.kopi.galite.demo.Client
import org.kopi.galite.demo.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.form.dsl.ReportSelectionForm
import org.kopi.galite.report.Report

class ClientForm : ReportSelectionForm() {
  override val locale = Locale.FRANCE
  override val title = "Clients"
  val page = page("Client")
  val action = menu("Action")
  val report = actor(
          ident = "report",
          menu = action,
          label = "CreateReport",
          help = "Create report",
  ) {
    key = Key.F8          // key is optional here
    icon = "preview"  // icon is optional here
  }

  val block = insertBlock(Clients(), page) {
    command(item = report) {
      action = {
        createReport(this@insertBlock)
      }
    }
  }

  override fun createReport(): Report {
    return ClientR()
  }
}

class Clients : FormBlock(1, 1, "Clients") {
  val u = table(Client)

  val idClt = visit(domain = Domain<Int>(15), position = at(1, 1)) {
    label = "ID"
    help = "The client id"
    columns(u.idClt)
  }
  val fstnameClt = mustFill(domain = Domain<String>(25), position = at(1, 1)) {
    label = "First Name"
    help = "The client first name"
    columns(u.firstNameClt)
  }
  val nameClt = visit(domain = Domain<String>(25), position = at(1, 2)) {
    label = "Last name"
    help = "The client last name"
    columns(u.lastNameClt)
  }
  val ageClt = visit(domain = Domain<Int>(3), position = at(2, 1)) {
    label = "Age"
    help = "The client age"
    columns(u.ageClt)
  }
  val addressClt = visit(domain = Domain<String>(20), position = at(2, 2)) {
    label = "Address"
    help = "The client address"
    columns(u.addressClt)
  }
  val countryClt = visit(domain = Domain<String>(12), position = at(3, 1)) {
    label = "Country"
    help = "The client country"
    columns(u.countryClt)
  }
  val cityClt = visit(domain = Domain<String>(12), position = at(3, 2)) {
    label = "City"
    help = "The client city"
    columns(u.cityClt)
  }
  val zipCodeClt = visit(domain = Domain<Int>(12), position = at(3, 3)) {
    label = "Zip code"
    help = "The client zip code"
    columns(u.zipCodeClt)
  }
}

fun main() {
  Application.runForm(formName = ClientForm())
}
