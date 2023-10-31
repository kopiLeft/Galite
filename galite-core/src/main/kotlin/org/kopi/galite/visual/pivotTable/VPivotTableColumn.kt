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

package org.kopi.galite.visual.pivotTable

import org.kopi.galite.visual.l10n.FieldLocalizer
import org.kopi.galite.visual.l10n.PivotTableLocalizer

/**
 * Represents a pivot table column description
 *
 * @param    ident        The identifier of the field
 *
 */
class VPivotTableColumn(val ident: String?) {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  var label: String = ""
  var help: String? = null
  var dimensionRow: Boolean? = null
  var dimensionColumn: Boolean? = null

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  /**
   * Localizes this field
   *
   * @param     parent         the caller localizer
   */
  fun localize(parent: PivotTableLocalizer) {
    if (ident != "") {
      val loc: FieldLocalizer = parent.getFieldLocalizer(ident!!)

      label = loc.getLabel() ?: ""
    }
  }

  fun helpOnColumn(help: VHelpGenerator) {
    help.helpOnColumn(label, this.help)
  }
}
