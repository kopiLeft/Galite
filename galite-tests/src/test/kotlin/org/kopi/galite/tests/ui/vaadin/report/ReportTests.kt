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
package org.kopi.galite.tests.ui.vaadin.report

import java.util.Locale

import kotlin.test.assertEquals

import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ui.vaadin.common.VCaption
import org.kopi.galite.visual.ui.vaadin.main.MainWindow
import org.kopi.galite.visual.ui.vaadin.main.VWindowContainer
import org.kopi.galite.visual.ui.vaadin.report.DReport
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.FormBlock
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm
import org.kopi.galite.visual.dsl.report.FieldAlignment
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.Triggers
import org.kopi.galite.visual.type.Decimal
import org.kopi.galite.tests.examples.initDatabase

import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10.expectRow
import com.github.mvysny.kaributesting.v10.expectRows
import com.vaadin.flow.component.grid.Grid

class ReportTests: GaliteVUITestBase() {

  private val formWithReport = FormWithReport().also { it.model } // initialize the model
  private val simpleReport = SimpleReport()
  private val mainWindow get() = _get<MainWindow>()
  private val windowCaption get() =
    mainWindow
      ._get<VWindowContainer>()
      ._get<VCaption>()

  @Test
  fun `test simple report`() {
    // Login
    login()

    // Opens a form that contain a report command
    formWithReport.open()

    // Trigger the report command
    formWithReport.report.triggerCommand()

    // Check that the report is displayed
    _expectOne<DReport>()

    // Check that the displayed title is the same title defined using the DSL
    assertEquals(simpleReport.title, windowCaption.getCaption())

    // Check that the grid data is correct
    val report = _get<Grid<*>>()
    /** Last column contains empty strings. That represents the values for [org.kopi.galite.report.VSeparatorColumn] */
    val data = arrayOf(
      arrayOf("", "23", "", "2.000,37000", ""),
      arrayOf("SAMI", "22", "", "2.000,00000", ""),
      arrayOf("SOFIA", "24", "", "2.000,55000", "")
    )

    report.expectRows(data.size)

    data.forEachIndexed { index, it ->
      report.expectRow(index, *it)
    }
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      initDatabase()
    }
  }
}

class FormWithReport : ReportSelectionForm() {
  override val locale = Locale.UK
  override val title = "form for test"

  val action = menu("Action")

  val report = actor(
    ident = "report",
    menu = action,
    label = "CreateReport",
    help = "Create report",
  ) {
    key = Key.F8          // key is optional here
    icon = "report"  // icon is optional here
  }

  val block = insertBlock(BlockSample()) {
    command(item = report) {
      action = {
        createReport(this@insertBlock)
      }
    }
  }

  override fun createReport(): Report {
    return SimpleReport()
  }

  inner class BlockSample : FormBlock(1, 1, "Test block") {
    val name = visit(domain = STRING(20), position = at(1, 1)) {
      label = "name"
      help = "The user name"
    }
  }
}

class SimpleReport : Report() {
  override val locale = Locale.UK
  override val title = "SimpleReport"

  val name = field(STRING(20)) {
    label = "name"
    help = "The user name"
    align = FieldAlignment.LEFT
    group = age
    format { value ->
      value.toUpperCase()
    }
  }

  val age = field(INT(3)) {
    label = "age"
    help = "The user age"
    align = FieldAlignment.LEFT
    compute {
      // Computes the average of ages
      Triggers.avgInteger(this)
    }
  }

  val profession = field(STRING(20)) {
    label = "profession"
    help = "The user profession"
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
      this[name] = "Sami"
      this[age] = 22
      this[profession] = "Journalist"
      this[salary] = Decimal("2000")
    }
    add {
      this[name] = "Sofia"
      this[age] = 23
      this[profession] = "Dentist"
      this[salary] = Decimal("2000.55")
    }
    add {
      this[age] = 25
      this[profession] = "Baker"
      this[name] = "Sofia"
      this[salary] = Decimal("2000.55")
    }
  }
}
