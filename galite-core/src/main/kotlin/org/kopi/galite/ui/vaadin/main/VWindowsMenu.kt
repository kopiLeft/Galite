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

import com.vaadin.flow.component.tabs.Tabs

/**
 * The already opened windows menu.
 * The menu aims to show the opened windows by the user.
 * From this menu, the user can switch to another window.
 */
class VWindowsMenu(val vertical: Boolean) : Tabs() {

  init {
    orientation = if (vertical) Orientation.VERTICAL else Orientation.HORIZONTAL
    // Make sure that CSS styles specified for the default Menu classes
    // do not affect this menu
    className = "k-windowsMenu"
  }
}
