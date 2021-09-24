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

package org.kopi.galite.visual

interface UserConfiguration {
  fun getPreviewMode(): Int
  fun getPreviewScreen(): Int
  fun getMailSignature(): String

  companion object {
    const val PRM_OPT = 1
    const val PRM_OPT_WIDHT = 2
    const val PRM_OPT_HEIGHT = 3
    const val PRS_FULLSCREEN = 1
    const val PRS_DEFAULT = 2
  }
}
