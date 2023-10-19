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

import org.kopi.galite.visual.pivotTable.MPivotTable
import org.kopi.galite.visual.pivotTable.UPivotTable
import org.kopi.galite.visual.pivotTable.VPivotTable
import org.kopi.galite.visual.ui.vaadin.visual.DWindow

import org.vaadin.addons.componentfactory.PivotTable

class DPivotTable(private val report: VPivotTable) : DWindow(report), UPivotTable {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val model: MPivotTable = report.model // report model
  private var pivotData = PivotTable.PivotData()
  private var pivotOptions = PivotTable.PivotOptions()
  private var listeRows = mutableListOf<Any>()
  private var listeDimensions = mutableListOf<String>()

  init {
    getModel()!!.setDisplay(this)
    setSizeFull()
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun run() {
    report.initPivotTable()
    report.setMenu()
  }

  override fun build() {

    model.columns.forEach {
      if(it?.label != "") {
        pivotData.addColumn(it?.label, it?.javaClass)
      }
      if (it?.dimension != null && it.dimension!!) {
        listeDimensions.add(it.label)
      }
    }

    model.userRows?.forEach {
      it.data.forEach { row ->
        if(row != null) {
          listeRows.add(row)
        }
      }
      pivotData.addRow(*listeRows.toTypedArray())
      listeRows.clear()
    }
    pivotOptions.setCols(*listeDimensions.toTypedArray())

    var pivot = PivotTable(pivotData, pivotOptions, PivotTable.PivotMode.INTERACTIVE)
    add(pivot)
  }
}
