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
package org.kopi.galite.ui.vaadin.main

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * Class for menu items in the already opened windows menu.
 *
 * @param title The window title.
 * @param window The window to be added.
 * @param container The container of the window.
 */
class VWindowsMenuItem(title : String, window : Component, container : VWindowContainer) : VerticalLayout() {

  init {
    val elements = VerticalLayout()
    elements.add(title)
    width = "350px"
    height = "350px"
    element.style["display"] = "inline-block"
    element.style["border"] = "1px solid #ddd"
    element.style["margin-right"] = "5px"
    // TODO(replace img by an interface from the opened window)
    val img = Image()
    img.src = "https://www.tmssoftware.com/site/img/webcore/vaadin.png"
    img.text = "test"
    img.setTitle("test")
    img.width = "320px"
    img.height = "320px"
    elements.add(img)

    this.add(elements)
    className = STYLENAME_DEFAULT
    element.style.set("whiteSpace", "nowrap")
    // adding listener on the item to open the form in the container
    addClickListener {
      window.isVisible = true
      container.showWindow(window)
    }
  }

  companion object {
    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------
    private const val STYLENAME_DEFAULT = "item"
  }
}
