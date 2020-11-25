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

import org.kopi.galite.form.dsl.KeyCode
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
class Actor(val ident: String, val menu: String, val label: String, val help: String) {
  var key: KeyCode? = null
    set(key) {
      checkKey(key)
      field = key
    }

  var icon: String? = null

  private fun checkKey(key : KeyCode?) {
    if (key == null) {
      keyModifier = 0
      keyCode = KeyEvent.VK_UNDEFINED
    } else {
      keyCode = key.value
      keyModifier = if (key.toString().indexOf("SHIFT_") == -1) {
        0
      } else {
        InputEvent.SHIFT_MASK
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

  var keyCode = KeyEvent.VK_UNDEFINED
  var keyModifier = 0
}
