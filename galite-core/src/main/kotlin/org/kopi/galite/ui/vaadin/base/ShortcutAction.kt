/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

import org.kopi.galite.ui.vaadin.field.VDateField
import org.kopi.galite.ui.vaadin.field.VTimeField
import org.kopi.galite.ui.vaadin.field.VTimeStampField

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.Shortcuts
import com.vaadin.flow.server.Command

/**
 * A shortcut action represented by its key code and modifiers.
 *
 * @param key The action key code.
 * @param modifiers The action modifiers key.
 * @param navigationAction lambda representing the action to perform
 */
abstract class ShortcutAction<T: Component>(
  protected val field: T,
  private val key: Key,
  private val modifiers: Array<out KeyModifier>,
  protected val navigationAction: () -> Unit
): Command {

  override fun execute() {
    performAction()
  }

  /**
   * Performs the action handled by this shortcut.
   */
  abstract fun performAction()

  fun registerShortcut() {
    val registration = Shortcuts.addShortcutListener(
      field,
      this,
      key,
      *modifiers
    ).listenOn(field)

    registration.isBrowserDefaultAllowed = false
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
