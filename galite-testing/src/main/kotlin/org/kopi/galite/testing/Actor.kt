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
package org.kopi.galite.testing

import org.kopi.galite.visual.dsl.common.Actor
import org.kopi.galite.visual.ui.vaadin.actor.VActorNavigationItem
import org.kopi.galite.visual.ui.vaadin.actor.VActorsNavigationPanel
import org.kopi.galite.visual.ui.vaadin.base.Utils
import org.kopi.galite.visual.ui.vaadin.main.MainWindow
import org.kopi.galite.visual.ui.vaadin.menu.VActorsRootNavigationItem
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
  val actor = findActor()

  actor._clickAndWait(20)

  // Wait after completing the view creation.
  // VWindowController.doNotModal() is crating the view synchronously.
  Thread.sleep(duration)
}

/**
 * Finds and returns the actor's component.
 *
 * @receiver the actor to find.
 */
fun Actor.findActor(): DActor {
  val mainWindow = _get<MainWindow>()
  val actors = mainWindow
    ._get<VActorPanel>()
    ._find<DActor>()

  return actors.single {
    it.getModel() == this
  }
}

/**
 * Finds and returns the navigation item of this actor.
 */
fun Actor.getNavigationItem(duration: Long = 200): VActorNavigationItem {
  val actorsNavigationPanel = _find<VActorsNavigationPanel>().singleOrNull() ?: openActorsNavigationPanel(duration)
  val navigationItems = actorsNavigationPanel._find<VActorNavigationItem>()

  return navigationItems.single {
    it.getCaption() == this.label
            && it.menu == this.menu.label
            && it._icon == Utils.getVaadinIcon(this.icon?.iconName)
  }
}

/**
 * Finds, opens and returns the actors' navigation panel.
 */
fun openActorsNavigationPanel(duration: Long = 200): VActorsNavigationPanel {
  val mainWindow = _get<MainWindow>()
  val actorsRootNavigationItem = mainWindow._get<VActorsRootNavigationItem>()

  actorsRootNavigationItem._clickAndWait(duration)

  return _get<VActorsNavigationPanel>()
}
