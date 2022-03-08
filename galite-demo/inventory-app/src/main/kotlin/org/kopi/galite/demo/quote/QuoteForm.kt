package org.kopi.galite.demo.quote

import org.kopi.galite.demo.database.Order
import org.kopi.galite.demo.database.Quote
import org.kopi.galite.demo.order.OrderR
import org.kopi.galite.visual.domain.DATE
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
import java.util.*

class QuoteForm : ReportSelectionForm("Quote Form", Locale.UK) {
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
  val quotePage = page("Quote")
  val quoteBlock = quotePage.insertBlock(Quote())

  inner class Quote : Block("Quote", 1, 100) {
    val q = table(Quote)

    val idQ = hidden(INT(5))
    {
      label = "ID"
      help = "Quote ID"
      columns(q.idQ)
    }

    val date = mustFill(DATE, at(1, 1))
    {
      label = "Date"
      help = "Date"
      columns(q.date)
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
          QuoteR()
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