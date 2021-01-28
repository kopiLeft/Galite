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
package org.kopi.galite.demo.provider

import java.util.Locale

import org.kopi.galite.demo.Application
import org.kopi.galite.demo.Provider
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.form.dsl.ReportSelectionForm
import org.kopi.galite.report.Report
import org.kopi.galite.type.Image

object ProviderForm : ReportSelectionForm() {
  override val locale = Locale.FRANCE
  override val title = "Provider form"
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

  val block = insertBlock(BlockProvider, page) {
    command(item = report) {
      action = {
        createReport(BlockProvider)
      }
    }
  }

  override fun createReport(): Report {
    return ProviderR
  }
}

object BlockProvider : FormBlock(1, 1, "Provider block") {
  val u = table(Provider)

  val idProvider = hidden(domain = Domain<Int>(20)) {
    label = "provider id"
    help = "The provider id"
    columns(u.idProvider)
  }
  val nameProvider = mustFill(domain = Domain<String>(50), position = at(1, 1)) {
    label = "provider name"
    help = "The provider name"
    columns(u.nameProvider)
  }
  val tel = mustFill(domain = Domain<Int>(25), position = at(2, 1)) {
    label = "PROVIDER PHONE"
    help = "The PROVIDER PHONE"
    columns(u.tel)
  }
  val description = visit(domain = Domain<String>(50), position = at(3, 1)) {
    label = "PROVIDER DESCRIPTION"
    help = "PROVIDER DESCRIPTION"
    columns(u.description)
  }

  val address = visit(domain = Domain<String>(50), position = at(4, 1)) {
    label = "PROVIDER ADDRESS"
    help = "THE PROVIDER ADDRESS"
    columns(u.address)
  }
  val postalCode = visit(domain = Domain<Int>(30), position = at(5, 1)) {
    label = "PROVIDER POSTAL CODE"
    help = "PROVIDER POSTAL CODE"
    columns(u.postalCode)
  }

  val logo = visit(domain = Domain<Image?>(20), position = at(6, 1)) {
    label = "provider company logo"
    help = "provider company logo"
  }
}

fun main() {
  Application.runForm(formName = ProviderForm)
}
