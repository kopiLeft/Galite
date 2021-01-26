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
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.Icon

class VMoreActors : Div() {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private val button = Button()
  private val icon = Icon("angle-double-right")
  private val menu = VMoreActorsMenu()

  init {
    button.icon = icon
    className = "more-actors"
    add(button)
    button.addClickListener { onClick() }
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Adds an extra actor to this more actors item.
   * @param actor The actor to be added.
   */
  fun <T> addActor(actor: T?) where T : Component?, T : HasEnabled? {
    if (actor != null) {
      menu.addActor(actor)
    }
  }

  /**
   * Returns the list of actors contained in this panel.
   * @return The list of actors contained in this panel.
   */
  protected fun getActors(): LinkedList<Component?> {
    val actors = LinkedList<Component?>()
    for (item in menu.getItems()) {
      actors.addLast(item.getActor())
    }
    return actors
  }

  /**
   * Removes the given actor from the menu.
   * @param actor The actor to be removed.
   */
  protected fun removeActor(actor: Component) {
    for (item in menu.getItems()) {
      if (item.getActor() == actor) {
        menu.removeItem(item)
      }
    }
  }

  /**
   * Returns `true` if there is no items to be shown.
   * @return `true` if there is no items to be shown.
   */
  protected fun isEmpty(): Boolean {
    return menu.isEmpty()
  }

  fun clear() {
    menu.clear()
  }

  fun onClick() {
    menu.createPopup()
    add(menu.popup)
    if (!menu.isEmpty()) {
        menu.openPopup()
        className = "open"
    }
  }
}
