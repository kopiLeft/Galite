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
 *
 * @param model The list dialog model.
 */
class DListDialog(
        private val model: VListDialog
) : GridListDialog(), UListDialog/*, CloseListener, SelectionListener, SearchListener TODO*/ {

  private var table: ListTable? = null
  private var escaped = true
  private var doNewForm = false
  private var selectedPos = -1

  init {
    // addCloseListener(this) TODO
    // addSelectionListener(this) TODO
    // addSearchListener(this) TODO
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

  override fun selectFromDialog(window: UWindow, showSingleEntry: Boolean): Int =
          selectFromDialog(window, null, showSingleEntry)

  /**
   * Returns the previous item ID according to the currently selected one.
   * @return The previous item ID according to the currently selected one.
   */
  protected val prevItemId: Int
    get() = TODO()

  /**
   * Looks for the next page item ID starting from the selected row.
   * @return The next page item ID.
   */
  protected val nextPageItemId: Int
    get() {
      TODO()
    }

  /**
   * Looks for the previous page item ID starting from the selected row.
   * @return The previous page item ID.
   */
  protected val prevPageItemId: Int
    get() {
      TODO()
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

  /**
   * Prepares the dialog content.
   */
  protected fun prepareDialog() {
    // TODO
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
    // TODO
  }
}
