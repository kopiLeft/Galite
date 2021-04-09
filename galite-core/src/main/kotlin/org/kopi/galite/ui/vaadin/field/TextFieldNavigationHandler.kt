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
package org.kopi.galite.ui.vaadin.field

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier

/**
 * A key navigation handler for a text input.
 *
 * @param isMulti is it a multiple line text field ?
 */
class TextFieldNavigationHandler protected constructor(private val isMulti: Boolean) : ShortcutActionHandler() {
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Creates the navigation actions.
   * @param field The text field.
   */
  protected fun createNavigatorKeys(field: InputTextField<*>) {
    addKeyNavigator(field, KeyNavigator.KEY_EMPTY_FIELD, Key.ENTER, KeyModifier.of("Control"))
    addKeyNavigator(field, KeyNavigator.KEY_NEXT_BLOCK, Key.ENTER, KeyModifier.of("Shift"))
    addKeyNavigator(field, KeyNavigator.KEY_DIAMETER, Key.KEY_D, KeyModifier.of("Control"))
    addKeyNavigator(field, KeyNavigator.KEY_REC_DOWN, Key.PAGE_DOWN)
    addKeyNavigator(field, KeyNavigator.KEY_REC_DOWN, Key.PAGE_DOWN, KeyModifier.of("Shift"))
    addKeyNavigator(field, KeyNavigator.KEY_REC_FIRST, Key.HOME, KeyModifier.of("Shift"))
    addKeyNavigator(field, KeyNavigator.KEY_REC_LAST, Key.END, KeyModifier.of("Shift"))
    addKeyNavigator(field, KeyNavigator.KEY_REC_UP, Key.PAGE_UP)
    addKeyNavigator(field, KeyNavigator.KEY_REC_UP, Key.PAGE_UP, KeyModifier.of("Shift"))
    addKeyNavigator(field, KeyNavigator.KEY_PREV_FIELD, Key.ARROW_LEFT, KeyModifier.of("Control"))
    addKeyNavigator(field, KeyNavigator.KEY_PREV_FIELD, Key.TAB, KeyModifier.of("Shift"))
    addKeyNavigator(field, KeyNavigator.KEY_PREV_FIELD, Key.ARROW_UP, KeyModifier.of("Shift"))
    addKeyNavigator(field, KeyNavigator.KEY_NEXT_FIELD, Key.ARROW_RIGHT, KeyModifier.of("Control"))
    addKeyNavigator(field, KeyNavigator.KEY_NEXT_FIELD, Key.TAB)
    addKeyNavigator(field, KeyNavigator.KEY_NEXT_FIELD, Key.ARROW_DOWN, KeyModifier.of("Shift"))
    addKeyNavigator(field, KeyNavigator.KEY_PRINTFORM, Key.PRINT_SCREEN, KeyModifier.of("Shift"))
    // the magnet card reader sends a CNTR-J as last character
    addKeyNavigator(field, KeyNavigator.KEY_NEXT_FIELD, Key.KEY_J, KeyModifier.of("Control"))
    addKeyNavigator(field, KeyNavigator.KEY_ESCAPE, Key.ESCAPE)
    addKeyNavigator(field, KeyNavigator.KEY_NEXT_VAL, Key.ARROW_DOWN, KeyModifier.of("Control"))
    addKeyNavigator(field, KeyNavigator.KEY_PREV_VAL, Key.ARROW_UP, KeyModifier.of("Control"))
    if (!isMulti) {
      // In multiline fields these keys are used for other stuff
      addKeyNavigator(field, KeyNavigator.KEY_PREV_FIELD, Key.ARROW_UP)
      addKeyNavigator(field, KeyNavigator.KEY_NEXT_FIELD, Key.ARROW_DOWN)
      addKeyNavigator(field, KeyNavigator.KEY_NEXT_FIELD, Key.ENTER)
    }
  }

  /**
   * Adds a key navigator action to this handler.
   * @param field The input text.
   * @param code The navigator code.
   * @param key The key code.
   * @param modifiers The modifiers.
   */
  protected fun addKeyNavigator(
    field: InputTextField<*>,
    code: Int,
    key: Key,
    vararg modifiers: KeyModifier
  ) {
    addAction(KeyNavigator(field, code, key, modifiers))
  }

  companion object {
    //---------------------------------------------------
    // STATIC UTILS
    //---------------------------------------------------
    /**
     * Statically create a new navigation handler for text fields.
     * @param field The text field.
     * @param isMulti Is it a multiple line field ?
     * @param hasAutocomplete if the field has the auto complete feature.
     * @return The navigation handler instance.
     */
    fun newInstance(
      field: InputTextField<*>,
      isMulti: Boolean
    ): TextFieldNavigationHandler {
      val handler = TextFieldNavigationHandler(isMulti)
      handler.createNavigatorKeys(field)
      return handler
    }
  }
}
