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

package org.kopi.galite.tests.ui.report

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.kopi.galite.domain.Domain
import org.kopi.galite.report.Report
import org.kopi.galite.ui.report.VReport

@Route("VReport")
class VReportTests : VerticalLayout() {

  init {
    val report = SimpleReport()
    val vreport = VReport(report)
    val verticalLayout = VerticalLayout()
    verticalLayout.add(vreport)
    add(verticalLayout)
  }

  /**
   * Simple report with two fields
   */
  class SimpleReport : Report() {
    val field1 = field(StringTestType()) {
      label = "field1"
    }
    val field2 = field(LongTestType()) {
      label = "field2"
    }

    init {
      add {
        this[field1] = "test1"
        this[field2] = 64L
      }
      add {
        this[field1] = "test2"
        this[field2] = 32L
      }
    }
  }

  /**
   * String type Domain
   */
  class StringTestType : Domain<String>(5) {
    override val type = code {
      this["cde1"] = "test1"
    }
  }

  /**
   * Long type Domain
   */
  class LongTestType : Domain<Long>(5) {
    override val type = code {
      this["cde1"] = 1
    }
  }
}
