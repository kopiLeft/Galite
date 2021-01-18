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

import java.io.Serializable

import org.kopi.galite.l10n.ChartLocalizer
import org.kopi.galite.l10n.FieldLocalizer

/**
 * Creates a new chart column from its identifier.
 *
 * @param ident The column identifier.
 */
abstract class VColumn internal constructor(var ident: String) : Serializable {
  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  /**
   * Generates the help for this column.
   * @param help The help generator.
   */
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
  fun localize(parent: ChartLocalizer) {
    if (ident != "") {
      val loc: FieldLocalizer = parent.getFieldLocalizer(ident)
      label = loc.getLabel()
      help = loc.getHelp()
      localize(loc)
    }
  }

  /**
   * Localizes this column
   *
   * @param     loc         the caller localizer
   */
  protected open fun localize(loc: FieldLocalizer) {
    // by default nothing to do
  }

  /**
   * The column label.
   */
  var label: String? = null

  /**
   * The column help.
   */
  var help: String? = null
}
