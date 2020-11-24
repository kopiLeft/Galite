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
package org.kopi.galite.common

import java.awt.event.InputEvent
import java.awt.event.KeyEvent

/**
 * This class represents an actor, ie a menu element with a name and may be an icon, a shortcut
 * and a help
 *
 * @param ident               the ident
 * @param menu                the containing menu
 * @param label               the label
 * @param help                the help
 * @param icon                the icon
 * @param key                 the shortcut
 */
class Actor(val ident: String, val menu: String, val label: String, val help: String, var key: String? = null) {

  var icon: String? = null

  init {
    checkKey(key)
  }

  private fun checkKey(key : String?) {
    if (key == null) {
      keyModifier = 0
      keyCode = KeyEvent.VK_UNDEFINED
    } else {
      val baseKey: String
      if (key.indexOf("Shift-") == -1) {
        baseKey = key
        keyModifier = 0
      } else {
        baseKey = key.substring(key.indexOf("Shift-") + "Shift-".length)
        keyModifier = InputEvent.SHIFT_MASK
      }
      when (baseKey) {
        "F1" -> {
          keyCode = KeyEvent.VK_F1
        }
        "F2" -> {
          keyCode = KeyEvent.VK_F2
        }
        "F3" -> {
          keyCode = KeyEvent.VK_F3
        }
        "F4" -> {
          keyCode = KeyEvent.VK_F4
        }
        "F5" -> {
          keyCode = KeyEvent.VK_F5
        }
        "F6" -> {
          keyCode = KeyEvent.VK_F6
        }
        "F7" -> {
          keyCode = KeyEvent.VK_F7
        }
        "F8" -> {
          keyCode = KeyEvent.VK_F8
        }
        "F9" -> {
          keyCode = KeyEvent.VK_F9
        }
        "F10" -> {
          keyCode = KeyEvent.VK_F10
        }
        "F11" -> {
          keyCode = KeyEvent.VK_F11
        }
        "F12" -> {
          keyCode = KeyEvent.VK_F12
        }
        "esc" -> {
          keyCode = KeyEvent.VK_ESCAPE
        }
        else -> {
          keyModifier = KeyEvent.VK_UNDEFINED
          keyCode = KeyEvent.VK_UNDEFINED
        }
      }
    }
  }
  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------
  /**
   * !!!FIX:taoufik
   */
  fun genLocalization(writer: LocalizationWriter) {
    writer.genActorDefinition(ident, label, help)
  }

  var keyCode = 0
  var keyModifier = 0
}
