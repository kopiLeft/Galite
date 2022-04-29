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

import org.kopi.galite.visual.cross.VFullCalendarForm
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.ActionHandler
import org.kopi.galite.visual.VActor
import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VHelpGenerator
import org.kopi.galite.visual.VWindow

class VFullCalendarCommand(
  val form: VFullCalendarForm,
  actor: VActor,
  mode: Int
) : VCommand(mode, null, actor, actor.number, actor.ident), ActionHandler {

  override fun setEnabled(enabled: Boolean) {
    if (actor != null) {
      actor!!.isEnabled = enabled
      actor!!.number = trigger
      actor!!.handler = this
    }
  }

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param    action        the action to perform.
   * @param    block        This action should block the UI thread ?
   */
  @Deprecated("use method performAsyncAction", ReplaceWith("performAsyncAction(action, block)"))
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
   * @param    VKT_Type    the number of the trigger
   */
  override fun executeVoidTrigger(VKT_Type: Int) {
    when (VKT_Type) {
      VConstants.CMD_QUIT -> form.close(VWindow.CDE_HIDE)
      VConstants.CMD_SAVE -> form.save()
      VConstants.CMD_DELETE -> form.delete()
      // TODO
    }
  }

  /**
   * Performs a void trigger
   *
   * @param    trigger    the  trigger
   */
  override fun executeVoidTrigger(trigger: Trigger?) {
    // DO NOTHING !
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------
  override fun helpOnCommand(help: VHelpGenerator) {
    if (actor == null) {
      return
    }
    actor!!.helpOnCommand(help)
  }
}
