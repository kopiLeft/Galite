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

import org.kopi.galite.l10n.LocalizationManager

class Module(val id: Int,
             val parent: Int,
             shortname: String,
             source: String,
             val objectName: String?,
             var accessibility: Int,
             priority: Int,
             icon: String?) : Comparable<Module?> {
  // ---------------------------------------------------------------------
  // LOCALIZATION
  // ---------------------------------------------------------------------
  /**
   * Localize this module
   *
   * @param     manager         the manger to use for localization
   */
  fun localize(manager: LocalizationManager) {
    TODO()
  }

  override operator fun compareTo(module: Module?): Int {
    TODO()
  }

  var description: String? = null
    private set
  var help: String? = null
    private set

  companion object {
    const val ACS_PARENT = 0
    const val ACS_TRUE = 1
    const val ACS_FALSE = 2

    fun Executable(`object`: String): Executable {
     TODO()
    }
  }
}
