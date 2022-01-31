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

package org.kopi.galite.visual.report

import java.math.BigDecimal

import org.kopi.galite.visual.dsl.report.ReportField

/**
 * This class implements predefined report triggers
 */
object Triggers {
  /**
   * Compute the integer sum in a report column
   */
  fun sumInteger(c: ReportField<Int>): VCalculateColumn {

    return object : VCCDepthFirstCircuitN() {

      override fun evalNode(row: VReportRow, column: Int): Any {
        val childCount = row.childCount
        var result = 0

        for (i in 0 until childCount) {
          val child = row.getChildAt(i) as VReportRow
          val value = child.getValueAt(column) as Int?

          if (value != null) {
            result += value
          }
        }
        return result
      }
    }
  }

  /**
   * Compute the number of entries in a report column
   */
  fun countInteger(c: ReportField<Int>): VCalculateColumn {
    return object : VCCDepthFirstCircuitN() {
      override fun evalNode(row: VReportRow, column: Int): Any {
        val childCount = row.childCount

        return if (row.level > 1) {
          // the value of a node is the sum of
          // other nodes (contain no leafs)
          var result = 0

          for (i in 0 until childCount) {
            val child = row.getChildAt(i) as VReportRow
            val value = child.getValueAt(column) as Int?

            if (value != null) {
              result += value
            }
          }
          result
        } else {
          // the value is the number of the children
          // if the children are leafs
          childCount
        }
      }
    }
  }

  /**
   * Compute the decimal sum in a report column
   */
  fun sumDecimal(c: ReportField<BigDecimal>): VCalculateColumn {
    return object : VCCDepthFirstCircuitN() {
      override fun evalNode(row: VReportRow, column: Int): Any {
        val childCount = row.childCount
        var result = BigDecimal.valueOf(0, 2)

        for (i in 0 until childCount) {
          val child = row.getChildAt(i) as VReportRow
          val value = child.getValueAt(column) as? BigDecimal

          if (value != null) {
            result += value
          }
        }
        return result
      }
    }
  }

  /**
   * Compute the integer sum in a report column
   */
  fun <T> sumNullInteger(c: ReportField<T>): VCalculateColumn where T: Comparable<T>, T: Number {
    return object : VCCDepthFirstCircuitN() {
      override fun evalNode(row: VReportRow, column: Int): Any? {
        val childCount = row.childCount
        var result = 0
        var valueFound = false

        for (i in 0 until childCount) {
          val child = row.getChildAt(i) as VReportRow
          val value = child.getValueAt(column) as Int?

          if (value != null) {
            valueFound = true
            result += value
          }
        }
        return if (valueFound) result else null
      }
    }
  }

  /**
   * Compute the decimal sum in a report column
   */
  fun sumNullDecimal(c: ReportField<BigDecimal>): VCalculateColumn {
    return object : VCCDepthFirstCircuitN() {
      override fun evalNode(row: VReportRow, column: Int): Any? {
        val childCount = row.childCount
        var valueFound = false
        var result = BigDecimal.valueOf(0, 2)

        for (i in 0 until childCount) {
          val child = row.getChildAt(i) as VReportRow
          val value = child.getValueAt(column) as? BigDecimal

          if (value != null) {
            valueFound = true
            result += value
          }
        }
        return if (valueFound) result else null
      }
    }
  }

  /**
   * Report a value when all child are identical
   */
  fun reportIdenticalValue(c: ReportField<*>): VCalculateColumn {
    return object : VCCDepthFirstCircuitN() {
      override fun evalNode(row: VReportRow, column: Int): Any? {
        val childCount = row.childCount
        val value = (row.getChildAt(0) as VReportRow).getValueAt(column)

        return if (value == null) {
          null
        } else {
          for (i in 1 until childCount) {
            val child = row.getChildAt(i) as VReportRow

            if (value != child.getValueAt(column)) {
              return null
            }
          }
          value
        }
      }
    }
  }

  /**
   * Compute the integer average in a report column
   */
  fun <T> avgInteger(c: ReportField<T>): VCalculateColumn where T: Comparable<T>, T: Number {
    return object : VCCDepthFirstCircuitN() {
      override fun evalNode(row: VReportRow, column: Int): Any {
        val childCount = row.childCount
        var result = 0

        for (i in 0 until childCount) {
          val child = row.getChildAt(i) as VReportRow
          val value = child.getValueAt(column) as Int?

          if (value != null) {
            result += value
          }
        }
        return result / childCount
      }
    }
  }

  /**
   * Compute the integer sum in a report column and the the value
   * in the leaves with a serial number
   */
  fun <T> serialInteger(c: ReportField<T>): VCalculateColumn where T: Comparable<T>, T: Number {
    return object : VCCDepthFirstCircuitN() {
      override fun evalNode(row: VReportRow, column: Int): Any {
        val childCount = row.childCount

        return if (row.level > 1) {
          // the value of a node is the sum of
          // other nodes (contain no leafs)
          var result = 0

          for (i in 0 until childCount) {
            val child = row.getChildAt(i) as VReportRow
            val value = child.getValueAt(column) as Int?

            if (value != null) {
              result += value
            }
          }
          result
        } else {
          // the value is the number of the children
          // if the children are leafs
          childCount
        }
      }

      /**
       * Add calculated data into the report row
       */
      override fun calculate(tree: VGroupRow, column: Int) {
        if (tree.level > 1) {
          val childCount = tree.childCount

          for (i in 0 until childCount) {
            calculate(tree.getChildAt(i) as VGroupRow, column)
          }
        } else {
          val childCount = tree.childCount

          for (i in 0 until childCount) {
            // set leave to serial number
            (tree.getChildAt(i) as VBaseRow).setValueAt(column, i + 1)
          }
        }
        tree.setValueAt(column, evalNode(tree, column))
      }
    }
  }

  /**
   * Compute the decimal average in a report column
   */
  fun avgDecimal(c: ReportField<BigDecimal>): VCalculateColumn {
    return object : VCCDepthFirstCircuitN() {
      override fun evalNode(row: VReportRow, column: Int): Any {
        val leafCount = row.leafCount
        var notNullLeafCount = 0.0
        var result = BigDecimal.valueOf(0, 2)
        var leaf = row.firstLeaf as? VReportRow

        for (i in 0 until leafCount) {
          val value = leaf!!.getValueAt(column) as? BigDecimal

          if (value != null) {
            result += value
            notNullLeafCount++
          }
          leaf = leaf.nextLeaf as? VReportRow
        }
        return if (notNullLeafCount != 0.0) {
          (result / BigDecimal(notNullLeafCount)).setScale(2)
        } else {
          BigDecimal.valueOf(0, 2)
        }
      }
    }
  }
}
