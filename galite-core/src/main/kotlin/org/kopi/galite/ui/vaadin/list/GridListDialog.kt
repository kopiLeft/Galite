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
package org.kopi.galite.ui.vaadin.list

import java.lang.reflect.Method

import org.kopi.galite.ui.vaadin.base.VInputButton
import org.kopi.galite.ui.vaadin.main.MainWindow
import org.kopi.galite.ui.vaadin.base.Styles

import com.vaadin.componentfactory.EnhancedDialog
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyDownEvent
import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.KeyPressEvent
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.data.selection.SelectionListener

/**
 * A list dialog
 */
open class GridListDialog : Div(),KeyNotifier {

  private var newForm: VInputButton? = null
  private var reference: Component? = null
  private var pattern: String? = null
  private val popup = EnhancedDialog()

  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------

  /**
   * Sets the grid component for this list.
   * @param table The grid component.
   */
  open fun setTable(table: ListTable?) {
    add(table)
  }

  /**
   * The list dialog selection target.
   */
  enum class SelectionTarget {
    /**
     * Selects the current row and close the list.
     */
    CURRENT_ROW,

    /**
     * Navigates to the next row.
     */
    NEXT_ROW,

    /**
     * Navigates to the previous row.
     */
    PREVIOUS_ROW,

    /**
     * Navigates to the next page.
     */
    NEXT_PAGE,

    /**
     * Navigates to the previous page.
     */
    PREVIOUS_PAGE,

    /**
     * Navigates to the first row.
     */
    FIRST_ROW,

    /**
     * Navigates to the last row.
     */
    LAST_ROW
  }

  /**
   * The list dialog selection event.
   */
  class SelectionEvent(source: Component?, val target: SelectionTarget)
  // : com.vaadin.flow.data.selection.SelectionEvent(source,target)
  {

    //---------------------------------------------------
    // ACCESSORS
    //---------------------------------------------------

    companion object {
      var SELECT_METHOD: Method? = null

      init {
        try {
          // Set the header click method
          SELECT_METHOD = SelectionListener::class.java.getDeclaredMethod("onSelection", SelectionEvent::class.java)
        } catch (e: NoSuchMethodException) {
          // This should never happen
          throw RuntimeException(e)
        }
      }
    }
  }

  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------

  open fun onKeyDown(event: KeyDownEvent) {
    if (event.code == Key.BACKSPACE) {
      if (pattern != null && pattern!!.length > 1) {
        pattern = pattern!!.substring(0, pattern!!.length - 1)
      } else {
        pattern = ""
      }
    }
    doKeyAction(event.key)
  }

  /**
   * Allows access to the key events.
   * @param keyCode The key code.
   */
  protected open fun doKeyAction(keyCode: Key) {
    /* when (keyCode) {
      Key.HOME -> {
        pattern = ""
        select(SelectionTarget.FIRST_ROW)
      }
      Key.END -> {
        pattern = ""
        fireSelection(SelectionTarget.LAST_ROW)
      }
      Key.ARROW_UP -> {
        pattern = ""
        fireSelection(SelectionTarget.PREVIOUS_ROW)
      }
      Key.ARROW_DOWN -> {
        pattern = ""
        fireSelection(SelectionTarget.NEXT_ROW)
      }
      Key.PAGE_UP -> {
        pattern = ""
        fireSelection(SelectionTarget.PREVIOUS_PAGE)
      }
      Key.PAGE_DOWN -> {
        pattern = ""
        fireSelection(SelectionTarget.NEXT_PAGE)
      }
      Key.SPACE -> if (newForm != null) {
        fireClosed(false, true)
      }
      Key.ENTER -> fireSelection(SelectionTarget.CURRENT_ROW)
      Key.ESCAPE -> fireClosed(true, false)
    }*/
  }

  open fun onKeyPress(event: KeyPressEvent) {
    val c: Char = event.code.toString()[0]
    if (pattern == null) {
      pattern = ""
    }
    if (c.toInt() != 0) {
      pattern += ("" + c).toLowerCase()[0]
    }
    fireEvent(event)
  }

  open fun showRelativeTo(reference: Component) {
    this.reference = reference
  }

  /**
   * Shows the list dialog.
   * @param parent The parent where to attach the list.
   */
  open fun show(parent: MainWindow) {
    if (popup != null) {
      parent.addWindow(popup, "")
      show(parent)
    }
  }

  /**
   * This is used to display a new button under the dialog.
   * No button will be drawn when it is `null`.
   * Sets the new text widget.
   * @param newText The new text widget.
   */
  open fun setNewText(newText: String?) {
    if (newText != null) {
      newForm = VInputButton(newText)
      newForm!!.element.text = newText
      newForm!!.className = "new-button"
      newForm!!.width = "100%" // occupy all available space.
    }
  }

  init {
    element.setAttribute("hideFocus", "true")
    element.style["outline"] = "0px"
    className = Styles.LIST_DIALOG
    addKeyDownListener { event -> onKeyDown(event) }
    addKeyPressListener { event -> onKeyPress(event) }
  }

}
