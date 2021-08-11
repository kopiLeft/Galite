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

import org.kopi.galite.form.dsl.Key
import org.kopi.galite.visual.VActor
import org.kopi.galite.visual.VDefaultActor

/**
 * This class represents an actor, ie a menu element with a name and may be an icon, a shortcut
 * and a help
 *
 * An Actor is an item to be linked to a command, if its [icon] is specified, it will appear
 * in the icon_toolbar located under the menu bar, otherwise, it will only be accessible from the menu bar
 *
 * @param ident               the ident
 * @param menu                the containing menu
 * @param label               the label
 * @param help                the help
 */
class Actor(val ident: String, val menu: Menu, val label: String, val help: String, val number: Int) {
  // The shortcut key
  var key: Key? = null
    set(key) {
      checkKey(key)
      field = key
    }

  // The actor icon
  var icon: String? = null
  var keyCode = 0
  var keyModifier = 0

  var model: VActor? = null

  private fun checkKey(key: Key?) {
    if (key == null) {
      keyModifier = 0
      keyCode = KeyEvent.VK_UNDEFINED
    } else {
      keyCode = key.value
      keyModifier = if (key.toString().contains("SHIFT_")) InputEvent.SHIFT_MASK else 0
    }
  }
  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  fun genLocalization(writer: LocalizationWriter) {
    writer.genActorDefinition(ident, label, help)
  }

  /**
   * Builds the actor model [VActor] from information provided by this actor.
   */
  internal fun buildModel(sourceFile: String) : VActor =
          if (number == 0) {
            VActor(menu.label, sourceFile, ident, sourceFile, icon, keyCode, keyModifier, true)
          } else {
            VDefaultActor(number, menu.label, sourceFile, ident, sourceFile, icon, keyCode, keyModifier, true)
          }.also {
            it.menuName = menu.label
            it.menuItem = label
            it.help = help
            model = it
          }
}
