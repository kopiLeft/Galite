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
package org.kopi.galite.demo.taxRule

import java.util.Locale

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
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.form.Commands

class TaxRuleForm : DictionaryForm(title = "TaxRules", locale = Locale.UK) {
  val page = page("TaxRule")

  init {
    insertMenus()
    insertCommands()
  }

  val list = actor(menu = actionMenu, label = "List", help = "Display List", ident = "list") {
    key = Key.F1
    icon = Icon.LIST
  }

  val block = page.insertBlock(TaxRuleBlock())

  inner class TaxRuleBlock : Block("TaxRule", 1, 10) {
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

    val percent = visit(domain = BOOL, position = at(2, 2)) {
      label = "%"
      help = "The tax rate in %"
    }

    init {
      blockVisibility(Access.VISIT, Mode.QUERY)

      command(item = report) {
        createReport {
          TaxRuleR()
        }
      }
      command(item = pivotTable) {
        createPivotTable {
          TaxRuleP()
        }
      }

      command(item = save) {
        saveBlock()
      }

      command(item = menuQuery) {
        Commands.recursiveQuery(block)
      }

      command(item = _break) {
        resetBlock()
      }

      command(item = delete) {
        deleteBlock()
      }
    }
  }
}

fun main() {
  runForm(form = TaxRuleForm::class)
}
