/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.common

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
class Command(val item: Actor, var mode: Int = VConstants.MOD_ANY) {
  var action: (() -> Unit)? = null
  lateinit var body: CommandBody

  /** function to change the access mode of the command **/
  fun mode(vararg access: Int) {
    mode = 0
    for (item in access) {
      mode = mode or (1 shl item)
    }
  }

  /**
   * Builds the command model [VCommand] from information provided by this command.
   */
  internal fun buildModel(handler: ActionHandler, formActors: Array<VActor?>) : VCommand =
          VCommand(mode,
                   handler,
                   formActors.find { it?.actorIdent ==  item.ident },
                   -1,
                   item.ident,
                   action
          )
}
