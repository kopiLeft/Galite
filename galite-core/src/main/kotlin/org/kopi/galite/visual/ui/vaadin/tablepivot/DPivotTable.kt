/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.tablepivot

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import org.kopi.galite.visual.tablepivot.UPivotTable
import org.kopi.galite.visual.tablepivot.VPivotTable
import org.kopi.galite.visual.ui.vaadin.visual.DWindow
import org.vaadin.addons.componentfactory.PivotTable
import org.vaadin.addons.componentfactory.PivotTable.*

class DPivotTable(private val pivotTable: VPivotTable) : DWindow(pivotTable), UPivotTable {

    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------
    private var pivotData = PivotData()
    private var pivotOptions = PivotOptions()
    private var button = Button("to Dialog")

    init {
        getModel()!!.setDisplay(this)
        setSizeFull()
    }

    //---------------------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------------------
    override fun run() {
        pivotTable.initReport()
        pivotTable.setMenu()
    }

    override fun build() {
        pivotData.addColumn("color", String::class.java)
        pivotData.addColumn("shape", String::class.java)
        pivotData.addColumn("size", Int::class.java)
        pivotData.addColumn("filled", Boolean::class.java)

        fun generatePivotDataRows(numRows: Int) {
            val colors = listOf("blue", "red", "orange", "yellow", "brown", "black", "white", "green", "purple") // Example colors
            val shapes = listOf("circle", "triangle", "square", "rectangle", "heptagone", "prism", "hexagon")  // Example shapes

            repeat(numRows) {
                val color = colors.random()
                val shape = shapes.random()
                val count = (1..100).random() // Example random count between 1 and 5
                val flag = listOf(true, false).random()

                pivotData.addRow(color, shape, count, flag)
            }
        }

        generatePivotDataRows(10000)


        pivotOptions.setRows("color")
        pivotOptions.setCols("shape")
        pivotOptions.setCharts(true)
        var table = PivotTable(pivotData, pivotOptions, PivotMode.INTERACTIVE)



        button.addClickListener { event: ClickEvent<Button?>? ->
            if (getChildren().anyMatch { child: Component -> child === table }
            ) {
                remove(table)
                button.setText("to Normal")
                val dialog = Dialog()
                dialog.add(table)
                dialog.setWidth("100%")
                dialog.setHeight("100%")
                dialog.open()
            } else {
                button.setText("to Dialog")
                add(table)
            }
        }

        add(table, button)
    }
}