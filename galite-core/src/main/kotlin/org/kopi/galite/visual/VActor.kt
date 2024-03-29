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

package org.kopi.galite.visual

import org.jetbrains.annotations.TestOnly
import org.kopi.galite.visual.base.UComponent
import org.kopi.galite.visual.dsl.common.LocalizableElement
import org.kopi.galite.visual.l10n.LocalizationManager

/**
 * Represents an actor model.
 *
 * @param menuIdent           the qualified name of menu source file which this actor belongs to
 * @param menuSource          the menu source qualified name
 * @param actorIdent          the qualified name of actor's source file
 * @param actorSource         the actor source qualified name
 * @param acceleratorKey      the accelerator key description
 * @param acceleratorModifier the modifier accelerator key
 * @param help                the help text
 * @param userActor           True if the actor is defined by the user.
 *                            False if the actor is generated by galite and then it should be localized by
 *                            galite localization files such as "org/kopi/galite/visual/General"
 */
open class VActor(val menuIdent: String,
                  private val menuSource: String?,
                  actorIdent: String?,
                  actorSource: String?,
                  var acceleratorKey: Int = 0,
                  var acceleratorModifier: Int = 0,
                  var help: String? = null,
                  val userActor: Boolean = false)
  : VModel, LocalizableElement(actorIdent, actorSource) {

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  var menuName: String = menuIdent
  var menuItem: String? = null
  var number = 0 // The number for the actor
  var iconName: String? = null
  var action: (() -> Unit)? = null
  internal var handler: ActionHandler? = null // the handler for the actor

  private var display: UActor? = null

  var isEnabled: Boolean
    get() = display != null && display!!.isEnabled() // Checks whether the actor is enabled
    set(enabled) {
      display?.setEnabled(enabled) // Enables/disables the actor.
    }

  constructor(menuIdent: String,
              menuSource: String?,
              actorIdent: String,
              actorSource: String?,
              iconName: String?,
              acceleratorKey: Int,
              acceleratorModifier: Int,
              userActor: Boolean = false)
          : this(menuIdent,
                 menuSource,
                 actorIdent,
                 actorSource,
                 acceleratorKey,
                 acceleratorModifier,
                 null,
                 userActor) {
    this.iconName = iconName
  }

  override fun getDisplay(): UActor? = display

  override fun setDisplay(display: UComponent) {
    assert(display is UActor) { "VActor display should be UActor" }
    this.display = display as UActor
  }

  // ----------------------------------------------------------------------
  // ACTIONS HANDLING
  // ----------------------------------------------------------------------
  fun performAction() {
    handler!!.performAsyncAction(object : Action("$menuItem in $menuName") {
      override fun execute() {
        handler!!.executeVoidTrigger(number)
        action?.let { it() }
      }

      /**
       * Returns `true` if this action can be cancelled in an action queue context.
       * This means that the action can be removed from an action queue when performing
       * a clear operation.
       *
       * quit a reset action cannot be cancelled. They will be executed even if the action
       * queue is cleared. Implementations should care about this.
       *
       * @return `true` if this not a reset action.
       */
      override fun isCancellable(): Boolean =
              !("quit".equals(ident, ignoreCase = true) || "break".equals(ident, ignoreCase = true))
    })
  }

  fun performBasicAction() {
    handler!!.executeVoidTrigger(number)
    action?.invoke()
  }

  // ----------------------------------------------------------------------
  // HASHCODE AND EQUALS REDEFINITION
  // ----------------------------------------------------------------------
  override fun hashCode(): Int = ident.hashCode() * ident.hashCode()

  override fun equals(other: Any?): Boolean {
    return if (other !is VActor) {
      false
    } else {
      menuName == other.menuName
              && menuItem == other.menuItem
              && ((iconName == null && other.iconName == null)
              || (iconName != null
              && other.iconName != null
              && iconName == other.iconName))
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
    val menuLoc = manager.getMenuLocalizer(menuSource, menuIdent)
    val actorLoc = manager.getActorLocalizer(sourceFile, ident)
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

  val _actorSource @TestOnly get() = sourceFile
  val _menuSource @TestOnly get() = menuSource
}
