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

class VCommand(private var mode: Int, handler: ActionHandler,
               protected var actor: VActor?,
               var trigger: Int,
               private var item: String) {
  /**
   * Kill a command: this command will never been enabled again
   */
  fun kill() {
    killed = true
  }
  /**
   * Returns the actor
   *///      actor.setSynchronous(false);
  /**
   * Returns the actor
   */
  var isEnabled: Boolean = actor != null && actor!!.isEnabled

  /**
   * Returns the name has defined in source
   */
  fun getIdent(): String {
    return item
  }


  /**
   * Returns true iff the command is active in given to mode.
   *
   * @param        mode                the mode to test
   */
  fun isActive(mode: Int): Boolean {
    return this.mode and (1 shl mode) != 0
  }

  /**
   * Returns true iff the command is active in given to mode.
   *
   * @param        b                set to be active
   */
  fun setActive(b: Boolean) {
    mode = if (b) {
      0xFFFF
    } else {
      0
    }
  }

  fun performAction() {
    actor?.number = trigger
    actor!!.setHandler(handler)
    //    actor.setSynchronous(false);
    actor!!.performAction()
  }

  fun performBasicAction() {
    actor?.number = trigger
    actor!!.setHandler(handler)
    //    actor.setSynchronous(false);
    actor!!.performBasicAction()
  }

  /**
   *
   */
  fun getKey(): Int {
    return if (actor != null) actor!!.acceleratorKey else 0
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------
  fun helpOnCommand(help: VHelpGenerator) {
    actor!!.helpOnCommand(help)
  }

  private var killed = false
  protected var handler: ActionHandler

  init {
    this.item = item
    this.handler = handler
  }
}
