/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.ui.vaadin.window

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.menubar.MenuBar

import org.kopi.galite.ui.vaadin.base.Styles

/**
 * The actor components container.
 */
@CssImport("./styles/galite/vActorPanelStyles.css")
class VActorPanel : MenuBar() {
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  init {
    className = Styles.WINDOW_VIEW_ACTORS
    this.setId("actors")
    this.setWidthFull()
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
}
