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
  private lateinit var values: MutableList<List<String>>
  private var valuesSize: Int = 0

  // Data to show
  private lateinit var rows: MutableList<MutableList<String>>
  private lateinit var groups: MutableList<MutableList<String>>
  private lateinit var groupsSpans: MutableList<MutableList<Int>>
  private lateinit var groupsSpanTypes: MutableList<MutableList<Span>>
  private lateinit var rowsSpanTypes: MutableList<MutableList<Span>>
  private lateinit var spanTypes: List<MutableList<Span>>
  private lateinit var data: List<MutableList<String>>

  fun build() {
    val cols = pivotTable.dataframe.columns()
    val columnGroups = cols.filterIsInstance(ColumnGroup::class.java)
    val valueColumns = cols.filterIsInstance(ValueColumn::class.java)

    values = mutableListOf()
    rows = mutableListOf()
    groups = mutableListOf()
    groupsSpans = mutableListOf()
    groupsSpanTypes = mutableListOf()
    rowsSpanTypes = mutableListOf()

    valuesSize = valueColumns.size

    columnGroups.forEach { column ->
      column.buildHeaderGrouping()
    }

    val rowGroupingValues = mutableListOf<String>()

    rows.add(rowGroupingValues)
    valueColumns.forEach {
      rowGroupingValues.add(it.name())
    }
    repeat(values.size + 1) { rowGroupingValues.add("") }

    repeat(pivotTable.dataframe.rowsCount()) { rowIndex ->
      val aggregationValues = mutableListOf<String>()
      rows.add(aggregationValues)
      valueColumns.forEach { vc ->
        val value = vc.values.elementAt(rowIndex)
        aggregationValues.add(pivotTable.aggregateField!!.model.format(value))
      }

      aggregationValues.add("")

      values.forEach {
        aggregationValues.add(it[rowIndex])
      }
    }

    buildRowsSpanTypes()

    for(j in 0 until valuesSize) {
      var i = 0

      while(i < rows.size) {
        var k = i + 1
        while(k < rows.size && rows[i][j] == rows[k][j] && (j == 0 || rowsSpanTypes[k][j - 1] == Span.ROW)) {
          rows[k][j] = ""
          rowsSpanTypes[k][j] = Span.ROW
          k++
        }
        i = k
      }
    }

    spanTypes = groupsSpanTypes + rowsSpanTypes
    data = groups + rows
  }

  private fun buildRowsSpanTypes() {
    rows.forEachIndexed { index, row ->
      val spans = row.mapIndexed { rowIndex, _ ->
        if(index == 0 && rowIndex > valuesSize) {
          Span.ROW
        } else if (rowIndex == valuesSize) {
          Span.COL
        } else {
          Span.NONE
        }
      }.toMutableList()
      rowsSpanTypes.add(spans)
    }
  }

  private fun ColumnGroup<*>.buildHeaderGrouping(columns: List<AnyCol> = columns(), columnIndex: Int = 0) {
    val group = mutableListOf<String>()
    val spans = mutableListOf<Int>()
    val spansTypes = mutableListOf<Span>()
    val groupingColumns = mutableListOf<AnyCol>()

    add("", valuesSize, group, spans, spansTypes)
    add(pivotTable.grouping.columns[columnIndex].model.label, 1, group, spans, spansTypes)

    groups.add(group)
    groupsSpans.add(spans)
    groupsSpanTypes.add(spansTypes)

    columns.forEach { column ->
      if(column is ColumnGroup<*>) {
        add(column.name(), column.columnsCount(), group, spans, spansTypes)
        groupingColumns.addAll(column.columns())
      } else {
        add(column.name(), valuesSize, group, spans, spansTypes)
        values.add(column.values.map { pivotTable.aggregateField!!.model.format(it) })
      }
    }

    if(groupingColumns.isNotEmpty()) {
      buildHeaderGrouping(groupingColumns, columnIndex + 1)
    }
  }

  private fun add(header: String,
                  spanSize: Int,
                  group: MutableList<String>,
                  spans: MutableList<Int>,
                  spansTypes: MutableList<Span>, ) {
    group.add(header)
    spans.add(spanSize)
    spansTypes.add(Span.NONE)
    repeat(spanSize - 1) {
      group.add("")
      spansTypes.add(Span.COL)
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

  fun getValueAt(row: Int, col: Int): String = data[row][col]

  fun isTitle(row: Int, col: Int): Boolean = (row in 0 .. groups.size) || (col in 0 .. valuesSize)

  fun getSpan(row: Int, col: Int): Span = spanTypes[row][col]
}
