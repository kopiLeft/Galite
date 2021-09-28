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
package org.kopi.galite.testing

import org.kopi.galite.visual.dsl.common.Actor
import org.kopi.galite.visual.ui.vaadin.main.MainWindow
import org.kopi.galite.visual.ui.vaadin.visual.DActor
import org.kopi.galite.visual.ui.vaadin.window.VActorPanel

import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._get

/**
 * Triggers a specific command.
 *
 * @param duration how much time it takes for the command to finish its action.
 * @receiver the actor of the command to trigger.
 */
fun Actor.triggerCommand(duration: Long = 300) {
  val mainWindow = _get<MainWindow>()
  val actors = mainWindow
    ._get<VActorPanel> {  }
    ._find<DActor> {  }

  val actor = actors.single {
    it.getModel() == this.model
  }

  actor._clickAndWait(200)

  // Wait after completing the view creation.
  // VWindowController.doNotModal() is crating the view synchronously.
  Thread.sleep(duration)
}
