package org.kopi.galite.demo.client

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.kopi.galite.visual.dsl.common.Window
import org.kopi.galite.visual.report.VReport
import org.kopi.galite.visual.ui.vaadin.visual.DWindow
import org.vaadin.addons.componentfactory.PivotTable
import org.vaadin.addons.componentfactory.PivotTable.PivotData
import java.util.Locale

abstract class DSLPivotTable(title: String) : AppLayout() {

    val pivotData = PivotData()
    val pivotOptions = PivotTable.PivotOptions()

    fun setColumns (columns: Map<String, Class<*>>) {
        for (column in columns)
            pivotData.addColumn(column.key, column.value)
    }

    fun setRows (rows: List<List<Any>>) {
        rows.forEach {
            pivotData.addRow(it)
        }
    }

    fun setOptions(cols: List<String>, rows: List<String>) {
        cols.forEach{pivotOptions.setCols(it)}
        rows.forEach{pivotOptions.setRows(it)}
        pivotOptions.setCharts(true)
    }


    init {
        val table = PivotTable(pivotData, pivotOptions, PivotTable.PivotMode.INTERACTIVE)
        this.setContent(table)
    }



























    // I added these two so that I don't get the abstract error in ClientR
//    override fun genLocalization(destination: String?, locale: Locale?) {}
//    override lateinit var model: VReport
}
