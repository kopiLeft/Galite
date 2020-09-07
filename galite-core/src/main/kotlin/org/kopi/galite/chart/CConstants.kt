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

package org.kopi.galite.chart

import org.kopi.galite.form.VConstants
import org.kopi.galite.visual.Constants

/**
 * Collects some constants for the chart implementation
 */
interface CConstants : Constants {
  companion object {
    // ---------------------------------------------------------------------
    // CHART TYPES
    // ---------------------------------------------------------------------
    const val TYPE_PIE = 0
    const val TYPE_COLUMN = 1
    const val TYPE_BAR = 2
    const val TYPE_LINE = 3
    const val TYPE_AREA = 4

    // ---------------------------------------------------------------------
    // EMPTY TEXT
    // ---------------------------------------------------------------------
    const val EMPTY_TEXT = ""

    // ---------------------------------------------------------------------
    // TRIGGERED EVENTS
    // ---------------------------------------------------------------------
    const val TRG_PRECHART = 0
    const val TRG_POSTCHART = 1
    const val TRG_CHARTTYPE = 2
    const val TRG_INIT = 3
    const val TRG_FORMAT = 4
    const val TRG_COLOR = 5
    const val TRG_CMDACCESS = 6
    const val TRG_VOID = VConstants.TRG_VOID
    const val TRG_OBJECT = VConstants.TRG_OBJECT
    const val TRG_BOOLEAN = VConstants.TRG_BOOLEAN

    // ---------------------------------------------------------------------
    // PREDEFINED COMMANDS
    // ---------------------------------------------------------------------
    const val CMD_QUIT = 0
    const val CMD_PRINT = 1
    const val CMD_PREVIEW = 2
    const val CMD_EXPORT_CSV = 3
    const val CMD_EXPORT_XLS = 4
    const val CMD_EXPORT_PDF = 5
    const val CMD_HELP = 6
    const val CMD_EXPORT_XLSX = 7

    // ---------------------------------------------------------------------
    // TRIGGER INFO
    // ---------------------------------------------------------------------
    val TRG_NAMES = arrayOf(
            "TRG_PRECHART",
            "TRG_POSTCHART",
            "TRG_CHARTTYPE",
            "TRG_INIT",
            "TRG_FORMAT",
            "TRG_COLOR",
            "TRG_CMDACCESS"
    )
    val TRG_TYPES = intArrayOf(
            TRG_VOID,
            TRG_VOID,
            TRG_OBJECT,
            TRG_VOID,
            TRG_OBJECT,
            TRG_OBJECT,
            TRG_BOOLEAN
    )
  }
}
