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
package org.kopi.galite.ui.vaadin.form

import org.kopi.galite.base.UComponent
import org.kopi.galite.form.Alignment
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VField
import org.kopi.galite.form.VFieldUI
import org.kopi.galite.ui.vaadin.block.BlockLayout
import org.kopi.galite.ui.vaadin.block.SingleComponentBlockLayout
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.VException

import com.vaadin.flow.component.Unit
import com.vaadin.flow.component.grid.ColumnResizeEvent
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridSortOrder
import com.vaadin.flow.component.grid.HeaderRow
import com.vaadin.flow.data.event.SortEvent

/**
 * Grid based chart block implementation.
 */
open class DGridBlock(parent: DForm, model: VBlock)
  : DBlock(parent, model)
/* ColumnResizeListener, SortListener TODO*/ {

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  protected lateinit var grid: Grid<DGridBlockContainer.GridBlockItem>

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
  private var itemToBeEdited: Int? = null

  /*
   * A flag used to force disabling the editor cancel when scroll is fired
   * by the application and not by the user.
   * This flag is set true when application requests to edit a specific item.
   * When the item needs to be scrolled through, the editor should not be cancelled.
   * Whereas, the editor should be cancelled when the edited item is not already in
   * the port view after a scroll fired by the user.
   */
  private var doNotCancelEditor = false

  private var filterRow: HeaderRow? = null

  override var sortedRecords = IntArray(0)
    set(value) {
      if (!model.noDetail() && !inDetailMode()) {
        field = value
      }
    }

  // --------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------
  /**
   * Differently create fields for this block
   */
  override fun createFields() {
    super.createFields()
    //BackgroundThreadHandler.access(Runnable { TODO
    grid = Grid<DGridBlockContainer.GridBlockItem>()
    /*grid = object : Grid(createContainerDataSource()) {
      protected fun doEditItem() {
        if (!inDetailMode()) {
          updateEditors()
          super.doEditItem()
          enterRecord(getEditedItemId() as Int)
        }
      }
    }*/
    grid.addSortListener(::sort)
    grid.setSelectionMode(Grid.SelectionMode.NONE)
    //grid.setEditorEnabled(model.isAccessible)
    //if (grid.isEditorEnabled()) {
    //  grid.setEditorBuffered(false)
    //}
    grid.isColumnReorderingAllowed = false
    //grid.setColumnResizeMode(ColumnResizeMode.ANIMATED)
    //grid.setHeightMode(HeightMode.ROW)
    //grid.setHeightByRows(model.displaySize)
    //grid.setCellStyleGenerator(DGridBlockCellStyleGenerator(model))
    grid.addColumnResizeListener(::columnResize)
    configure()
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

  /**
   * Notifies the block that the UI is focused on the given record.
   * @param recno The record number
   */
  override fun enterRecord(recno: Int) {
    super.enterRecord(recno)
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
          // see EditorHandlingExtensionConnector#CustomEventHandler.
          /*if (model.activeRecord != -1 && model.activeRecord != grid.getEditedItemId() as Int) { TODO
            editRecord(model.activeRecord)
          }*/
          throw e
        } finally {
          doNotCancelEditor = false
        }
      }
    })
  }

  override fun createFieldDisplay(index: Int, model: VField): VFieldUI {
    return DGridBlockFieldUI(this, model, index)
  }

  override fun add(comp: UComponent?, constraints: Alignment) {}
  override fun blockAccessChanged(block: VBlock, newAccess: Boolean) {
    // TODO
  }

  override fun createLayout(): BlockLayout {
    return SingleComponentBlockLayout()
  }

  override fun refresh(force: Boolean) {
    super.refresh(force)
    refreshAllRows()
  }

  override fun getDisplayLine(recno: Int): Int {
    return 0
  }

  /*override fun getRecordFromDisplayLine(line: Int): Int {
    TODO()
  }*/

  override fun validRecordNumberChanged() {
    // optimized to not fire an item set change if the number
    // of valid records is not changed
    if (model.numberOfValidRecord != lastValidRecords) {
      contentChanged()
      lastValidRecords = model.numberOfValidRecord
    }
  }

  override fun filterShown() {
    if (filterRow != null) {
      return
    }
    /*BackgroundThreadHandler.access(Runnable { TODO
      filterRow = grid.appendHeaderRow()
      // filterRow.setStyleName("block-filter")
      for (propertyId in containerDatasource.containerPropertyIds) {
        val cell: HeaderRow.HeaderCell = filterRow!!.getCell(propertyId)
        val filter = TextField()
        filter.setStyleName("filter-text")
        filter.setImmediate(true)
        filter.addTextChangeListener(object : TextChangeListener() {
          fun textChange(event: TextChangeEvent) {
            if (grid.isEditorActive()) {
              grid.cancelEditor()
            }
            containerDatasource.removeContainerFilters(propertyId)
            if (event.getText().length() > 0) {
              containerDatasource.addContainerFilter(propertyId,
                                                     event.getText(),
                                                     true,
                                                     false)
            }
          }
        })
        cell.setComponent(filter)
      }
    })*/
  }

  override fun filterHidden() {
    //BackgroundThreadHandler.access(Runnable { TODO
    if (filterRow != null) {
      containerDatasource.removeAllContainerFilters()
      //grid.removeHeaderRow(filterRow) TODO
      filterRow = null
    }
    //})
  }

  override fun blockChanged() {
    refresh(true)
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
    //BackgroundThreadHandler.access(Runnable { TODO
    if (grid != null) {
      grid.scrollToStart()
    }
    //})
  }

  /**
   * Clears the grid sort order
   */
  protected fun clearSortOrder() {
    /*BackgroundThreadHandler.access(Runnable { TODO
      if (grid != null) {
        grid.clearSortOrder()
      }
    })*/
  }

  /**
   * Cancels the grid editor
   */
  protected fun cancelEditor() {
    /*BackgroundThreadHandler.access(Runnable { TODO
      if (grid != null) {
        if (grid.isEditorEnabled() && grid.isEditorActive()) {
          grid.cancelEditor()
        }
      }
    })*/
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

  override fun fireActiveRecordChanged(record: Int) {
    if (!model.noDetail()) {
      super.fireActiveRecordChanged(record)
    }
  }

  override fun orderChanged() {
    //BackgroundThreadHandler.access(Runnable { TODO
    if (grid != null) {
      cancelEditor()
      clearSortOrder()
      containerDatasource.doSort()
      contentChanged()
      if (model.activeRecord != -1) {
        editRecord(model.activeRecord)
      }
    }
    //})
  }

  fun sort(
          event: SortEvent<Grid<DGridBlockContainer.GridBlockItem>, GridSortOrder<DGridBlockContainer.GridBlockItem>>?,
  ) {
    if (event!!.isFromClient) {
      cancelEditor()
      // update model sorted records
      /*for (i in 0 until containerDatasource.allItemIds.size()) { TODO
        model.sortedRecords[i] = containerDatasource.allItemIds[i]
      }*/
    }
  }



  fun columnResize(event: ColumnResizeEvent<DGridBlockContainer.GridBlockItem>?) {
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
    //BackgroundThreadHandler.access(Runnable { TODO
    containerDatasource.fireContentChanged()
    // correct grid width to add scroll bar width
    if (model.numberOfValidRecord > model.displaySize) {
      if (!widthAlreadyAdapted) {
        //grid.setWidth(grid.width.substring(0, grid.width.indexOfLast { it.isDigit() } + 1).toFloat() + 16, Unit.PIXELS)
        widthAlreadyAdapted = true
      }
    }
    //})
  }

  /**
   * Refreshes, i.e. causes the client side to re-render all rows.
   */
  protected fun refreshAllRows() {
    //BackgroundThreadHandler.access(Runnable { TODO
    //grid.refreshAllRows()
    //})
  }

  /**
   * Configures the columns of this block
   */
  protected fun configure() {
    val width = 0

    for (i in 0 until model.getFieldCount()) {
      if (!model.fields[i].isInternal() && !model.fields[i].noChart()) {
        val columnView: DGridBlockFieldUI = columnViews[i] as DGridBlockFieldUI

        if (columnView.hasDisplays()) {
          grid.addComponentColumn { columnView.editor }
            .setAutoWidth(true)
            .setKey(i.toString())
            .setHeader(columnView.editorField.label)
        }
      }
    }
    val items = mutableListOf<DGridBlockContainer.GridBlockItem>()

    repeat(model.bufferSize) {
      items.add(DGridBlockContainer.GridBlockItem(it))
    }
    grid.setItems(items)
    for (column in grid.columns) {
      /*val field = getField(column.getPropertyId()) TODO
      val columnView: DGridBlockFieldUI = columnViews.get(model.getFieldIndex(field))
      if (columnView.hasDisplays()) {
        column.setHidable(false)
        column.setEditorField(columnView.editor)
        column.setRenderer(columnView.editorField.createRenderer())
        column.setConverter(columnView.editorField.createConverter())
        column.isSortable = field.isSortable()
        grid.getDefaultHeaderRow().getCell(column.getPropertyId()).setComponent(columnView.editorField.label)
        if (field.isNumeric()) {
          column.setWidth(FontMetrics.DIGIT.getWidth() * field.width + 12) // add padding
        } else {
          if (field is VBooleanField) {
            column.setWidth(46) // boolean field length
          } else if (field is VActorField) {
            column.setWidth(148) // actor field field length
          } else {
            column.setWidth(FontMetrics.LETTER.getWidth() * field.width + 12) // add padding
          }
        }
        column.setEditable(true)
        column.setHidden(field.getDefaultAccess() == VConstants.ACS_HIDDEN)
        width += column.width
      }*/
    }
    //grid.setWidth((width + 16).toFloat(), Unit.PIXELS)
  }

  /**
   * Updates the grid editors access and content
   */
  protected fun updateEditors() {
    for (columnView in columnViews) {
      if (columnView != null) {
        val rowController = columnView as DGridBlockFieldUI
        if (rowController.hasDisplays()) {
          rowController.editorField.reset()
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
    get() = TODO()

  /**
   * Returns the edited record in this block
   * @return the edited record in this block
   */
  val editedRecord: Int
    get() = TODO()

  /**
   * Returns the field model for a given property ID.
   * @param propertyId The column property ID.
   * @return The field model.
   */
  protected fun getField(propertyId: Any): VField {
    return model.fields[propertyId as Int]
  }

  /**
   * Returns the container data source of this block.
   * @return The container data source of this block.
   */
  protected val containerDatasource: DGridBlockContainer
    protected get() = TODO()

  /**
   * Creates the data source container.
   * @return The data source container.
   */
  protected fun createContainerDataSource(): DGridBlockContainer {
    //return DGridBlockContainer(model)
    TODO()
  }

  /**
   * Orders the columns by chart position in the model.
   * @return The chart position order.
   */
  protected val columnsOrder: Array<Any?>
    get() {
      TODO()
    }

  /**
   * Updates the column access for a given column identified by the associated field model.
   * @param f The field model.
   */
  fun updateColumnAccess(f: VField, rec: Int) {
    /*BackgroundThreadHandler.access(Runnable { TODO
      if (grid != null && grid.getColumn(model.getFieldIndex(f)) != null) {
        grid.getColumn(model.getFieldIndex(f)).setHidden(f.getAccess(rec) == VConstants.ACS_HIDDEN)
      }
    })*/
  }

  /**
   * Refreshes a given row in the data grid.
   * @param row The row index
   */
  fun refreshRow(row: Int) {
    /*BackgroundThreadHandler.access(Runnable { TODO
      grid.refreshRows(row)
    })*/
  }

  /**
   * Opens the editor interface for the provided record.
   * @param record The record number
   */
  fun editRecord(record: Int) {
    if (grid != null) {
      itemToBeEdited = record
      /*BackgroundThreadHandler.access(Runnable { TODO
        if (grid.isEditorEnabled()
                && (grid.getEditedItemId() == null
                        || (itemToBeEdited != null
                        && grid.getEditedItemId() as Int != itemToBeEdited))) {
          /*if (!containerDatasource.containsId(itemToBeEdited)) {
            itemToBeEdited = containerDatasource.firstItemId()
          }*/
          doNotCancelEditor = true
          if (!inDetailMode()) {
            grid.editItem(itemToBeEdited)
          }
        }
      })*/
    }
  }
}
