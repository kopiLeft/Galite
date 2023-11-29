/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2023 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.pivottable

import org.vaadin.addons.componentfactory.PivotTable
import com.vaadin.flow.component.dependency.CssImport

import org.kopi.galite.visual.dsl.pivottable.Dimension.Position
import org.kopi.galite.visual.pivottable.MPivotTable
import org.kopi.galite.visual.pivottable.UPivotTable
import org.kopi.galite.visual.pivottable.VPivotTable
import org.kopi.galite.visual.ui.vaadin.visual.DWindow

@CssImport("./styles/galite/pivottable.css")
class DPivotTable(private val pivotTable: VPivotTable) : DWindow(pivotTable), UPivotTable {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val model: MPivotTable = pivotTable.model // pivot table model
  private val pivotData = PivotTable.PivotData()
  private val pivotOptions = PivotTable.PivotOptions()
  private val rows = mutableListOf<String>()
  private val columns = mutableListOf<String>()

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
    model.columns.forEach {
      pivotData.addColumn(it?.label, it?.javaClass)
      if (it?.position == Position.ROW) {
        rows.add(it.label)
      }
      if (it?.position == Position.COLUMN) {
        columns.add(it.label)
      }
    }

    model.userRows
      ?.flatMap { it.data.asIterable() }
      ?.chunked(model.columns.count()) { rows ->
        pivotData.addRow(*rows.map { it ?: "" }.toTypedArray())}

    // Pivot table dimension
    pivotOptions.setRows(*rows.toTypedArray())
    pivotOptions.setCols(*columns.toTypedArray())

    // Pivot table default renderer
    pivotOptions.setRenderer(pivotTable.defaultRenderer)

    // Pivot table aggregate function
    pivotOptions.setAggregator(pivotTable.aggregator.first, pivotTable.aggregator.second)

    // Pivot table renderer
    if(rows.isNotEmpty() && columns.isNotEmpty()) {
      pivotOptions.setCharts(true)
    }
    pivotOptions.setDisabledRenderers(*pivotTable.disabledRerenders.toTypedArray())

    // Pivot table mode
    val pivotMode = if (pivotTable.interactive == 0) {
      PivotTable.PivotMode.INTERACTIVE
    } else {
      PivotTable.PivotMode.NONINTERACTIVE
    }

    val pivot = PivotTable(pivotData, pivotOptions, pivotMode)

    add(pivot)
  }
}
