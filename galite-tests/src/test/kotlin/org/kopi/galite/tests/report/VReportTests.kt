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
package org.kopi.galite.tests.report

import java.awt.event.KeyEvent
import java.io.File
import java.io.FileInputStream
import java.math.BigDecimal
import java.util.Locale
import java.util.Scanner

import kotlin.test.assertEquals

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.Test
import org.kopi.galite.tests.ui.swing.JApplicationTestBase
import org.kopi.galite.visual.base.Utils
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.report.FieldAlignment
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.Constants
import org.kopi.galite.visual.report.PConfig
import org.kopi.galite.visual.report.Triggers
import org.kopi.galite.visual.report.VReport
import org.kopi.galite.visual.report.VReport.Companion.TYP_CSV
import org.kopi.galite.visual.report.VReport.Companion.TYP_XLS
import org.kopi.galite.visual.report.VReport.Companion.TYP_XLSX
import org.kopi.galite.visual.visual.VActor

/**
 *
 * This class contains tests for the report model [VReport]
 *
 * @see VReport
 */
class VReportTests: JApplicationTestBase() {

  /**
   * Checks that export report as CSV working correctly.
   */
  @Test
  fun exportFile_CSV() {
    withReport(SimpleReport()) {
      val file = Utils.getTempFile("galite", "csv")
      val sc = Scanner(file)
      var result = ""

      model.export(file, TYP_CSV)
      sc.useDelimiter(" ")

      while (sc.hasNext()) {
        result += sc.next()
      }
      sc.close()

      assertEquals("id\tname\tage\tsalary\n" +
                           "1\tHichem\t26\t2.000,88000\n" +
                           "2\tSarra\t23\t2.000,55000\n" +
                           "3\tHoussem\t25\t2.000,44000\n" +
                           "4\tZied\t24\t2.000,33000\n" +
                           "\t\t\t2.000,55000\n", result)
    }
  }

  /**
   * Reading Excel file.
   */
  fun readExcelFile(file: File, type: Int) : String {
    var result = ""

    try {
      val file = FileInputStream(file)
      val workbook = if(type == TYP_XLS) {
        //Create Workbook instance holding reference to .xls file
        HSSFWorkbook(file) //xls
      } else {
        //Create Workbook instance holding reference to .xlsx file
        XSSFWorkbook(file) // xlsx
      }

      //Get first/desired sheet from the workbook
      val sheet = workbook.getSheetAt(0)

      //Iterate through each rows one by one
      val rowIterator: Iterator<Row> = sheet.iterator()

      while (rowIterator.hasNext()) {
        val row: Row = rowIterator.next()
        //For each row, iterate through all the columns
        val cellIterator: Iterator<Cell> = row.cellIterator()
        while (cellIterator.hasNext()) {
          val cell: Cell = cellIterator.next()
          when (cell.cellType) {
            CellType.NUMERIC -> result += cell.numericCellValue.toString() + "  "
            CellType.STRING -> result += cell.stringCellValue.toString() + "  "
          }
        }
        result += ""
      }
      file.close()
    } catch (e: Exception) {
      e.printStackTrace()
    }

    return result
  }
  /**
   * Checks that export report as XLS working correctly.
   */
  @Test
  fun exportFile_XLS() {
    withReport(SimpleReport()) {
      val file = Utils.getTempFile("galite", "xls")

      model.export(file, TYP_XLS)

      val result = readExcelFile(file, TYP_XLS)

      assertEquals("id  name  age  salary  " +
                           "1.0  Hichem  26.0  2000.88" +
                           "  2.0  Sarra  23.0  2000.55  " +
                           "3.0  Houssem  25.0  2000.44  " +
                           "4.0  Zied  24.0  2000.33  2000.55  ", result)
    }
  }

  /**
   * Checks that export report as XLSX working correctly.
   */
  @Test
  fun exportFile_XLSX() {
    withReport(SimpleReport()) {
      val file = Utils.getTempFile("galite", "xlsx")

      model.export(file, TYP_XLSX)

      val result = readExcelFile(file, TYP_XLSX)

      assertEquals("id  name  age  salary  " +
                           "1.0  Hichem  26.0  2000.88" +
                           "  2.0  Sarra  23.0  2000.55  " +
                           "3.0  Houssem  25.0  2000.44  " +
                           "4.0  Zied  24.0  2000.33  2000.55  ", result)
    }
  }

  /**
   * Checks that f12 actor is the first report actor.
   */
  @Test
  fun reportVActorTest() {
    withReport(SimpleReport()) {
      val f12 = VActor("File",
                       "org/kopi/galite/visual/Window",
                       "GotoShortcuts",
                       "org/kopi/galite/visual/Window",
                       null,
                       KeyEvent.VK_F12,
                       0)
              .also {
                it.menuName = "File"
                it.menuItem = "Shortcuts"
              }

      // Actor checks
      assertEquals(f12, model.actors[0])
      assertEquals(f12, model.actors[0])
      assertEquals(-8, model.actors[0]!!.number)
    }
  }

  /**
   * Tests the report source, title, small icon and print config
   */
  @Test
  fun reportSourceTitleTest() {
    withReport(SimpleReport()) {
      assertEquals("org/kopi/galite/tests/report/SimpleReport", model.source)
      assertEquals("SimpleReport", model.getTitle())
      assertEquals(null, model.smallIcon)
      assertEquals(PConfig(), model.printOptions)
    }
  }

  /**
   * Tests the report data model
   */
  @Test
  fun vmodelTest() {
    withReport(SimpleReport()) { mReport ->
      val id = mReport.columns[0]!!
      val name = mReport.columns[1]!!

      // Columns checks
      assertEquals("ANM_1", name.ident)
      assertEquals("name", name.label)
      assertEquals("The user name", name.help)
      assertEquals(Constants.CLO_VISIBLE, name.options)
      assertEquals(Constants.ALG_DEFAULT, name.align)
      assertEquals(-1, name.groups)
      assertEquals(null, name.function)
      assertEquals(true, name.isVisible)
      assertEquals(false, name.isFolded)
      // assertEquals(0, name.width) TODO
      // assertEquals(0, name.height) TODO
      assertEquals(false, id.isHidden())
      // assertEquals(0, name.width) TODO
      // assertEquals(0, name.height) TODO
    }
  }

  /**
   * Simple Report with two fields.
   */
  class SimpleReport : Report(title = "SimpleReport", locale = Locale.UK) {

    val id = field(INT(20)) {
      label = "id"
      help = "The user id"
    }

    val name = field(STRING(20)) {
      label = "name"
      help = "The user name"
    }

    val age = field(INT(3)) {
      label = "age"
      help = "The user age"
    }

    val salary = field(DECIMAL(width = 10, scale = 5)) {
      label = "salary"
      help = "The user salary"
      align = FieldAlignment.LEFT
      compute {
        // Computes the average of ages
        Triggers.avgDecimal(this)
      }
    }

    init {
      add {
        this[id] = 1
        this[name] = "Hichem"
        this[age] = 26
        this[salary] = BigDecimal("2000.88")
      }
      add {
        this[id] = 2
        this[name] = "Sarra"
        this[age] = 23
        this[salary] = BigDecimal("2000.55")
      }
      add {
        this[id] = 3
        this[name] = "Houssem"
        this[age] = 25
        this[salary] = BigDecimal("2000.44")
      }
      add {
        this[id] = 4
        this[name] = "Zied"
        this[age] = 24
        this[salary] = BigDecimal("2000.33")
      }
    }
  }
}
