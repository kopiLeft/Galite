/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.demo.common

import java.util.Locale

import org.kopi.galite.visual.dsl.common.Actor
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.VReport

open class ReportDefault(title: String, help: String?, locale: Locale? = null) : Report(title, help, locale) {

  constructor(title: String, locale: Locale? = null) : this(title, null, locale)

  fun reportCommands() {
    quitCmd
    cmdCSV
    cmdPDF
    cmdXLS
    cmdXLSX
  }

  // --------------------MENUS-----------------
  val file by lazy { FileMenu() }
  val edit by lazy { EditMenu() }
  val action by lazy { ActionMenu() }

  // --------------------Actors-----------------
  val quit  by lazy {
    actor(QuitActor())
  }

  val csv  by lazy {
    actor(CSVActor())
  }

  val xls  by lazy {
    actor(XLSActor())
  }

  val xlsx  by lazy {
    actor(XLSXActor())
  }

  val pdf  by lazy {
    actor(PDFActor())
  }

  // -------------------------------------------------------------------
  // REPORT COMMANDS
  // -------------------------------------------------------------------
  val quitCmd  by lazy {
    command(item = quit) {
      model.close()
    }
  }

  val cmdCSV  by lazy {
    command(item = csv) {
      model.export(VReport.TYP_CSV)
    }
  }

  val cmdPDF  by lazy {
    command(item = pdf) {
      model.export(VReport.TYP_PDF)
    }
  }

  val cmdXLS  by lazy {
    command(item = xls) {
      model.export(VReport.TYP_XLS)
    }
  }

  val cmdXLSX  by lazy {
    command(item = xlsx) {
      model.export(VReport.TYP_XLSX)
    }
  }
}

private class CSVActor: Actor(
  menu = ActionMenu(),
  label = "CSV",
  help = "CSV Format",
) {
  init {
    key = Key.F8
    icon = Icon.EXPORT_CSV
  }
}

class XLSActor: Actor(
  menu = ActionMenu(),
  label = "XLS",
  help = "Excel (XLS) Format",
) {
  init {
    key = Key.SHIFT_F8
    icon = Icon.EXPORT_XLSX
  }
}

class XLSXActor: Actor(
  menu = ActionMenu(),
  label = "XLSX",
  help = "Excel (XLSX) Format",
) {
  init {
    key = Key.SHIFT_F8
    icon = Icon.EXPORT
  }
}
