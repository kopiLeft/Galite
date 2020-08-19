package org.kopi.galite.demo

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.kopi.galite.domain.Domain
import org.kopi.galite.visual.report.Report
import org.kopi.galite.visual.report.VReport

@Route("ReportDemo")
class VReportDemo : VerticalLayout() {
  init {
    var report = SimpleReport()
    add(VReport(report))
  }

  /**
   * Simple Report with two fields.
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

  class StringTestType : Domain<String>(5) {
    override val values = code {
      this["cde1"] = "test1"
    }
  }

  class LongTestType : Domain<Long>(5) {
    override val values = code {
      this["cde1"] = 1
    }
  }
}