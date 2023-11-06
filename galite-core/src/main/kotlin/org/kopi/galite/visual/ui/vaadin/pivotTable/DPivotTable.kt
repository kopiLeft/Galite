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
package org.kopi.galite.visual.ui.vaadin.pivotTable

import com.vaadin.flow.component.dependency.CssImport
import org.kopi.galite.visual.dsl.pivotTable.Position
import org.kopi.galite.visual.pivotTable.MPivotTable
import org.kopi.galite.visual.pivotTable.UPivotTable
import org.kopi.galite.visual.pivotTable.VPivotTable
import org.kopi.galite.visual.ui.vaadin.visual.DWindow

import org.vaadin.addons.componentfactory.PivotTable

@CssImport("./styles/galite/pivottable.css")
class DPivotTable(private val pivotTable: VPivotTable) : DWindow(pivotTable), UPivotTable {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val model: MPivotTable = pivotTable.model // pivot table model
  private val pivotData = PivotTable.PivotData()
  private val pivotOptions = PivotTable.PivotOptions()
  private val listeDimensionRows = mutableListOf<String>()
  private val listeDimensionColumns = mutableListOf<String>()

  init {
    getModel()!!.setDisplay(this)
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun run() {
    pivotTable.initPivotTable()
    pivotTable.setMenu()
  }

  override fun build() {
    model.columns
      .forEach {
        pivotData.addColumn(it?.label, it?.javaClass)
        if (it?.position == Position.DIMENSION_ROW) {
          listeDimensionRows.add(it.label)
        }
        if (it?.position == Position.DIMENSION_COL) {
          listeDimensionColumns.add(it.label)
        }
      }

    model.userRows
      ?.flatMap { it.data.filterNotNull() }
      ?.chunked(model.columns.count()) { rows ->
        pivotData.addRow(*rows.toTypedArray())
      }

    // Pivot table dimension
    pivotOptions.setRows(*listeDimensionRows.toTypedArray())
    pivotOptions.setCols(*listeDimensionColumns.toTypedArray())

    // Pivot table default renderer
    pivotOptions.setRenderer(pivotTable.pivottableType)

    // Pivot table aggregate function
    pivotOptions.setAggregator(pivotTable.aggregator.first, pivotTable.aggregator.second)

    // Pivot table renderer
    if(listeDimensionRows.isNotEmpty() && listeDimensionColumns.isNotEmpty()) {
      pivotOptions.setCharts(true)
    }
    pivotOptions.setDisabledRenderers(*pivotTable.disabledRerenders.toTypedArray())

    // Pivot table mode
    val pivotMode = if(pivotTable.interactive == 0) {
      PivotTable.PivotMode.INTERACTIVE
    } else {
      PivotTable.PivotMode.NONINTERACTIVE
    }

    val pivot = PivotTable(pivotData, pivotOptions, pivotMode)

    add(pivot)
  }
}
