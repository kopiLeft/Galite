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
package org.kopi.galite.ui.vaadin.form

import org.kopi.galite.base.UComponent
import org.kopi.galite.form.Alignment
import org.kopi.galite.form.UMultiBlock
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VField

/**
 * A based Grid multi block implementation
 */
class DGridMultiBlock(parent: DForm, model: VBlock) : DGridBlock(parent, model), UMultiBlock {

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  override fun switchView(row: Int) {
    TODO("Not yet implemented")
  }

  override fun getRecordFromDisplayLine(line: Int): Int {
    TODO("Not yet implemented")
  }

  override fun addToDetail(comp: UComponent?, constraint: Alignment) {
    TODO("Not yet implemented")
  }

  override fun inDetailMode(): Boolean {
    TODO("Not yet implemented")
  }

  override fun blockViewModeLeaved(block: VBlock, activeField: VField?) {
    TODO("Not yet implemented")
  }

  override fun blockViewModeEntered(block: VBlock, activeField: VField?) {
    TODO("Not yet implemented")
  }

  override fun blockChanged() {
    TODO("Not yet implemented")
  }

  override fun enterRecord(recno: Int) {
    TODO("Not yet implemented")
  }

  private var itemHasDetailVisible: Int? = null
}
