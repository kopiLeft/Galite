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

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.values
import org.kopi.galite.visual.dsl.report.ReportField
import org.kopi.galite.visual.dsl.report.ReportRow
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.report.VReportColumn
import org.kopi.galite.visual.visual.ApplicationContext
import org.kopi.galite.visual.visual.VWindow

open class PivotTable : VWindow() {
  /** Report's fields. */
  val fields = mutableListOf<ReportField<*>>()

  /** A report data row */
  val rows = mutableListOf<ReportRow>()

  val columns = mutableListOf<VReportColumn>()

  lateinit var dataframe: AnyFrame
  lateinit var grouping: Grouping
  var funct = Function.NONE
  var help: String? = null

  private val data = mutableListOf<MutableList<Any?>>()
  private val rowGroupings = mutableListOf<MutableList<Any?>>()

  fun getValueAt(row: Int, col: Int): Any? = data[row][col]

  // ----------------------------------------------------------------------
  // DISPLAY INTERFACE
  // ----------------------------------------------------------------------
  open fun initReport() {
    build()
    //callTrigger(Constants.TRG_PREPIVOT) TODO
  }

  /**
   * Localizes this pivot table.
   *
   * @param     manager         the manger to use for localization.
   */
  private fun localize(manager: LocalizationManager) {
    if (ApplicationContext.getDefaultLocale() != locale) {
      val loc = manager.getReportLocalizer(source)

      setTitle(loc.getTitle())
      help = loc.getHelp()
      columns.forEach { it.localize(loc) }
    }
  }

  fun build() {
    localize(manager)
    buildDataFrame()
    dataframe.buildGroupings()
  }

  private fun buildDataFrame() {
    val df = dataFrameOf(fields) { field ->
      rows.map { field.model.format(it.data[field]) }
    }

    dataframe = if (grouping.columns.isEmpty() && grouping.rows.isEmpty()) {
      df.aggregate()
    } else if (grouping.rows.isEmpty()) {
      df.pivot(grouping.columns.size).aggregate()
    } else if (grouping.columns.isEmpty()) {
      df.groupBy(grouping.rows.size).aggregate()
    } else {
      df.pivot(grouping.columns.size).groupBy(grouping.rows.size).aggregate()
    }
  }

  private fun AnyFrame.buildGroupings() {
    val cols = columns()
    val columnGroups = cols.filterIsInstance(ColumnGroup::class.java)
    val valueColumns = cols.filterIsInstance(ValueColumn::class.java)
    val rowGroupingValues = mutableListOf<Any?>()

    columnGroups.forEach { col ->
      col.buildHeaderGrouping(valueColumns.size)
    }

    data.add(rowGroupingValues)
    valueColumns.forEach {
      rowGroupingValues.add(it.name())
    }
    repeat(rowGroupings.size) { rowGroupingValues.add(null) }

    repeat(rowsCount()) { r ->
      val agregationValues = mutableListOf<Any?>()
      data.add(agregationValues)
      valueColumns.forEach { vc ->
        agregationValues.add(vc.values.elementAt(r))
      }

      rowGroupings.forEach {
        agregationValues.add(it[r])
      }
    }
  }

  private fun ColumnGroup<*>.buildHeaderGrouping(nullRows: Int) {
    val firstGrouping = mutableListOf<Any?>()
    val secondGrouping: MutableList<Any?> = mutableListOf()

    data.add(firstGrouping)
    repeat(nullRows) { firstGrouping.add(null) }
    firstGrouping.add(name())
    repeat(columns().size - 1) { firstGrouping.add(null) }

    data.add(secondGrouping)
    buildHeaderGrouping(nullRows, secondGrouping)
  }

  private fun ColumnGroup<*>.buildHeaderGrouping(
    nullRows: Int,
    groupings: MutableList<Any?> = mutableListOf(),
    pads: Boolean = true
  ) {
    val nextGroupings: MutableList<Any?> = mutableListOf()
    if (pads) repeat(nullRows) { groupings.add(null) }
    columns().forEach { col ->
      groupings.add(col.name())
      if (col is ColumnGroup<*>) {
        repeat(col.columns().size - 1) { groupings.add(null) }
        col.buildHeaderGrouping(nullRows, nextGroupings, nextGroupings.isEmpty())
      } else {
        rowGroupings.add(col.values.toMutableList())
      }
    }
    if (nextGroupings.isNotEmpty()) {
      data.add(nextGroupings)
    }
  }

