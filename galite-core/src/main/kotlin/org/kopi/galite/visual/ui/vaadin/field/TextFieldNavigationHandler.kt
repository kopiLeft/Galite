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
package org.kopi.galite.visual.ui.vaadin.field

import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.base.Utils
import org.kopi.galite.visual.ui.vaadin.base.addJSKeyDownListener

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.UI

/**
 * A key navigation handler for a text input.
 *
 * @param isMulti is it a multiple line text field ?
 */
open class TextFieldNavigationHandler protected constructor(private val isMulti: Boolean) {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Creates the navigation actions.
   * @param field The text field.
   */
  internal fun createNavigatorKeys(field: InputTextField<*>) {
    addKeyNavigator(field, Key.ENTER, KeyModifier.of("Control")) {
      field.fieldConnector.gotoNextEmptyMustfill()
    }
    addKeyNavigator(field, Key.ENTER, KeyModifier.of("Shift")) {
      field.connector.fireGotoNextBlock()
    }
    addKeyNavigator(field, Key.KEY_D, KeyModifier.of("Control")) {
      val ui = UI.getCurrent()

      Thread {
        val text = StringBuffer(field.value)
        println("Thread text :: "+text)
        println("Thread field.value :: "+field.value)
        text.insert(Utils.getCursorPos(field), "\u00D8")
        access(ui) {
          field.value = text.toString()
        }
      }.start()
    }
    addKeyNavigator(field, Key.PAGE_DOWN) {
      field.fieldConnector.gotoNextRecord()
    }
    addKeyNavigator(field, Key.PAGE_DOWN, KeyModifier.of("Shift")) {
      field.fieldConnector.gotoNextRecord()
    }
    addKeyNavigator(field, Key.HOME, KeyModifier.of("Shift")) {
      field.fieldConnector.gotoFirstRecord()
    }
    addKeyNavigator(field, Key.END, KeyModifier.of("Shift")) {
      field.fieldConnector.gotoLastRecord()
    }
    addKeyNavigator(field, Key.PAGE_UP) {
      field.fieldConnector.gotoPrevRecord()
    }
    addKeyNavigator(field, Key.PAGE_UP, KeyModifier.of("Shift")) {
      field.fieldConnector.gotoPrevRecord()
    }
    addKeyNavigator(field, Key.ARROW_LEFT, KeyModifier.of("Control")) {
      field.fieldConnector.gotoPrevField()
    }
    addKeyNavigator(field, Key.TAB, KeyModifier.of("Shift")) {
      field.fieldConnector.gotoPrevField()
    }
    addKeyNavigator(field, Key.ARROW_UP, KeyModifier.of("Shift")) {
      field.fieldConnector.gotoPrevField()
    }
    addKeyNavigator(field, Key.ARROW_RIGHT, KeyModifier.of("Control")) {
      field.fieldConnector.gotoNextField()
    }
    addKeyNavigator(field, Key.TAB) {
      field.fieldConnector.gotoNextField()
    }
    addKeyNavigator(field, Key.ARROW_DOWN, KeyModifier.of("Shift")) {
      field.fieldConnector.gotoNextField()
    }
    addKeyNavigator(field, Key.PRINT_SCREEN, KeyModifier.of("Shift")) {
      field.connector.firePrintForm()
    }
    // the magnet card reader sends a CTRL-J as last character
    addKeyNavigator(field, Key.KEY_J, KeyModifier.of("Control")) {
      field.fieldConnector.gotoNextField()
    }
    addKeyNavigator(field, Key.ARROW_DOWN, KeyModifier.of("Control")) {
      field.connector.fireNextEntry()
    }
    addKeyNavigator(field, Key.ARROW_UP, KeyModifier.of("Control")) {
      field.connector.firePreviousEntry()
    }
    if (!isMulti) {
      // In multiline fields these keys are used for other stuff
      addKeyNavigator(field, Key.ARROW_UP) {
        field.fieldConnector.gotoPrevField()
      }
      addKeyNavigator(field, Key.ARROW_DOWN) {
        field.fieldConnector.gotoNextField()
      }
      addKeyNavigator(field, Key.ENTER) {
        field.fieldConnector.gotoNextField()
      }
    }
  }

  /**
   * Adds a key navigator action to this handler.
   * @param field The input text.
   * @param key The key code.
   * @param modifiers The modifiers.
   * @param navigationAction lambda representing the action to perform
   */
  private fun addKeyNavigator(
    field: InputTextField<*>,
    key: Key,
    vararg modifiers: KeyModifier,
    navigationAction: () -> Unit,
  ) {
    val keyNavigator = KeyNavigator(field, key, modifiers, navigationAction)
    val navigatorKey = keyNavigator.getKey()

    field.keyNavigators[navigatorKey] = keyNavigator
  }

  companion object {
    //---------------------------------------------------
    // STATIC UTILS
    //---------------------------------------------------
    /**
     * Statically create a new navigation handler for text fields.
     * @param field The text field.
     * @param isMulti Is it a multiple line field ?
     * @return The navigation handler instance.
     */
    fun createNavigator(
      field: InputTextField<*>,
      isMulti: Boolean
    ): TextFieldNavigationHandler {
      val handler = TextFieldNavigationHandler(isMulti)

      handler.createNavigatorKeys(field)
      field.addJSKeyDownListener(field.keyNavigators)
      return handler
    }
  }
}
