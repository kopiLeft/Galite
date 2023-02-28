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
package org.kopi.galite.visual.dsl.form

import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.form.Block

class BlockModel(vForm: VForm, val block: Block, formSource: String? = null)
  : VBlock(block.title,
           block.bufferSize,
           block.displaySize,
           vForm) {

  init {
    initializeBlock(block, formSource)
  }

  override fun setInfo(form: VForm) {
    block.fields.forEach {
      it.setInfo(super.source)
    }
  }
}

fun VBlock.initializeBlock(block: Block, formSource: String?) {

  this.source = if (block::class.isInner && formSource != null) formSource else block.sourceFile
  help = block.help
  pageNumber = block.pageNumber
  border = block.border
  maxRowPos = block.maxRowPos
  maxColumnPos = block.maxColumnPos
  displayedFields = block.displayedFields
  options = block.options
  name = block.name
  block.access.copyInto(access)
  alignment = block.alignment
  dropListMap = block.dropListMap

  blockFields = block.fields.map { formField ->
    formField.vField
  }.toMutableList()

  // Initialize from model
  VKT_Block_Triggers = block.VKT_Block_Triggers
  VKT_Field_Triggers = block.VKT_Field_Triggers
  VKT_Command_Triggers = block.VKT_Command_Triggers
  VKT_Field_Command_Triggers = block.VKT_Field_Command_Triggers
  commands = block.commands
  tables = block.tables
  blockFields = block.blockFields
  indices = block.indices
  indicesIdents = block.indicesIdents
}
