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
package org.kopi.galite.demo.taxRule

import java.util.Locale

import org.kopi.galite.demo.Application
import org.kopi.galite.demo.TaxRule
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Access
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.form.dsl.Modes
import org.kopi.galite.form.dsl.ReportSelectionForm
import org.kopi.galite.report.Report

object TaxRuleForm : ReportSelectionForm() {
  override val locale = Locale.FRANCE
  override val title = "TaxRules"
  val page = page("TaxRule")
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

  val list = actor(
    ident = "list",
    menu = action,
    label = "list",
    help = "Display List",
  ) {
    key = Key.F1   // key is optional here
    icon = "list"  // icon is optional here
  }

  val resetBlock = actor(
    ident = "reset",
    menu = action,
    label = "break",
    help = "Reset Block",
  ) {
    key = Key.F3   // key is optional here
    icon = "break"  // icon is optional here
  }

  val block = insertBlock(TaxRuleBlock, page) {
    command(item = report) {
      action = {
        createReport(TaxRuleBlock)
      }
    }

    command(item = list) {
      action = {
        println("-----------Generating list-----------------")
        recursiveQuery()
      }
    }

    command(item = resetBlock) {
      action = {
        resetBlock()
      }
    }
  }

  override fun createReport(): Report {
    return TaxRuleR
  }
}

object TaxRuleBlock : FormBlock(1, 10, "TaxRule") {
  val u = table(TaxRule)

  val idTaxe = hidden(domain = Domain<Int>(20)) {
    label = "ID"
    help = "The tax ID"
    columns(u.idTaxe)
  }

  val taxName = mustFill(domain = Domain<String>(20), position = at(1, 1)) {
    label = "Name"
    help = "The tax name"
    columns(u.taxName) {
      priority = 1
    }
  }

  val rate = mustFill(domain = Domain<Int>(25), position = at(2, 1)) {
    label = "Rate in %"
    help = "The tax rate in %"
    columns(u.rate) {
      priority = 1
    }
  }

  init {
    blockVisibility(Access.VISIT, Modes.QUERY)
  }
}

fun main() {
  Application.runForm(formName = TaxRuleForm)
}
