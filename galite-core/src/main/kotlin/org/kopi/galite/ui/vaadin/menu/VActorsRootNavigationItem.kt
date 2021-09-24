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
package org.kopi.galite.ui.vaadin.menu

import org.kopi.galite.ui.vaadin.actor.VActorsNavigationPanel

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.dom.Element

class VActorsRootNavigationItem : Button() {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  var menu: VNavigationMenu = VNavigationMenu(this)
  val rootIcon = Icon(VaadinIcon.ALIGN_JUSTIFY)

  init {
    className = "actors-rootNavigationItem"
    element.setAttribute("part" ,"rootNavigation")
   // rootIcon.addStyleDependentName("actors")
    icon = rootIcon
    menu.className = "actors-navigationMenu"
    this.addClickListener { onClick() }
    menu.addDialogCloseActionListener {
      menu.close()
      element.removeAttribute("part")
      element.setAttribute("part" ,"rootNavigation")
    }
  }

  fun onClick() {
   if (menu.isOpened) {
      menu.close()
      //parent.getElement().removeClassName("open")
    } else {
      menu.open()
     element.setAttribute("part" ,"rootNavigation-open")
    }
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  fun setCaption(text: String) {
    setText(text)
  }

  fun getCaption(): String? {
    return element.text
  }

  /**
   * Sets the actors navigation panel associated with this root navigation item.
   * @param panel The actors navigation item.
   */
  fun setActorsNavigationPanel(panel: VActorsNavigationPanel) {
    menu.setNavigationPanel(panel)
  }

  /**
   * Returns the icon DOM element.
   * @return The icon DOM element.
   */
  fun getIconElement(): Element? {
    return icon.element
  }
}
