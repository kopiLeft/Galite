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
package org.kopi.galite.visual.ui.vaadin.base

/**
 * Collects needed constants.
 */
interface VConstants {
  companion object {
    // --------------------------------------------------
    // ACCESS
    // --------------------------------------------------
    // access flags
    const val ACS_HIDDEN = 0
    const val ACS_SKIPPED = 1
    const val ACS_VISIT = 2
    const val ACS_MUSTFILL = 4
    const val ACS_ACCESS = 1 + 2 + 4

    // record info flags
    const val RCI_FETCHED = 0x00000001
    const val RCI_CHANGED = 0x00000002
    const val RCI_DELETED = 0x00000004
    const val RCI_TRAILED = 0x00000008

    // menus types
    const val MAIN_MENU = -1
    const val USER_MENU = -2
    const val ADMIN_MENU = -3
    const val BOOKMARK_MENU = -4
  }
}
