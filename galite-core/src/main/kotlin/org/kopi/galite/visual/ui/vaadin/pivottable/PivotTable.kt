/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dnd.DragSource
import com.vaadin.flow.component.dnd.DropEffect
import com.vaadin.flow.component.dnd.DropTarget
import com.vaadin.flow.component.dnd.EffectAllowed
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.HeaderRow
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.splitlayout.SplitLayout
import kotlin.math.pow
import kotlin.streams.toList

@CssImport("./styles/galite/pivottable.css")
class PivotTable(leftWidth: String = "15%", rightWidth: String = "85%") : SplitLayout() {
  private var fields = mutableListOf<PivotTableColumn>()
  private var selectedFields = mutableListOf<PivotTableColumn>()
  private var crossFields = mutableListOf<PivotTableColumn>()
  private lateinit var data: List<List<String>>
  private lateinit var pivotTableData: PivotTableData
  private lateinit var pivotTableHeader: MutableList<PivotTableHeader>
  private lateinit var rightLayout: VerticalLayout
  lateinit var grid: Grid<List<String>>
  private val layout = VerticalLayout()
  private val topLayout = VerticalLayout()

  init {
    addToPrimary(initLeftLayout())
    addToSecondary(initRightLayout())
    layout.width = leftWidth
    rightLayout.width = rightWidth
    setHeightFull()
    setWidthFull()

  }

  private fun initLeftLayout(): VerticalLayout {

    topLayout.isSpacing = true
    topLayout.isPadding = true
    topLayout.isMargin = true

    val mode = Select("Bar chart", "Line chart")
    val operation = Select("Count", "Average", "etc")

    mode.setWidthFull()
    operation.setWidthFull()
    topLayout.add(mode)
    topLayout.add(operation)

    val bottomLayout = VerticalLayout()

    bottomLayout.isSpacing = true
    bottomLayout.isPadding = true
    bottomLayout.minHeight = "20%"
    bottomLayout.style.set("border", "1px solid #505050")
    bottomLayout.style.set("border-collapse","collapse")

    val buttons = listOf(Button("age"), Button("course"))

    buttons.map { DragSource.create(it) }.forEach {
      it.effectAllowed = EffectAllowed.MOVE
      bottomLayout.add(it.dragSourceComponent)
      selectedFields.add(it.dragSourceComponent.text)
    }

    val dndBottomLayout = DropTarget.create(bottomLayout)

    dndBottomLayout.dropEffect = DropEffect.MOVE
    dndBottomLayout.addDropListener {
      if (it.dropEffect === DropEffect.MOVE) {
        bottomLayout.add(it.dragSourceComponent.get())
        selectedFields.removeIf { element -> element == it.dragSourceComponent.get().element.text }
        selectedFields.add(it.dragSourceComponent.get().element.text)
        crossFields.removeIf { ele -> ele == it.dragSourceComponent.get().element.text }
        fields.removeIf { ele -> ele == it.dragSourceComponent.get().element.text }
        initGrid()
      }
    }

    layout.add(topLayout, dndBottomLayout.dropTargetComponent)
    layout.setFlexGrow(1.0, dndBottomLayout.dropTargetComponent)
    return layout
  }

