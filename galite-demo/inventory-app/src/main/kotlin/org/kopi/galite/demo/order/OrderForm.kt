package org.kopi.galite.demo.order


import org.kopi.galite.demo.database.Order
import org.kopi.galite.visual.domain.DATE
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Menu
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm
import java.util.*

class OrderForm : ReportSelectionForm("Order Form", Locale.UK) {
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
  val orderPage = page("Order")
  val orderBlock = orderPage.insertBlock(Order())

  inner class Order : Block("Order", 1, 100) {
    val o = table(Order)

    val idO = hidden(INT(5))
    {
      label = "ID"
      help = "Order ID"
      columns(o.idOrd)
    }

    val dateO = mustFill(DATE, at(1, 1))
    {
      label = "Date"
      help = " Date Order"
      columns(o.dateOrd)
      { priority = 2 }
    }

    val qtyO = mustFill(INT(10), at(1, 2)) {
      label = "Quantity"
      help = " Quantity"
      columns(o.qtyOrd)
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
          OrderR()
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