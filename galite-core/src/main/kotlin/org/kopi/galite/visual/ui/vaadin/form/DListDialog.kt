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

import org.kopi.galite.visual.form.UField
import org.kopi.galite.visual.form.UListDialog
import org.kopi.galite.visual.form.VDictionary
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.form.VListDialog
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.releaseLock
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.startAndWaitAndPush
import org.kopi.galite.visual.ui.vaadin.list.GridListDialog
import org.kopi.galite.visual.ui.vaadin.list.ListTable
import org.kopi.galite.visual.ui.vaadin.notif.InformationNotification
import org.kopi.galite.visual.ui.vaadin.notif.NotificationListener
import org.kopi.galite.visual.ui.vaadin.visual.VApplication
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.UWindow
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VRuntimeException
import org.kopi.galite.visual.VlibProperties

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyDownEvent
import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.KeyPressEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridSingleSelectionModel
import com.vaadin.flow.data.provider.ListDataProvider

/**
 * The `DListDialog` is the vaadin implementation of the
 * [UListDialog] specifications.
 *
 * @param model The list dialog model.
 */
class DListDialog(private val model: VListDialog) : GridListDialog(), KeyNotifier, UListDialog/*, CloseListener, SelectionListener, SearchListener TODO*/ {

  private var escaped = true
  private var doNewForm = false
  private var selectedPos = -1
  private val lock = Object()

