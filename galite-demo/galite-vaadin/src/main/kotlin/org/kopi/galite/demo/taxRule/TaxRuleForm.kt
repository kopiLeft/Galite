/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.demo.taxRule

import java.util.Locale

import org.kopi.galite.demo.common.FormDefaultImpl
import org.kopi.galite.demo.common.IFormDefault
import org.kopi.galite.demo.database.TaxRule
import org.kopi.galite.demo.desktop.runForm
import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.Fixed
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.form.Access
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm

class TaxRuleForm : ReportSelectionForm(), IFormDefault by FormDefaultImpl() {
  override val locale = Locale.UK
  override val title = "TaxRules"
  val page = page("TaxRule")

  init {
    insertMenus()
    insertCommands()
  }

  val list = actor(
          ident = "list",
          menu = action,
          label = "list",
          help = "Display List",
  ) {
    key = Key.F1
    icon = Icon.LIST
  }

  val block = page.insertBlock(TaxRuleBlock()) {
    command(item = report) {
      createReport(TaxRuleR())
    }
    saveCmd
    recursiveQueryCmd
    breakCmd
    deleteCmd
  }
}

class TaxRuleBlock : Block(1, 10, "TaxRule") {
  val u = table(TaxRule)

  val idTaxe = hidden(domain = INT(20)) {
    label = "ID"
    help = "The tax ID"
    columns(u.idTaxe)
  }

  val taxName = mustFill(domain = STRING(20), position = at(1, 1)) {
    label = "Name"
    help = "The tax name"
    columns(u.taxName) {
      priority = 1
    }
  }

  val rate = mustFill(domain = INT(25), position = at(2, 1)) {
    label = "Rate"
    help = "The tax rate in %"
    columns(u.rate) {
      priority = 1
    }
  }

  val informations = visit(domain = STRING(80, 50, 10, Fixed.ON, styled = true), position = at(3, 1)) {
    label = "tax informations"
    help = "The tax informations"
    columns(u.informations) {
      priority = 1
    }
  }

  init {
    blockVisibility(Access.VISIT, Mode.QUERY)
  }

  val percent = visit(domain = BOOL, position = at(2, 2)) {
    label = "%"
    help = "The tax rate in %"
  }
}

fun main() {
  runForm(formName = TaxRuleForm())
}
