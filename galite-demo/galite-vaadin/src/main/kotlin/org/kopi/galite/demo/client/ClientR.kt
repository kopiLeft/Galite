/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.demo.client

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.vaadin.addons.componentfactory.PivotTable
import org.vaadin.addons.componentfactory.PivotTable.*

//@Route
class ClientR : AppLayout()  {

  // Create Table and fill it with content
  fun createTable(): PivotData {
    val pivotData = PivotData()

    // Add Columns to pivotData
    val columns = mapOf("firstName" to String::class.java,
      "lastName" to String::class.java,
      "addressClt" to String::class.java,
      "ageClt" to Int::class.java,
      "countryClt" to String::class.java,
      "cityClt" to String::class.java,
      "zipCodeClt" to Int::class.java,
      "activeClt" to String::class.java)
    for (column in columns)
      pivotData.addColumn(column.key, column.value)

    pivotData.addRow("abc", "def", "tunis", 15, "mourouj", "tunis", 8555, "yes")
    pivotData.addRow("abc", "def", "tunis", 15, "mourouj", "tunis", 8555, "yes")
    pivotData.addRow("abc", "def", "tunis", 15, "mourouj", "tunis", 8555, "yes")
    pivotData.addRow("abc", "def", "tunis", 15, "mourouj", "tunis", 8555, "yes")
    pivotData.addRow("abc", "def", "tunis", 15, "mourouj", "tunis", 8555, "yes")
    pivotData.addRow("abc", "def", "tunis", 15, "mourouj", "tunis", 8555, "yes")
    pivotData.addRow("abc", "def", "tunis", 15, "mourouj", "tunis", 8555, "yes")
    pivotData.addRow("abc", "def", "tunis", 15, "mourouj", "tunis", 8555, "yes")
    pivotData.addRow("abc", "def", "tunis", 15, "mourouj", "tunis", 8555, "yes")
    pivotData.addRow("abc", "def", "tunis", 15, "mourouj", "tunis", 8555, "yes")
    pivotData.addRow("abc", "def", "tunis", 15, "mourouj", "tunis", 8555, "yes")

    return pivotData
  }

  // Create Pivoting options
  fun createOptions() : PivotOptions {
    val pivotOptions = PivotOptions()

    pivotOptions.setRows("countryClt", "activeClt")
    pivotOptions.setCols("ageClt")
    pivotOptions.setCharts(true)

    return pivotOptions
  }

  // Create Pivot Table

  init {
    val table = PivotTable(createTable(), createOptions(), PivotMode.INTERACTIVE)
    content = table
    addToDrawer(content)
  }
}
