/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH
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

package org.kopi.galite.report

import com.sun.source.tree.ExpressionTree
import org.kopi.util.base.Utils
import java.io.Serializable
import java.util.*
import javax.swing.event.EventListenerList

class MReport : Serializable {

  /** Columns contains all columns defined by the user
  accessiblecolumns is a part of columns which contains only visible columns*/
  lateinit var columns: Array<VReportColumn?>
  lateinit var accessiblecolumns: Array<VReportColumn?>

  /**Root is the root of the tree (which is our model to manipulate data)*/
  val root: VGroupRow? = null

  /** Initial capacity for a user defined row*/
  val initialCapacity = 500

  /** Baserows contains data give by the request of the user
  visibleRows contains all data which will be displayed. It's like a buffer. visibleRows
  is changed when a column move or one or more row are folded*/
  var userRows: Vector<VBaseRow>? = Vector(initialCapacity)
  lateinit var baseRows: Array<VReportRow?>

  /** Visible rows when grouping*/
  lateinit var visibleRows: Array<VReportRow>
  val maxRowCount = 0

  /** Sortedcolumn contain the index of the sorted column
  sortingOrder store the type of sort of the sortedColumn : ascending or descending*/
  val sortedColumn = 0
  val sortingOrder = 0

  /** displayOrder contains index column model in display order
  reverseOrder is calculate with displayOrder and contains index column display into model order*/
  lateinit var displayOrder: IntArray
  lateinit var reverseOrder: IntArray

  /** The displayLevels variable is a table which contains the level of each column*/
  lateinit var displayLevels: IntArray

  protected var listenerList = EventListenerList()

  // --------------------------------------------------------------------
  // CONSTRUCTION
  // --------------------------------------------------------------------
  /**
   * Constructs a new report instance
   */

  fun computeColumnWidth(column: Int): Int {
    var max = 0
    baseRows.filter { it?.getValueAt(column) != null }.forEach {
      max = maxOf(max, it?.getValueAt(column).toString().length)
    }
    return max + 2
  }

  fun removeColumn(position: Int) {
    var position = position
    val size = columns.size - 1
    val cols = arrayOfNulls<VReportColumn>(size)
    var hiddenColumns = 0

    hiddenColumns += columns.count { it?.options?.and(Constants.CLO_HIDDEN) !== 0 }
    position += hiddenColumns

    // copy columns before and after position
    // cols table will contain columns with column removed
    var index = 0
    columns.filter { it != columns[position] }.forEach {
      if (index < position) {
        cols[index] = columns[index]
      } else {
        cols[index] = columns[index + 1]
      }
      index++
    }

    position -= hiddenColumns
    // clone cols into columns
    columns = cols.clone()
    createAccessibleTab()

    val rows = arrayOfNulls<VBaseRow>(baseRows.size)
    baseRows.map {
      val data = arrayOfNulls<Any>(accessiblecolumns.size)
    }
    for (i in baseRows.indices) {
      val data = arrayOfNulls<Any>(accessiblecolumns.size)
      for (j in 0 until position) {
        data[j] = baseRows[i]?.getValueAt(j)
      }
      // skip position.
      for (j in position until accessiblecolumns.size) {
        data[j] = baseRows[i]?.getValueAt(j + 1)
      }
      rows[i] = VBaseRow(data)
    }
    baseRows = rows
  }

  fun initializeAfterRemovingColumn(position: Int) {
    val newDisplayOrder: IntArray
    val columnCount: Int
    columnCount = accessiblecolumns.size
    newDisplayOrder = IntArray(columnCount)
    reverseOrder = IntArray(columnCount)
    displayLevels = IntArray(columnCount)
    for (i in 0 until position) {
      newDisplayOrder[i] = displayOrder[i]
    }
    for (i in position until columnCount) {
      newDisplayOrder[i] = displayOrder[i + 1]
    }
    for (i in 0 until columnCount) {
      reverseOrder[i] = i
      displayLevels[i] = -1
    }
    displayOrder = newDisplayOrder
  }

  /**
   * Adds a column at runtime.
   */
  fun addColumn(label: String?, position: Int) {
    val cols = arrayOfNulls<VReportColumn>(columns.size + 1)

    // add the new column;
    cols[columns.size] = VFixnumColumn(null,
            0,
            4,
            -1,
            null,
            15,
            7,
            null)
    cols[columns.size]?.label = label
    cols[columns.size]?.addedAtRuntime = true
    // copy the other columns.
    for (i in columns.indices) {
      cols[i] = columns[i]
    }
    columns = cols.clone()
    initializeAfterAddingColumn()
    val rows = arrayOfNulls<VBaseRow>(baseRows.size)
    for (i in baseRows.indices) {
      val data = arrayOfNulls<Any>(accessiblecolumns.size)
      for (j in 0 until accessiblecolumns.size - 1) {
        data[j] = baseRows[i]?.getValueAt(j)
      }
      // fill the new column with  null , column data will be set by user.
      data[accessiblecolumns.size - 1] = null
      rows[i] = VBaseRow(data)
    }
    baseRows = rows
    //createTree();
  }

