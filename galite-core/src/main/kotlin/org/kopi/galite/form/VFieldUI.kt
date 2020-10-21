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
package org.kopi.galite.form

import org.kopi.galite.visual.Action
import org.kopi.galite.visual.ActionHandler
import java.io.Serializable

class VFieldUI  : VConstants, ActionHandler, Serializable {
  fun getModel(): VField = TODO()
  fun getDisplay(): UField = TODO()
  fun displayFieldError(message: String): Any = TODO()
  fun transferFocus(display: UField): Any = TODO()
  fun getBlock(): VBlock = TODO()
  fun fillField(): Boolean = TODO()

    override fun executeVoidTrigger(VKT_Type: Int) {
      TODO("Not yet implemented")
    }

    override fun performAsyncAction(action: Action) {
      TODO()
    }

    override fun performAction(action: Action, block: Boolean) {
      TODO("Not yet implemented")
    }
}
