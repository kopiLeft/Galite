/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.label

import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon

/**
 * The sortable label component.
 */
open class SortableLabel() : Label() {
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val listeners = mutableListOf<SortableLabelListener>()
  private val sortIconNone = Icon(VaadinIcon.SORT)
  private val sortIconAsc = Icon(VaadinIcon.CARET_UP)
  private val sortIconDesc = Icon(VaadinIcon.CARET_DOWN)
  private val sortIcon = Div(sortIconNone)
  private var sortMode = 0

  /**
   * Switches the sort image.
   */
  protected open fun switchSortImage() {
    sortMode++
    if (sortMode > SORT_DESC) {
      sortMode = SORT_NONE
    }

    when (sortMode) {
      SORT_NONE -> {
        setIcon(sortIconNone)
      }
      SORT_ASC -> {
        setIcon(sortIconAsc)
      }
      SORT_DESC -> {
        setIcon(sortIconDesc)
      }
    }
  }

  private fun setIcon(icon: Icon) {
    sortIcon.removeAll()
    sortIcon.add(icon)
  }

  /**
   * Registers a new sortable label listener
   * @param l The listener to be registered.
   */
  open fun addSortableLabelListener(l: SortableLabelListener) {
    listeners.add(l)
  }

  /**
   * Removes a new sortable label listener
   * @param l The listener to be removed.
   */
  open fun removeSortableLabelListener(l: SortableLabelListener?) {
    listeners.remove(l)
  }

  /**
   * Is it a sortable label ?
   */
  var sortable = false
    set(value) {
      field = value

      if (value) {
        addStyleDependentName("sortable")
        add(sortIcon)
        sortIcon.addClickListener {
          // switch sort
          switchSortImage()

          // fire event.
          fireOnSort()
        }
      }
    }

  /**
   * Fires a sort event.
   */
  protected open fun fireOnSort() {
    for (l in listeners) {
      l.onSort()
    }
  }

  companion object {
    /**
     * Constants defining the current direction of the sort.
     */
    const val SORT_NONE = 0
    const val SORT_ASC = 1
    const val SORT_DESC = 2
  }
}
