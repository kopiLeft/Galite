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

package org.kopi.galite.visual.form

import javax.swing.SwingConstants

import org.kopi.galite.visual.visual.Constants

interface VConstants : Constants {
  companion object {

    // ---------------------------------------------------------------------
    // ACCESS
    // ---------------------------------------------------------------------
    const val ACS_HIDDEN = 0
    const val ACS_SKIPPED = 1
    const val ACS_VISIT = 2
    const val ACS_MUSTFILL = 4
    const val ACS_ACCESS = 1 + 2 + 4

    // ---------------------------------------------------------------------
    // MODES
    // ---------------------------------------------------------------------
    const val MOD_QUERY = 0
    const val MOD_INSERT = 1
    const val MOD_UPDATE = 2
    const val MOD_ANY = 1 + 2 + 4

    // ---------------------------------------------------------------------
    // TRIGGERED EVENTS (MAX 32)
    // ---------------------------------------------------------------------
    const val TRG_PREQRY = 0
    const val TRG_POSTQRY = 1
    const val TRG_PREDEL = 2
    const val TRG_POSTDEL = 3
    const val TRG_PREINS = 4
    const val TRG_POSTINS = 5
    const val TRG_PREUPD = 6
    const val TRG_POSTUPD = 7
    const val TRG_PRESAVE = 8
    const val TRG_PREREC = 9
    const val TRG_POSTREC = 10
    const val TRG_PREBLK = 11
    const val TRG_POSTBLK = 12
    const val TRG_VALBLK = 13
    const val TRG_VALREC = 14
    const val TRG_DEFAULT = 15
    const val TRG_INIT = 16
    const val TRG_RESET = 17
    const val TRG_CHANGED = 18
    const val TRG_ACCESS = 27
    const val TRG_AUTOLEAVE = 31
    const val TRG_POSTCHG = 19
    const val TRG_PREFLD = 20
    const val TRG_POSTFLD = 21
    const val TRG_PREVAL = 22
    const val TRG_VALFLD = 23
    const val TRG_FORMAT = 24
    const val TRG_PREDROP = 33
    const val TRG_POSTDROP = 34
    const val TRG_ACTION = 35
    const val TRG_PREFORM = 25
    const val TRG_POSTFORM = 26
    const val TRG_FLDACCESS = 28
    const val TRG_VALUE = 29
    const val TRG_QUITFORM = 30
    const val TRG_VOID = 0
    const val TRG_BOOLEAN = 1
    const val TRG_INT = 2
    const val TRG_OBJECT = 3
    const val TRG_PRTCD = 4
    const val TRG_CMDACCESS = 32

    // ---------------------------------------------------------------------
    // OPTIONS FOR BLOCKS
    // ---------------------------------------------------------------------
    const val BKO_NOINSERT = 1
    const val BKO_NODELETE = 2
    const val BKO_NOMOVE = 4
    const val BKO_INDEXED = 8
    const val BKO_ALWAYS_ACCESSIBLE = 16
    const val BKO_NOCHART = 32
    const val BKO_NODETAIL = 64

    // ---------------------------------------------------------------------
    // OPTIONS FOR FIELDS
    // ---------------------------------------------------------------------
    const val FDO_NOECHO = 1
    const val FDO_NOEDIT = 2
    const val FDO_TRANSIENT = 4
    const val FDO_DO_NOT_ERASE_ON_LOOKUP = 8
    const val FDO_SEARCH_MASK = 0x00F0
    const val FDO_SEARCH_NONE = 0x0000
    const val FDO_SEARCH_UPPER = 0x0010
    const val FDO_SEARCH_LOWER = 0x0020
    const val FDO_CONVERT_MASK = 0x0F00
    const val FDO_CONVERT_NONE = 0x0000
    const val FDO_CONVERT_UPPER = 0x0100
    const val FDO_CONVERT_LOWER = 0x0200
    const val FDO_CONVERT_NAME = 0x0400
    const val FDO_NODETAIL = 0x1000
    const val FDO_NOCHART = 0x2000
    const val FDO_SORT = 0x4000
    const val FDO_DYNAMIC_NL = 0x10000
    const val FDO_FIX_NL = 0x20000

    // ---------------------------------------------------------------------
    // SEARCH OPERATORS
    // ---------------------------------------------------------------------
    const val SOP_EQ = 0
    const val SOP_LT = 1
    const val SOP_GT = 2
    const val SOP_LE = 3
    const val SOP_GE = 4
    const val SOP_NE = 5

    // ---------------------------------------------------------------------
    // SEARCH OPERATORS NAMES
    // ---------------------------------------------------------------------
    val OPERATOR_NAMES = arrayOf("=", "<", ">", "<=", ">=", "<>")

    // ---------------------------------------------------------------------
    // SEARCH TYPE
    // ---------------------------------------------------------------------
    const val STY_NO_COND = 0 // no conditions for field
    const val STY_EXACT = 1 // requires exact match
    const val STY_MANY = 2 // many values can match

    // ---------------------------------------------------------------------
    // BORDERS
    // ---------------------------------------------------------------------
    const val BRD_NONE = 0
    const val BRD_LINE = 1
    const val BRD_RAISED = 2
    const val BRD_LOWERED = 3
    const val BRD_ETCHED = 4
    const val BRD_HIDDEN = 5

    // ---------------------------------------------------------------------
    // ALIGNMENT
    // ---------------------------------------------------------------------
    const val ALG_DEFAULT = SwingConstants.LEFT
    const val ALG_LEFT = SwingConstants.LEFT
    const val ALG_CENTER = SwingConstants.CENTER
    const val ALG_RIGHT = SwingConstants.RIGHT

    // ---------------------------------------------------------------------
    // POSITION
    // ---------------------------------------------------------------------
    const val POS_LEFT = 0
    const val POS_TOP = 1

    // ---------------------------------------------------------------------
    // PREDEFINED COMMANDS
    // ---------------------------------------------------------------------
    const val CMD_QUIT = 0

    // ---------------------------------------------------------------------
    // TRIGGER INFO
    // ---------------------------------------------------------------------
    val TRG_NAMES = arrayOf(
            "TRG_PREQRY", "TRG_POSTQRY", "TRG_PREDEL", "TRG_POSTDEL", "TRG_PREINS", "TRG_POSTINS",
            "TRG_PREUPD", "TRG_POSTUPD", "TRG_PRESAVE", "TRG_PREREC", "TRG_POSTREC", "TRG_PREBLK",
            "TRG_POSTBLK", "TRG_VALBLK", "TRG_VALREC", "TRG_DEFAULT", "TRG_INIT", "TRG_RESET",
            "TRG_CHANGED", "TRG_POSTCHG", "TRG_PREFLD", "TRG_POSTFLD", "TRG_PREVAL",
            "TRG_VALFLD", "TRG_FORMAT", "TRG_PREFORM", "TRG_POSTFORM", "TRG_ACCESS",
            "TRG_FLDACCESS", "TRG_VALUE", "TRG_AUTOLEAVE", "TRG_QUITFORM", "TRG_CMDACCESS",
            "TRG_PREDROP", "TRG_POSTDROP", "TRG_ACTION"
    )
    val TRG_TYPES = intArrayOf(
            TRG_PRTCD, TRG_PRTCD, TRG_PRTCD, TRG_PRTCD, TRG_PRTCD, TRG_PRTCD,
            TRG_PRTCD, TRG_PRTCD, TRG_PRTCD, TRG_VOID, TRG_VOID, TRG_VOID,
            TRG_VOID, TRG_VOID, TRG_VOID, TRG_VOID, TRG_VOID, TRG_BOOLEAN,
            TRG_BOOLEAN, TRG_VOID, TRG_VOID, TRG_VOID, TRG_VOID,
            TRG_VOID, TRG_VOID, TRG_VOID, TRG_VOID, TRG_BOOLEAN,
            TRG_OBJECT, TRG_OBJECT, TRG_BOOLEAN, TRG_BOOLEAN, TRG_BOOLEAN,
            TRG_VOID, TRG_VOID, TRG_VOID
    )

    // ---------------------------------------------------------------------
    //
    // ---------------------------------------------------------------------
    const val EMPTY_TEXT = ""
    const val RESOURCE_DIR = "org/kopi/galite/visual/util"

    // ---------------------------------------------------------------------
    // IMAGE DOCUMENT EXTENSIONS
    // ---------------------------------------------------------------------
    const val IMAGE_DOC_PDF = 0
    const val IMAGE_DOC_JPEG = 1
    const val IMAGE_DOC_TIF = 2
  }
}
