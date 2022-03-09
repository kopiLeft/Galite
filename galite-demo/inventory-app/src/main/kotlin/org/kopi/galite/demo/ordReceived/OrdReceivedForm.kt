package org.kopi.galite.demo.ordReceived

import org.kopi.galite.demo.database.Delivery
import org.kopi.galite.demo.database.OrdReceived
import org.kopi.galite.demo.database.OrdReceivedid
import org.kopi.galite.demo.delivery.DeliveryR
import org.kopi.galite.visual.domain.DECIMAL
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

class OrdReceivedForm : ReportSelectionForm("Form Order Received", Locale.UK) {
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
  val ordReceivedPage = page("Order Received")
  val ordReceivedBlock = ordReceivedPage.insertBlock(OrdReceived())

  inner class OrdReceived : Block("Order Received", 1, 100) {
    val o = table(OrdReceived)

    val idRec = hidden(INT(5))
    {
      label = "ID"
      help = "Order Received ID"
      columns(o.idRec)
    }
    val total = mustFill(DECIMAL(10, 5), at(1, 1))
    {
      label = "Total"
      help = "Total"
      columns(o.total)
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
          OrdReceivedR()
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