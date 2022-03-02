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

import java.util.stream.Collectors

import org.kopi.galite.visual.report.VReportColumn


/**
 * A pivot table is a table of grouped values that summarizes sums, averages, or other statistics.
 *
 * Do not use this in your code, it's still under construction.
 */
class PivotTable() {
  // Testing data
  val rows = mutableListOf(
    Row(arrayOf("Ablonczy Diane", "Female", 63)),
    Row(arrayOf("Adams Eve", "Female", 38)),
    Row(arrayOf("Adler Mark", "Male", 50)),
    Row(arrayOf("Aglukkaq Leona", "Female", 45)),
    Row(arrayOf("Albas Dan", "Male", 36))
  )

  var groupingRows: List<Int> = listOf()
  var groupingColumns: List<Int> = listOf()
  var sum: Int? = null
  var columns = mutableListOf<VReportColumn>()
}

fun main() {
  val pivot = PivotTable()
  pivot.groupingRows = listOf(0, 1)
  pivot.groupingColumns = listOf(1)
  pivot.sum = 2
  pivot.print()
}

fun PivotTable.print() {
  val grouped: MutableMap<Grouping, List<Row>> = rows.groupBy(groupingRows, groupingColumns)
  val groupedColumns: Set<Any?> = grouped.uniqueValues(1)
  print(',')
  groupedColumns.stream().forEach { t: Any? -> print("$t,") }
  println()
  val groupedRows = grouped.uniqueValues(0)

  groupedRows.stream()
    .forEach { y ->
      print("$y,")
      groupedColumns.stream().forEach { t: Any? ->
        val grouping = Grouping(mapOf(0 to y, 1 to t))
        val rows = grouped[grouping]
        if (sum!= null && rows != null) {
          val total = rows
            .stream()
            .collect(Collectors.summingLong {
              (it.getValueAt(sum!!) as Number).toLong()
            })
          print(total)
        }
        print(',')
      }
      println()
    }
}

fun List<Row>.groupBy(groupingRows: List<Int>, groupingColumns: List<Int>): MutableMap<Grouping, List<Row>> {
  val grouped: MutableMap<Grouping, List<Row>> = stream()
    .collect(Collectors.groupingBy { x ->
      Grouping(
        (groupingRows + groupingColumns).map {
          it to x.getValueAt(it)
        }.toMap()
      )
    })

  return grouped
}

fun MutableMap<Grouping, List<Row>>.uniqueValues(groupingColumn: Int) : Set<Any?> {
  return keys
    .stream()
    .map { x: Grouping -> x.values[groupingColumn] }
    .collect(Collectors.toSet())
}

class Grouping(val values: Map<Int, Any?>) {
  override fun equals(other: Any?): Boolean {
    if (other == null) return false
    if (this === other) return true
    if (other is Grouping) {
      val yt = other
      if (values == yt.values) return true
    }
    return false
  }

  override fun hashCode(): Int {
    return values.hashCode()
  }

  override fun toString(): String {
    return buildString {
      append('[')
      values.values.forEach {
        append(it)
        append(", ")
      }
      append(']')
    }
  }
}
