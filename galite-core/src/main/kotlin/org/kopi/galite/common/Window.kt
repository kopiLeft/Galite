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

import java.util.Locale

import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VTrigger

/**
 * This class represents the definition of a window
 *
 * @param where                the token reference of this node
 * @param title                the title of this form
 * @param superName                the type of the form
 */
abstract class Window {
  abstract val title: String
  open val locale: Locale? = null
  var options: Int? = null
  lateinit var commands: Array<VCommand>
  lateinit var triggers: Array<VTrigger>
}
