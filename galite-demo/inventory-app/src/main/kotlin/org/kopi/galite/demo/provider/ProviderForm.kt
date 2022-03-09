package org.kopi.galite.demo.provider

import org.kopi.galite.demo.database.OrdReceived
import org.kopi.galite.demo.database.Provider
import org.kopi.galite.demo.ordReceived.OrdReceivedR
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

class ProviderForm : ReportSelectionForm("Provider Form", Locale.UK) {
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
  val providerPage = page("Provider")
  val providerBlock = providerPage.insertBlock(Provider())

  inner class Provider : Block("Provider", 1, 100) {
    val p = table(Provider)

    val idPro = hidden(INT(5))
    {
      label = "ID"
      help = "Provider ID"
      columns(p.idPro)
    }

    val fName = mustFill(STRING(15), at(1, 1))
    {
      label = "First Name"
      help = "FirstName"
      columns(p.firstName)
      {
        priority = 4
      }
    }

    val lName = mustFill(STRING(15), at(1, 2))
    {
      label = "Last Name"
      help = "LastName"
      columns(p.lastName)
      {
        priority = 3
      }
    }

    val addr = visit(STRING(15), at(2, 1))
    {
      label = "Address"
      help = "Address"
      columns(p.address)
      {
        priority = 3
      }
    }

    val accNum = mustFill(STRING(15), at(2, 2))
    {
      label = "Account Number"
      help = "Account Number"
      columns(p.accountNum)
      {
        priority = 2
      }
    }

    val tel = mustFill(INT(8), at(2, 3))
    {
      label = "Telephone"
      help = "Telephone"
      columns(p.telephone)
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
          ProviderR()
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