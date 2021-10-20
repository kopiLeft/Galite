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

package org.kopi.galite.visual.chart

import org.kopi.galite.visual.visual.Action
import org.kopi.galite.visual.visual.ActionHandler
import org.kopi.galite.visual.visual.VActor
import org.kopi.galite.visual.visual.VCommand
import org.kopi.galite.visual.visual.VHelpGenerator

class VChartCommand(private val chart: VChart, actor: VActor)
  : VCommand(0xFFFF, null, actor, actor.number, actor.actorIdent), ActionHandler {

  override fun setEnabled(enabled: Boolean) {
    if (actor != null) {
      actor!!.isEnabled = enabled
      actor!!.number = trigger
      actor!!.handler = this
    }
  }

  override fun executeVoidTrigger(VKT_Type: Int?) {
    // TODO
  }

  override fun performAction(action: Action, block: Boolean) {
    chart.performAction(action, block)
  }

  override fun performAsyncAction(action: Action) {
    chart.performAsyncAction(action)
  }

  // --------------------------------------------------------------------
  // HELP HANDLING
  // --------------------------------------------------------------------

  override fun helpOnCommand(help: VHelpGenerator) {
    if (actor == null) {
      return
    }
    actor!!.helpOnCommand(help)
  }
}
