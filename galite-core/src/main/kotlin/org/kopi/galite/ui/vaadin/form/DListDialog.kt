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
import org.kopi.galite.ui.vaadin.list.ListTable
import org.kopi.galite.ui.vaadin.notif.InformationNotification
import org.kopi.galite.ui.vaadin.notif.NotificationListener
import org.kopi.galite.ui.vaadin.visual.VApplication
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.UWindow
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VRuntimeException
import org.kopi.galite.visual.VlibProperties

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.data.selection.SelectionListener


/**
 * The `DListDialog` is the vaadin implementation of the
 * [UListDialog] specifications.
 *
 * @param model The list dialog model.
 */
class DListDialog(private val model: VListDialog)
  : GridListDialog(), UListDialog/*, CloseListener, SelectionListener, SearchListener TODO*/ {

  val table: ListTable? = ListTable(model)
  private var escaped = true
  private var doNewForm = false
  private var selectedPos = -1

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
        showRelativeTo(field)
      } else if (field is DGridEditorField<*>) {
        showRelativeTo(field.editor)
      }
    }
    showDialogAndWait()
    return handleClientResponse()
  }

  override fun selectFromDialog(window: UWindow, showSingleEntry: Boolean): Int =
          selectFromDialog(window, null, showSingleEntry)

  /**
   * Returns the previous item ID according to the currently selected one.
   * @return The previous item ID according to the currently selected one.
   */
  protected val prevItemId: Int
    get() = if (table!!.headerRows as Int == table!!.dataProvider.items.indices.first) {
      table!!.dataProvider.items.indices.first
    } else {
      table!!.dataProvider.items.indexOf(table!!.selectedItems as Int - 1)
    }

  /**
   * Looks for the next page item ID starting from the selected row.
   * @return The next page item ID.
   */
  protected val nextPageItemId: Int
    get() {
      var nextPageItemId: Int
      nextPageItemId = table!!.headerRows as Int
      var i = 0
      while (i < 20 && nextPageItemId != table!!.dataProvider.items.indices.last) {
        nextPageItemId = table!!.dataProvider.items.indexOf(nextPageItemId + 1)
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
      prevPageItemId = table!!.selectedItems as Int
      var i = 0
      while (i < 20 && prevPageItemId != table!!.dataProvider.items.indices.first) {
        prevPageItemId = table!!.dataProvider.items.indexOf(prevPageItemId - 1)
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
  protected fun handleClientResponse(): Int =
          when {
            escaped -> -1
            doNewForm -> {
              try {
                doNewForm(model.form, model.newForm)
              } catch (e: VException) {
                throw VRuntimeException(e)
              }
            }
            selectedPos != -1 -> model.convert(selectedPos)

            else -> -1 // in all other cases return -1 indicating no choice.
          }

  /**
   * Displays a window to insert a new record
   * @param form The [VForm] instance.
   * @param cstr The class path.
   * @return The selected item.
   * @throws VException Visual errors.
   */
  protected fun doNewForm(form: VForm?, cstr: VDictionary?): Int =
          if (form != null && cstr != null) {
            cstr.add(form)
          } else {
            VListDialog.NEW_CLICKED
          }

  /**model
   * Prepares the dialog content.
   */
  protected fun prepareDialog() {
    setTable(table!!)
    table!!.selectionModel.selectFromClient(table!!.dataCommunicator.keyMapper.get(table!!.dataProvider.items.indices.first.toString()))
    table!!.addItemClickListener { event ->
      doSelectFromDialog((event!!.item as Int), false, false)
    }
    table!!.addSelectionListener(SelectionListener<Grid<VListDialog>, VListDialog> { event ->
      if (!event.firstSelectedItem.isEmpty) {
        table!!.scrollToIndex(event.firstSelectedItem.hashCode())
      }
    })
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

    notice.addNotificationListener(object : NotificationListener {
      override fun onClose(yes: Boolean) {
        application.detachComponent(notice)
        //BackgroundThreadHandler.releaseLock(lock)
      }
    })
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
      sel = if (table!!.selectedItems != null) {
        table!!.selectedItems as Int
      } else {
        0
      }
      left = table!!.columns[0].key.toInt()
    }
    model.sort(left)
    if (table != null) {
      table!!.tableChanged()
      table!!.selectionModel.selectFromClient(table!!.dataCommunicator.keyMapper.get(sel.toString()))
    }
  }

  init {
    //setItems(table!!.dataProvider.items)
    add(table)
    // addCloseListener(this) TODO
    // addSelectionListener(this) TODO
    // addSearchListener(this) TODO
  }

  fun onClose() {
    doSelectFromDialog(-1, escaped, doNewForm)
  }

  /**
   * Ensures that a row is selected in the list dialog table.
   * The selected row will be set to the first visible row when
   * the selected row is null
   */
  protected fun ensureTableSelection() {
    /*  if (table.se() == null) {
        table!!.select(table.getContainerDataSource().firstItemId())
      }*/
  }

  fun selectionChange() {
    /* if (table!!.dataProvider.items.isEmpty()) {
      return
    }

    ensureTableSelection()
    when (event) {
      CURRENT_ROW -> doSelectFromDialog((table.getSelectedRow() as Int?)!!, false, false)
      NEXT_ROW -> table.select(getNextItemId())
      PREVIOUS_ROW -> table.select(prevItemId)
      NEXT_PAGE -> table.select(nextPageItemId)
      PREVIOUS_PAGE -> table.select(getPrevPageItemId())
      FIRST_ROW -> table.select(table.getContainerDataSource().firstItemId())
      LAST_ROW -> table.select(table.getContainerDataSource().lastItemId())
      else -> {
      }
    }*/
  }

  fun selectionChange(event: com.vaadin.flow.data.selection.SelectionEvent<DListDialog, com.vaadin.flow.data.selection.SelectionEvent<DListDialog, ListTable>>?) {
/*    if (table!!.dataProvider.items.isEmpty()) {
      return
    }

    ensureTableSelection()
    when (event) {
      CURRENT_ROW -> doSelectFromDialog((table.getSelectedRow() as Int?)!!, false, false)
      NEXT_ROW -> table.select(getNextItemId())
      PREVIOUS_ROW -> table.select(prevItemId)
      NEXT_PAGE -> table.select(nextPageItemId)
      PREVIOUS_PAGE -> table.select(getPrevPageItemId())
      FIRST_ROW -> table.select(table.getContainerDataSource().firstItemId())
      LAST_ROW -> table.select(table.getContainerDataSource().lastItemId())
      else -> {
      }
    }*/
  }
}