  private fun initRightLayout(): VerticalLayout {
    rightLayout = VerticalLayout()

    val fieldsContainer = HorizontalLayout()

    fieldsContainer.isSpacing = true
    fieldsContainer.isPadding = true
    fieldsContainer.minWidth = "100%"
    fieldsContainer.minHeight = "50px"
    fieldsContainer.style.set("border", "1px solid #505050")
    fieldsContainer.style.set("border-collapse","collapse")

    val fieldsButtons = arrayOf(Button("nickname"), Button("address"), Button("salary"))

    fieldsButtons.map { DragSource.create(it) }.forEach {
      it.effectAllowed = EffectAllowed.MOVE
      fieldsContainer.add(it.dragSourceComponent)
      fields.add(it.dragSourceComponent.text)
    }

    val fieldsSelectedContainer = HorizontalLayout()

    fieldsSelectedContainer.isSpacing = true
    fieldsSelectedContainer.isPadding = true
    fieldsSelectedContainer.minWidth = "100%"
    fieldsSelectedContainer.minHeight = "50px"
    fieldsSelectedContainer.style.set("border", "1px solid #505050")
    fieldsSelectedContainer.style.set("border-collapse","collapse")

    val selectButtons = arrayOf(Button("name"), Button("year"))

    selectButtons.map { DragSource.create(it) }.forEach {
      it.effectAllowed = EffectAllowed.MOVE
      fieldsSelectedContainer.add(it.dragSourceComponent)
      crossFields.add(it.dragSourceComponent.element.text)
    }

    val dndFieldsContainer = DropTarget.create(fieldsContainer)

    dndFieldsContainer.dropEffect = DropEffect.MOVE
    dndFieldsContainer.addDropListener {
      if (it.dropEffect === DropEffect.MOVE) {
        fieldsContainer.add(it.dragSourceComponent.get())
        fields.removeIf { element -> element == it.dragSourceComponent.get().element.text }
        fields.add(it.dragSourceComponent.get().element.text)
        selectedFields.removeIf { ele -> ele == it.dragSourceComponent.get().element.text }
        crossFields.removeIf { ele -> ele == it.dragSourceComponent.get().element.text }
        initGrid()
      }
    }

    val dndFieldsSelectedContainer = DropTarget.create(fieldsSelectedContainer)

    dndFieldsSelectedContainer.dropEffect = DropEffect.MOVE
    dndFieldsSelectedContainer.addDropListener {
      if (it.dropEffect === DropEffect.MOVE) {
        fieldsSelectedContainer.add(it.dragSourceComponent.get())
        crossFields.removeIf { element -> element == it.dragSourceComponent.get().element.text }
        crossFields.add(it.dragSourceComponent.get().element.text)
        selectedFields.removeIf { ele -> ele == it.dragSourceComponent.get().element.text }
        fields.removeIf { ele -> ele == it.dragSourceComponent.get().element.text }
        initGrid()
      }
    }

    rightLayout.add(dndFieldsContainer.dropTargetComponent)
    rightLayout.add(dndFieldsSelectedContainer.dropTargetComponent)
    rightLayout.setFlexGrow(1.0, dndFieldsContainer.dropTargetComponent)
    rightLayout.setFlexGrow(1.0, dndFieldsSelectedContainer.dropTargetComponent)

    initGrid()
    return rightLayout
  }

  private fun initGrid(): Grid<List<String>>? {
    initGridData()
    if (!this::data.isInitialized) {
      rightLayout.add(Div(Label("No data provided")))
    }

    val initialGrid = rightLayout.children.toList().firstOrNull { it::class.java == Grid::class.java }
    // remove the old grid to be changed with a new one
    // as cleaning the grid Headers Rows is not possible
    if (initialGrid != null) {
      rightLayout.remove(initialGrid)
    }

    grid = Grid<List<String>>()
    updateGridLayout()
    rightLayout.add(grid)

    return grid
  }

  private fun updateGridLayout() {
    // Resetting the grid data to match the selected and cross Fields
    initGridData()
    grid.setItems(data)

    val gridColumns = mutableListOf<Grid.Column<List<String>>>()
    val numberOfInitialCrossFields = 2.0.pow(crossFields.size).toInt()
    val headersRows = mutableListOf<HeaderRow>()

    // Adding the header columns of the selected fields
    for (index in 1..selectedFields.size) {
      val column = grid.addColumn {
        it[index - 1]
      }
              .setHeader("${selectedFields[index - 1]}_header")
              .setTextAlign(ColumnTextAlign.CENTER)
              .setClassNameGenerator(ColumnStyleGenerator())
      column.isVisible = true
      column.isResizable = true
      gridColumns.add(column)
    }

    // add an empty column, used to add the cross fields headers
    val column = grid.addColumn {
      it[selectedFields.size]
    }.setHeader("").setTextAlign(ColumnTextAlign.CENTER)
    column.isResizable = true
    gridColumns.add(column)

    // Build the headers from down up
    for (index in crossFields.size downTo 1) {
      if (index == crossFields.size) {
        // Add the columns to the grid
        // which will be used in order to create the headers row
        for (i in 1..numberOfInitialCrossFields) {
          val crossFieldColumn = grid.addColumn {
            it[selectedFields.size + i]
          }.setHeader(crossFields[index - 1]).setTextAlign(ColumnTextAlign.CENTER)
          column.isResizable = true
          gridColumns.add(crossFieldColumn)
        }
      } else {
        // create the headers rows (starting from the second row from the bottom)
        val numberOfHeadersFields = 2.0.pow(index).toInt()
        val headerRow: HeaderRow = grid.prependHeaderRow()

        // adding the cross fields names headers (inside the empty column)
        if (selectedFields.size >= 1) {
          val headerDiv = Div(Span("${crossFields[index - 1]}_header"))
          headerDiv.setSizeFull()
          headerDiv.style.set("text-align", "center")
          headerRow.cells[selectedFields.size].setComponent(headerDiv)
        }

        // adding the headers (grouped upon the existing columns)
        for (i in 0 until numberOfHeadersFields) {
          val headerSize = numberOfInitialCrossFields / numberOfHeadersFields
          val joinedColumns = gridColumns.slice(selectedFields.size + 1 + (i * headerSize)..selectedFields.size + ((i + 1) * headerSize)).toTypedArray()
          val header = Div(Span("${crossFields[index - 1]}_v"))

          header.setSizeFull()
          header.style.set("text-align", "center")
          headerRow.join(*joinedColumns).setComponent(header)
        }
        headersRows.add(headerRow)
      }
    }
  }

