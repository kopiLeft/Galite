package org.kopi.galite.demo.client

import org.kopi.galite.visual.VWindow
import org.vaadin.addons.componentfactory.PivotTable
import org.vaadin.addons.componentfactory.PivotTable.*

abstract class VPivotTable internal constructor() : VWindow() {

    abstract val pivotData : PivotData
    abstract val pivotOptions: PivotOptions
    abstract val pivotMode: PivotMode

    val model: PivotTable = PivotTable(pivotData, pivotOptions, pivotMode)

}
