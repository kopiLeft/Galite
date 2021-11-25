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

import java.awt.event.KeyEvent

import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VDefaultFormActor
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.form.VFormCommand
import org.kopi.galite.visual.visual.VActor

abstract class VFullCalendarForm : VForm() {
  private lateinit var actorsDef: Array<VActor?>
  private var number = 0

  init {
    initDefaultActors()
    initDefaultCommands()
  }

  companion object {
    const val QUIT_ICON = "quit"
  }

  // ----------------------------------------------------------------------
  // Default Actors
  // ----------------------------------------------------------------------
  private fun initDefaultActors() {
    actorsDef = arrayOfNulls(1)
    createActor("File", "Quit", QUIT_ICON, KeyEvent.VK_ESCAPE, 0, VConstants.CMD_QUIT)
    addActors(actorsDef.requireNoNulls())
  }

  // ----------------------------------------------------------------------
  // Default Actors
  // ----------------------------------------------------------------------
  private fun createActor(menuIdent: String,
                          actorIdent: String,
                          iconIdent: String,
                          key: Int,
                          modifier: Int,
                          trigger: Int) {
    actorsDef[number] = VDefaultFormActor(menuIdent, actorIdent, iconIdent, key, modifier)
    actorsDef[number]!!.number = trigger
    number++
  }

  // ----------------------------------------------------------------------
  // Default Commands
  // ----------------------------------------------------------------------
  private fun initDefaultCommands() {
    val commands = (actorsDef.indices).map { i ->
      VFormCommand(this, actorsDef[i]!!)
    }
    super.commands = commands.toTypedArray()

    commands.forEach {
      val fieldTriggerArray = arrayOfNulls<Trigger>(VConstants.TRG_TYPES.size)
      VKT_Triggers.add(fieldTriggerArray)
    }
  }
}
