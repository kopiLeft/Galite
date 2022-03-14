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

import java.util.Locale

import org.kopi.galite.visual.cross.VFullCalendarForm
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.fullcalendar.VFullCalendarBlock
import org.kopi.galite.visual.visual.VDefaultActor

class BlockModel(vForm: VForm, val block: Block, formSource: String? = null): VBlock(vForm) {

  init {
    initializeBlock(block, formSource)
  }

  override fun setInfo(form: VForm) {
    block.fields.forEach {
      it.setInfo(super.source, form)
    }
  }
}

fun VBlock.initializeBlock(block: Block, formSource: String?) {
  val model = block.block

  this.source = if (block::class.isInner && formSource != null) formSource else block.sourceFile
  title = block.title
  help = block.help
  bufferSize = block.buffer
  displaySize = block.visible
  pageNumber = block.pageNumber
  border = block.border.value
  maxRowPos = block.maxRowPos
  maxColumnPos = block.maxColumnPos
  displayedFields = block.displayedFields
  name = block.ident
  options = block.options
  access = block.access
  alignment = block.align?.getBlockAlignModel()
  dropListMap = block.dropListMap

  fields = block.fields.map { formField ->
    formField.vField
  }.toMutableList()

  // Initialize from model
  VKT_Block_Triggers = model.VKT_Block_Triggers
  VKT_Field_Triggers = model.VKT_Field_Triggers
  VKT_Command_Triggers = model.VKT_Command_Triggers
  VKT_Field_Command_Triggers = model.VKT_Field_Command_Triggers
  commands = model.commands
  tables = model.tables
  fields = model.fields
  indices = model.indices
  indicesIdents = model.indicesIdents
}

class FullCalendarBlockModel(val block: FullCalendar): VFullCalendarBlock() {

  override fun setInfo(form: VForm) {
    block.fields.forEach {
      it.setInfo(super.source, form)
    }
  }

  fun buildFullCalendarForm(): VFullCalendarForm {
    return object : VFullCalendarForm() {

      init {
        init()
        initDefaultActors()
        initDefaultCommands()
      }

      override val locale: Locale?
        get() = this@FullCalendarBlockModel.block.form.locale
      override val fullCalendarBlock: VFullCalendarBlock
        get() = this@FullCalendarBlockModel

      fun init() {

        val vSimpleBlock = BlockModel(this, this@FullCalendarBlockModel.block, source)
        vSimpleBlock.setInfo(pageNumber, this)
        vSimpleBlock.initIntern()

        val defaultActors = form.actors.filter { actor ->
          actor is VDefaultActor &&
                  (actor.code == CMD_AUTOFILL
                          || actor.code == CMD_EDITITEM
                          || actor.code == CMD_EDITITEM_S
                          || actor.code == CMD_NEWITEM)

        }.toTypedArray()
        addActors(defaultActors)

        addBlock(vSimpleBlock)
        source = vSimpleBlock.source
        setTitle(vSimpleBlock.title)
      }

      override fun formClassName(): String = block.javaClass.name
    }
  }
}
