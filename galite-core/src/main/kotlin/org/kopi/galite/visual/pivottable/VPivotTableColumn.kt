/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2023 kopiRight Managed Solutions GmbH, Wien AT
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

import org.kopi.galite.visual.dsl.pivottable.Dimension
import org.kopi.galite.visual.l10n.FieldLocalizer
import org.kopi.galite.visual.l10n.PivotTableLocalizer
import org.kopi.galite.visual.report.VCellFormat

/**
 * Represents a pivot table column description
 * @param    ident        The identifier of the field
 * @param    position     The position of the dimension field
 * @param    format        format of the cells
 */
abstract class VPivotTableColumn(val ident: String?,
                                 val position: Dimension.Position?,
                                 protected var format: VCellFormat?) {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  var label: String = ""
  var help: String? = null

  open fun format(o: Any?): String {
    return if (format != null) {
      format!!.format(o)
    } else {
      o.toString()
    }
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  /**
   * Localizes this field
   *
   * @param     parent         the caller localizer
   */
  open fun localize(parent: PivotTableLocalizer) {
    if (ident != "") {
      val loc: FieldLocalizer = parent.getFieldLocalizer(ident!!)

      label = loc.getLabel() ?: ""
    }
  }

  fun helpOnColumn(help: VHelpGenerator) {
    help.helpOnColumn(label, this.help)
  }
}
