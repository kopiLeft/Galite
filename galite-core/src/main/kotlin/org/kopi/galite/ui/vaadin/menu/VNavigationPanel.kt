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

import java.util.LinkedList

import com.vaadin.flow.component.orderedlayout.FlexLayout

open class VNavigationPanel : FlexLayout() {
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val columns = LinkedList<VNavigationColumn>()

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Adds the given navigation column to this navigation panel.
   * @param column The column to be added.
   */
  open fun addColumn(column: VNavigationColumn) {
    columns.add(column)
    add(column)
  }

  /**
   * Removes the given column from this navigation panel.
   * @param column The column to be removed.
   */
  open fun removeColumn(column: VNavigationColumn) {
    remove(column)
    columns.remove(column)
  }

  /**
   * Returns the navigation columns contained in this panel.
   * @return The navigation columns contained in this panel.
   */
  open fun getColumns(): List<VNavigationColumn> {
    return columns
  }
}
