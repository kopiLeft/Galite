package org.kopi.galite.ui.report

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.grid.ItemClickEvent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.renderer.ClickableRenderer
import org.kopi.galite.report.ReportRow
import org.kopi.galite.report.UReport
import org.kopi.galite.report.VReportColumn
import org.kopi.galite.report.VSeparatorColumn
import java.io.Serializable
import java.lang.reflect.Method

class DTable(val model: VTable) : VerticalLayout(), UReport.UTable, ClickableRenderer.ItemClickListener<Any> {

  /**
   * Creates a new `DTable` instance.
   * @param model The table model.
   */
  init {

    width = "100%"
    onItemClicked(this)
    add(model.createGrid())
  }

  override fun convertColumnIndexToModel(viewColumnIndex: Int): Int {
    TODO("Not yet implemented")
  }

  override fun convertColumnIndexToView(modelColumnIndex: Int): Int {
    TODO("Not yet implemented")
  }

  override fun onItemClicked(item: Any?) {
    TODO("Not yet implemented")
  }
}
