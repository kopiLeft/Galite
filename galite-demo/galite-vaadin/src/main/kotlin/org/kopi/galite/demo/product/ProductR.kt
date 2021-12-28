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
package org.kopi.galite.demo.product

import java.util.Locale

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Product
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.VReport

/**
 * Product Report
 */
class ProductReport : Report() {
  override val locale = Locale.UK

  override val title = "Products"

  val action = menu("Action")

  val quit = actor(
          ident = "quit",
          menu = action,
          label = "Quit",
          help = "Quit",
  ) {
    key = Key.F1
    icon = Icon.QUIT
  }

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


  val cmdQuit = command(item = quit) {
    model.close()
  }

  val category = field(Category) {
    label = "Category"
    help = "The product category"
    group = department
  }

  val department = field(STRING(20)) {
    label = "Department"
    help = "The product department"
    group = description
  }

  val description = field(STRING(50)) {
    label = "Description"
    help = "The product description"
    format { value ->
      value.toUpperCase()
    }
  }

  val supplier = field(STRING(20)) {
    label = "Supplier"
    help = "The supplier"
  }

  val taxName = field(STRING(10)) {
    label = "Tax"
    help = "The product tax name"
  }

  val price = field(DECIMAL(10, 5)) {
    label = "Price"
    help = "The product unit price excluding VAT"
  }

  val products = Product.selectAll()

  init {
    transaction {
      products.forEach { result ->
        add {
          this[description] = result[Product.description]
          this[department] = result[Product.department]
          this[supplier] = result[Product.supplier]
          this[category] = result[Product.category]
          this[taxName] = result[Product.taxName]
          this[price] = result[Product.price]
        }
      }
    }
  }
}
