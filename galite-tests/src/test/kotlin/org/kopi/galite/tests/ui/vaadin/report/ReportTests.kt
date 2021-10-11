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

import kotlin.test.assertEquals

import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.form.FormWithReport
import org.kopi.galite.tests.report.SimpleReport
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ui.vaadin.common.VCaption
import org.kopi.galite.visual.ui.vaadin.main.MainWindow
import org.kopi.galite.visual.ui.vaadin.main.VWindowContainer
import org.kopi.galite.visual.ui.vaadin.report.DReport

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
    formWithReport.open(500)

    // Trigger the report command
    formWithReport.report.triggerCommand()

    // Check that the report is displayed
    _expectOne<DReport>()

    // Check that the displayed title is the same title defined using the DSL
    assertEquals(simpleReport.title, windowCaption.getCaption())

    // Check that the grid data is correct
    val report = _get<Grid<*>>()
    /** Last column contains empty strings. That represents the values for [org.kopi.galite.visual.report.VSeparatorColumn] */
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
      org.kopi.galite.tests.examples.initModules()
    }
  }
}
