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

import org.kopi.galite.form.UField
import org.kopi.galite.form.UListDialog
import org.kopi.galite.form.VDictionary
import org.kopi.galite.form.VForm
import org.kopi.galite.form.VListDialog
import org.kopi.galite.ui.vaadin.list.GridListDialog
import org.kopi.galite.ui.vaadin.notif.InformationNotification
import org.kopi.galite.ui.vaadin.visual.VApplication
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.UWindow
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VRuntimeException
import org.kopi.galite.visual.VlibProperties

/**
 * The `DListDialog` is the vaadin implementation of the
 * [UListDialog] specifications.
 */
class DListDialog(
        model: VListDialog
) : GridListDialog(), UListDialog, CloseListener, SelectionListener, SearchListener {
  //---------------------------------------------------
  // LISTDIALOG IMPLEMENTATION
  //---------------------------------------------------
  override fun selectFromDialog(window: UWindow?, field: UField?, showSingleEntry: Boolean): Int {
    if (!showSingleEntry && model.count == 1) {
      return model.convert(0)
    }

    // too many rows case
    if (model.isTooManyRows) {
      handleTooManyRows()
    }
    prepareDialog() // prepares the dialog data.
    if (field != null) {
      // show the dialog beside the field.
      // otherwise show it centered.
      if (field is DField) {
        showRelativeTo(field as DField?)
      } else if (field is DGridEditorField<*>) {
        showRelativeTo((field as DGridEditorField<*>).getEditor())
      }
    }
    showDialogAndWait()
    return handleClientResponse()
  }

  override fun selectFromDialog(window: UWindow, showSingleEntry: Boolean): Int {
    return selectFromDialog(window, null, showSingleEntry)
  }

  fun onClose(event: CloseEvent) {
    doSelectFromDialog(-1, event.isEscaped(), event.isNewForm())
  }

  fun onSelection(event: SelectionEvent) {
    if (table.getContainerDataSource().size() === 0) {
      return
    }
    ensureTableSelection()
    when (event.getTarget()) {
      SelectionTarget.CURRENT_ROW -> doSelectFromDialog(table.getSelectedRow() as Int, false, false)
      SelectionTarget.NEXT_ROW -> table.select(nextItemId)
      SelectionTarget.PREVIOUS_ROW -> table.select(prevItemId)
      SelectionTarget.NEXT_PAGE -> table.select(nextPageItemId)
      SelectionTarget.PREVIOUS_PAGE -> table.select(prevPageItemId)
      SelectionTarget.FIRST_ROW -> table.select(table.getContainerDataSource().firstItemId())
      SelectionTarget.LAST_ROW -> table.select(table.getContainerDataSource().lastItemId())
      else -> {
      }
    }
  }

  fun onSearch(event: SearchEvent) {
    if (!table.getContainerDataSource().hasFilters()) {
      if (event.getPattern() == null || event.getPattern().length() === 0) {
        ensureTableSelection()
      } else {
        val itemId: Any
        itemId = table.search(event.getPattern())
        if (itemId != null) {
          table.select(itemId)
        }
      }
    }
  }

  /**
   * Ensures that a row is selected in the list dialog table.
   * The selected row will be set to the first visible row when
   * the selected row is null
   */
  protected fun ensureTableSelection() {
    if (table.getSelectedRow() == null) {
      table.select(table.getContainerDataSource().firstItemId())
    }
  }

  /**
   * Returns the next item ID according to the currently selected one.
   * @return The next item ID according to the currently selected one.
   */
  protected val nextItemId: Int
    get() = if (table.getSelectedRow() as Int == table.getContainerDataSource().lastItemId()) {
      table.getContainerDataSource().lastItemId()
    } else {
      table.getContainerDataSource().nextItemId(table.getSelectedRow())
    }

  /**
   * Returns the previous item ID according to the currently selected one.
   * @return The previous item ID according to the currently selected one.
   */
  protected val prevItemId: Int
    get() = if (table.getSelectedRow() as Int == table.getContainerDataSource().firstItemId()) {
      table.getContainerDataSource().firstItemId()
    } else {
      table.getContainerDataSource().prevItemId(table.getSelectedRow())
    }

  /**
   * Looks for the next page item ID starting from the selected row.
   * @return The next page item ID.
   */
  protected val nextPageItemId: Int
    get() {
      var nextPageItemId: Int
      nextPageItemId = table.getSelectedRow()
      var i = 0
      while (i < 20 && nextPageItemId != table.getContainerDataSource().lastItemId()) {
        nextPageItemId = table.getContainerDataSource().nextItemId(nextPageItemId)
        i++
      }
      return nextPageItemId
    }

  /**
   * Looks for the previous page item ID starting from the selected row.
   * @return The previous page item ID.
   */
  protected val prevPageItemId: Int
    get() {
      var prevPageItemId: Int
      prevPageItemId = table.getSelectedRow()
      var i = 0
      while (i < 20 && prevPageItemId != table.getContainerDataSource().firstItemId()) {
        prevPageItemId = table.getContainerDataSource().prevItemId(prevPageItemId)
        i++
      }
      return prevPageItemId
    }
  //------------------------------------------------------
  // UTILS
  //------------------------------------------------------
  /**
   * Handles the client response after thread release.
   * @return The selected position.
   */
  protected fun handleClientResponse(): Int {
    if (escaped) {
      return -1
    } else if (doNewForm) {
      return try {
        doNewForm(model.form, model.newForm)
      } catch (e: VException) {
        throw VRuntimeException(e)
      }
    } else if (selectedPos != -1) {
      return model.convert(selectedPos)
    }
    return -1 // in all other cases return -1 indicating no choice.
  }

  /**
   * Displays a window to insert a new record
   * @param form The [VForm] instance.
   * @param cstr The class path.
   * @return The selected item.
   * @throws VException Visual errors.
   */
  protected fun doNewForm(form: VForm?, cstr: VDictionary?): Int {
    return if (form != null && cstr != null) {
      cstr.add(form)
    } else {
      VListDialog.NEW_CLICKED
    }
  }

  /**
   * Prepares the dialog content.
   */
  protected fun prepareDialog() {
    table = ListTable(model)
    setTable(table)
    table.select(table.getContainerDataSource().firstItemId())
    table.addItemClickListener(object : ItemClickListener() {
      fun itemClick(event: ItemClickEvent) {
        doSelectFromDialog(event.getItemId() as Int, false, false)
      }
    })
    table.addSelectionListener(object : SelectionListener() {
      fun select(event: SelectionEvent) {
        if (!event.getSelected().isEmpty()) {
          table.scrollTo(event.getSelected().toArray().get(0))
        }
      }
    })
    table.addColumnReorderListener(object : ColumnReorderListener() {
      fun columnReorder(event: ColumnReorderEvent?) {
        sort()
      }
    })
    // set the new button if needed.
    if (model.newForm != null || model.isForceNew) {
      setNewText(VlibProperties.getString("new-record"))
    }
  }

  /**
   * Shows the dialog and wait until it is closed from client side.
   */
  protected fun showDialogAndWait() {
    //BackgroundThreadHandler.startAndWait(Runnable { TODO
      application.attachComponent(this@DListDialog)
    //}, this)
  }

  /**
   * Returns the current application instance.
   * @return Tshe current application instance.
   */
  protected val application: VApplication
    get() = ApplicationContext.applicationContext.getApplication() as VApplication

  /**
   * Handles the too many rows case.
   * This will show a user notification.
   */
  protected fun handleTooManyRows() {
    val lock = Any()
    val notice = InformationNotification(VlibProperties.getString("Notice"),
                                         MessageCode.getMessage("VIS-00028"),
                                         application.defaultLocale.toString())

    notice.show()
  }

  /**
   * Confirms the user selection and closes the list.
   * @param selectedPos The selected position.
   * @param escaped Was the list escaped ?
   * @param doNewForm Should we do a new dictionary form ?
   */
  protected fun doSelectFromDialog(selectedPos: Int, escaped: Boolean, doNewForm: Boolean) {
    this.selectedPos = selectedPos
    this.escaped = escaped
    this.doNewForm = doNewForm
    application.detachComponent(this)
    // BackgroundThreadHandler.releaseLock(this) // release the background thread lock. TODO
  }

  /**
   * Bubble sort the columns from right to left
   */
  private fun sort() {
    var left = 0
    var sel = -1
    if (table != null) {
      sel = if (table.getSelectedRow() != null) {
        table.getSelectedRow()
      } else {
        0
      }
      left = table.getColumns().get(0).getPropertyId()
    }
    model.sort(left)
    if (table != null) {
      table.tableChanged()
      table.select(sel)
    }
  }

  //------------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------------
  private val model: VListDialog
  private var table: ListTable? = null
  private var escaped = true
  private var doNewForm = false
  private var selectedPos = -1
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates a new `DListDialog` instance.
   * @param model The list dialog model.
   */
  init {
    setImmediate(true)
    this.model = model
    addCloseListener(this)
    addSelectionListener(this)
    addSearchListener(this)
  }
}
