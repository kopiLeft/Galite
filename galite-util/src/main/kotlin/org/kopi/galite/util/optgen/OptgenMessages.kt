/*
 * Copyright (c) 2013-2026 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2026 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.util.optgen

import org.kopi.compiler.base.CompilerMessages
import org.kopi.galite.util.base.MessageDescription

interface OptgenMessages : CompilerMessages {
  companion object {
    val DUPLICATE_DEFINITION: MessageDescription = MessageDescription("Option \"{0}\" redefined in \"{1}\": previous definition in \"{2}\"", null, 0)
    val DUPLICATE_SHORTCUT: MessageDescription = MessageDescription("Shortcut \"{0}\" redefined in \"{1}\": previous definition in \"{2}\"", null, 0)
  }
}
