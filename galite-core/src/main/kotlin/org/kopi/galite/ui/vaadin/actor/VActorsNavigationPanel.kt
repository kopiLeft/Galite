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
package org.kopi.galite.ui.vaadin.actor

import org.kopi.galite.ui.vaadin.menu.VNavigationColumn
import org.kopi.galite.ui.vaadin.menu.VNavigationMenu
import org.kopi.galite.ui.vaadin.menu.VNavigationPanel
import org.kopi.galite.visual.VActor

class VActorsNavigationPanel : VNavigationPanel() {
  init {
    className = "actors-navigationPanel"
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Adds the given actor to this navigation panel.
   * @param actor The actor to be added.
   */
  fun addActor(actor: Actor, action: VActor?) {
    var column: VNavigationColumn?
    val item = VActorNavigationItem(actor.text, actor.menu, actor.acceleratorKey, actor.modifiersKey, actor.icon, action)

    column = getColumn(if (isHelpMenu(item.menu)) "help" else item.menu)
    if (column == null) {
      // The header item is not created
      // we will create the navigation column and the header item
      column = VNavigationColumn(if (isHelpMenu(item.menu)) "help" else item.menu!!)
      column.className = "actor-navigationColumn"
      column.setHeader(item.menu!!)
    }
    // now we can add the actor item.
    column.addClickableItem(item)
    addColumn(column)
  }

  /**
   * Returns the navigation column having the given identifier.
   * @param ident The column identifier.
   * @return The navigation column.
   */
  protected fun getColumn(ident: String?): VNavigationColumn? {
    for (column in getColumns()) {
      if (column.ident != null && column.ident == ident) {
        return column
      }
    }
    return null
  }

  /**
   * Returns `true` if it is the help menu.
   * @param caption The menu caption.
   * @return `true` if it is the help menu.
   */
  protected fun isHelpMenu(caption: String?): Boolean {
    return caption != null && (caption.equals("help", ignoreCase = true)
            || caption.equals("aide", ignoreCase = true)
            || caption.equals("hilfe", ignoreCase = true))
  }
}
