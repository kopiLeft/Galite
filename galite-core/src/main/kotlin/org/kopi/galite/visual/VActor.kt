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

import org.kopi.galite.base.UComponent
import org.kopi.galite.l10n.LocalizationManager

/**
 * Represents an actor.
 */
class VActor(
        val menuIdent: String,
        private val menuSource: String,
        val actorIdent: String,
        private val actorSource: String,
        var iconName: String?,
        val acceleratorKey: Int,
        val acceleratorModifier: Int) : VModel {

  override fun setDisplay(display: UComponent) {
    TODO("Not yet implemented")
  }

  override fun getDisplay(): UComponent {
    TODO("Not yet implemented")
  }

  fun setEnabled(enabled: Boolean) {
    TODO()
  }

  fun localize(manager: LocalizationManager) {
    TODO()
  }

  fun isEnabled(): Boolean {
    TODO()
  }

  var number: Int? = null
  var handler: ActionHandler? = null
}
