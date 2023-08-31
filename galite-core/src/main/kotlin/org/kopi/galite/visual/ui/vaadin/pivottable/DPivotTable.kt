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
package org.kopi.galite.visual.ui.vaadin.pivottable

import org.kopi.galite.visual.pivottable.DSLPivotTable
import org.kopi.galite.visual.ui.vaadin.visual.DWindow

import org.vaadin.addons.componentfactory.PivotTable
import org.vaadin.addons.componentfactory.PivotTable.*

class DPivotTable(private val model: DSLPivotTable) : DWindow(model) {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  val pivotData = PivotData()
  val pivotOptions = PivotOptions()

  //---------------------------------------------------
  // METHODS
  //---------------------------------------------------

  fun setColumns (columns: Map<String, Class<*>>) {
    for (column in columns)
      pivotData.addColumn(column.key, column.value)
  }

  fun setRows (rows: List<List<Any>>) {
    rows.forEach {
      pivotData.addRow(*it.toTypedArray())
    }
  }

  fun setOptions(options: Map<String, List<String>>) {
    val cols = options["cols"]
    val rows = options["rows"]
    pivotOptions.setCols(*cols!!.toTypedArray())
    pivotOptions.setRows(*rows!!.toTypedArray())
  }

  fun buildPivotTable() {
    setColumns(model.columns)
    setRows(model.rows)
    setOptions(model.options)
  }

  //---------------------------------------------------
  // INIT
  //---------------------------------------------------

  init {
    buildPivotTable()

    val pivotTable = PivotTable(pivotData, pivotOptions, PivotMode.INTERACTIVE)

    // setContent(pivotTable)
  }

  override fun run() {
    TODO("Not yet implemented")
  }

}