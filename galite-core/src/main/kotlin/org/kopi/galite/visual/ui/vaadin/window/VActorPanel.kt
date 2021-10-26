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
package org.kopi.galite.visual.ui.vaadin.window

import org.kopi.galite.visual.ui.vaadin.actor.VActorsNavigationPanel
import org.kopi.galite.visual.ui.vaadin.base.Styles
import org.kopi.galite.visual.ui.vaadin.menu.VActorsRootNavigationItem
import org.kopi.galite.visual.ui.vaadin.menu.VNavigationMenu

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.menubar.MenuBar

/**
 * The actor components container.
 */
class VActorPanel : MenuBar() {
  private val actorsNavigationItem = VActorsRootNavigationItem()
  private val menu = Div()

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  init {
    className = Styles.WINDOW_VIEW_ACTORS
    menu.element.setAttribute("id", "menu")
    this.setId("actors")
    menu.add(actorsNavigationItem)
    this.addItem(menu)
  }

  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  /**
   * Adds an actor to this actor panel.
   * @param actor The actor to be added.
   */
  fun addActor(actor: Component) {
    this.addItem(actor)
  }

  /**
   * Adds the actors menu to be shown.
   * @param panel The menu to be shown.
   */
  fun addActorsNavigationPanel(panel: VActorsNavigationPanel) {
    actorsNavigationItem.setActorsNavigationPanel(panel)
  }

  val navigationMenu get(): VNavigationMenu = actorsNavigationItem.menu
}
