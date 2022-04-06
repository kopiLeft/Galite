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
package org.kopi.galite.visual.dsl.common

import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.visual.ActionHandler
import org.kopi.galite.visual.visual.VCommand
import org.kopi.galite.visual.visual.VHelpGenerator

/**
 * This class represent a command, ie a link between an actor and
 * an action
 *
 * @param item                 the item
 */
class Command(val item: Actor, modes: Array<out Mode>, val handler: ActionHandler, val action: () -> Unit = {}) {

  var mode: Int = VConstants.MOD_ANY
    private set

  init {
    if (modes.isNotEmpty()) {
      setMode(*modes)
    }
  }

  /**
   * Changes the access mode of the command
   **/
  fun setMode(vararg access: Mode) {
    mode = 0
    for (item in access) {
      mode = mode or (1 shl item.value)
    }
  }

  /**
   * Kill a command: this command will never been enabled again
   */
  fun kill() {
    model.kill()
  }

  fun setEnabled(enabled: Boolean) {
    model.setEnabled(enabled)
  }

  /**
   * Returns true if the command is active in given to mode.
   *
   * @param    mode        the mode to test
   */
  fun isActive(mode: Int): Boolean = model.isActive(mode)

  /**
   * Returns the actor
   */
  fun isEnabled(): Boolean = model.isEnabled()

  /**
   * Returns true if the command is active in given to mode.
   *
   * @param    b            set to be active
   */
  fun setActive(b: Boolean) {
    model.setActive(b)
  }

  fun performAction() {
    model.performAction()
  }

  fun performBasicAction() {
    model.performBasicAction()
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------
  fun helpOnCommand(help: VHelpGenerator) {
    model.helpOnCommand(help)
  }

  // ----------------------------------------------------------------------
  // MODEL
  // ----------------------------------------------------------------------

  val model: VCommand = buildModel()

  /**
   * Builds the command model [VCommand] from information provided by this command.
   */
  private fun buildModel() : VCommand {
    return VCommand(
      mode,
      handler,
      item.model,
      -1,
      item.ident,
      action
    )
  }
}
