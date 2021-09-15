/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.testing

import org.kopi.galite.form.VBlock
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.ui.vaadin.field.Field
import org.kopi.galite.ui.vaadin.form.DBlock
import org.kopi.galite.ui.vaadin.form.DGridBlock
import org.kopi.galite.ui.vaadin.main.MainWindow

import com.github.mvysny.kaributesting.v10.MockVaadin
import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._fireEvent
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._internalId
import com.github.mvysny.kaributesting.v10.checkEditableByUser
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.ItemClickEvent

/**
 * Enters a block.
 *
 * @receiver the block to go to.
 */
fun FormBlock.enter(duration: Long = 50) {
  val block = findBlock()

  if(block is DGridBlock) {
    block.grid.clickItem(0, block.grid.columns.first())
  } else {
    block._find<Field>().first().focus()
  }

  // Wait for async updates in transferFocus
  MockVaadin.runUIQueue()
  Thread.sleep(duration)
  MockVaadin.runUIQueue()
}

/**
 * Edits a record in this form block.
 *
 * @param record the record number to edit. It starts from 0.
 */
fun FormBlock.editRecord(record: Int, duration: Long = 50) {
  val block = findBlock()

  if(block is DGridBlock) {
    block.grid.clickItem(record, block.grid.columns.first())
  } else {
    TODO("Edit record for simple block not implemented yet.")
  }

  // Wait for async updates in transferFocus
  MockVaadin.runUIQueue()
  Thread.sleep(duration)
  MockVaadin.runUIQueue()
}

/**
 * Finds the Vaadin block component of this form block.
 */
fun FormBlock.findBlock(): DBlock {
  val mainWindow = _get<MainWindow>()
  val blocks = mainWindow
    ._find<DBlock>()

  return blocks.single {
    it.model eq this.vBlock
  }
}

/**
 * Finds the Vaadin block component of this multiple-block.
 */
fun FormBlock.findMultipleBlock(): DGridBlock =
        (findBlock() as? DGridBlock) ?: throw Exception("$ident is not a multiple block")

// TODO: Remove this when using karibu-testing 1.3.1 as it will be added when this version will be released.
fun <T : Any> Grid<T>.clickItem(rowIndex: Int,
                                column: Grid.Column<*>,
                                button: Int = 1,
                                ctrlKey: Boolean = false,
                                shiftKey: Boolean = false,
                                altKey: Boolean = false,
                                metaKey: Boolean = false) {
  checkEditableByUser()
  val itemKey: String = dataCommunicator.keyMapper.key(_get(rowIndex))
  val internalId: String = column._internalId
  val event = ItemClickEvent(this, true, itemKey, internalId, -1, -1, -1, -1, 1, button, ctrlKey, shiftKey, altKey, metaKey)
  _fireEvent(event)
}

infix fun VBlock.eq(block: VBlock): Boolean {
  return this.title == block.title
          && this.form eq block.form
          && this.name == block.name
          && this.bufferSize == block.bufferSize
          && this.displaySize == block.displaySize
}
