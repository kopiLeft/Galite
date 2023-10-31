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
  private var pivotData = PivotTable.PivotData()
  private var pivotOptions = PivotTable.PivotOptions()
  private val listeDimensions = mutableListOf<String>()

  init {
    getModel()!!.setDisplay(this)
    setSizeFull()
    element.style["display"] = "flex!important"
    element.style["flex-direction"] = "column!important"
    element.style["overflow"] = "auto!important"
    element.style["position"] = "relative!important"
    element.style["outline"] = "none!important"
    element.style["z-index"] = "0!important"
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
        if (it?.dimension == true) {
          listeDimensions.add(it.label)
        }
      }

    model.userRows
      ?.flatMap { it.data.filterNotNull() }
      ?.chunked(model.columns.count()) { rows ->
        pivotData.addRow(*rows.toTypedArray())
      }

    pivotOptions.setRows(*listeDimensions.toTypedArray())
    pivotOptions.setRenderer(pivotTable.pivottableType)
    pivotOptions.setAggregator(pivotTable.aggregator.first, pivotTable.aggregator.second)
    val pivot = PivotTable(pivotData, pivotOptions, PivotTable.PivotMode.INTERACTIVE)

    add(pivot)
  }
}