  private fun initializeAfterAddingColumn() {
    val columnCount: Int
    val newDisplayOrder: IntArray
    createAccessibleTab()
    columnCount = accessiblecolumns.size

    newDisplayOrder = IntArray(columnCount)
    displayLevels = IntArray(columnCount)
    reverseOrder = IntArray(columnCount)
    for (i in 0 until columnCount - 1) {
      newDisplayOrder[i] = displayOrder[i]
    }
    newDisplayOrder[columnCount - 1] = columnCount - 1
    displayOrder = newDisplayOrder
    for (i in 0 until columnCount) {
      reverseOrder[i] = i
      displayLevels[i] = -1
    }
  }

  @Throws(VExecFailedException::class)
  fun computeDataForColumn(column: Int, columnIndexes: IntArray, formula: String?) {
    val x: Expression
    x = try {
      ExpressionTree.parse(formula)
    } catch (e: Exception) {
      throw VExecFailedException(MessageCode.getMessage("VIS-00064", formula, """
   
   $e
   """.trimIndent()))
    }
    val params: Array<String> = x.getVariableNames()
    val paramColumns = IntArray(params.size)
    val functions = IntArray(params.size)
    val NONE = -1
    val MAX = 0
    val MIN = 1
    val OVR = 2
    val SUM = 3
    for (i in params.indices) {
      try {
        if (params[i].startsWith("C")) {
          paramColumns[i] = params[i].substring(1).toInt()
          functions[i] = NONE
        } else if (params[i].startsWith("maxC")) {
          paramColumns[i] = params[i].substring(4).toInt()
          functions[i] = MAX
        } else if (params[i].startsWith("minC")) {
          paramColumns[i] = params[i].substring(4).toInt()
          functions[i] = MIN
        } else if (params[i].startsWith("ovrC")) {
          paramColumns[i] = params[i].substring(4).toInt()
          functions[i] = OVR
        } else if (params[i].startsWith("sumC")) {
          paramColumns[i] = params[i].substring(4).toInt()
          functions[i] = SUM
        } else {
          throw VExecFailedException(MessageCode.getMessage("VIS-00061", """
   ${params[i]}
   
   """.trimIndent(), "Cx, maxCx, minCx, ovrCx, sumCx"))
        }
      } catch (e: NumberFormatException) {
        throw VExecFailedException(MessageCode.getMessage("VIS-00062", params[i].substring(1)))
      }
      // test column indexes.
      var test = false
      for (j in columnIndexes.indices) {
        if (paramColumns[i] == columnIndexes[j]) {
          test = true
          break
        }
      }
      if (!test) {
        throw VExecFailedException(MessageCode.getMessage("VIS-00063", params[i].substring(1)))
      }
    }
    val vm = VarMap(false /* case sensitive */)
    val fm: FuncMap? = null // no functions in expression
    for (i in baseRows.indices) {
      for (j in paramColumns.indices) {
        when (functions[j]) {
          NONE -> vm.setValue(params[j],
                  if (baseRows[i].getValueAt(paramColumns[j]) == null) 0 else  // !!! wael 20070622 : use 0 unstead of null values.
                    (baseRows[i].getValueAt(paramColumns[j]) as NotNullFixed).floatValue())
          MAX -> {
            var max: Float
            var tmp: Float
            // init max
            max = if (baseRows[0].getValueAt(paramColumns[j]) == null) 0 else (baseRows[0].getValueAt(paramColumns[j]) as NotNullFixed).floatValue().toFloat()
            // calculate max value.
            var k = 1
            while (k < baseRows.size) {
              tmp = if (baseRows[k].getValueAt(paramColumns[j]) == null) 0 else (baseRows[k].getValueAt(paramColumns[j]) as NotNullFixed).floatValue().toFloat()
              if (tmp > max) {
                max = tmp
              }
              k++
            }
            vm.setValue(params[j], max)
          }
          MIN -> {
            var min: Float

            // init max
            min = if (baseRows[0].getValueAt(paramColumns[j]) == null) 0 else (baseRows[0].getValueAt(paramColumns[j]) as NotNullFixed).floatValue().toFloat()
            // calculate min value.
            var k = 1
            while (k < baseRows.size) {
              tmp = if (baseRows[k].getValueAt(paramColumns[j]) == null) 0 else (baseRows[k].getValueAt(paramColumns[j]) as NotNullFixed).floatValue().toFloat()
              if (tmp < min) {
                min = tmp
              }
              k++
            }
            vm.setValue(params[j], min)
          }
          OVR -> {
            var ovr: Float
            ovr = 0f
            // calculate moyenne.
            var k = 1
            while (k < baseRows.size) {
              tmp = if (baseRows[k].getValueAt(paramColumns[j]) == null) 0 else (baseRows[k].getValueAt(paramColumns[j]) as NotNullFixed).floatValue().toFloat()
              ovr += tmp / baseRows.size
              k++
            }
            vm.setValue(params[j], ovr)
          }
          SUM -> {
            var sum: Float
            sum = 0f
            // calculate sum.
            var k = 1
            while (k < baseRows.size) {
              tmp = if (baseRows[k].getValueAt(paramColumns[j]) == null) 0 else (baseRows[k].getValueAt(paramColumns[j]) as NotNullFixed).floatValue().toFloat()
              sum += tmp
              k++
            }
            vm.setValue(params[j], sum)
          }
        }
      }
      try {
        baseRows[i].setValueAt(column, NotNullFixed(x.eval(vm, fm)))
      } catch (e: NumberFormatException) {
        // this exception occurs with INFINITE double values. (ex : division by ZERO)
        // return a null value (can not evaluate expression)
        baseRows[i].setValueAt(column, null as NotNullFixed?)
      } catch (e: Exception) {
        throw VExecFailedException(MessageCode.getMessage("VIS-00066"))
      }
    }
  }

