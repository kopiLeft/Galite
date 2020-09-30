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

package org.kopi.galite.tests.ui.demo

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.kopi.galite.report.Report
import org.kopi.galite.ui.report.DReport
import org.kopi.galite.ui.visual.VApplication
import org.kopi.galite.visual.UIFactory

@Route("VReport")
class VReportTests : VerticalLayout() {

  init {
    setupApplication()
    val model = SimpleReport()
    val dReport = UIFactory.uiFactory.createView(model) as DReport
    dReport.run()
    add(dReport)
  }

  private fun setupApplication() {
    val application = object : VApplication() {
    }
  }

  /**
   * Simple report with two fields
   */
  class SimpleReport : Report() {
    val field1 = field<String> {
      label = "Pays"
    }
    val field2 = field<Int> {
      label = "CodeDouanier"
    }

    init {
      add {
        this[field1] = "Tunis"
        this[field2] = 123456789
      }
      add {
        this[field1] = "France"
        this[field2] = 987654321
      }
    }
  }
}
