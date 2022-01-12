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
 *  Report Tests :
 *    test title
 *    test locale
 *    test menu
 *    test actors
 *    test commands
 *    test group
 *    test align field
 *    test format
 */
class DocumentationReportR : Report(title = "Test Report", locale = Locale.UK) {

  //test Menu
  val action = menu("Action")

  //test Actors
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
    icon = Icon.EXPORT_XLSX
  }

  val pdf = actor(
    menu = action,
    label = "PDF",
    help = "PDF Format",
  ) {
    key = Key.F9
    icon = Icon.EXPORT_PDF
  }

  val editColumnData = actor(
    menu = action,
    label = "Edit Column Data",
    help = "Edit Column Data",
  ) {
    key = Key.F8
    icon = Icon.FORMULA
  }

  val helpForm = actor(
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

  // test to upper Case format + align left
  val name = field(STRING(25)) {
    label = "Name"
    help = "The name"
    align = FieldAlignment.LEFT
    format { value ->
      value.toUpperCase()
    }
    group = age
  }

  // test to lower Case format + align right
  val lastName = nullableField(STRING(25)) {
    label = "last Name "
    help = "The last name"
    align = FieldAlignment.RIGHT
    format { value ->
      value.toLowerCase()
    }
    group = age
  }

  // test normal format + align center
  val middleName = nullableField(STRING(25)) {
    label = "middleName"
    help = "The middle Name"
    align = FieldAlignment.CENTER
    group = age
  }

  val age = nullableField(INT(25)) {
    label = "age"
    help = "age"
  }

  //test hidden field
  val hiddenField = field(STRING(10)) {
    label = "hidden Field"
    hidden = true
  }

  val testTable = TestTable.selectAll()

  init {
    transaction {
      testTable.forEach { result ->
        add {
          this[name] = result[TestTable.name]
          this[lastName] = result[TestTable.lastName]
          this[middleName] = result[TestTable.lastName]
          this[age] = result[TestTable.age]
        }
      }
    }
  }
}
