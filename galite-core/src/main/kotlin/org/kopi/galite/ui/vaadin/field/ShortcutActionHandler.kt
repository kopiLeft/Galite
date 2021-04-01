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

import org.kopi.galite.ui.vaadin.base.ShortcutAction

import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.KeyDownEvent

/**
 * A shortcut action handler based on key down event.
 */
open class ShortcutActionHandler : ComponentEventListener<KeyDownEvent> {

  private val actions: MutableMap<String, ShortcutAction> = mutableMapOf()

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Handles the action of a given key down event.
   * @param event The key down event.
   */
  fun handleAction(event: KeyDownEvent) {
    val key = ShortcutAction.createKey(event.key, ShortcutAction.createModifierMask(event.modifiers))

    val action: ShortcutAction? = actions[key]
    if (action != null) {
      //event.preventDefault() // prevent default action. TODO
      action.performAction()
    }
  }

  override fun onComponentEvent(event: KeyDownEvent) {
    handleAction(event)
  }
  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  /**
   * Adds an action to this handler.
   * @param action the action.
   */
  fun addAction(action: ShortcutAction) {
    actions[action.getKey()] = action
  }

  companion object {
    /**
     * Gets the keyboard modifiers associated with a DOMEvent.
     *
     * @param event the event.
     * @return the modifiers as defined in [KeyboardListener].
     */
    fun getKeyboardModifiers(event: KeyDownEvent): Int {
      return ((if (event.equals("event.shiftKey")) 1 else 0)
              or (if (event.equals("event.metaKey")) 8 else 0)
              or (if (event.equals("event.ctrlKey")) 2 else 0)
              or if (event.equals("event.altKey")) 4 else 0)
    }
  }
}
