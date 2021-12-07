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
package org.kopi.galite.visual.dsl.form

import java.util.Locale

import org.kopi.galite.visual.cross.VFullCalendarForm
import org.kopi.galite.visual.dsl.common.Command
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VDateField
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.form.VTimeField
import org.kopi.galite.visual.form.VTimestampField
import org.kopi.galite.visual.fullcalendar.VFullCalendarBlock
import org.kopi.galite.visual.visual.VDefaultActor

class BlockModel(vForm: VForm, val block: FormBlock, source: String? = null): VBlock(vForm) {

  init {
    initializeBlock(block, source)
  }

  override fun setInfo(form: VForm) {
    block.fields.forEach {
      it.setInfo(super.source, form)
    }
  }
}

class FullCalendarBlockModel(vForm: VForm, val block: FullCalendarBlock, source: String? = null): VFullCalendarBlock(vForm) {

  init {
    initializeBlock(block, source)
    dateField = block.dateField?.vField as? VDateField
    fromTimeField = block.fromTimeField?.vField as? VTimeField
    toTimeField = block.toTimeField?.vField as? VTimeField
    fromField = block.fromField?.vField as? VTimestampField
    toField = block.toField?.vField as? VTimestampField
  }

  override fun setInfo(form: VForm) {
    block.fields.forEach {
      it.setInfo(super.source, form)
    }
  }

  override fun buildFullCalendarForm(): VFullCalendarForm {
    return object : VFullCalendarForm() {

      override val locale: Locale?
        get() = form.locale
      override val fullCalendarBlock: VFullCalendarBlock
        get() = this@FullCalendarBlockModel

      override fun init() {

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
        addActors(defaultActors.requireNoNulls())

        dBContext = vSimpleBlock.dBContext
        blocks = arrayOf(vSimpleBlock)
        source = vSimpleBlock.source
        setTitle(vSimpleBlock.title)
        pages = arrayOf()
        pagesIdents = arrayOf()
      }
    }
  }
}

fun VBlock.initializeBlock(block: FormBlock, source: String?) {
  handleTriggers(block)
  this.source = source ?: block.sourceFile
  title = block.title
  help = block.help
  bufferSize = block.buffer
  displaySize = block.visible
  pageNumber = block.pageNumber
  border = block.border.value
  maxRowPos = block.maxRowPos
  maxColumnPos = block.maxColumnPos
  displayedFields = block.displayedFields
  commands = block.commands.map { command ->
    command.buildModel(this, form.actors)
  }.toTypedArray()
  name = block.ident
  options = block.options
  access = block.access
  tables = block.tables.map {
    it.table
  }.toTypedArray()
  fields = block.fields.map { formField ->
    formField.vField
  }.toTypedArray()
  indices = block.indices.map {
    it.message
  }.toTypedArray()
  indicesIdents = block.indices.map {
    it.ident
  }.toTypedArray()
  alignment = block.align?.getBlockAlignModel()
  dropListMap = block.dropListMap
}

/**
 * Handling triggers
 */
fun VBlock.handleTriggers(block: FormBlock) {
  // BLOCK TRIGGERS
  val blockTriggerArray = arrayOfNulls<Trigger>(VConstants.TRG_TYPES.size)

  block.triggers.forEach { trigger ->
    for (i in VConstants.TRG_TYPES.indices) {
      if (trigger.events shr i and 1 > 0) {
        blockTriggerArray[i] = trigger
      }
    }
    VKT_Triggers[0] = blockTriggerArray
  }

  // FIELD TRIGGERS
  block.fields.forEach { field ->
    val fieldTriggerArray = arrayOfNulls<Trigger>(VConstants.TRG_TYPES.size)

    field.triggers.forEach { trigger ->
      for (i in VConstants.TRG_TYPES.indices) {
        if (trigger.events shr i and 1 > 0) {
          fieldTriggerArray[i] = trigger
        }
      }
    }
    VKT_Triggers.add(fieldTriggerArray)
  }

  // COMMANDS TRIGGERS
  block.commands.forEach {
    val fieldTriggerArray = arrayOfNulls<Trigger>(VConstants.TRG_TYPES.size)
    // TODO : Add commands triggers here
    VKT_Triggers.add(fieldTriggerArray)
  }

  // FIELDS COMMANDS TRIGGERS
  val fieldsCommands = getFieldsCommands(block)
  fieldsCommands.forEach {
    val fieldTriggerArray = arrayOfNulls<Trigger>(VConstants.TRG_TYPES.size)
    // TODO : Add field commands triggers here
    VKT_Triggers.add(fieldTriggerArray)
  }
}

fun getFieldsCommands(block: FormBlock): List<Command> {
  return block.fields.map {
    it.commands
  }.flatten()
}
