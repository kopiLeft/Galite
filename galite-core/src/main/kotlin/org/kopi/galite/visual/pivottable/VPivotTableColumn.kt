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

/**
 * Represents a pivot table column description
 * @param    ident        The identifier of the field
 * @param    function    An (optional) function
 * @param    position     The position of the dimension field
 */
abstract class VPivotTableColumn(val ident: String?,
                                 val function: VCalculateColumn?,
                                 val position: Dimension.Position?) {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  var label: String = ""
  var help: String? = null

  open fun format(o: Any?): String {
    return o.toString()
  }
  /**
   * Compare two objects.
   *
   * @param    object1    the first operand of the comparison
   * @param    object2    the second operand of the comparison
   * @return    -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  abstract fun compareTo(object1: Any, object2: Any): Int

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

  /**
   * Localizes this field
   *
   * @param     parentLocalizer         the caller localizer
   */
  protected open fun localize(parentLocalizer: FieldLocalizer) {
    // by default nothing to do
  }

  fun helpOnColumn(help: VHelpGenerator) {
    help.helpOnColumn(label, this.help)
  }
}
