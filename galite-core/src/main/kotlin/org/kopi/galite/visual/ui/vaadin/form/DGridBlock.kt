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
package org.kopi.galite.visual.ui.vaadin.form

import kotlin.streams.toList

import org.kopi.galite.visual.base.UComponent
import org.kopi.galite.visual.form.Alignment
import org.kopi.galite.visual.form.VActorField
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VBooleanField
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.form.VFieldUI
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.block.BlockLayout
import org.kopi.galite.visual.ui.vaadin.block.SingleComponentBlockLayout
import org.kopi.galite.visual.ui.vaadin.grid.GridEditorField
import org.kopi.galite.visual.visual.Action
import org.kopi.galite.visual.visual.VException

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.grid.ColumnResizeEvent
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridSortOrder
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.grid.HeaderRow
import com.vaadin.flow.component.grid.editor.Editor
import com.vaadin.flow.component.grid.editor.EditorImpl
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.event.SortEvent
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.function.SerializableConsumer
import com.vaadin.flow.internal.ExecutionContext
import com.vaadin.flow.data.provider.Query

/**
 * Grid based chart block implementation.
 */
open class DGridBlock(parent: DForm, model: VBlock) : DBlock(parent, model) {

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  lateinit var grid: Grid<GridBlockItem>

  init {
    grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES)
    themeList.add("grid-block")
    grid.isAllRowsVisible = true
  }

  /*
   * We use this to fire a set item change event only when
   * the valid records is changed. It is not necessary to
   * lock the session when nothing is really changed.
   */
  private var lastValidRecords = 0

  // flag used to adapt grid width when a scroll bar is displayed
  // or when the scroll bar is removed.
  private var widthAlreadyAdapted = false

  /*
   * A workaround for a Grid behavior: If two bind request are sent
   * to the grid editor, the confirm bind callback will be done only
   * for the first bind request. This is not the expected result since
   * we expect that the last bind request will be confirmed.
   * Since the session task execution is done synchronously, this item
   * will only take the last requested item to be binded and thus we force
   * the editor to bind the last record. Typically, this is used when multiple
   * gotoRecord are called via the window action queue.
   */
  protected var itemToBeEdited: Int? = null

  /*
   * A flag used to force disabling the editor cancel when scroll is fired
   * by the application and not by the user.
   * This flag is set true when application requests to edit a specific item.
   * When the item needs to be scrolled through, the editor should not be cancelled.
   * Whereas, the editor should be cancelled when the edited item is not already in
   * the port view after a scroll fired by the user.
   */
  private var doNotCancelEditor = true // TODO

  private var filterRow: HeaderRow? = null
  private lateinit var sortableHeaders: MutableMap<Grid.Column<*>, DGridEditorLabel>
  var lastSortOrder: List<GridSortOrder<GridBlockItem>>? = null
  lateinit var editor: Editor<GridBlockItem>
  val isEditorInitialized get() = ::editor.isInitialized

  // --------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------
  /**
   * Differently create fields for this block
   */
  override fun createFields() {
    super.createFields()
    grid = object : Grid<GridBlockItem>() {
      override fun createEditor(): Editor<GridBlockItem> {
        return object : EditorImpl<GridBlockItem>(this, propertySet) {
          private var itemToEdit: GridBlockItem? = null
          private var editItemRequest: SerializableConsumer<ExecutionContext>? = null

          override fun closeEditor() {
            if(!doNotCancelEditor) {
              super.closeEditor()
            }
          }

          override fun editItem(item: GridBlockItem) {
            if (!inDetailMode()) {
              updateEditors()
              doEditItem(item)
            }
          }

          // Workaround for https://github.com/vaadin/flow-components/issues/1997
          fun doEditItem(item: GridBlockItem) {
            itemToEdit = item

            if (editItemRequest == null) {
              editItemRequest = SerializableConsumer {
                super.editItem(itemToEdit)
                editItemRequest = null
              }
              grid.element.node.runWhenAttached { ui: UI ->
                ui.internals.stateTree
                  .beforeClientResponse(grid.element.node, editItemRequest)
              }
            }
          }
        }
      }
    }
    editor = grid.editor
    grid.addSortListener(::sort)
    grid.setSelectionMode(Grid.SelectionMode.NONE)
    grid.isEnabled = model.isAccessible
    if (grid.isEnabled) {
      editor.isBuffered = false
    }
    grid.isColumnReorderingAllowed = false
    //grid.setColumnResizeMode(ColumnResizeMode.ANIMATED)
    //grid.setHeightMode(HeightMode.ROW)
    //grid.setCellStyleGenerator(DGridBlockCellStyleGenerator(model))
    grid.addColumnResizeListener(::columnResize)
    configure()
    setHeightByRows(model.bufferSize, model.displaySize)
    //grid.setColumnOrder(columnsOrder)
    /*if (detailsGenerator != null) { TODO
      grid.setDetailsGenerator(detailsGenerator)
    }
    object : GridEditorHandlingExtension() {
      protected fun onRowEdit(row: Int, col: Int) {
        val columnView = getColumnView(col)
        if (columnView != null) {
          model.form.performAsyncAction(object : Action() {
            override fun execute() {
              if (columnView.hasDisplays() && !columnView.hasAction()) {
                columnView.transferFocus(columnView.editorField)
              }
            }
          })
        }
      }

      protected fun onCancelEditor() {
        if (!doNotCancelEditor && grid.isEditorEnabled() && grid.isEditorActive()) {
          grid.cancelEditor()
        }
      }

      protected fun onGotoFirstEmptyRecord() {
        var rec = model.bufferSize - 1
        while (rec >= 0) {
          if (model.isRecordFilled(rec)) {
            break
          }
          rec -= 1
        }
        editRecord(rec + 1)
      }

      *//**
     * Returns the column view associated with the given grid position
     * @param position The field position in the grid.
     * @return The column view.
     *//*
        protected fun getColumnView(position: Int): DGridBlockFieldUI? {
          for (columnView in columnViews) {
            if (columnView != null && model.getFieldPos(columnView.model) == position) {
              return columnView
            }
          }
          return null
        }
      }.extend(grid)*/
    addComponent(grid, 0, 0, 1, 1, false, false)
    // ensures that the drop handler is set because
    // the create fields method is called in a session
    // lock context so call done in DBlock may find the
    // drag and drop wrapper null cause it is not created
    // yet.
    if (model.isDroppable) {
      // setDropHandler(DBlockDropHandler(model, this)) TODO
      // setDragStartMode(DragStartMode.HTML5) TODO
    }
    //})
  }

  private fun setHeightByRows(buffer: Int, rows: Int) {
    if(buffer == rows) {
      grid.isAllRowsVisible = true
    } else {
      grid.height = "calc(var(--_lumo-grid-border-width) + ${(rows + 1) * 24}px)"
    }
  }

  /**
   * Notifies the block that the UI is focused on the given record.
   * @param recno The record number
   */
  override fun enterRecord(recno: Int) {
    model.form.performAsyncAction(object : Action() {
      override fun execute() {
        try {
          // go to the correct block if necessary
          if (model !== model.form.getActiveBlock()) {
            if (model.isAccessible) {
              model.form.gotoBlock(model)
            }
          }
          // go to the correct record if necessary
          // but only if we are in the correct block now
          if (model == model.form.getActiveBlock() && model.isMulti()
                  && recno != model.activeRecord && model.isRecordAccessible(recno)) {
            model.gotoRecord(recno)
          }
        } catch (e: VException) {
          // if any error occurs in the goto record process
          // we go back to the active block record again cause
          // in UI the edited record is in fact the next target record.
          // cursor moves to the next grid record before model do it cause
          // jumping between records is done by the UI.
          // TODO : DOC
          /*if (model.activeRecord != -1 && model.activeRecord != grid.getEditedItemId() as Int) { TODO
            editRecord(model.activeRecord)
          }*/
          throw e
        } finally {
          //doNotCancelEditor = false todo
        }
      }
    })
  }

  override fun createFieldDisplay(index: Int, model: VField): VFieldUI {
    return DGridBlockFieldUI(this, model, index)
  }

  override fun add(comp: UComponent?, constraints: Alignment) {}

  override fun blockAccessChanged(block: VBlock, newAccess: Boolean) {
    access(currentUI) {
      if (editor.item != null) {
        editor.cancel()
      }
      grid.isEnabled = newAccess
      if (newAccess) {
        editor.isBuffered = false
      }
    }
  }

  override fun createLayout(): BlockLayout {
    return SingleComponentBlockLayout(this)
  }

  override fun refresh(force: Boolean) {
    super.refresh(force)
    refreshAllRows()
  }

  override fun getDisplayLine(recno: Int): Int {
    return 0
  }


  override fun getRecordFromDisplayLine(line: Int): Int {
    return if (itemToBeEdited != null) {
      itemToBeEdited!!
    } else if (isEditorInitialized && editor.item != null) {
      editor.item.record
    } else {
      super.getRecordFromDisplayLine(line)
    }
  }

  override fun validRecordNumberChanged() {
    // optimized to not fire an item set change if the number
    // of valid records is not changed
    if (model.numberOfValidRecord != lastValidRecords) {
      contentChanged()
      lastValidRecords = model.numberOfValidRecord
    }
  }

  override fun filterShown() {
    val dataProvider = grid.dataProvider as ListDataProvider

    if (filterRow != null) {
      access(currentUI) {
        grid.element.themeList.remove("hidden-filter")
        grid.element.themeList.add("shown-filter")
      }
      return
    }

    access(currentUI) {
      filterRow = grid.appendHeaderRow()
      filterRow.also { element.classList.add("block-filter") }
      val filterFields = grid.columns.mapIndexed { index, column ->
        val cell = filterRow!!.getCell(column)
        val filter = TextField()
        val search = Icon(VaadinIcon.SEARCH)
        val field = (column.editorComponent as GridEditorField<*>).dGridEditorField.getModel()
        filter.setWidthFull()

        filter.suffixComponent = search
        filter.className = "block-filter-text"
        filter.addValueChangeListener {
          dataProvider.refreshAll()
        }

        filter.valueChangeMode = ValueChangeMode.EAGER
        cell.setComponent(filter)

        FilterField(field, filter)
      }
      dataProvider.filter = DGridBlockFilter(filterFields, true, false)
    }
  }

  override fun filterHidden() {
    access {
      if (filterRow != null) {
        grid.element.themeList.remove("shown-filter")
        grid.element.themeList.add("hidden-filter")
      }
    }
  }

  override fun blockCleared() {
    contentChanged()
    clear()
  }

  override fun clear() {
    cancelEditor()
    scrollToStart()
  }

  /**
   * Scrolls the to beginning of the block
   */
  internal fun scrollToStart() {
    access(currentUI) {
      if (::grid.isInitialized) {
        grid.scrollToStart()
      }
    }
  }

  /**
   * Cancels the grid editor
   */
  protected fun cancelEditor() {
    access(currentUI) {
      if (::grid.isInitialized) {
        if (grid.isEnabled && editor.item != null) {
          editor.cancel()
        }
      }
    }
  }

  override fun fireValueChanged(col: Int, rec: Int, value: String?) {
    // no client side cache
  }

  override fun fireColorChanged(col: Int, rec: Int, foreground: String?, background: String?) {
    // no client side cache
  }

  override fun fireRecordInfoChanged(rec: Int, info: Int) {
    // no client side cache
  }

  override fun orderChanged() {
    access(currentUI) {
      if (::grid.isInitialized) {
        cancelEditor()
        contentChanged()
        if (model.activeRecord != -1) {
          editRecord(model.activeRecord)
        }
      }
    }
  }

  fun sort(
          event: SortEvent<Grid<GridBlockItem>, GridSortOrder<GridBlockItem>>?,
  ) {
    if (event!!.isFromClient) {
      // Sort records
      if(event.sortOrder.isNotEmpty()) {
        sort(event.sortOrder)
      } else {
        sort(lastSortOrder!!)
      }

      lastSortOrder = event.sortOrder
    }
  }

  fun sort(sortOrder: List<GridSortOrder<GridBlockItem>>) {
    sortOrder.forEach {
      sortableHeaders[it.sorted]?.let { label ->
        label.model?.sortColumn(label.fieldIndex!!)
      }
    }
  }

  private fun getActualItems(): List<GridBlockItem> {
    val dataProvider = grid.dataProvider as ListDataProvider
    val totalSize = dataProvider.items.size
    val dataCommunicator = grid.dataCommunicator
    val stream = dataProvider.fetch(
      Query(0, totalSize, dataCommunicator.backEndSorting, dataCommunicator.inMemorySorting, dataProvider.filter)
    )
    return stream.toList()
  }

  fun columnResize(event: ColumnResizeEvent<GridBlockItem>?) {
    // on column resize, we cancel editor to be resized
    // cause size is CSS imposed and not refreshed until
    // editor is cancelled
    /*if (grid.isEditorEnabled() && grid.isEditorActive()) { TODO
      BackgroundThreadHandler.access(Runnable {
        val lastEditeditemId: Any = grid.getEditedItemId()
        grid.cancelEditor()
        grid.editItem(lastEditeditemId)
      })
    }*/
  }

  /**
   * Notifies the data source that the content of the block has changed.
   */
  protected fun contentChanged() {
    if(::grid.isInitialized) {
      access(currentUI) {
        grid.dataProvider.refreshAll()
        // correct grid width to add scroll bar width
        if (model.numberOfValidRecord > model.displaySize) {
          if (!widthAlreadyAdapted) {
            //grid.setWidth(grid.width.substring(0, grid.width.indexOfLast { it.isDigit() } + 1).toFloat() + 16, Unit.PIXELS)
            widthAlreadyAdapted = true
          }
        }
      }
    }
  }

  /**
   * Refreshes, i.e. causes the client side to re-render all rows.
   */
  protected fun refreshAllRows() {
    access(currentUI) {
      grid.dataProvider.refreshAll()
    }
  }

  /**
   * Configures the columns of this block
   */
  protected fun configure() {
    val binder: Binder<GridBlockItem> = Binder()

    sortableHeaders = mutableMapOf()
    editor.binder = binder

    grid.addItemClickListener {
      val gridEditorFieldToBeEdited = it.column.editorComponent as GridEditorField<*>

      itemToBeEdited = it.item.record

      gridEditorFieldToBeEdited.dGridEditorField.onClick()
    }

    for (i in 0 until model.getFieldCount()) {
      val field = model.fields[i]

      if (!field.isInternal() && !field.noChart()) {
        val columnView: DGridBlockFieldUI = columnViews[i] as DGridBlockFieldUI

        if (columnView.hasDisplays()) {
          val label = columnView.editorField.label
          val column = grid.addColumn { columnView.editorField.format(it.getValue(field)) }
                  .setKey(i.toString())
                  .setHeader(label)
                  .setEditorComponent(columnView.editor)
                  .setResizable(true)

          //column.setRenderer(columnView.editorField.createRenderer()) TODO
          //column.setConverter(columnView.editorField.createConverter()) TODO
          column.isSortable = field.isSortable()
          if(field.isSortable()) {
            val comparator = DGridBlockItemSorter.DefaultComparator(model, field)

            column.setComparator(comparator)

            if(label != null) {
              sortableHeaders[column] = label
            }
          }
          val width =
                  when (field) {
                    is VBooleanField -> 46 // boolean field length
                    is VActorField -> 148 // actor field field length
                    else -> 8 * field.width + 12  // add padding TODO
                  }

          column.width = if(field.hasAutofill()) {
            "" + (width + 18) + "px" // Add more width for the autofill button
          }  else {
            "" + width + "px"
          }
          column.isVisible = field.getDefaultAccess() != VConstants.ACS_HIDDEN
        }
      }
    }
    val items = mutableListOf<GridBlockItem>()

    repeat(model.bufferSize) {
      items.add(GridBlockItem(it))
    }
    grid.setItems(items)
  }

  /**
   * Updates the grid editors access and content
   */
  protected fun updateEditors() {
    for (columnView in columnViews) {
      if (columnView != null) {
        val rowController = columnView as DGridBlockFieldUI
        if (rowController.hasDisplays()) {
          if (rowController.editorField.modelHasFocus()) {
            rowController.editorField.updateFocus()
          }
          rowController.editorField.updateAccess()
          rowController.editorField.updateText()
          rowController.editorField.updateColor()
        }
      }
    }
  }

  /**
   * Returns true if the grid editor is active and an item is being edited.
   * @return true if the grid editor is active and an item is being edited.
   */
  val isEditorActive: Boolean
    get() = ::editor.isInitialized && editor.isOpen

  /**
   * Returns the edited record in this block
   * @return the edited record in this block
   */
  val editedRecord: Int
    get() = editor.item.record

  /**
   * Returns the field model for a given property ID.
   * @param propertyId The column property ID.
   * @return The field model.
   */
  protected fun getField(propertyId: Any): VField {
    return model.fields[propertyId as Int]
  }

  /**
   * Updates the column access for a given column identified by the associated field model.
   * @param f The field model.
   */
  fun updateColumnAccess(f: VField, rec: Int) {
    access(currentUI) {
      val column = grid.getColumnByKey(model.getFieldIndex(f).toString())

      if (::grid.isInitialized && column != null) {
        column.isVisible  = f.getAccess(rec) != VConstants.ACS_HIDDEN
      }
    }
  }

  /**
   * Refreshes a given row in the data grid.
   * @param row The row index
   */
  fun refreshRow(row: Int) {
    if(::grid.isInitialized) {
      access(currentUI) {
        val itemToRefresh = grid.dataCommunicator.getItem(row)

        grid.dataProvider.refreshItem(itemToRefresh)
      }
    }
  }

  /**
   * Opens the editor interface for the provided record.
   * @param record The record number
   */
  fun editRecord(record: Int) {
    if (::grid.isInitialized) {
      itemToBeEdited = record
      access(currentUI) {
        if (grid.isEnabled
                && (editor.item == null
                        || (itemToBeEdited != null
                        && editor.item.record != itemToBeEdited))
        ) {
          if(itemToBeEdited!! < 0 || itemToBeEdited!! >= grid.dataCommunicator.itemCount) {
            itemToBeEdited = 0
          }

          // doNotCancelEditor = true TODO
          if (!inDetailMode()) {
            editor.editItem(getActualItems().single { it.record == record })
          }
        }
      }
    }
  }
}
