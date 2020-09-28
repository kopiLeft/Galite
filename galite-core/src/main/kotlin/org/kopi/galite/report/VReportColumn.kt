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

package org.kopi.galite.report

import org.kopi.galite.l10n.FieldLocalizer
import org.kopi.galite.l10n.ReportLocalizer
import org.kopi.galite.util.LineBreaker

/**
 * Constructs a report column description
 *
 * @param    ident        The identifier of the field
 * @param    options        The column options as bitmap
 * @param    align        The column alignment
 * @param    groups        The index of the column grouped by this one or -1
 * @param    function    An (optional) function
 * @param    width        The width of a cell in characters
 * @param    height        The height of a cell in characters
 * @param    format        format of the cells
 */
abstract class VReportColumn(
        private val ident: String,
        val options: Int,
        val align: Int,
        val groups: Int,
        val function: VCalculateColumn?,
        var width: Int,
        var height: Int,
        protected var format: VCellFormat?) {

  /**
   * Returns true if this Column is hidden
   */
  open fun isHidden(): Boolean {
    return options and Constants.CLO_HIDDEN > 0
  }

  /**
   * Returns the width of cells in this column in characters
   */
  open fun getPrintedWidth(): Double {
    return width.toDouble()
  }

  open fun format(o: Any?): String {
    return if (folded || o == null) {
      ""
    } else if (format != null) {
      format!!.format(o)!!
    } else if (height == 1) {
      val str = o.toString()
      val strLength = str.length
      if (strLength <= width) str else str.substring(0, width)
    } else {
      o.toString()
    }
  }

  fun formatWithLineBreaker(o: Any?): String {
    return LineBreaker.modelToText(format(o), width)
  }

  /**
   * Compare two objects.
   *
   * @param    o1    the first operand of the comparison
   * @param    o2    the second operand of the comparison
   * @return    -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  abstract fun compareTo(o1: Any, o2: Any): Int

  open fun formatColumn(exporter: PExport, index: Int) {
    exporter.formatStringColumn(this, index)
  }

  fun helpOnColumn(help: VHelpGenerator) {
    help.helpOnColumn(label, this.help)
  }
  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  /**
   * Localizes this field
   *
   * @param     parent         the caller localizer
   */
  fun localize(parent: ReportLocalizer) {
    if (!isHidden() && ident != "") {
      val loc: FieldLocalizer = parent.getFieldLocalizer(ident)

      label = loc.getLabel()
      help = loc.getHelp()
      localize(loc)
    }
  }

  /**
   * Localizes this field
   *
   * @param     parentLocalizer         the caller localizer
   */
  protected open fun localize(parentLocalizer: FieldLocalizer) {
    // by default nothing to do
  }

   fun getStyles(): Array<ColumnStyle> {
    return if (styles == null) {
      val style = ColumnStyle()
      style.fontName = 0
      style.background = Constants.CLR_WHITE
      style.foreground = Constants.CLR_BLACK
      arrayOf(style)
    } else {
      styles!!
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  var label: String = ""
  private var help: String? = null
  var visible: Boolean = true
  var folded: Boolean = false
  var addedAtRuntime: Boolean = false
  var userDefinedLabel: Boolean = false
  private var styles: Array<ColumnStyle>? = null
}
