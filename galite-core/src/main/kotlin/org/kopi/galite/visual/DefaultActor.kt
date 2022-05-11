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

import org.kopi.galite.visual.dsl.common.Menu
import org.kopi.galite.visual.dsl.common.PredefinedCommand

/**
 * Represents an default actor.
 *
 * @param menu                the containing menu
 * @param label               the label
 * @param help                the help
 * @param command             a predefined command that can be linked to this actor
 * @param source              path localization file
 */
open class DefaultActor(menu: Menu,
                        label: String,
                        help: String,
                        val command: PredefinedCommand,
                        ident: String? = command.ident,
                        source: String? = null)
  : Actor(menu, label,help, ident, source) {

  /**
   * the code of this default command
   */
  val code get() = command.number
}
