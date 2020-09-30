/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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

import java.io.Serializable

import javax.swing.event.EventListenerList

import kotlin.math.max

import com.graphbuilder.math.Expression
import com.graphbuilder.math.ExpressionTree
import com.graphbuilder.math.FuncMap
import com.graphbuilder.math.VarMap

import org.kopi.galite.type.NotNullFixed
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.VExecFailedException

class MReport : Constants, Serializable {
  fun computeColumnWidth(column: Int): Int {
    var max = 0

    baseRows.forEach {
      if (it!!.getValueAt(column) != null) {
        max = max(max, it.getValueAt(column).toString().length)
      }
    }
    return max + 2
  }

  fun removeColumn(position: Int) {
    var position = position
    val cols = arrayOfNulls<VReportColumn>(columns.size - 1)
    var hiddenColumns = 0

    columns.forEach {
      if (it!!.options and Constants.CLO_HIDDEN != 0) {
        hiddenColumns += 1
      }
    }
    position += hiddenColumns
    // copy columns before position.
    for (i in 0 until position) {
      cols[i] = columns[i]
    }
    // copy columns after position.
    for (i in position until columns.size - 1) {
      cols[i] = columns[i + 1]
    }
    position -= hiddenColumns
    columns = cols.clone()
    createAccessibleTab()
    val rows = arrayOfNulls<VBaseRow>(baseRows.size)

    baseRows.forEachIndexed {  index, element ->
      val data = arrayOfNulls<Any>(getAccessibleColumnCount())

      for (j in 0 until position) {
        data[j] = element!!.getValueAt(j)
      }
      // skip position.
      for (j in position until getAccessibleColumnCount()) {
        data[j] = element!!.getValueAt(j + 1)
      }
      rows[index] = VBaseRow(data)
    }
    baseRows = rows.filterIsInstance<VBaseRow>().toTypedArray()
  }

