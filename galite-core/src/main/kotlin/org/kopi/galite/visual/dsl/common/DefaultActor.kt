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

import org.kopi.galite.visual.visual.VDefaultActor

/**
 * This class represents an actor, ie a menu element with a name and may be an icon, a shortcut
 * and a help
 *
 * An Actor is an item to be linked to a command, if its [icon] is specified, it will appear
 * in the icon_toolbar located under the menu bar, otherwise, it will only be accessible from the menu bar
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
  : VDefaultActor(command.number, menu, label,help, ident, source)