  init {
    addDialogCloseActionListener(::onClose)
    addKeyDownListener(::onKeyDown)
    addKeyPressListener(::onKeyPress)
    // addSelectionListener(this) TODO
    // addSearchListener(this) TODO
    close.addClickListener {
      doSelectFromDialog(-1, true, false)
    }
  }

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
        //showRelativeTo(field as DField?) TODO
      } else if (field is DGridEditorField<*>) {
        //showRelativeTo((field as DGridEditorField<*>).getEditor()) TODO
      }
    }
    showDialogAndWait()
    return handleClientResponse()
  }

  override fun selectFromDialog(window: UWindow?, showSingleEntry: Boolean): Int =
          selectFromDialog(window, null, showSingleEntry)

  /**
   * invoked when the user clicks outside the overlay or presses the escape key.
   */
  private fun onClose(event: DialogCloseActionEvent?) {
    doSelectFromDialog(-1, true, false)
  }

  private fun onKeyDown(event: KeyDownEvent) {
    if (event.key == Key.BACKSPACE) {
      pattern = if (pattern != null && pattern!!.length > 1) {
        pattern!!.substring(0, pattern!!.length - 1)
      } else {
        ""
      }
    }
    doKeyAction(event.key)
  }

  private fun onKeyPress(event: KeyPressEvent) {
    event.key.keys.forEach {
      if (pattern == null) {
        pattern = ""
      }
      if (it.toInt() != 0) {
        pattern += it.lowercase()[0]
      }
      onSearch()
    }
  }

  /**
   * Allows access to the key events.
   * @param keyCode The key code.
   */
  private fun doKeyAction(keyCode: Key) {
    if (tableItems.isEmpty()) {
      return
    }
    ensureTableSelection()
    when (keyCode) {
      Key.HOME -> {
        pattern = ""
        table!!.select(tableItems.first())
      }
      Key.END -> {
        pattern = ""
        table!!.select(tableItems.last())
      }
      Key.ARROW_UP -> {
        pattern = ""
        table!!.select(prevItemId)
      }
      Key.ARROW_DOWN -> {
        pattern = ""
        table!!.select(nextItemId)
      }
      Key.PAGE_UP -> {
        pattern = ""
        table!!.select(prevPageItemId)
      }
      Key.PAGE_DOWN -> {
        pattern = ""
        table!!.select(nextPageItemId)
      }
      Key.SPACE -> if (newForm != null) {
        if (newForm != null) {
          doSelectFromDialog(-1, false, true)
        }
      }
      Key.ENTER -> {
        doSelectFromDialog(tableItems.indexOf(table!!.selectedItems.first()), false, false)
      }
      else -> {
      }
    }
  }

  fun onSearch() {
    /*if (!table.getContainerDataSource().hasFilters()) { TODO
      if (pattern == null || pattern.length() == 0) {
        ensureTableSelection()
      } else {
        val itemId: Any?
        itemId = table.search(pattern)
        if (itemId != null) {
          table.select(itemId)
        }
      }
    }*/
  }

  /**
   * Ensures that a row is selected in the list dialog table.
   * The selected row will be set to the first visible row when
   * the selected row is null
   */
  private fun ensureTableSelection() {
    if (table!!.selectedItems.isEmpty()) {
      table!!.select(tableItems.first())
    }
  }

  private val tableItems: Collection<ListTable.ListDialogItem>
    get() = (table!!.dataProvider as ListDataProvider<ListTable.ListDialogItem>).items

  private val nextItem: ListTable.ListDialogItem?
    get() {
      var index = tableItems.indexOf(table!!.selectedItem) + 1

      if (index >= table!!.dataCommunicator.itemCount) {
        index = 0
      }

      return table!!.dataCommunicator.getItem(index)
    }

  private val previousItem: ListTable.ListDialogItem
    get() {
      var index = tableItems.indexOf(table!!.selectedItem) - 1

      if (index < 0) {
        index = table!!.dataCommunicator.itemCount - 1
      }

      return table!!.dataCommunicator.getItem(index)
    }

  /**
   * Returns the next item ID according to the currently selected one.
   * @return The next item ID according to the currently selected one.
   */

  private val nextItemId: ListTable.ListDialogItem
    get() {
      return if (table!!.selectedItems.first() == tableItems.last()) {
        tableItems.last()
      } else {
        tableItems.elementAt(tableItems.indexOf(table!!.selectedItems.first()) + 1)
      }
    }

  /**
   * Returns the previous item ID according to the currently selected one.
   * @return The previous item ID according to the currently selected one.
   */
  private val prevItemId: ListTable.ListDialogItem
    get() {
      return if (table!!.selectedItems.first() == tableItems.first()) {
        tableItems.first()
      } else {
        tableItems.elementAt(tableItems.indexOf(table!!.selectedItems.first()) - 1)
      }
    }

  /**
   * Looks for the next page item ID starting from the selected row.
   * @return The next page item ID.
   */
  private val nextPageItemId: ListTable.ListDialogItem?
    get() {
      var nextPageItemId = table!!.selectedItems.first()
      var i = 0
      while (i < 20 && nextPageItemId != tableItems.last()) {
        nextPageItemId = tableItems.elementAt(tableItems.indexOf(nextPageItemId) + 1)
        i++
      }
      return nextPageItemId
    }

  /**
   * Looks for the previous page item ID starting from the selected row.
   * @return The previous page item ID.
   */
  private val prevPageItemId: ListTable.ListDialogItem?
    get() {
      var prevPageItemId = table!!.selectedItems.first()
      var i = 0
      while (i < 20 && prevPageItemId != tableItems.first()) {
        prevPageItemId = tableItems.elementAt(tableItems.indexOf(prevPageItemId) - 1)
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
  private fun handleClientResponse(): Int =
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
  private fun doNewForm(form: VForm?, cstr: VDictionary?): Int =
          if (form != null && cstr != null) {
            cstr.add()
          } else {
            VListDialog.NEW_CLICKED
          }

  /**
   * Prepares the dialog content.
   */
  private fun prepareDialog() {
    val table = ListTable(model)
    super.table = table
    table.select(tableItems.first())
    table.element.setAttribute("tabindex", "0")  // Make the element focusable
    (table.selectionModel as GridSingleSelectionModel).addSingleSelectionListener {
      if (it.isFromClient) {
        doSelectFromDialog(tableItems.indexOf(it.value ?: it.oldValue), false, false)
      }
    }
    table.addKeyDownListener(::onKeyDown)
    table.addColumnReorderListener {
      sort(it.columns)
    }
    // TODO
  }

  /**
   * Shows the dialog and wait until it is closed from client side.
   */
  internal fun showDialogAndWait() {
    startAndWaitAndPush(lock, currentUI) {
      showListDialog()
      table?.focus()
    }
  }

  /**
   * Returns the current application instance.
   * @return Tshe current application instance.
   */
  private val application: VApplication
    get() = ApplicationContext.applicationContext.getApplication() as VApplication

  /**
   * Handles the too many rows case.
   * This will show a user notification.
   */
  private fun handleTooManyRows() {
    val lock = Object()
    val application = application
    val notice = InformationNotification(
      VlibProperties.getString("Notice"),
      MessageCode.getMessage("VIS-00028"),
      application.defaultLocale.toString(),
      application.mainWindow
    )

    notice.addNotificationListener(object : NotificationListener {
      override fun onClose(action: Boolean?) {
        releaseLock(lock)
      }
    })
    startAndWaitAndPush(lock, currentUI) {
      notice.show()
    }
  }

  /**
   * Confirms the user selection and closes the list.
   * @param selectedPos The selected position.
   * @param escaped Was the list escaped ?
   * @param doNewForm Should we do a new dictionary form ?
   */
  private fun doSelectFromDialog(selectedPos: Int, escaped: Boolean, doNewForm: Boolean) {
    this.selectedPos = selectedPos
    this.escaped = escaped
    this.doNewForm = doNewForm
    close()
    releaseLock(lock) // release the background thread lock.
  }

  /**
   * Bubble sort the columns from right to left
   *
   * @param columns the new order of the columns
   */
  private fun sort(columns: MutableList<Grid.Column<ListTable.ListDialogItem>>) {
    var left = 0
    var sel: ListTable.ListDialogItem? = null

    if (table != null) {
      sel = table!!.selectedItem
      left = columns[0].key.toInt()
    }

    model.sort(left)

    if (table != null) {
      table!!.dataProvider.refreshAll()
      table!!.select(sel)
    }
  }

  var currentUI: UI? = null

  override fun onAttach(attachEvent: AttachEvent) {
    currentUI = attachEvent.ui
  }

  override fun isEnabled(): Boolean { return super.isEnabled() }

  override fun setEnabled(enabled: Boolean) { super.setEnabled(enabled) }
}
