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
package org.kopi.galite.visual.pivottable

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.values

class MPivotTable(private val pivotTable: PivotTable) {
  private val cols = pivotTable.dataframe.columns()
  private val columnGroups = cols.filterIsInstance(ColumnGroup::class.java)
  private val valueColumns = cols.filterIsInstance(ValueColumn::class.java)
  private val values = mutableListOf<List<Any?>>()

  // Data to show
  private val rows = mutableListOf<MutableList<Any?>>()
  private val groups = mutableListOf<MutableList<Any?>>()
  private val groupsSpans = mutableListOf<MutableList<Int>>()
  private val groupsSpanTypes = mutableListOf<MutableList<Span>>()
  private lateinit var data: List<MutableList<Any?>>

  fun build() {
    columnGroups.forEach { c ->
      c.buildHeaderGrouping()
    }

    val rowGroupingValues = mutableListOf<Any?>()

    rows.add(rowGroupingValues)
    valueColumns.forEach {
      rowGroupingValues.add(it.name())
    }
    repeat(values.size) { rowGroupingValues.add(null) }

    repeat(pivotTable.dataframe.rowsCount()) { r ->
      val aggregationValues = mutableListOf<Any?>()
      rows.add(aggregationValues)
      valueColumns.forEach { vc ->
        aggregationValues.add(vc.values.elementAt(r))
      }

      values.forEach {
        aggregationValues.add(it[r])
      }
    }

    data = groups + rows
  }

  private fun ColumnGroup<*>.buildHeaderGrouping(columns: List<AnyCol> = columns(), i: Int = 0) {
    val group = mutableListOf<Any?>(pivotTable.fields[i].label)
    val spans = mutableListOf(1)
    val spansTypes = mutableListOf(Span.NONE)
    val groupingColumns = mutableListOf<AnyCol>()

    groups.add(group)
    groupsSpans.add(spans)
    groupsSpanTypes.add(spansTypes)

    columns.forEach { c ->
      group.add(c.name())
      if(c is ColumnGroup<*>) {
        val ccolumns = c.columns()

        spans.add(c.columnsCount())
        groupingColumns.addAll(ccolumns)
        repeat(c.columnsCount()) {
          if(it == 0) {
            spansTypes.add(Span.NONE)
          } else {
            spansTypes.add(Span.LEFT)
            group.add(null)
          }
        }
      } else {
        spans.add(1)
        spansTypes.add(Span.NONE)
        values.add(c.values.toList())
      }
    }

    if(groupingColumns.isNotEmpty()) {
      buildHeaderGrouping(groupingColumns)
    }
  }

  /**
   * Returns the number of columns managed by the data source object.
   *
   * @return    the number or columns to display
   */
  fun getColumnCount(): Int = data.lastOrNull()?.size ?: 0

  /**
   * Returns the number of records managed by the data source object.
   *
   * @return    the number or rows in the model
   */
  fun getRowCount(): Int = data.size

  fun getValueAt(row: Int, col: Int): Any? = data[row][col]
}
