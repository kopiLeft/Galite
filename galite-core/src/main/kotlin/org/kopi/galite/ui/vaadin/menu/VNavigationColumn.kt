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

import org.kopi.galite.ui.vaadin.actor.VActorNavigationItem
import org.kopi.galite.ui.vaadin.actor.VHeaderNavigationItem

import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class VNavigationColumn(val ident : String?) : VerticalLayout(), HasStyle {

  init {
    width = "calc(25% - 3px)"
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val items = ArrayList<VActorNavigationItem>()
  private val header = VHeaderNavigationItem()

  /**
   * Sets the header item of this navigation column.
   * @param header The header item.
   */
  fun setHeader(headerName: String?) {
    header.setCaption(headerName)
    add(header)
  }

  /**
   * Adds the given clickable item to this navigation column.
   * @param item The clickable item to be added.
   */
  fun addClickableItem(item: VActorNavigationItem) {
    items.add(item)
    add(item)
  }
}
