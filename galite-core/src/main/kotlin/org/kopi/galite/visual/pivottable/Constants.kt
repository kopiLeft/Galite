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

package org.kopi.galite.visual.pivottable

import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.Constants

interface Constants : Constants {
  companion object {

    // ---------------------------------------------------------------------
    // TRIGGERED EVENTS (MAX 32)
    // ---------------------------------------------------------------------
    const val TRG_PREPIVOT = 0
    const val TRG_POSTPIVOT = 1
    const val TRG_INIT = 2
    const val TRG_FORMAT = 3
    const val TRG_COMPUTE = 4
    const val TRG_CMDACCESS = 5
    const val TRG_VOID: Int = VConstants.TRG_VOID
    const val TRG_OBJECT: Int = VConstants.TRG_OBJECT
    const val TRG_BOOLEAN: Int = VConstants.TRG_BOOLEAN

    // ---------------------------------------------------------------------
    // PREDEFINED COMMANDS
    // ---------------------------------------------------------------------
    const val CMD_QUIT = 0
    const val CMD_PRINT = 1
    const val CMD_PREVIEW = 2
    const val CMD_PRINT_OPTIONS = 3
    const val CMD_EXPORT_CSV = 4
    const val CMD_EXPORT_XLS = 5
    const val CMD_EXPORT_XLSX = 6
    const val CMD_EXPORT_PDF = 7
    const val CMD_FOLD = 8
    const val CMD_UNFOLD = 9
    const val CMD_SORT = 10
    const val CMD_FOLD_COLUMN = 11
    const val CMD_UNFOLD_COLUMN = 12
    const val CMD_COLUMN_INFO = 13
    const val CMD_OPEN_LINE = 14
    const val CMD_REMOVE_CONFIGURATION = 15
    const val CMD_LOAD_CONFIGURATION = 16
    const val CMD_HELP = 17

    // ---------------------------------------------------------------------
    // TRIGGER INFO
    // ---------------------------------------------------------------------
    val TRG_NAMES = arrayOf(
      "TRG_PREPIVOT", "TRG_POSTPIVOT", "TRG_INIT", "TRG_FORMAT", "TRG_COMPUTE", "TRG_CMDACCESS")
    val TRG_TYPES = intArrayOf(
      TRG_VOID, TRG_VOID, TRG_VOID, TRG_OBJECT, TRG_OBJECT, TRG_BOOLEAN
    )
  }
}
