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
package org.kopi.galite.demo.client

import java.util.Locale

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

import org.kopi.galite.demo.Client
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Key
import org.kopi.galite.report.FieldAlignment
import org.kopi.galite.report.Report
import org.kopi.galite.report.VCellFormat
import org.kopi.galite.report.VReport

/**
 * Client Report
 */
object ClientR : Report() {
  override val locale = Locale.FRANCE

  override val title = "Clients_Report"

  val action = menu("Action")

  val csv = actor(
          ident = "CSV",
          menu = action,
          label = "CSV",
          help = "Obtenir le format CSV",
  ) {
    key = Key.F8          // key is optional here
    icon = "export"  // icon is optional here
  }

  val xls = actor(
          ident = "XLS",
          menu = action,
          label = "XLS",
          help = "Obtenir le format Excel (XLS)",
  ) {
    key = Key.SHIFT_F8          // key is optional here
    icon = "export"  // icon is optional here
  }

  val xlsx = actor(
          ident = "XLSX",
          menu = action,
          label = "XLSX",
          help = "Obtenir le format Excel (XLSX)",
  ) {
    key = Key.SHIFT_F8          // key is optional here
    icon = "export"  // icon is optional here
  }

  val pdf = actor(
          ident = "PDF",
          menu = action,
          label = "PDF",
          help = "Obtenir le format PDF",
  ) {
    key = Key.F9          // key is optional here
    icon = "export"  // icon is optional here
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

  val fstnameClt = field(Domain<String>(25)) {
    label = "First Name"
    help = "The client first name"
    align = FieldAlignment.LEFT
    format {
      object : VCellFormat() {
        override fun format(value: Any?): String {
          return (value as String).toUpperCase()
        }
      }
    }
  }

  val nameClt = field(Domain<String>(25)) {
    label = "Last Name"
    help = "The client last name"
    align = FieldAlignment.LEFT
    format {
      object : VCellFormat() {
        override fun format(value: Any?): String {
          return (value as String).toUpperCase()
        }
      }
    }
  }

  val addressClt = field(Domain<String>(50)) {
    label = "Address"
    help = "The client address"
    align = FieldAlignment.LEFT
    format {
      object : VCellFormat() {
        override fun format(value: Any?): String {
          return (value as String).toUpperCase()
        }
      }
    }
  }

  val ageClt = field(Domain<Int>(2)) {
    label = "Age"
    help = "The client age"
    align = FieldAlignment.LEFT
  }

  val cityClt = field(Domain<String>(50)) {
    label = "City"
    help = "The client city"
    align = FieldAlignment.LEFT
  }

  val postalCodeClt = field(Domain<Int>(2)) {
    label = "Zip code"
    help = "The client zip code"
    align = FieldAlignment.LEFT
  }

  val q = Client.selectAll()

  init {
    transaction {
      q.forEach { result ->
        add {
          this[nameClt] = result[Client.nameClt]
          this[fstnameClt] = result[Client.fstnameClt]
          this[addressClt] = result[Client.addressClt]
          this[cityClt] = result[Client.cityClt]
          this[postalCodeClt] = result[Client.postalCodeClt]
          this[ageClt] = result[Client.ageClt]
        }
      }
    }
  }
}
