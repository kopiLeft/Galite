/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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

import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.ui.vaadin.field.Field
import org.kopi.galite.visual.ui.vaadin.form.DBlock
import org.kopi.galite.visual.ui.vaadin.form.DGridBlock
import org.kopi.galite.visual.ui.vaadin.main.MainWindow

import com.github.mvysny.kaributesting.v10.MockVaadin
import com.github.mvysny.kaributesting.v10._click
import com.github.mvysny.kaributesting.v10._clickItem
import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._get

/**
 * Enters a block.
 *
 * @receiver the block to go to.
 */
fun Block._enter(duration: Long = 50) {
  val block = findBlock()

  // Click on the first field in the block
  if(block is DGridBlock) {
    block.grid._clickItem(0, block.grid.columns.first())
  } else {
    block._find<Field>().first().wrappedField._click()
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
fun Block.editRecord(record: Int, duration: Long = 50) {
  val block = findBlock()

  if(block is DGridBlock) {
    block.grid._clickItem(record, block.grid.columns.first())
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
fun Block.findBlock(): DBlock {
  val mainWindow = _get<MainWindow>()
  val blocks = mainWindow
    ._find<DBlock>()

  val block = blocks.singleOrNull {
    it.model eq this.block
  }

  if (block != null) {
    return block
  } else {
    throw Exception("Block '$title' not found.")
  }
}

/**
 * Finds the Vaadin block component of this multiblock.
 */
fun Block.findMultiBlock(): DGridBlock =
        (findBlock() as? DGridBlock) ?: throw Exception("$ident is not a multiple block")

infix fun VBlock.eq(block: VBlock): Boolean {
  return this.title == block.title
          && this.form eq block.form
          && this.name == block.name
          && this.bufferSize == block.bufferSize
          && this.displaySize == block.displaySize
}
