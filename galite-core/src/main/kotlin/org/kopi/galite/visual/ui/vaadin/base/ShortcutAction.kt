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
package org.kopi.galite.visual.ui.vaadin.base

import org.kopi.galite.visual.ui.vaadin.field.VDateField
import org.kopi.galite.visual.ui.vaadin.field.VTimeField
import org.kopi.galite.visual.ui.vaadin.field.VTimeStampField

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier

/**
 * A shortcut action represented by its key code and modifiers.
 *
 * @param key The action key code.
 * @param modifiers The action modifiers key.
 * @param navigationAction lambda representing the action to perform
 */
abstract class ShortcutAction<T: Component>(
  protected val field: T,
  internal val key: Key,
  internal val modifiers: Array<out KeyModifier>,
  protected val navigationAction: () -> Unit
) {
  protected val modifierMask = createModifierMask(modifiers)

  /**
   * Performs the action handled by this shortcut.
   */
  abstract fun performAction(eagerValue: String?)

  /**
   * Creates the action key.
   * @return The action key.
   */
  fun getKey(): String {
    // key is based on key & modifiers
    return createKey(key.keys, modifierMask)
  }

  companion object {

    /**
     * Creates a unique key for a key code and a modifier mask.
     * @param keys The keys.
     * @param modifierMask The modifier mask
     * @return The unique key.
     */
    fun createKey(keys: List<String>, modifierMask: Int): String {
      val stringKeys  = keys.joinToString(separator = "-")
      return "$stringKeys-$modifierMask"
    }

    /**
     * Creates the modifier mask of this shortcut action.
     * @return The modifier mask to be used.
     */
    fun createModifierMask(modifiers: Array<out KeyModifier>): Int {
      var modifiersMask = 0
      for (i in modifiers.indices) {
        when (modifiers[i]) {
          KeyModifier.of("Shift") -> modifiersMask = modifiersMask or 1
          KeyModifier.of("Control") -> modifiersMask = modifiersMask or 2
          KeyModifier.of("Alt") -> modifiersMask = modifiersMask or 3
          KeyModifier.of("AltGraph") -> modifiersMask = modifiersMask or 4
          KeyModifier.of("Meta") -> modifiersMask = modifiersMask or 5
          else -> {
          }
        }
      }
      return modifiersMask
    }
  }
}

fun <V> V.runAfterGetValue(function: () -> Unit) where V: Component, V: HasValue<*, *> {
  // Workaround for issue: https://github.com/vaadin/flow/issues/5959
  // Execute shortcut action when receiving the field's value using javascript call
  // The field's value is used later to check if value has been changed
  if (this is VDateField || this is VTimeField || this is VTimeStampField) {
    this.element.callJsFunction("blur").then {
      function()
    }
  } else {
    this.element.executeJs("return $0.value")
      .then {
        // Synchronize with server side
        this.value = it?.asString()
        function()
      }
  }
}
