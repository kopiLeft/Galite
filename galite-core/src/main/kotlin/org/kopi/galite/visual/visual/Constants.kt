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

package org.kopi.galite.visual.visual

interface Constants {
  companion object {
    // --------------------------------------------------------------------
    // MODEL TYPES
    // --------------------------------------------------------------------
    const val MDL_UNKNOWN = 0
    const val MDL_HELP = 5
    const val MDL_FORM = 10
    const val MDL_REPORT = 20
    const val MDL_PREVIEW = 30
    const val MDL_MENU_TREE = 40
    const val MDL_CHART = 50
    const val MDL_ITEM_TREE = 60

    // --------------------------------------------------------------------
    // STANDARD FONT FAMILIES
    // --------------------------------------------------------------------
    const val FNT_FIXED_WIDTH = "dialoginput"
    const val FNT_PROPORTIONAL = "helvetica"

    // ---------------------------------------------------------------------
    // Predefined triggers
    // ---------------------------------------------------------------------
    const val PRE_AUTOFILL = -3

    // ---------------------------------------------------------------------
    // Predefined commands
    // ---------------------------------------------------------------------
    const val CMD_AUTOFILL = -3
    const val CMD_HELP = -7
    const val CMD_GOTO_SHORTCUTS = -8

    // ---------------------------------------------------------------------
    // Known bug work-arounds
    // ---------------------------------------------------------------------
    const val BUG_JDC_4103341 = true

    // ---------------------------------------------------------------------
    // Resource Dir
    // ---------------------------------------------------------------------
    const val RESOURCE_DIR = "org/kopi/galite/visual/util"
  }
}
