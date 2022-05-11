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

import org.kopi.galite.visual.form.VConstants

/**
 * This class represent a command, ie a link between an actor and
 * an action
 *
 * @param item                 the item
 */
open class Command(val item: Actor?,
                   modes: Array<out Mode>,
                   protected var handler: ActionHandler?,
                   internal val action: () -> Unit) {

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  var mode: Int = VConstants.MOD_ANY
    protected set

  private var killed = false

  var actor: Actor? = item
  internal var trigger: Int = -1
  var actorIdent: String = item?.ident.orEmpty()

  init {
    if (modes.isNotEmpty()) {
      setModes(*modes)
    }
  }

  internal constructor(mode: Int,
                       handler: ActionHandler?,
                       actor: Actor?,
                       trigger: Int,
                       actorIdent: String,
                       action: () -> Unit = {})
                : this(actor,
                       *arrayOf(),
                       handler = handler,
                       action = action
  ) {
    this.mode = mode
    this.trigger = trigger
    this.actorIdent = actorIdent
  }

  /**
   * Changes the access mode of the command
   **/
  fun setModes(vararg access: Mode) {
    mode = 0
    for (item in access) {
      mode = mode or (1 shl item.value)
    }
  }

  /**
   * Kill a command: this command will never been enabled again
   */
  fun kill() {
    killed = true
  }

  open fun setEnabled(enabled: Boolean) {
    this.actor?.let {
      if (!killed) {
        it.isEnabled = enabled
        it.number = trigger
        it.action = action
        it.handler = handler
      }
    }
  }

  /**
   * Returns true if the command is active in given to mode.
   *
   * @param    mode        the mode to test
   */
  fun isActive(mode: Int): Boolean = this.mode and (1 shl mode) != 0

  /**
   * Returns the actor
   */
  fun isEnabled(): Boolean = actor != null && actor!!.isEnabled

  /**
   * Returns the name has defined in source
   */
  fun getIdent(): String = actorIdent

  /**
   * Returns true if the command is active in given to mode.
   *
   * @param    b            set to be active
   */
  fun setActive(b: Boolean) {
    mode = if (b) {
      0xFFFF
    } else {
      0
    }
  }

  fun performAction() {
    actor!!.number = trigger
    actor!!.handler = handler
    actor!!.performAction()
  }

  fun performBasicAction() {
    actor!!.number = trigger
    actor!!.handler = handler
    actor!!.performBasicAction()
  }

  /**
   * Returns the actor
   */
  open fun getKey(): Int {
    return actor?.acceleratorKey ?: 0
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------
  open fun helpOnCommand(help: VHelpGenerator) {
    actor!!.helpOnCommand(help)
  }
}
