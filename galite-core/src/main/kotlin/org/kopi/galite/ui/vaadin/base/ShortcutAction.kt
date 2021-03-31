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
package org.kopi.galite.ui.vaadin.base

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier

/**
 * A shortcut action represented by its key code and modifiers.
 *
 * @param caption The action caption.
 * @param key The action key code.
 * @param modifiers The action modifiers key.
 */
abstract class ShortcutAction(protected val caption: String,
                              protected val key: Key,
                              vararg modifiers: KeyModifier
) {

  protected val modifierMask: Int = createModifierMask(modifiers.toList())

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Performs the action handled by this shortcut.
   */
  abstract fun performAction()

  /**
   * Creates the action key.
   * @return The action key.
   */
  fun getKey(): String {
    // key is based on key & modifiers
    return createKey(key, modifierMask)
  }

  companion object {
    /**
     * Creates a unique key for a key code and a modifier mask.
     *
     * @param keyCode The key code.
     * @param modifierMask The modifier mask
     * @return The unique key.
     */
    fun createKey(keyCode: Key, modifierMask: Int): String = "${keyCode.keys}-$modifierMask"

    /**
     * Creates the modifier mask of this shortcut action.
     * @param modifiers The modifiers key.
     * @return The modifier mask to be used.
     */
    fun createModifierMask(modifiers: Collection<KeyModifier>?): Int {
      var modifiersMask = 0
      modifiers?.forEach {
        modifiersMask = modifiersMask or it.ordinal
      }
      return modifiersMask
    }
  }
}
