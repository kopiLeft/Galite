/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.form

import java.io.Serializable

import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.visual.Action
import org.kopi.galite.visual.visual.ActionHandler
import org.kopi.galite.visual.visual.VCommand
import org.kopi.galite.visual.visual.VHelpGenerator

class VFieldCommand(private val form: VForm,
                    type: Int)
  : VCommand(0xFFFF,
             null,
             null,
             type,
             "Standard $type"), ActionHandler, Serializable {

  override fun setEnabled(enabled: Boolean) {
    if (actor == null) {
      handler = this
      actor = form.getDefaultActor(trigger)
    }
    actor!!.isEnabled = enabled
    actor!!.number = trigger
    actor!!.handler = handler
  }

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param    action        the action to perform.
   * @param    block        This action should block the UI thread ?
   */
  @Deprecated("use method performAsyncAction", ReplaceWith("performAsyncAction(action)"))
  override fun performAction(action: Action, block: Boolean) {
    form.performAsyncAction(action)
  }

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param    action        the action to perform.
   */
  override fun performAsyncAction(action: Action) {
    form.performAsyncAction(action)
  }

  /**
   * Performs a void trigger
   *
   * @param    trigger    the trigger
   */
  override fun executeVoidTrigger(trigger: Trigger?) {
    // DO NOTHING !
  }

  /**
   * Performs a void trigger
   *
   * @param    VKT_Type    the number of the trigger
   */
  override fun executeVoidTrigger(VKT_Type: Int) {
    when (VKT_Type) {
      VForm.CMD_AUTOFILL ->
        form.getActiveBlock()!!.activeField!!.predefinedFill()
      VForm.CMD_EDITITEM, VForm.CMD_EDITITEM_S ->
        form.getActiveBlock()!!.activeField!!.loadItem(VForm.CMD_EDITITEM)
      VForm.CMD_NEWITEM ->
        form.getActiveBlock()!!.activeField!!.loadItem(VForm.CMD_EDITITEM)
    }
  }

  override fun getKey(): Int {
    if (actor == null) {
      handler = this
      actor = form.getDefaultActor(trigger)
    }
    return super.getKey()
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------

  override fun helpOnCommand(help: VHelpGenerator) {
    if (actor == null) {
      handler = this
      actor = form.getDefaultActor(trigger)
    }
    if (actor == null) {
      return
    }
    actor!!.helpOnCommand(help)
  }
}
