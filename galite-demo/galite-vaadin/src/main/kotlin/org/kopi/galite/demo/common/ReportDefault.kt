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

import org.kopi.galite.visual.dsl.common.Actor
import org.kopi.galite.visual.dsl.common.Command
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Menu
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.VReport

interface IReportDefault {
  // -------------------------------------------------------------------
  // MENUS
  // -------------------------------------------------------------------
  val file: Menu
  val edit: Menu
  val action: Menu

  // -------------------------------------------------------------------
  // ACTORS
  // -------------------------------------------------------------------
  val csv: Actor
  val xls: Actor
  val xlsx: Actor
  val pdf: Actor
  val cmdCSV: Command
  val cmdPDF: Command
  val cmdXLS: Command
  val cmdXLSX: Command
}

open class ReportDefaultImpl : IReportDefault, Report("") {

  // --------------------MENUS-----------------
  final override val file = menu("File")
  final override val edit = menu("Edit")
  final override val action = menu("Action")

  // --------------------Actors-----------------
  override val csv = actor(
    menu = action,
    label = "CSV",
    help = "CSV Format",
  ) {
    key = Key.F8
    icon = Icon.EXPORT_CSV
  }

  override val xls = actor(
    menu = action,
    label = "XLS",
    help = "Excel (XLS) Format",
  ) {
    key = Key.SHIFT_F8
    icon = Icon.EXPORT_XLSX
  }

  override val xlsx = actor(
    menu = action,
    label = "XLSX",
    help = "Excel (XLSX) Format",
  ) {
    key = Key.SHIFT_F8
    icon = Icon.EXPORT
  }

  override val pdf = actor(
    menu = action,
    label = "PDF",
    help = "PDF Format",
  ) {
    key = Key.F9
    icon = Icon.EXPORT_PDF
  }

  // -------------------------------------------------------------------
  // REPORT COMMANDS
  // -------------------------------------------------------------------
  override val cmdCSV = command(item = csv) {
    model.export(VReport.TYP_CSV)
  }

  override val cmdPDF = command(item = pdf) {
    model.export(VReport.TYP_PDF)
  }

  override val cmdXLS = command(item = xls) {
    model.export(VReport.TYP_XLS)
  }

  override val cmdXLSX = command(item = xlsx) {
    model.export(VReport.TYP_XLSX)
  }
}
