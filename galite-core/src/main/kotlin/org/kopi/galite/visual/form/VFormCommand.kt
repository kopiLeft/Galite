/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.print.DefaultPrintManager
import org.kopi.galite.visual.print.PrintManager
import org.kopi.galite.visual.visual.Action
import org.kopi.galite.visual.visual.ActionHandler
import org.kopi.galite.visual.visual.PrinterManager
import org.kopi.galite.visual.visual.VActor
import org.kopi.galite.visual.visual.VCommand
import org.kopi.galite.visual.visual.VHelpGenerator
import org.kopi.galite.visual.visual.VWindow

class VFormCommand(
  val form: VForm,
  actor: VActor
) : VCommand(0xFFFF, null, actor, actor.number, actor.actorIdent), ActionHandler {
  /**
   * Returns the actor
   */
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
    form.performAction(action, block)
    /*try {
      executeVoidTrigger(getTrigger());
    } catch (Exception e) {
      e.printStackTrace();
    }*/
  }

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param    action        the action to perform.
   */
  override fun performAsyncAction(action: Action) {
    form.performAsyncAction(action)
    /*try {
      executeVoidTrigger(getTrigger());
    } catch (Exception e) {
      e.printStackTrace();
    }*/
  }

  /**
   * Performs a void trigger
   *
   * @param    VKT_Type    the number of the trigger
   */
  override fun executeVoidTrigger(VKT_Type: Int) {
    when (VKT_Type) {
      VConstants.CMD_QUIT -> form.close(VWindow.CDE_QUIT)
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
