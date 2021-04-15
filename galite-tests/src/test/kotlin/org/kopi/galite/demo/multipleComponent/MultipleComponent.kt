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
package org.kopi.galite.demo.multipleComponent

import java.util.Locale

import org.kopi.galite.demo.Application
import org.kopi.galite.demo.Client
import org.kopi.galite.demo.TaxRule
import org.kopi.galite.demo.client.ClientR
import org.kopi.galite.demo.taxRule.TaxRuleForm
import org.kopi.galite.domain.CodeDomain
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.dsl.Access
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.form.dsl.Modes
import org.kopi.galite.form.dsl.ReportSelectionForm
import org.kopi.galite.report.Report
import org.kopi.galite.type.Date
import org.kopi.galite.type.Decimal
import org.kopi.galite.type.Image
import org.kopi.galite.type.Month
import org.kopi.galite.type.Time
import org.kopi.galite.type.Timestamp
import org.kopi.galite.type.Week

object MultipleComponent : ReportSelectionForm() {
  override val locale = Locale.UK
  override fun createReport(): Report {
    return ClientR()
  }

  override val title = "All field types test"
  val page = page("Simple Block")
  val page2 = page("Multiple Block")
  val page3 = page("list Block")
  val action = menu("Action")
  val autoFill = actor(
          ident = "Autofill",
          menu = action,
          label = "Autofill",
          help = "Autofill",
  )
  val report = actor(
          ident = "report",
          menu = action,
          label = "CreateReport",
          help = "Create report",
  ) {
    key = Key.F8          // key is optional here
    icon = "preview"  // icon is optional here
  }
  val dynamicReport = actor(
          ident = "dynamicReport",
          menu = action,
          label = "DynamicReport",
          help = " Create Dynamic Report",
  ) {
    key = Key.F6      // key is optional here
    icon = "preview"  // icon is optional here
  }
  val quit = actor(
          ident = "quit",
          menu = action,
          label = "quit",
          help = "Quit",
  ) {
    key = Key.ESCAPE          // key is optional here
    icon = "quit"  // icon is optional here
  }
  val helpForm = actor(
          ident = "helpForm",
          menu = action,
          label = "Help",
          help = " Help"
  ) {
    key = Key.F1
    icon = "help"
  }
  val helpCmd = command(item = helpForm) {
    action = {
      showHelp()
    }
  }
  val quitCmd = command(item = quit) {
    action = {
      quitForm()
    }
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
  val serialQuery = actor(
          ident = "serialQuery",
          menu = action,
          label = "serialQuery",
          help = "serial query",
  ) {
    key = Key.F6   // key is optional here
    icon = "serialquery"  // icon is optional here
  }
  val save = actor(
          ident = "save",
          menu = action,
          label = "save",
          help = "save",
  ) {
    key = Key.F2   // key is optional here
    icon = "save"  // icon is optional here
  }
  val add = actor(
          ident = "add",
          menu = action,
          label = "add",
          help = "add",
  ) {
    key = Key.F2   // key is optional here
    icon = "save"  // icon is optional here
  }
  val deleteBlock = actor(
          ident = "deleteBlock",
          menu = TaxRuleForm.action,
          label = "deleteBlock",
          help = " deletes block",
  ) {
    key = Key.F5
    icon = "delete"
  }

  val blockSimple = insertBlock(BlockAllFields(), page)
  val blockMutliple = insertBlock(Clients(), page2) {
    command(item = report) {
      action = {
        createReport(this@insertBlock)
      }
    }
    command(item = dynamicReport) {
      action = {
        createDynamicReport()
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
    command(item = serialQuery) {
      action = {
        serialQuery()
      }
    }
  }
  val blockList = insertBlock(TaxRuleBlock(), page3) {
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
    command(item = serialQuery) {
      action = {
        serialQuery()
      }
    }
    command(item = add) {
      action = {
        this@insertBlock.vBlock.setMode(VConstants.MOD_INSERT)
      }
    }
    command(item = save) {
      action = {
        println("-----------Saving-----------------")
        saveBlock()
      }
    }
    command(item = deleteBlock) {
      action = {
        deleteBlock()
      }
    }

  }
}

class BlockAllFields : FormBlock(1, 1, "AllFields") {

  val stringField = mustFill(domain = Domain<String>(50), position = at(1, 1)) {
    label = "String Field"
    help = "The String Field"
  }
  val intField = mustFill(domain = Domain<Int>(25), position = at(2, 1)) {
    label = "Int Field"
    help = "The Int Field"
  }
  val booleanField = visit(domain = Domain<Boolean>(50), position = at(3, 1)) {
    label = "Boolean Field"
    help = "The Boolean Field"
  }
  val decimalField = visit(domain = Domain<Decimal>(50), position = at(4, 1)) {
    label = "Decimal Field"
    help = "The Decimal Field"
  }
  val dateField = visit(domain = Domain<Date>(30), position = at(5, 1)) {
    label = "Date Field"
    help = "The Date Field"
  }
  val weekField = visit(domain = Domain<Week>(500), position = at(6, 1)) {
    label = "Week Field"
    help = "The Week Field"
  }
  val monthField = visit(domain = Domain<Month>(700), position = at(7, 1)) {
    label = "Month Field"
    help = "The Month Field"
  }
  val timeField = visit(domain = Domain<Time>(20), position = at(8, 1)) {
    label = "Time Field"
    help = "The Time Field"
  }
  val timestampField = visit(domain = Domain<Timestamp>(20), position = at(9, 1)) {
    label = "Timestamp Field"
    help = "Timestamp Time Field"
  }
  val codeDomain = visit(domain = Color, position = at(10, 1)) {
    label = "CodeDomain Field"
    help = "The CodeDomain Field"
  }
  val imageField = visit(domain = Domain<Image>(100, 100), position = at(11, 1)) {
    label = "Image Field"
    help = "Image Time Field"
  }
  init {
    blockVisibility(Access.VISIT, Modes.QUERY)
  }
}
object Color : CodeDomain<String>() {
  init {
    "value 1" keyOf "val1"
    "value 2" keyOf "val2"
    "value 3" keyOf "val3"
    "value 4" keyOf "val4"
    "value 5" keyOf "val5"
  }
}

class Clients : FormBlock(6, 6, "Clients") {
  val u = table(Client)

  val idClt = visit(domain = Domain<Int>(15), position = at(1, 1)) {
    label = "ID"
    help = "The client id"
    columns(u.idClt)
  }
  val fstnameClt = visit(domain = Domain<String>(25), position = at(2, 1)) {
    label = "First Name"
    help = "The client first name"
    columns(u.firstNameClt)
  }
  val nameClt = visit(domain = Domain<String>(25), position = at(2, 2)) {
    label = "Last name"
    help = "The client last name"
    columns(u.lastNameClt)
  }
  val ageClt = visit(domain = Domain<Int>(3), position = at(3, 1)) {
    label = "Age"
    help = "The client age"
    columns(u.ageClt)
  }
  val addressClt = visit(domain = Domain<String>(20), position = at(3, 2)) {
    label = "Address"
    help = "The client address"
    columns(u.addressClt)
  }
  val countryClt = visit(domain = Domain<String>(12), position = at(4, 1)) {
    label = "Country"
    help = "The client country"
    columns(u.countryClt)
  }
  val cityClt = visit(domain = Domain<String>(12), position = at(4, 2)) {
    label = "City"
    help = "The client city"
    columns(u.cityClt)
  }
  val zipCodeClt = visit(domain = Domain<Int>(12), position = at(4, 3)) {
    label = "Zip code"
    help = "The client zip code"
    columns(u.zipCodeClt)
  }
  init {
    blockVisibility(Access.VISIT, Modes.QUERY)
  }
}

class TaxRuleBlock : FormBlock(1, 10, "TaxRule") {
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
    label = "Rate"
    help = "The tax rate in %"
    columns(u.rate) {
      priority = 1
    }
  }

  init {
    blockVisibility(Access.VISIT, Modes.QUERY)
  }

  val percent = visit(domain = Domain<Boolean>(25), position = at(2, 2)) {
    label = "%"
    help = "The tax rate in %"
  }
}

fun main() {
  Application.runForm(formName = MultipleComponent)
}
