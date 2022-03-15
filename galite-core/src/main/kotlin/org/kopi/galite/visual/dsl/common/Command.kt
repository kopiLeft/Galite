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
import org.kopi.galite.visual.visual.VActor
import org.kopi.galite.visual.visual.VCommand

/**
 * This class represent a command, ie a link between an actor and
 * an action
 *
 * @param item                 the item
 */
class Command(val item: Actor) {

  var action: () -> Unit = {}
    internal set

  var mode: Int = VConstants.MOD_ANY
    private set

  /**
   * Changes the access mode of the command
   **/
  fun setMode(vararg access: Mode) {
    mode = 0
    for (item in access) {
      mode = mode or (1 shl item.value)
    }
  }

  lateinit var model: VCommand

  /**
   * Builds the command model [VCommand] from information provided by this command.
   */
  internal fun buildModel(handler: ActionHandler, formActor: VActor?) : VCommand {
    model = VCommand(
      mode,
      handler,
      formActor,
      -1,
      item.ident,
      action
    )

    return model
  }

}
