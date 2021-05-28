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
package org.kopi.galite.ui.vaadin.base

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div

/**
 * A scrollable vertical panel.
 */
class VScrollablePanel : Div {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var content = Div()

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates the scrollable panel instance.
   */
  constructor() : super() {
    initialize()
  }

  /**
   * Creates the scrollable panel instance.
   *
   * @param children the items to add to this component
   */
  constructor(vararg children: Component) : super() {
    initialize()
    this.add(*children)
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Sets up the correct styling
   *
   */
  private fun initialize() {
    content.style["display"] = "block"
    super.add(content)
    content.style["display"] = "table"
    content.setSizeFull()
  }

  fun clear() {
    removeAll()
  }

  override fun add(vararg components: Component) {
    if(components[0] is Grid<*>) {
      setSizeFull()
    }
    content.add(*components)
  }

  override fun remove(vararg components: Component) {
    content.remove(*components)
  }

  override fun removeAll() {
    content.removeAll()
  }

  override fun addComponentAtIndex(index: Int, component: Component) {
    content.addComponentAtIndex(index, component)
  }

  override fun addComponentAsFirst(component: Component) {
    content.addComponentAtIndex(0, component)
  }
}
