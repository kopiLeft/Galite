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
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class VMoreActorsMenu : VerticalLayout() {

  init {
    className = "more-actors-menu"
    width = "300px"
    height = "300px"
    isMargin = true
    style["box-shadow"] = "0 0 5px rgba(0,0,0,0.5)"
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  fun addCloseHandler() {
    if (popup != null) {
      popup?.hide()
    }
  }

  /**
   * Adds an actor to this menu.
   * @param actor The actor to be added.
   */
  fun addActor(actor: Component) {
    val item = VMoreActorsItem()

    item.setActor(actor)
    addItem(item)
  }

  /**
   * Adds an item to this menu.
   * @param item The item to be added.
   */
  fun addItem(item: VMoreActorsItem) {
    items.add(item)
    add(item)
  }

  /**
   * Removes the given item.
   * @param item The item to be removed
   */
  fun removeItem(item: VMoreActorsItem?) {
    items.remove(item)
    remove(item)
  }

  fun createPopup() {
    if (popup == null) {
      popup = VMoreActorsPopup(this)
    }
  }
  /**
   * Shows the popup menu containing additional
   * @param parent The parent more actors item.
   */
  fun openPopup() {
    popup!!.show()
  }

  /**
   * Returns the list of items present in this menu.
   * @return The list of items present in this menu.
   */
  fun getItems() = items

  /**
   * Returns `true` if there is no items to be shown.
   * @return `true` if there is no items to be shown.
   */
  fun isEmpty() = items.isEmpty()

  fun clear() {
    items.clear()
  }

  /**
   * Closes the more actors menu.
   */
  fun close() {
    if (popup != null) {
      popup!!.hide()
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private val items = LinkedList<VMoreActorsItem>()
  var popup: VMoreActorsPopup? = null
}
