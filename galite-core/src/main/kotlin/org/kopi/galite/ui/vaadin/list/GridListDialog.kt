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
package org.kopi.galite.ui.vaadin.list

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasEnabled

/**
 * A list dialog
 * TODO: Implement this class with appropriate component
 */
open class GridListDialog : Component(), HasEnabled {

  /**
   * This is used to display a new button under the dialog.
   * No button will be drawn when it is `null`.
   */
  var newText: String? = null

  /**
   * The list dialog selection target.
   */
  enum class SelectionTarget {
    /**
     * Selects the current row and close the list.
     */
    CURRENT_ROW,

    /**
     * Navigates to the next row.
     */
    NEXT_ROW,

    /**
     * Navigates to the previous row.
     */
    PREVIOUS_ROW,

    /**
     * Navigates to the next page.
     */
    NEXT_PAGE,

    /**
     * Navigates to the previous page.
     */
    PREVIOUS_PAGE,

    /**
     * Navigates to the first row.
     */
    FIRST_ROW,

    /**
     * Navigates to the last row.
     */
    LAST_ROW
  }
}
