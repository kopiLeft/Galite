/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH
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

package org.kopi.galite.report

object Constants : org.kopi.galite.ui.common.Constants {
  // --------------------------------------------------------------------
  // COLUMN OPTIONS
  // --------------------------------------------------------------------
  const val CLO_VISIBLE = 0
  const val CLO_HIDDEN = 1

  // ---------------------------------------------------------------------
  // ALIGNMENT
  // ---------------------------------------------------------------------
  const val ALG_DEFAULT = 0
  const val ALG_LEFT = 1
  const val ALG_CENTER = 2
  const val ALG_RIGHT = 4

  // ---------------------------------------------------------------------
  // COLOR
  // ---------------------------------------------------------------------
  const val CLR_WHITE = 0
  const val CLR_BLACK = 1
  const val CLR_RED = 2
  const val CLR_GREEN = 3
  const val CLR_BLUE = 4
  const val CLR_YELLOW = 5
  const val CLR_PINK = 6
  const val CLR_CYAN = 7
  const val CLR_GRAY = 8

  // ---------------------------------------------------------------------
  // PRINT OPTIONS
  // ---------------------------------------------------------------------
  const val SUM_AT_HEAD = 1
  const val SUM_AT_TAIL = 2

  // ---------------------------------------------------------------------
  // TRIGGERED EVENTS (MAX 32)
  // ---------------------------------------------------------------------
  const val TRG_PREREPORT = 0
  const val TRG_POSTREPORT = 1
  const val TRG_INIT = 2
  const val TRG_FORMAT = 3
  const val TRG_COMPUTE = 4
  const val TRG_CMDACCESS = 5
  //  val TRG_VOID: Int = org.kopi.vkopi.lib.form.VConstants.TRG_VOID
  //  val TRG_OBJECT: Int = org.kopi.vkopi.lib.form.VConstants.TRG_OBJECT
  //  val TRG_BOOLEAN: Int = org.kopi.vkopi.lib.form.VConstants.TRG_BOOLEAN

  // ---------------------------------------------------------------------
  // CELL STATE
  // ---------------------------------------------------------------------
  const val STA_SEPARATOR = -2
  const val STA_FOLDED = -1
  const val STA_STANDARD = 0
  const val STA_EMPTY = 1
  const val STA_NEGATIVE = 2
  const val STA_NULL = 3
  const val STA_DEFAULT = 4

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
          "TRG_PREREPORT", "TRG_POSTREPORT", "TRG_INIT", "TRG_FORMAT", "TRG_COMPUTE", "TRG_CMDACCESS")
  //val TRG_TYPES = intArrayOf(
  //       TRG_VOID, TRG_VOID, TRG_VOID, TRG_OBJECT, TRG_OBJECT, TRG_BOOLEAN)
}