  private fun dataFrameOf(header: Iterable<ReportField<*>>, fill: (ReportField<*>) -> Iterable<String>): AnyFrame =
    header.map { value ->
      fill(value).asList().let {
        DataColumn.create(
          value.model.label,
          it
        )
      }
    }.toDataFrame()

  private fun DataFrame<Any?>.pivot(l1: Int): Pivot<Any?> {
    return if (l1 == 1) {
      this.pivot(grouping.columns[0].label!!)
    } else {
      this.pivot {
        grouping.columns
          .subList(2, grouping.columns.size)
          .fold(grouping.columns[0].label!! then grouping.columns[1].label!!) { a, b ->
            a then b.label!!
          }
      }
    }
  }

  fun DataFrame<Any?>.groupBy(l2: Int): GroupBy<Any?, Any?> {
    return if (l2 == 1) {
      this.groupBy(grouping.rows[0].label!!)
    } else {
      this.groupBy {
        grouping.rows
          .subList(2, grouping.rows.size)
          .fold(grouping.rows[0].label!! and grouping.rows[1].label!!) { a, b ->
            a and b.label!!
          }
      }
    }
  }

  fun Pivot<*>.groupBy(l2: Int): PivotGroupBy<Any?> {
    return if (l2 == 1) {
      this.groupBy(grouping.rows[0].label!!)
    } else {
      this.groupBy {
        grouping.rows
          .subList(2, grouping.rows.size)
          .fold(grouping.rows[0].label!! and grouping.rows[1].label!!) { a, b ->
            a and b.label!!
          }
      }
    }
  }

  /**
   * Adds a row to the report.
   *
   * @param init initializes the row with values.
   */
  fun add(init: ReportRow.() -> Unit) {
    val row = ReportRow(fields)
    row.init()

    // Last null value is added for the separator column
    rows.add(row)
  }

  private fun Aggregatable<Any?>.aggregate(): DataFrame<Any?> {
    return when (funct) {
      Function.MAX -> _max()
      Function.MEAN -> _mean()
      Function.SUM -> _sum()
      Function.MIN -> _min()
      else -> TODO()
    }
  }

  private fun Aggregatable<*>._max(): DataFrame<Any?> {
    return when (this) {
      is Pivot<*> -> {
        this.max().toDataFrame()
      }
      is DataFrame<*> -> {
        this.max().toDataFrame()
      }
      is GroupBy<*, *> -> {
        this.max()
      }
      is PivotGroupBy<*> -> {
        this.max()
      }
      else -> {
        throw UnsupportedOperationException()
      }
    }
  }

  private fun Aggregatable<*>._mean(): DataFrame<Any?> {
    return when (this) {
      is Pivot<*> -> {
        this.mean().toDataFrame()
      }
      is DataFrame<*> -> {
        this.mean().toDataFrame()
      }
      is GroupBy<*, *> -> {
        this.mean()
      }
      is PivotGroupBy<*> -> {
        this.mean()
      }
      else -> {
        throw UnsupportedOperationException()
      }
    }
  }

  private fun Aggregatable<*>._sum(): DataFrame<Any?> {
    return when (this) {
      is Pivot<*> -> {
        this.sum().toDataFrame()
      }
      is DataFrame<*> -> {
        this.sum().toDataFrame()
      }
      is GroupBy<*, *> -> {
        this.sum()
      }
      is PivotGroupBy<*> -> {
        this.sum()
      }
      else -> {
        throw UnsupportedOperationException()
      }
    }
  }

  private fun Aggregatable<*>._min(): DataFrame<Any?> {
    return when (this) {
      is Pivot<*> -> {
        this.min().toDataFrame()
      }
      is DataFrame<*> -> {
        this.min().toDataFrame()
      }
      is GroupBy<*, *> -> {
        this.min()
      }
      is PivotGroupBy<*> -> {
        this.min()
      }
      else -> {
        throw UnsupportedOperationException()
      }
    }
  }
}

enum class Function {
  NONE,
  SUM,
  MEAN,
  MIN,
  MAX
}

class Grouping(val columns: List<ReportField<*>>, val rows: List<ReportField<*>>)