  fun initializeAfterRemovingColumn(position: Int) {
    val columnCount: Int = getAccessibleColumnCount()
    val newDisplayOrder = IntArray(columnCount)

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
  fun addColumn(label: String, position: Int) {
    val cols = arrayOfNulls<VReportColumn>(columns.size + 1)

    // add the new column;
    cols[columns.size] = VFixnumColumn(null, 0, 4, -1, null, 15, 7, null)
    cols[columns.size]!!.label = label
    cols[columns.size]!!.addedAtRuntime = true
    // copy the other columns.
    columns.forEachIndexed { index, element ->
      cols[index] = element
    }
    columns = cols.clone()
    initializeAfterAddingColumn()
    val rows = arrayOfNulls<VBaseRow>(baseRows.size)

    baseRows.forEachIndexed { index, element ->
      val data = arrayOfNulls<Any>(getAccessibleColumnCount())

      for (j in 0 until getAccessibleColumnCount() - 1) {
        data[j] = element!!.getValueAt(j)
      }
      // fill the new column with  null , column data will be set by user.
      data[getAccessibleColumnCount() - 1] = null
      rows[index] = VBaseRow(data)
    }
    baseRows = rows.filterIsInstance<VBaseRow>().toTypedArray()
  }

  private fun initializeAfterAddingColumn() {
    createAccessibleTab()
    val columnCount: Int = getAccessibleColumnCount()
    val newDisplayOrder = IntArray(columnCount)

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

  fun computeDataForColumn(column: Int, columnIndexes: IntArray, formula: String) {
    val x: Expression = try {
      ExpressionTree.parse(formula)
    } catch (e: Exception) {
      throw VExecFailedException(MessageCode.getMessage("VIS-00064", formula, "\n$e")) }
    val params: Array<String> = x.variableNames
    val paramColumns = IntArray(params.size)
    val functions = IntArray(params.size)
    val NONE = -1
    val MAX = 0
    val MIN = 1
    val OVR = 2
    val SUM = 3

    for (i in params.indices) {
      try {
        when {
          params[i].startsWith("C") -> {
            paramColumns[i] = params[i].substring(1).toInt()
            functions[i] = NONE
          }
          params[i].startsWith("maxC") -> {
            paramColumns[i] = params[i].substring(4).toInt()
            functions[i] = MAX
          }
          params[i].startsWith("minC") -> {
            paramColumns[i] = params[i].substring(4).toInt()
            functions[i] = MIN
          }
          params[i].startsWith("ovrC") -> {
            paramColumns[i] = params[i].substring(4).toInt()
            functions[i] = OVR
          }
          params[i].startsWith("sumC") -> {
            paramColumns[i] = params[i].substring(4).toInt()
            functions[i] = SUM
          }
          else -> {
            throw VExecFailedException(MessageCode.getMessage("VIS-00061",
                    "${params[i]}\n",
                    "Cx, maxCx, minCx, ovrCx, sumCx"))
          }
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
    val fm: FuncMap? = null   // no functions in expression
    var tmp: Float

    for (i in baseRows.indices) {
      for (j in paramColumns.indices) {
        when (functions[j]) {
          NONE -> vm.setValue(params[j],
                  (if (baseRows[i]!!.getValueAt(paramColumns[j]) == null) 0 else  // !!! wael 20070622 : use 0 unstead of null values.
                    (baseRows[i]!!.getValueAt(paramColumns[j]) as NotNullFixed).toFloat()) as Double)
          MAX -> {
            var max: Float
            // init max
            max = if (baseRows[0]!!.getValueAt(paramColumns[j]) == null) 0F
                  else (baseRows[0]!!.getValueAt(paramColumns[j]) as NotNullFixed?)!!.toFloat()
            // calculate max value.
            baseRows.forEach {
              tmp = if (it!!.getValueAt(paramColumns[j]) == null) 0F
                    else (it.getValueAt(paramColumns[j]) as NotNullFixed?)!!.toFloat()
              if (tmp > max) {
                max = tmp
              }
            }
            vm.setValue(params[j], max.toDouble())
          }
          MIN -> {
            var min: Float

            // init max
            min = if (baseRows[0]!!.getValueAt(paramColumns[j]) == null) 0F
                  else (baseRows[0]!!.getValueAt(paramColumns[j]) as NotNullFixed?)!!.toFloat()
            // calculate min value.
            baseRows.forEach  {
              tmp = if (it!!.getValueAt(paramColumns[j]) == null) 0F
                    else (it.getValueAt(paramColumns[j]) as NotNullFixed?)!!.toFloat()
              if (tmp < min) {
                min = tmp
              }
            }
            vm.setValue(params[j], min.toDouble())
          }
          OVR -> {
            var ovr: Float = 0f
            // calculate average.
            baseRows.forEach  {
              tmp = if (it!!.getValueAt(paramColumns[j]) == null) 0F
                    else (it.getValueAt(paramColumns[j]) as NotNullFixed?)!!.toFloat()
              ovr += tmp / baseRows.size
            }
            vm.setValue(params[j], ovr.toDouble())
          }
          SUM -> {
            var sum = 0f
            // calculate sum.
            baseRows.forEach  {
              tmp = if (it!!.getValueAt(paramColumns[j]) == null) 0F
                    else (it.getValueAt(paramColumns[j]) as NotNullFixed?)!!.toFloat()
              sum += tmp
            }
            vm.setValue(params[j], sum.toDouble())
          }
        }
      }
      try {
        baseRows[i]!!.setValueAt(column, NotNullFixed(x.eval(vm, fm)))
      } catch (e: NumberFormatException) {
        // this exception occurs with INFINITE double values. (ex : division by ZERO)
        // return a null value (can not evaluate expression)
        baseRows[i]!!.setValueAt(column, null as NotNullFixed?)
      } catch (e: Exception) {
        throw VExecFailedException(MessageCode.getMessage("VIS-00066"))
      }
    }
  }

  /**
   * Add a row to the list of rows defined by the user
   */
  fun addLine(line: Array<Any?>) {
    userRows!!.add(VBaseRow(line))
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
    baseRows = userRows!!.toTypedArray()
    userRows = null

    // build working tables
    columnCount = getAccessibleColumnCount()
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
  // MEMBER ACCESS
  // --------------------------------------------------------------------
  /**
   * Return a column definition
   *
   * @param    column        the index of the desired column
   * @return    the desired column
   */
  fun getModelColumn(column: Int): VReportColumn = columns[column]!!

  /**
   * Returns the number of model columns
   *
   * @return    the number or columns to display
   */
  fun getModelColumnCount(): Int = columns.size

  /**
   * Return a column definition
   *
   * @param    column        the index of the desired column
   * @return    the desired column
   */
  fun getAccessibleColumn(column: Int): VReportColumn? = accessibleColumns[column]

  /**
   * Returns the number of columns visible or hide
   *
   * @return    the number or columns in the model
   */
  fun getAccessibleColumnCount(): Int = accessibleColumns.size

  /**
   * Return a row definition
   *
   * @param    row        the index of the desired row
   * @return    the desired row
   */
  fun getRow(row: Int): VReportRow? = visibleRows!![row]

  /**
   * Return the tree used by the model
   */
  fun getTree(): VGroupRow? = root

  // --------------------------------------------------------------------
  // GROUPING TREE
  // --------------------------------------------------------------------
  protected fun createTree() {
    // compute grouping columns in displayed column order
    computeGroupings()

    // sort base rows wrt to each grouping column
    sortBaseRows()

    // build the grouping tree recursively
    buildGroupingTree()

    // compute all intermediate columns
    calculateColumns()

    // create the array of displayed rows
    updateTableModel()
  }

  /**
   * Returns an array of grouping columns in displayed column order
   * For each column, the value is the column of the next (lower) level
   * grouping or -1 if the column has no further sub-grouping.
   * The columns are given in displayed column order
   */
  private fun computeGroupings() {
    val columnCount = accessibleColumns.size
    val defaultGroups = IntArray(columnCount)
    val displayGroups = IntArray(columnCount)
    var separatorPos = Int.MAX_VALUE

    // retrieve the groups in original column order
    for (i in 0 until columnCount) {
      defaultGroups[i] = accessibleColumns[i]!!.groups
    }

    // reorder the groups in displayed column order
    for (i in 0 until columnCount) {
      when {
        defaultGroups[displayOrder[i]] == -1 -> { displayGroups[i] = -1 }
        defaultGroups[displayOrder[i]] >= reverseOrder.size -> { displayGroups[i] = reverseOrder.size }  // not shown
        else -> { displayGroups[i] = reverseOrder[defaultGroups[displayOrder[i]]] }
      }
    }

    // retrieve separator
    for (i in 0 until columnCount) {
      if (accessibleColumns[displayOrder[i]] is VSeparatorColumn) {
        separatorPos = i
      }
    }
    var level = 0

    //top is reached
    run {
      var i = 0
      while (i < displayGroups.size) {
        displayLevels[i] = level
        if (accessibleColumns[displayOrder[i]]!!.visible) {
          if (displayGroups[i] == -1 || i == separatorPos) {
            while (i < displayGroups.size) {
              displayLevels[i] = level
              i++
            }
            break
          } else if (displayGroups[i] > i) {
            while (i + 1 < displayGroups.size && displayGroups[i + 1] <= i + 1 && displayGroups[i + 1] != -1) {
              displayLevels[i + 1] = level
              i++
            }
            level++
          }
        }
        i++
      }
    }

    // renumber levels from highest to lowest (0)
    for (i in 0 until columnCount) {
      displayLevels[i] = level - displayLevels[i]
    }
  }

  /**
   * Sort base rows wrt to each grouping column
   * Note: we assume that the sorting algorithm ist stable: we thus
   * can sort the complete table for each column, starting with the
   * last grouping column.
   */
  private fun sortBaseRows() {
    visibleRows = arrayOfNulls(baseRows.size)
    sortBaseRows(0)
  }

  private fun sortBaseRows(column: Int) {
    if (displayLevels[column] > 0) {
      var next = column + 1

      while (displayLevels[next] == displayLevels[next - 1]) {
        next += 1
      }
      sortBaseRows(next)
    }
    // sort in ascending order
    var i = column
    while (!accessibleColumns[i]!!.visible) {
      i += 1
    }
    if (i >= 0) {
      sortArray(baseRows, displayOrder[i], 1)
      // this value is overwritten in each pass: at the end
      // of the recursion it will hold the first column
      sortedColumn = displayOrder[i]
      sortingOrder = 1
    }
  }

  /**
   * Build the grouping tree
   */
  private fun buildGroupingTree() {
    maxRowCount = baseRows.size + 1
    root = VGroupRow(arrayOfNulls(getModelColumnCount()), displayLevels[0] + 1)
    // even if column 0 is hidden, it has the highest level
    buildGroupingTree(root!!, 0, baseRows.size - 1, 0)
    visibleRows = arrayOfNulls(maxRowCount)
    root!!.visible = true
    for (i in 0 until root!!.childCount) {
      (root!!.getChildAt(i) as VReportRow).visible = true
    }
  }

  private fun buildGroupingTree(tree: VReportRow, loRow: Int, hiRow: Int, start: Int) {
    var loRow = loRow
    var start = start

    if (displayLevels[start] == 0) {    // even if the 0-index column is hidden, its displayLevels == 0
      for (i in loRow..hiRow) {
        tree.add(baseRows[i])
      }
    } else {
      // get the interval of columns at this level
      var next: Int = start + 1

      while (displayLevels[next] == displayLevels[start]) {
        next++
      }
      while (!accessibleColumns[start]!!.visible) {
        // to get the first visible column of this level
        start++
      }

      do {
        val value = baseRows[loRow]!!.getValueAt(displayOrder[start])
        var split = loRow

        while (split <= hiRow && (value == null && baseRows[split]!!.getValueAt(displayOrder[start]) == null
                        || value != null && value == baseRows[split]!!.getValueAt(displayOrder[start]))) {
          split += 1
        }
        val newRow = VGroupRow(arrayOfNulls(getModelColumnCount()), displayLevels[start])

        maxRowCount++
        for (i in 0 until next) {
          newRow.setValueAt(displayOrder[i], baseRows[loRow]!!.getValueAt(displayOrder[i])!!)
        }
        buildGroupingTree(newRow, loRow, split - 1, next)
        tree.add(newRow)
        loRow = split
      } while (loRow <= hiRow)
    }
  }

  /**
   * Sorts an array of rows wrt to given column using straight two-way merge sorting.
   *
   * @param	array		The array to sort
   * @param	column		The index of the column on which to sort
   * @param	order		The sorting order (1: ascending, -1: descending)
   */
  private fun sortArray(array: Array<VReportRow?>, column: Int, order: Int) {
    mergeSort(array, column, order, 0, array.size - 1, visibleRows)
  }

  private fun mergeSort(array: Array<VReportRow?>,
                        column: Int,
                        order: Int,
                        lo: Int,
                        hi: Int,
                        scratch: Array<VReportRow?>?) {
    // a one-element array is always sorted
    if (lo < hi) {
      val mid = (lo + hi) / 2

      // split into 2 sublists and sort them
      mergeSort(array, column, order, lo, mid, scratch)
      mergeSort(array, column, order, mid + 1, hi, scratch)

      // Merge sorted sublists
      var t_lo = lo
      var t_hi = mid + 1

      for (k in lo..hi) {
        if (t_lo > mid || t_hi <= hi && order * array[t_hi]!!.compareTo(array[t_lo]!!,
                        column, getModelColumn(column)!!) < 0) {
          scratch!![k] = array[t_hi++]
        } else {
          scratch!![k] = array[t_lo++]
        }
      }

      // Copy back to array
      for (k in lo..hi) {
        array[k] = scratch!![k]
      }
    }
  }

  /**
   * Calculate all columns which need to be calculated
   */
  fun calculateColumns() {
    for (i in columns.indices) {
      val function: VCalculateColumn? = columns[i]!!.function

      if (function != null) {
        function.init()
        function.calculate(root!!, i)
      }
    }
  }

  /**
   * fill table of visible rows
   */
  private fun updateTableModel() {
    maxRowCount = addRowsInArray(root, 0)
    fireContentChanged()
  }

  /**
   * add visible rows in a vector
   *
   * @param     node        node to test.
   * @param     pos         position of the node.
   */
  private fun addRowsInArray(node: VReportRow?, pos: Int): Int {
    var pos = pos

    if (node!!.visible) {
      visibleRows!![pos++] = node
      for (i in 0 until node.childCount) {
        val row = node.getChildAt(i) as VReportRow

        if (row.level == 0) {
          if (row.visible) {
            visibleRows!![pos++] = row
          }
        } else {
          pos = addRowsInArray(node.getChildAt(i) as VReportRow, pos)
        }
      }
    }
    return pos
  }
  // --------------------------------------------------------------------
  // EVENTS FROM DISPLAY
  // --------------------------------------------------------------------
  /**
   * Sort the displayed tree wrt to a column
   *
   * @param    column        the model column index used for sorting in display order.
   */
  fun sortColumn(column: Int) {
    sortTree(column)
    calculateColumns()
    updateTableModel()
  }

  /**
   * Sort the displayed tree wrt to a column
   *
   * @param     column          the model column index used for sorting in display order.
   * @param     order           sort order.
   */
  fun sortColumn(column: Int, order: Int) {
    sortTree(root, column, order)
    calculateColumns()
    updateTableModel()
  }

  /**
   * Sort the display tree wrt to a column; if it is already sorted
   * wrt to this column, invert the sorting order.
   *
   * @param    column        The model column index given in display order
   */
  private fun sortTree(column: Int) {
    val order: Int = if (column != sortedColumn) 1 else -sortingOrder

    sortTree(root, column, order)
    sortedColumn = column
    sortingOrder = order
  }

  private fun sortTree(tree: VReportRow?, column: Int, order: Int) {

    // place the childs of the root in an array
    val rowTab: Array<VReportRow?> = arrayOfNulls(tree!!.childCount)

    for (i in 0 until tree.childCount) {
      rowTab[i] = tree.getChildAt(i) as VReportRow
    }

    // sort the array wrt to column: if already sorted, invert order
    sortArray(rowTab, column, order)

    // re-add the rows as childs
    tree.removeAllChildren()
    for (i in rowTab.indices) {
      tree.add(rowTab[i])
    }

    // sort sub-trees recursively
    if (tree.level > 1) {
      for (i in 0 until tree.childCount) {
        sortTree(tree.getChildAt(i) as VReportRow, column, order)
      }
    }
  }

  /**
   * Returns true is the specified column is fold
   */
  fun isColumnFold(column: Int): Boolean {
    return if (root!!.level > 1) {
      val level = displayLevels[reverseOrder[column]]

      !root!!.isUnfolded(level)
    } else {
      false
    }
  }

  /**
   * Returns true if the specified row is fold at the specified column
   */
  fun isRowFold(row: Int, column: Int): Boolean {
    return if (root!!.level > 1) {
      val level = displayLevels[reverseOrder[column]]
      var currentRow = visibleRows!![row]

      while (currentRow!!.level < level) {
        currentRow = currentRow.parent as VReportRow
      }
      if (currentRow is VGroupRow) !currentRow.isUnfolded(level) else true
    } else {
      false
    }
  }

  /**
   * Folds the specified column
   */
  fun foldingColumn(column: Int) {
    if (root!!.level > 1) {
      val level = displayLevels[reverseOrder[column]]

      root!!.setChildNodesInvisible(level)
      updateTableModel()
    }
  }

  /**
   * Unfolds the specified column
   */
  fun unfoldingColumn(column: Int) {
    if (root!!.level > 1) {
      val level = displayLevels[reverseOrder[column]]

      root!!.setChildNodesVisible(level)
      updateTableModel()
    }
  }

  /**
   * Folds the specified column
   */
  fun setColumnFolded(column: Int, fold: Boolean) {
    accessibleColumns[column]!!.folded = fold
    fireContentChanged()
  }

  /**
   * Folds the specified column
   */
  fun switchColumnFolding(column: Int) {
    accessibleColumns[column]!!.folded = (!accessibleColumns[column]!!.folded)
    fireContentChanged()
  }

  /**
   * Folds the specified row to specified column
   *
   * @param    column        the model index of the column
   */
  fun foldingRow(row: Int, column: Int) {
    if (root!!.level > 1) {
      val level = displayLevels[reverseOrder[column]]
      var currentRow = visibleRows!![row]

      while (currentRow!!.level < level) {
        currentRow = currentRow.parent as VReportRow
      }
      if (currentRow is VGroupRow) {
        currentRow.setChildNodesInvisible(level)
      }
      updateTableModel()
    }
  }

  /**
   * Unfolds the specified row to specified column
   *
   * @param    column        the model index of the column
   */
  fun unfoldingRow(row: Int, column: Int) {
    if (root!!.level > 1) {
      val level = displayLevels[reverseOrder[column]]
      val currentRow = visibleRows!![row]

      if (currentRow is VGroupRow) {
        currentRow.setChildNodesVisible(level)
      }
      updateTableModel()
    }
  }

  /**
   * Returns true if the specified row is fold at the specified column
   */
  fun isRowLine(row: Int): Boolean {
    return if (visibleRows != null) {
      row in 0 until maxRowCount && visibleRows!![row]!!.level == 0
    } else {
      false
    }
  }

  /**
   * @param    newOrder    the table of the display order columns
   */
  fun columnMoved(newOrder: IntArray) {
    displayOrder = newOrder

    // rebuild column mapping from model to display
    displayOrder.forEachIndexed { index, element ->
      reverseOrder[displayOrder[index]] = index
    }
    createTree()
  }
  // --------------------------------------------------------------------
  // REDEFINITION OF METHODS IN AbstractTableModel
  // --------------------------------------------------------------------
  /**
   * Returns the number of columns managed by the data source object.
   *
   * @return    the number or columns to display
   */
  fun getColumnCount(): Int = accessibleColumns.size

  /**
   * Returns the number of records managed by the data source object.
   *
   * @return    the number or rows in the model
   */
  fun getRowCount(): Int = maxRowCount

  /**
   * Returns always false since report cells are never editable.
   *
   * @param    row        the index of the row whose value is to be looked up
   * @param    column        the index of the column whose value is to be looked up
   * @return    true if the cell is editable.
   */
  fun isCellEditable(row: Int, column: Int): Boolean = false

  /**
   * Returns an attribute value for a cell.
   *
   * @param    row        the index of the row whose value is to be looked up
   * @param    column        the index of the column whose value is to be looked up (column of the model)
   * @return    the value Object at the specified cell
   */
  fun getValueAt(row: Int, column: Int): Any? {
    var x: Any? = null

    try {
      x = visibleRows!![row]!!.getValueAt(column)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return if (visibleRows!![row]!!.level < displayLevels[reverseOrder[column]]) null else x
  }

  /**
   * Returns the name of a column.
   * Note, this name does not need to be unique.
   *
   * @param    column        the index of the column
   * @return    the name of the column
   */
  fun getColumnName(column: Int): String {
    val label: String? = accessibleColumns[column]!!.label

    if (label == null || label.isEmpty()) {
      return ""
    }
    return if (accessibleColumns[column]!!.folded) label.substring(0, 1) else label
  }

  /**
   * Makes the table of accessible columns from the columns variable
   */
  private fun createAccessibleTab() {
    val columnCount = columns.size
    var accessiblecolumnCount = 0

    for (i in 0 until columnCount) {
      if (columns[i]!!.options and Constants.CLO_HIDDEN == 0) {
        accessiblecolumnCount += 1
      }
    }
    accessibleColumns = arrayOfNulls(accessiblecolumnCount)
    accessiblecolumnCount = 0
    for (i in 0 until columnCount) {
      if (columns[i]!!.options and Constants.CLO_HIDDEN == 0) {
        accessibleColumns[accessiblecolumnCount++] = columns[i]
      }
    }
  }

  fun getDisplayLevels(column: Int): Int = displayLevels[column]

  fun getReverseOrder(column: Int): Int = reverseOrder[column]

  fun getDisplayOrder(column: Int): Int = displayOrder[column]

  /**
   * Returns the number of base rows.
   */
  fun getBaseRowCount(): Int = baseRows.size

  /**
   * Returns the number of visible rows.
   */
  fun getVisibleRowCount(): Int = visibleRows!!.size

  // --------------------------------------------------------------------
  // LISTENERS HANDLING
  // --------------------------------------------------------------------
  /**
   * Adds a listener to the list that's notified each time a change
   * to the data model occurs.
   *
   * @param l The ReportListener
   */
  fun addReportListener(l: ReportListener) {
    listenerList.add(ReportListener::class.java, l)
  }

  /**
   * Removes a listener from the list that's notified each time a
   * change to the data model occurs.
   *
   * @param l The ReportListener
   */
  fun removeReportListener(l: ReportListener) {
    listenerList.remove(ReportListener::class.java, l)
  }

  /**
   * Notifies all listeners that the report model has changed.
   */
  protected fun fireContentChanged() {
    val listeners = listenerList.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == ReportListener::class.java) {
        (listeners[i + 1] as ReportListener).contentChanged()
      }
      i -= 2
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  // Columns contains all columns defined by the user
  // accessiblecolumns is a part of columns which contains only visible columns
  private lateinit var columns : Array<VReportColumn?>    // array of column definitions
  lateinit var accessibleColumns : Array<VReportColumn?> // array of visible or hide columns
    private set

  // Root is the root of the tree (which is our model to manipulate data)
  private var root : VGroupRow? = null    // root of grouping tree

  // Baserows contains data give by the request of the user
  // visibleRows contains all data which will be displayed. It's like a buffer. visibleRows
  // is changed when a column move or one or more row are folded
  private var userRows: ArrayList<VBaseRow>? = ArrayList(500)
  private lateinit var baseRows : Array<VReportRow?>    // array of base data rows
  private var visibleRows : Array<VReportRow?>? = null  // array of visible rows
  private var maxRowCount = 0

  // Sortedcolumn contain the index of the sorted column
  // sortingOrder store the type of sort of the sortedColumn : ascending or descending
  private var sortedColumn = 0    // the table is sorted wrt. to this column
  private var sortingOrder = 0    // 1: ascending, -1: descending

  // displayOrder contains index column model in display order
  // reverseOrder is calculate with displayOrder and contains index column display into model order
  private lateinit var displayOrder : IntArray    // column mapping from display to model
  private lateinit var reverseOrder : IntArray    // column mapping from model to display

  // The displayLevels variable is a table which contains the level of each column
  private lateinit var displayLevels : IntArray   // column levels in display order
  private val listenerList = EventListenerList()  // List of listeners
}
