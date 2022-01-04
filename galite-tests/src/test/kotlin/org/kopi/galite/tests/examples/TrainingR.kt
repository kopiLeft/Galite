/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.tests.examples

import java.util.Locale

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.report.FieldAlignment
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.UReport
import org.kopi.galite.visual.report.VReport
import org.kopi.galite.visual.visual.WindowController

/**
 * Training Report
 */
class TrainingR : Report() {
  override val locale = Locale.UK

  override val title = "Clients_Report"

  val action = menu("Action")

  val csv = actor(
    ident = "CSV",
    menu = action,
    label = "CSV",
    help = "CSV Format",
  ) {
    key = Key.F8
    icon = Icon.EXPORT_CSV
  }

  val xls = actor(
    ident = "XLS",
    menu = action,
    label = "XLS",
    help = "Excel (XLS) Format",
  ) {
    key = Key.SHIFT_F8
    icon = Icon.EXPORT_XLSX
  }

  val xlsx = actor(
    ident = "XLSX",
    menu = action,
    label = "XLSX",
    help = "Excel (XLSX) Format",
  ) {
    key = Key.SHIFT_F8
    icon = Icon.EXPORT_XLSX
  }

  val pdf = actor(
    ident = "PDF",
    menu = action,
    label = "PDF",
    help = "PDF Format",
  ) {
    key = Key.F9
    icon = Icon.EXPORT_PDF
  }

  val editColumnData = actor(
    ident = "EditColumnData",
    menu = action,
    label = "Edit Column Data",
    help = "Edit Column Data",
  ) {
    key = Key.F8
    icon = Icon.FORMULA
  }

  val helpForm = actor(
    ident = "helpForm",
    menu = action,
    label = "Help",
    help = " Help"
  ) {
    key = Key.F1
    icon = Icon.HELP
  }

  val cmdCSV = command(item = csv) {
    model.export(VReport.TYP_CSV)
  }

  val cmdPDF = command(item = pdf) {
    model.export(VReport.TYP_PDF)
  }

  val cmdXLS = command(item = xls) {
    model.export(VReport.TYP_XLS)
  }

  val cmdXLSX = command(item = xlsx) {
    model.export(VReport.TYP_XLSX)
  }

  val helpCmd = command(item = helpForm) {
    model.showHelp()
  }

  val editColumn = command(item = editColumnData) {
    if ((model.getDisplay() as UReport).getSelectedColumn() != -1) {
      val formula  = FormExample()
      WindowController.windowController.doModal(formula)
    }
  }

  val type = field(INT(25)) {
    label = "training type"
    help = "The training type"
    align = FieldAlignment.LEFT
    group = trainingName
  }

  val trainingName = field(STRING(25)) {
    label = "training Name"
    help = "The training name"
    align = FieldAlignment.LEFT
    format { value ->
      value.toUpperCase()
    }
  }

  val price = field(DECIMAL(20, 10)) {
    label = "price"
    help = "The price"
    align = FieldAlignment.LEFT
  }
  val disponibility = field(BOOL) {
    label = "disponibility"
    help = "disponibility"
    align = FieldAlignment.LEFT
  }

  val training = Training.selectAll()

  init {
    transaction {
      training.forEach { result ->
        add {
          this[trainingName] = result[Training.trainingName]
          this[type] = result[Training.type]
          this[price] = result[Training.price]
          this[disponibility] = result[Training.active]
        }
      }
    }
  }
}
