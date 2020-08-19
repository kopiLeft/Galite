package org.kopi.galite.visual.report

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.kopi.galite.visual.field.Field

class VReport(report: Report) : VerticalLayout() {
  var reportGrid = Grid<MutableMap<Field<*>, Any>>()

  init {
    addColumns(report.fields)
    reportGrid.setItems(report.getLines())
    add(reportGrid)
  }

  // Add columns in the report based on the given fields
  fun addColumns(Fields: List<Field<*>>) {
    for (field in Fields) {
      reportGrid.addColumn { hashmap -> hashmap.get(field) }
          .setHeader(field.label)
          .setKey(field.label)
          .setSortable(true)
    }
  }
}