  /**
   * Add a row to the list of rows defined by the user
   */
  fun addLine(line: Array<Any?>?) {
    userRows!!.addElement(line?.let { VBaseRow(it) })
  }

  /**
   * Build the base row table + intialisation
   */
  protected fun build() {
    var columnCount = columns.size
    // build accessible columns
    if (userRows!!.size == 0) {
      throw VNoRowException(MessageCode.getMessage("VIS-00015"))
    }
    createAccessibleTab()
    baseRows = Utils.toArray(userRows, VBaseRow::class.java) as Array<VBaseRow?>
    userRows = null

    // build working tables
    columnCount = accessiblecolumns.size
    displayOrder = IntArray(columnCount)
    reverseOrder = IntArray(columnCount)
    displayLevels = IntArray(columnCount)
    for (i in 0 until columnCount) {
      displayOrder[i] = i
      reverseOrder[i] = i
      displayLevels[i] = -1
    }
  }

  // --------------------------------------------------------------------
  // REDEFINITION OF METHODS IN AbstractTableModel
  // --------------------------------------------------------------------
  /**
   * Returns the number of columns managed by the data source object.
   *
   * @return        the number or columns to display
   */
  fun getColumnCount(): Int {
    return accessiblecolumns.size
  }

  /**
   * Returns the number of records managed by the data source object.
   *
   * @return        the number or rows in the model
   */
  fun getRowCount(): Int {
    return maxRowCount
  }

  /**
   * Returns always false since report cells are never editable.
   *
   * @param        row                the index of the row whose value is to be looked up
   * @param        column                the index of the column whose value is to be looked up
   * @return        true if the cell is editable.
   */
  fun isCellEditable(row: Int, column: Int): Boolean {
    return false
  }

  /**
   * Returns an attribute value for a cell.
   *
   * @param        row                the index of the row whose value is to be looked up
   * @param        column                the index of the column whose value is to be looked up (column of the model)
   * @return        the value Object at the specified cell
   */
  fun getValueAt(row: Int, column: Int): Any? {
    var x: Any? = null
    try {
      x = visibleRows[row].getValueAt(column)
    } catch (e: java.lang.Exception) {
      e.printStackTrace()
    }
    return if (visibleRows[row].getLevel() < displayLevels[reverseOrder[column]]) null else x
  }

  /**
   * Returns the name of a column.
   * Note, this name does not need to be unique.
   *
   * @param        column                the index of the column
   * @return        the name of the column
   */
  fun getColumnName(column: Int): String? {
    val label: String? = accessiblecolumns[column]?.label
    if (label == null || label.length == 0) {
      return ""
    }
    return if (accessiblecolumns[column]?.folded!!) label.substring(0, 1) else label
  }

  /**
   * Makes the table of accessible columns from the columns variable
   */
  private fun createAccessibleTab() {
    /*val columnCount = columns.size
    var accessiblecolumnCount = 0
    for (i in 0 until columnCount) {
      if (columns[i]?.options?.and(Constants.CLO_HIDDEN) === 0) {
        accessiblecolumnCount += 1
      }
    }*/

    var accessiblecolumnCount = columns.filter { it?.options?.and(Constants.CLO_HIDDEN) === 0 }.count()

    accessiblecolumns = arrayOfNulls(accessiblecolumnCount)
    accessiblecolumnCount = 0

    /* for (i in 0 until columnCount) {
       if (columns[i].getOptions() and Constants.CLO_HIDDEN === 0) {
         accessiblecolumns[accessiblecolumnCount++] = columns[i]
       }
     }*/

    columns.filter { it?.options?.and(Constants.CLO_HIDDEN) === 0 }
            .forEach { accessiblecolumns[accessiblecolumnCount++] = it }
  }

  fun getDisplayLevels(column: Int): Int {
    return displayLevels[column]
  }

  fun getReverseOrder(column: Int): Int {
    return reverseOrder[column]
  }

  fun getDisplayOrder(column: Int): Int {
    return displayOrder[column]
  }

  /**
   * Returns the number of base rows.
   */
  fun getBaseRowCount(): Int {
    return baseRows.size
  }

  /**
   * Returns the number of visible rows.
   */
  fun getVisibleRowCount(): Int {
    return visibleRows.size
  }

}
