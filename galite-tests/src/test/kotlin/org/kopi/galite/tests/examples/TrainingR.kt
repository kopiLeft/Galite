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
package org.kopi.galite.tests.examples

import java.util.Locale

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.report.FieldAlignment
import org.kopi.galite.report.Report
import org.kopi.galite.report.UReport
import org.kopi.galite.report.VCellFormat
import org.kopi.galite.report.VReport
import org.kopi.galite.type.Decimal
import org.kopi.galite.visual.WindowController

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
    key = Key.F8          // key is optional here
    icon = "exportCsv"  // icon is optional here
  }

  val xls = actor(
    ident = "XLS",
    menu = action,
    label = "XLS",
    help = "Excel (XLS) Format",
  ) {
    key = Key.SHIFT_F8          // key is optional here
    icon = "exportXlsx"  // icon is optional here
  }

  val xlsx = actor(
    ident = "XLSX",
    menu = action,
    label = "XLSX",
    help = "Excel (XLSX) Format",
  ) {
    key = Key.SHIFT_F8          // key is optional here
    icon = "exportXlsx"  // icon is optional here
  }

  val pdf = actor(
    ident = "PDF",
    menu = action,
    label = "PDF",
    help = "PDF Format",
  ) {
    key = Key.F9          // key is optional here
    icon = "exportPdf"  // icon is optional here
  }

  val editColumnData = actor(
    ident = "EditColumnData",
    menu = action,
    label = "Edit Column Data",
    help = "Edit Column Data",
  ) {
    key = Key.F8          // key is optional here
    icon = "formula"  // icon is optional here
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

  val cmdCSV = command(item = csv) {
    action = {
      model.export(VReport.TYP_CSV)
    }
  }

  val cmdPDF = command(item = pdf) {
    action = {
      model.export(VReport.TYP_PDF)
    }
  }

  val cmdXLS = command(item = xls) {
    action = {
      model.export(VReport.TYP_XLS)
    }
  }

  val cmdXLSX = command(item = xlsx) {
    action = {
      model.export(VReport.TYP_XLSX)
    }
  }

  val helpCmd = command(item = helpForm) {
    action = {
      model.showHelp()
    }
  }

  val editColumn = command(item = editColumnData) {
    action = {
      if ((model.getDisplay() as UReport).getSelectedColumn() != -1) {
        val formula  = org.kopi.galite.demo.product.ProductForm()
        WindowController.windowController.doModal(formula)
      }
    }
  }

  val type = field(Domain<String>(25)) {
    label = "training type"
    help = "The training type"
    align = FieldAlignment.LEFT
    group = trainingName
    format {
      object : VCellFormat() {
        override fun format(value: Any?): String {
          return (value as String).toUpperCase()
        }
      }
    }
  }

  val trainingName = field(Domain<String>(25)) {
    label = "training Name"
    help = "The training name"
    align = FieldAlignment.LEFT
    format {
      object : VCellFormat() {
        override fun format(value: Any?): String {
          return (value as String).toUpperCase()
        }
      }
    }
  }

  val price = field(Domain<Decimal>(50)) {
    label = "price"
    help = "The price"
    align = FieldAlignment.LEFT
  }
  val disponibility = field(Domain<Boolean>(2)) {
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
