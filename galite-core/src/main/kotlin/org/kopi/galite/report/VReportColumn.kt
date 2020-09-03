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

import org.kopi.galite.util.LineBreaker

open abstract class VReportColumn {

  var ident: String?
  var label: String? = ""
  val help: String? = ""
  var options: Int
  var align: Int
  var groups: Int
  var function: VCalculateColumn
  var format: VCellFormat
  var visible = false
  var folded = false
  var addedAtRuntime = false
  var userDefinedLabel = false
  lateinit var styles: Array<ColumnStyle>
    get() {
      return if (styles == null) {
        val style = ColumnStyle()
        style.fontName = 0
        style.background = Constants.CLR_WHITE
        style.foreground = Constants.CLR_BLACK
        arrayOf(style)
      } else {
        styles
      }
    }
  var width = 0
  var height = 0

  constructor(ident: String?,
              options: Int,
              align: Int,
              groups: Int,
              function: VCalculateColumn,
              width: Int,
              height: Int,
              format: VCellFormat?) {
    this.ident = ident
    this.options = options
    this.align = align
    this.groups = groups
    this.function = function
    this.width = width
    this.height = height
    this.format = format!!
    visible = true
    folded = false
    addedAtRuntime = false
    userDefinedLabel = false

  }


  /**
   * Compare two objects of type Any
   *
   * @param        object1        the first operand of the comparison
   * @param        object2        the second operand of the comparison
   * @return        -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  abstract fun compareTo(object1: Any?, object2: Any?): Int

  /**
   *Returns True if column is hidden
   */
  fun isHidden(): Boolean {
    return (options and Constants.CLO_HIDDEN) > 0
  }

  open fun format(o: Any?): String? {
    return if (this.folded || o == null) {
      ""
    } else if (format != null) {
      format.format(o)
    } else if (height == 1) {
      val str = o.toString()
      val strLength = str.length
      if (strLength <= width) str else str.substring(0, width)
    } else {
      o.toString()
    }
  }

  open fun formatWithLineBreaker(o: Any?): String? {
    return LineBreaker.modelToText(format(o), width)
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION : NOT IMPLEMENTED YET
  // ----------------------------------------------------------------------

}

