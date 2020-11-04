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
import org.kopi.galite.visual.VCommand

open class VFieldUI(blockView: UBlock, model: VField) {
  fun getModel(): VField = TODO()
  fun getDisplay(): UField = TODO()
  fun displayFieldError(message: String): Any = TODO()
  fun transferFocus(display: UField): Any = TODO()
  fun getBlock(): VBlock = TODO()
  fun fillField(): Boolean = TODO()

  fun executeAction() {
    TODO()
  }

  open fun hasAutofill(): Boolean {
    TODO()
  }

  fun fireColorHasChanged(recno: Int) {
    TODO()
  }

  fun fireAccessHasChanged(recno: Int) {
    TODO()
  }

  fun getDetailDisplay(): UField {
    TODO()
  }

  fun getDisplays(): Array<UField> {
    TODO()
  }

  open fun getBlockView(): UBlock? {
    TODO()
  }

  fun getLabel(): ULabel {
    TODO()
  }

  fun getDetailLabel(): ULabel {
    TODO()
  }

  // ----------------------------------------------------------------------
  // DISPLAY UTILS
  // ----------------------------------------------------------------------
  fun scrollTo(toprec: Int) {
    TODO()
  }

  fun previousEntry() {
    TODO()
  }

  fun nextEntry() {
    TODO()
  }

  fun resetCommands() {
    TODO()
  }

  fun resetLabel() {
    TODO()
  }

  fun hasAction(): Boolean {
    TODO()
  }

  fun autofillButton() {
    TODO()
  }

  fun performAsyncAction(action: Action) {
    TODO()
  }

  open fun getIncrementCommand(): VCommand {
    TODO()
  }

  open fun getDecrementCommand(): VCommand {
    TODO()
  }


}
