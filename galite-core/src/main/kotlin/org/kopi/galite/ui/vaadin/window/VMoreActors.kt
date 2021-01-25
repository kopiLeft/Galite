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

import java.util.LinkedList

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.html.Div

class VMoreActors : Div() {

  /**
   * Returns `true` if there is no items to be shown.
   * @return `true` if there is no items to be shown.
   */
  fun isEmpty(): Boolean {
    TODO()
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Adds an extra actor to this more actors item.
   * @param actor The actor to be added.
   */
  fun <T> addActor(actor: T?) where T : Component?, T : HasEnabled? {
    TODO()
  }

  /**
   * Returns the list of actors contained in this panel.
   * @return The list of actors contained in this panel.
   */
  fun getActors(): LinkedList<Component> {
    TODO()
  }

  fun clear() {
    TODO()
  }

  /**
   * Removes the given actor from the menu.
   * @param actor The actor to be removed.
   */
  fun removeActor(actor: Component) {
    TODO()
  }
}
