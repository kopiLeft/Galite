//package org.kopi.galite.visual.pivottable
//
//import org.vaadin.addons.componentfactory.PivotTable
//import org.vaadin.addons.componentfactory.PivotTable.*
//
//class PivotTable2 ()
//{
//    fun createTable () : PivotData {
//        val pivotData = PivotData()
//        pivotData.addColumn("color", String::class.java)
//        pivotData.addColumn("shape", String::class.java)
//        pivotData.addColumn("size", Int::class.java)
//        pivotData.addColumn("filled", Boolean::class.java)
//        pivotData.addRow("blue", "circle", 2, true)
//        pivotData.addRow("red", "triangle", 3, false)
//        pivotData.addRow("orange", "square", 1, true)
//        pivotData.addRow("yellow", "circle", 3, false)
//        pivotData.addRow("brown", "circle", 2, true)
//        return pivotData
//    }
//
//    fun createOptions() : PivotOptions {
//        val pivotOptions = PivotOptions()
//
//        pivotOptions.setRows("color", "size")
//        pivotOptions.setCols("shape")
//        pivotOptions.setCharts(true)
//        return pivotOptions
//    }
//
//    var table = PivotTable(createTable(), createOptions(), PivotMode.INTERACTIVE)
//}