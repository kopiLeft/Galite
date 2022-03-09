package org.kopi.galite.demo.bill

import org.kopi.galite.demo.client.ClientR
import org.kopi.galite.demo.database.Bill
import org.kopi.galite.demo.database.Client
import org.kopi.galite.visual.domain.DATE
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Menu
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm
import java.math.BigDecimal
import java.util.*

class BillForm : ReportSelectionForm("Bill Form", Locale.UK) {

  val action: Menu = menu("action")
  val list = actor(
    menu = action,
    label = "List",
    help = "Display List",
  ) {
    key = Key.F10
    icon = Icon.LIST
  }
  val reset = actor(
    menu = action,
    label = "reset",
    help = " reset",
  ) {
    key = Key.F11
    icon = Icon.BREAK
  }

  val new = actor(
    menu = action,
    label = "New",
    help = " New",
  ) {
    key = Key.F11
    icon = Icon.INSERT
  }
  val save = actor(
    menu = action,
    label = "Save",
    help = " Save",
  ) {
    key = Key.F11
    icon = Icon.SAVE
  }
  val autofill = actor(
    menu = action,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  ) {
    key = Key.F2
  }

  val dynamicReport = actor(
    menu = action,
    label = "DynamicReport",
    help = " Create Dynamic Report",
  ) {
    key = Key.F6
    icon = Icon.REPORT
  }
  val report = actor(
    menu = action,
    label = "Report",
    help = " Create Report",
  ) {
    key = Key.F5
    icon = Icon.REPORT
  }

  /**
   * Insert Block
   */
  val billPage = page("Bill")
  val billBlock = billPage.insertBlock(Bill())

  inner class Bill : Block("Bill", 1, 100) {
    val b = table(Bill)

    val idB = hidden(INT(5))
    {
      label = "ID"
      help = "BIll ID"
      columns(b.idB)
    }

    val date = mustFill(DATE, at(1, 1))
    {
      label = "Date"
      help = "Date"
      columns(b.date)
      { priority = 4 }
    }

    val priceT = visit(DECIMAL(10, 5), at(1, 2))
    {
      label = "Total Price"
      help = "Total Price"
      columns(b.priceT)
      { priority = 3 }
    }

    val payMeth = visit(STRING(15), at(2, 1))
    {
      label = "Payment Method"
      help = "Payment Method"
      columns(b.payMeth)
      { priority = 2 }
    }

    val payDate = visit(DATE, at(2, 2))
    {
      label = "Payment Date"
      help = "Payment Date"
      columns(b.payDate)
      { priority = 1 }
    }

    init {
      border = Border.LINE

      command(item = reset) {
        resetBlock()
      }

      command(item = new) {
        insertMode()
      }
      command(item = report) {
        createReport {
          BillR()
        }
      }
      command(item = dynamicReport) {
        createDynamicReport()
      }
      command(item = list) {
        recursiveQuery()
      }
      command(item = save, Mode.INSERT) {
        saveBlock()
      }
    }
  }
}