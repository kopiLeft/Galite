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
package org.kopi.galite.demo.provider

import java.util.Locale

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.database.Provider
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.report.FieldAlignment
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.VReport

/**
 * Provider Report
 */
class ProviderR : Report() {
  override val locale = Locale.UK

  override val title = "Providers_Report"

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
    icon = Icon.EXPORT
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

  val nameProvider = field(STRING(50)) {
    label = "Name"
    help = "The provider name"
    align = FieldAlignment.LEFT
    format { value ->
      value.toUpperCase()
    }
  }

  val tel = field(INT(25)) {
    label = "Phone number"
    help = "The provider phone number"
    align = FieldAlignment.LEFT
  }

  val description = field(STRING(255)) {
    label = "Description"
    help = "The provider description"
    align = FieldAlignment.LEFT
    format { value ->
      value.toUpperCase()
    }
  }

  val address = field(STRING(70)) {
    label = "Address"
    help = "The provider address"
    align = FieldAlignment.LEFT
  }

  val zipCode = field(INT(50)) {
    label = "Zip code"
    help = "The provider zip code"
    align = FieldAlignment.LEFT
  }

  val providers = Provider.selectAll()

  init {
    transaction {
      providers.forEach { result ->
        add {
          this[nameProvider] = result[Provider.nameProvider]
          this[description] = result[Provider.description]
          this[tel] = result[Provider.tel]
          this[zipCode] = result[Provider.zipCode]
          this[address] = result[Provider.address]
        }
      }
    }
  }
}
