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

import java.util.EventListener

interface MessageListener : EventListener {
  /**
   * Displays a notice.
   */
  fun notice(message: String)

  /**
   * Displays an error message.
   */
  fun error(message: String)

  /**
   * Displays a warning message.
   */
  fun warn(message: String)

  /**
   * Displays an ask dialog box
   */
  fun ask(message: String, yesIsDefault: Boolean): Int

  companion object {
    const val AWR_YES = 1
    const val AWR_NO = 2
    const val AWR_UNDEF = 3
  }
}