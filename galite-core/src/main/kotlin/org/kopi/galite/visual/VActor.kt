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
package org.kopi.galite.visual

import org.kopi.galite.base.UComponent
import org.kopi.galite.l10n.ActorLocalizer
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.l10n.MenuLocalizer

class VActor(val menuIdent: String,
        // qualified name of menu's source file
             private var menuSource: String,
             actorIdent: String,
             actorSource: String,
             iconName: String?,
             acceleratorKey: Int,
             acceleratorModifier: Int) : VModel {

  /**
   * Checks whether the actor is enabled.
   * Enables/disables the actor.
   */
  var isEnabled: Boolean
    get() = display != null && display!!.isEnabled()
    set(enabled) {
      if (display != null) {
        display?.setEnabled(enabled)
      }
    }

  override fun getDisplay(): UComponent? {
    return display
  }

  override fun setDisplay(display: UComponent) {
    assert(display is UActor) { "VActor display should be UActor" }
    this.display = display as UActor
  }

  fun setDisplay(display: UActor) {
    this.display = display
  }

  // ----------------------------------------------------------------------
  // ACTIONS HANDLING
  // ----------------------------------------------------------------------
  fun performAction() {
    handler.performAsyncAction(object : Action("$menuItem in $menuName") {
      override fun execute() {
        handler.executeVoidTrigger(number)
      }

      // quit an reset action cannot be cancelled. They will be executed even if the action
      // queue is cleared. Implementations should care about this.
      override fun isCancellable() = !("quit".equals(actorIdent, ignoreCase = true) || "break".equals(actorIdent, ignoreCase = true))
    })
  }

  fun performBasicAction() {
    handler.executeVoidTrigger(number)
  }

  // ----------------------------------------------------------------------
  // HASHCODE AND EQUALS REDEFINITION
  // ----------------------------------------------------------------------
  override fun hashCode(): Int {
    return actorIdent.hashCode() * actorIdent.hashCode()
  }

  override fun equals(obj: Any?): Boolean {
    return if (obj !is VActor) {
      false
    } else {
      menuName == obj.menuName
              && menuItem == obj.menuItem
              && (iconName == null
              && obj.iconName == null
              || iconName != null
              && obj.iconName != null
              && iconName == obj.iconName)
    }
  }
  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  /**
   * Localizes this actor
   *
   * @param     manager         the manger to use for localization
   */
  fun localize(manager: LocalizationManager) {
    val menuLoc: MenuLocalizer = manager.getMenuLocalizer(menuSource, menuIdent)
    val actorLoc: ActorLocalizer = manager.getActorLocalizer(actorSource, actorIdent)
    menuName = menuLoc.getLabel()
    menuItem = actorLoc.getLabel()
    help = actorLoc.getHelp()
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------
  fun helpOnCommand(help: VHelpGenerator) {
    help.helpOnCommand(menuName,
            menuItem,
            iconName,
            acceleratorKey,
            acceleratorModifier,
            this.help)
  }

  // --------------------------------------------------------------------
  // DEBUG
  // --------------------------------------------------------------------
  override fun toString(): String {
    val buffer = StringBuffer()
    buffer.append("VActor[")
    buffer.append("menu=$menuName:$menuItem")
    if (iconName != null) {
      buffer.append(", ")
      buffer.append("icon=$iconName")
    }
    if (acceleratorKey != 0) {
      buffer.append(", ")
      buffer.append("key=$acceleratorKey:$acceleratorModifier")
    }
    buffer.append(", ")
    buffer.append("help=$help")
    buffer.append("]")
    return buffer.toString()
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  val acceleratorKey: Int = 0
  val acceleratorModifier: Int = 0
  lateinit var menuName: String
  lateinit var menuItem: String
  private var display: UActor? = null

  // qualified name of actor's source file
  private val actorSource: String = ""

  /**
   * Sets and gets the number for the actor.
   */
  var number = 0
  lateinit var handler: ActionHandler
  var iconName: String? = null
  protected lateinit var actorIdent: String
  lateinit var help: String
}
