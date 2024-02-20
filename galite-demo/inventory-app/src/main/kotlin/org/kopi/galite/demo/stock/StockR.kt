/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.demo.stock

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Bill
import org.kopi.galite.demo.database.Stocks
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Menu
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm
import org.kopi.galite.visual.dsl.report.FieldAlignment
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.VReport
import java.util.*

class StockR : Report("Stock report", Locale.UK) {
  val act: Menu = menu("action")

  val pdf = actor(
    menu = act,
    label = "PDF",
    help = "PDF"
  )
  {
    key = Key.F11
    icon = Icon.EXPORT_PDF
  }

  val xlsx = actor(
    menu = act,
    label = "XLSX",
    help = "XLSX"
  )
  {
    key = Key.F10
    icon = Icon.EXPORT_XLSX
  }

  val csv = actor(
    menu = act,
    label = "CSV",
    help = "CSV"
  )
  {
    key = Key.F8
    icon = Icon.EXPORT_CSV
  }

  val xls = actor(
    menu = act,
    label = "XLS",
    help = "XLS"
  )
  {
    key = Key.F7
    icon = Icon.EXPORT_XLSX
  }

  val helps = actor(
    menu = act,
    label = "Help",
    help = "Help"
  )
  {
    key = Key.F9
    icon = Icon.HELP
  }

  /**
   * Actions
   */

  val cmdpdf = command(item = pdf)
  {
    model.export(VReport.TYP_PDF)
  }

  val cmdxls = command(item = xls)
  {
    model.export(VReport.TYP_XLS)
  }

  val cmdxlsx = command(item = xlsx)
  {
    model.export(VReport.TYP_XLSX)
  }

  val cmdcsv = command(item = csv)
  {
    model.export(VReport.TYP_CSV)
  }

  val idS = field(INT(10)) {
    label = "ID"
    help = "ID Stock"
    align = FieldAlignment.LEFT
  }

  val qty = field(INT(10)) {
    label = "Quantity"
    help = "ID Stock"
    align = FieldAlignment.LEFT
  }

  val type = field(STRING(20)) {
    label = "Type"
    help = "Type Product"
    align = FieldAlignment.LEFT
    format { v ->
      v.toUpperCase()
    }
  }

  val stocks = Stocks.selectAll()

  init {
    transaction {
      stocks.forEach() { result ->
        add {
          this[idS] = result[Stocks.idS]
          this[qty] = result[Stocks.qty]
          this[type] = result[Stocks.type]
        }
      }
    }
  }


}