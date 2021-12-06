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
package org.kopi.galite.visual.cross

import kotlin.jvm.Throws

import java.awt.event.KeyEvent

import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.form.Commands
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VDefaultFormActor
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.form.VFullCalendarCommand
import org.kopi.galite.visual.fullcalendar.VFullCalendarBlock
import org.kopi.galite.visual.visual.VActor
import org.kopi.galite.visual.visual.VCommand
import org.kopi.galite.visual.visual.VException
import org.kopi.galite.visual.visual.WindowController

abstract class VFullCalendarForm : VForm() {
  private var actorsDef: MutableList<VActor> = mutableListOf()
  private var commandsDef: MutableList<VCommand> = mutableListOf()

  init {
    initDefaultActors()
    initDefaultCommands()
  }

  companion object {
    const val QUIT_ICON = "quit"
    const val SAVE_ICON = "save"
    const val DELETE_ICON = "delete"
  }

  val block: VBlock get() = blocks[0]

  // ----------------------------------------------------------------------
  // Default Actors
  // ----------------------------------------------------------------------
  private fun initDefaultActors() {
    createActor("File", "Quit", QUIT_ICON, KeyEvent.VK_ESCAPE, 0, VConstants.CMD_QUIT)
    createActor("File", "Save", SAVE_ICON, 0, 0, VConstants.CMD_SAVE, mode(VConstants.MOD_INSERT, VConstants.MOD_UPDATE))
    createActor("File", "Delete", DELETE_ICON, 0, 0, VConstants.CMD_DELETE, mode(VConstants.MOD_UPDATE))
    addActors(actorsDef.toTypedArray())
  }

  fun mode(vararg access: Int): Int {
    var mode = 0
    for (item in access) {
      mode = mode or (1 shl item)
    }
    return mode
  }

  // ----------------------------------------------------------------------
  // Default Actors
  // ----------------------------------------------------------------------
  private fun createActor(menuIdent: String,
                          actorIdent: String,
                          iconIdent: String,
                          key: Int,
                          modifier: Int,
                          trigger: Int,
                          mode: Int = 0xFFFF) {
    val actorDef = VDefaultFormActor(menuIdent, actorIdent, iconIdent, key, modifier)

    actorDef.number = trigger
    actorsDef.add(actorDef)
    commandsDef.add(VFullCalendarCommand(this, actorDef, mode))
  }

  // ----------------------------------------------------------------------
  // DISPLAY INTERFACE
  // ----------------------------------------------------------------------

  /**
   * doNotModal
   * no modal call to this form
   * @exception        VException        an exception may be raised by triggers
   */
  @Throws(VException::class)
  override fun doNotModal() {
    if(getDisplay() == null) {
      WindowController.windowController.doNotModal(this)
    } else {
      WindowController.windowController.doNotModal(getDisplay()!!)
    }
  }

  // ----------------------------------------------------------------------
  // Default Commands
  // ----------------------------------------------------------------------
  private fun initDefaultCommands() {
    super.commands = arrayOf()
    block.commands = block.commands?.plus(commandsDef.toTypedArray()) ?: commandsDef.toTypedArray()

    commandsDef.forEach {
      val fieldTriggerArray = arrayOfNulls<Trigger>(VConstants.TRG_TYPES.size)
      block.VKT_Triggers.add(fieldTriggerArray)
    }
  }

  /**
   * close the form
   */
  override fun close(code: Int) {
    if (block.isCurrentRecordFetched()) {
      fullCalendarBlock.refreshEntries()
    }

    super.close(code)
  }

  fun save() {
    Commands.saveBlock(block)
  }

  fun delete() {
    Commands.deleteBlock(block)
  }

  abstract val fullCalendarBlock: VFullCalendarBlock
}
