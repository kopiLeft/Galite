package org.kopi.galite.demo.client

import org.kopi.galite.demo.database.Client

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

class ClientForm : ReportSelectionForm("Form Client", Locale.UK) {


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
  val clientPage = page("Client")
  val clientBlock = clientPage.insertBlock(Client())

  inner class Client : Block("Client", 1, 100) {
    val c = table(Client)
    val idStock = hidden(INT(1))
    {
      label = "ID"
      help = "Client ID"
      columns(c.idCl)

    }

    val fName = mustFill(STRING(10), at(1, 1))
    {
      label = "First Name"
      help = "First Name"
      columns(c.firstNameCl)
      {
        priority = 4
      }

    }

    val lName = mustFill(STRING(10), at(1, 2))
    {
      label = "Last Name"
      help = "Last Name"
      columns(c.lastNameCl)
      {
        priority = 3
      }
    }


    val tel = visit(INT(8), at(2, 1))
    {
      label = "Telephone"
      help = "Telephone Number"
      columns(c.telephoneCl)
      {
        priority = 2
      }
    }
    val email = mustFill(STRING(30), at(2, 2))
    {
      label = "Email"
      help = "Email "
      columns((c.emailCl))
      {
        priority = 1
      }
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
          ClientR()
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