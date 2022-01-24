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

import java.util.Locale

import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Product
import org.kopi.galite.demo.database.Provider
import org.kopi.galite.demo.database.Stock
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.report.FieldAlignment
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.VReport

/**
 * Stock Report
 */
class StockR : Report(title = "Stocks", locale = Locale.UK) {

  val action = menu("Action")

  val csv = actor(
          menu = action,
          label = "CSV",
          help = "CSV Format",
  ) {
    key = Key.F8
    icon = Icon.EXPORT_CSV
  }

  val xls = actor(
          menu = action,
          label = "XLS",
          help = "Excel (XLS) Format",
  ) {
    key = Key.SHIFT_F8
    icon = Icon.EXPORT_XLSX
  }

  val xlsx = actor(
          menu = action,
          label = "XLSX",
          help = "Excel (XLSX) Format",
  ) {
    key = Key.SHIFT_F8
    icon = Icon.EXPORT
  }

  val pdf = actor(
          menu = action,
          label = "PDF",
          help = "PDF Format",
  ) {
    key = Key.F9
    icon = Icon.EXPORT_PDF
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

  val description = field(STRING(25)) {
    label = "Description"
    help = "The product description"
    align = FieldAlignment.LEFT
  }
  val nameProvider = field(STRING(25)) {
    label = "Provider name"
    help = "The provider name"
    align = FieldAlignment.LEFT
  }
  val minAlert = field(INT(25)) {
    label = "Min Alert"
    help = "The stock's min alert"
    align = FieldAlignment.LEFT
  }

  val stocks = Stock.join(Provider, JoinType.INNER, Stock.idStckProv, Provider.idProvider)
          .join(Product, JoinType.INNER, Stock.idStckProv, Product.idPdt)
          .slice(Stock.minAlert, Product.description, Provider.nameProvider)
          .selectAll()

  init {
    transaction {
      stocks.forEach { result ->
        add {
          this[minAlert] = result[Stock.minAlert]
          this[description] = result[Product.description]
          this[nameProvider] = result[Provider.nameProvider]
        }
      }
    }
  }
}
