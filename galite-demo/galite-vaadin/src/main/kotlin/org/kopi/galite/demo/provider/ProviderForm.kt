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
package org.kopi.galite.demo.provider

import java.util.Locale

import org.kopi.galite.demo.common.FormDefault
import org.kopi.galite.demo.database.Provider
import org.kopi.galite.demo.desktop.runForm
import org.kopi.galite.visual.domain.IMAGE
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.Block

class ProviderForm : FormDefault(title = "Providers", locale = Locale.UK) {
  val page = page("Provider")

  init {
    insertMenus()
    insertCommands()
  }

  val block = page.insertBlock(BlockProvider()) {
    command(item = report) {
      createReport {
        ProviderR()
      }
    }
  }
}

class BlockProvider : Block("Providers", 1, 1) {
  val u = table(Provider)

  val idProvider = hidden(domain = INT(20)) {
    label = "ID"
    help = "The provider ID"
    columns(u.idProvider)
  }
  val nameProvider = mustFill(domain = STRING(50), position = at(1, 1)) {
    label = "Name"
    help = "The provider name"
    columns(u.nameProvider)
  }
  val tel = mustFill(domain = INT(25), position = at(2, 1)) {
    label = "Phone number"
    help = "The provider phone number"
    columns(u.tel)
  }
  val description = visit(domain = STRING(50), position = at(3, 1)) {
    label = "Description"
    help = "The provider description"
    columns(u.description)
  }
  val address = visit(domain = STRING(50), position = at(4, 1)) {
    label = "Address"
    help = "The provider address"
    columns(u.address)
  }
  val zipCode = visit(domain = INT(30), position = at(5, 1)) {
    label = "Zip code"
    help = "The provider zip code"
    columns(u.zipCode)
  }
  val logo = visit(domain = IMAGE(100, 100), position = at(6, 1)) {
    label = "Provider company logo"
    help = "The provider company logo"
    columns(u.logo)
  }
}

fun main() {
  runForm(form = ProviderForm::class)
}
