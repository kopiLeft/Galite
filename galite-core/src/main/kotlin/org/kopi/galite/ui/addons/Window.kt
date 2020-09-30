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

package org.kopi.galite.ui.addons

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * The window component.
 */
open class Window : VerticalLayout() {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Adds an actor to this window.
   * @param actor The actor to be added.
   */
  fun addActor(actor: Actor) {
    TODO()
  }

  fun replaceComponent(oldComponent: Component, newComponent: Component) {
    // component replacement are not supported for a window
  }

  override fun getComponentCount(): Int {
    return actors.size + if (content != null) 1 else 0
  }

  /**
   * Sets the window content.
   * @param content The window content.
   */
  fun setContent(content: Component) {
    this.content = content
    add(content)
  }

  /**
   * Returns the window content.
   * @return The window content.
   */
  fun getContent(): Component? {
    return content
  }

  operator fun iterator(): Iterator<Component> {
    val components: MutableList<Component> = actors.toMutableList()
    if (content != null) {
      components.add(content!!)
    }
    return components.iterator()
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  val actors: List<Actor> = listOf()
  private var content: Component? = null
}
