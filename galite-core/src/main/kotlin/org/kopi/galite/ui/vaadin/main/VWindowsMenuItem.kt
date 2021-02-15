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
import com.vaadin.flow.component.button.Button
import org.kopi.galite.ui.vaadin.common.VContent

/**
 * Class for menu items in the already opened windows menu.
 */
class VWindowsMenuItem(title : String, window : Component, container : VWindowContainer) : Button(title) {

  init {

    addClickListener {
      ui.ifPresent { myUi ->
      myUi.access {
        container.addWindow(window, title)
        container.showWindow(window)
      }
    }
    }
  }

  companion object {
    //---------------------------------------------------
    // DATA MEMBERS
    //---------------------------------------------------
    private const val STYLENAME_DEFAULT = "item"
  }

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates a new already opened window item.
   */
  init {
    element.setAttribute("name",STYLENAME_DEFAULT)
    element.setAttribute("whiteSpace","nowrap")
  }
}