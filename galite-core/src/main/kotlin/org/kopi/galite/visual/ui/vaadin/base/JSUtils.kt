/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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

import org.kopi.galite.visual.ui.vaadin.field.VTimeField
import org.kopi.galite.visual.ui.vaadin.field.VTimeStampField

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.KeyModifier

fun Component.addJSKeyDownListener(shortCuts: MutableMap<String, ShortcutAction<*>>) {
  val jsCall = """
      this.addEventListener("keydown", event => {
         ${keysConditions(shortCuts)}
      });
    """.trimIndent()
  element.executeJs(jsCall)
}

private fun Component.keysConditions(shortCuts: MutableMap<String, ShortcutAction<*>>): String {
  var first = true

  return buildString {
    shortCuts.forEach { (navigatorKey, keyNavigator) ->
      val key = keyNavigator.key
      val modifiers = keyNavigator.modifiers
      val keyConditions = key.keys.joinToString(" && ", prefix = "event.key ==='", postfix = "'")
      val modifiersConditions = when {
        modifiers.isEmpty() -> ""
        key.keys.isEmpty() -> modifiers.modifiersCondition()
        else -> " && " + modifiers.modifiersCondition()
      }

      val conditions = "$keyConditions $modifiersConditions"

      if (first) {
        append("if ($conditions) { this.${"$"}server.onKeyDown('$navigatorKey', ${inputValueExpression()}); event.preventDefault();}")
      } else {
        append("else if ($conditions) { this.${"$"}server.onKeyDown('$navigatorKey', ${inputValueExpression()}); event.preventDefault();}")
      }

      first = false
    }
  }
}

fun Component.inputValueExpression(): String {
  return when (this) {
    is VTimeField -> "this.inputElement.value"
    is VTimeStampField -> "this.__datePicker.inputElement.value + ' ' + this.__timePicker.inputElement.value"
    else -> "this.value"
  }
}

fun Array<out KeyModifier>.modifiersCondition(): String {
  val modifiers = this

  return buildString {
    if (KeyModifier.of("Shift") in modifiers) {
      append("event.shiftKey && ")
    } else {
      append("!event.shiftKey && ")
    }
    if (KeyModifier.of("Control") in modifiers) {
      append("event.ctrlKey && ")
    } else {
      append("!event.ctrlKey && ")
    }
    if (KeyModifier.of("Alt") in modifiers) {
      append("event.altKey && ")
    } else {
      append("!event.altKey && ")
    }
    if (KeyModifier.of("AltGraph") in modifiers) {
      append("event.altKey && ")
    } else {
      append("!event.altKey && ")
    }
    if (KeyModifier.of("Meta") in modifiers) {
      append("event.metaKey")
    } else {
      append("!event.metaKey")
    }
  }
}