  private fun initGridData() {
    // TODO: make the function check if the data was provided already
    //  instead of making fake rows
    val dataRowSize = 2.0.pow(crossFields.size).toInt()
    val rowsNumber =  2.0.pow(selectedFields.size).toInt()

    val rows = (1..rowsNumber).map { rowIndex ->
      val row = mutableListOf(*(1..selectedFields.size).map { "${selectedFields[it-1]}_v" }.toTypedArray(),
              "",
              *(1..dataRowSize).map { "field $it" }.toTypedArray()
      )
      for( index in 1..selectedFields.size) {
        val headersNumber = rowsNumber / 2.0.pow(index-1).toInt()
        row.set(index -1, "${selectedFields[index -1]}_v_${(rowIndex-1) / headersNumber  }")
      }
      row.toList()
    }
    data = rows
/*
    val dataColumnsSize = 2.0.pow(crossFields.size).toInt()

  // PIVOT TABLE HEADERS
    val headersSize = crossFields.size
    val headersStack = mutableListOf<PivotTableHeader>()

    for (i in 1..headersSize) {
      if(i == 1) {
        pivotTableHeader = mutableListOf<PivotTableHeader>()
        for (j in 1..2) {
          val curr = PivotTableHeader("${crossFields[i-1]}_{j}", mutableListOf(), i == headersSize)
          pivotTableHeader.add(curr)
          if (i != headersSize) {
            headersStack.add(curr)
          }
        }
      } else {
        for( header in headersStack) {
          for (j in 1..2) {
            val curr = PivotTableHeader("${crossFields[i-1]}_{j}", mutableListOf(), i == headersSize)
            if (i != headersSize) {
              headersStack.add(curr)
            }
          }
          headersStack.remove(header)
        }
      }
    }
//    println(pivotTableHeader)

    // PIVOT TABLE DATA
    val pivotTableRows = mutableListOf<List<PivotTableColumn>>()
    val rowsStack = mutableListOf<PivotTableData>()

    for( i in 1..selectedFields.size) {

      if(i == 1) {
        pivotTableData = PivotTableData(selectedFields[i -1], mutableListOf(), i == selectedFields.size)
        if (i != selectedFields.size)
          rowsStack.add(pivotTableData)
      } else {
        if (rowsStack.size != 0){


        for( row in rowsStack) {
          row.children = (1..2).map { PivotTableData(selectedFields[i - 1],
                  if (i != selectedFields.size) mutableListOf() else (1..dataColumnsSize).map{ PivotTableData("data_${it}", listOf(), true)}.toMutableList(), i == selectedFields.size) }
          row.children.forEach{
            if (it.isLast == false){
              rowsStack.add(it)
            }
          }
          rowsStack.remove(row)
        }
        }
      }
    }

    val stack = mutableListOf<PivotTableData>()
    stack.add(pivotTableData)
    val row = mutableListOf<PivotTableColumn>()
    while (stack.size != 0) {
      val curr = stack.removeAt(stack.size -1)
      if (curr.isLast) {
        for(child in children) {
          pivotTableRows.add(row.plus(child) as List<PivotTableColumn>)
        }
        if (row.size >= 1) {
          row.removeAt(row.size -1)
        }
      }else {
        row.add(curr.value)
        for(child in curr.children) {
          stack.add(child)
        }
      }
    }*/
//    data = rows

//    println(pivotTableData)
  }
}

// Using a typealias to enable easier modification of column type in the future
typealias PivotTableColumn = String

class PivotTableData(val value: String, var children: Collection<PivotTableData>, val isLast: Boolean)
class PivotTableHeader(val value:String, val children: Collection<PivotTableHeader>, val isLast: Boolean)
