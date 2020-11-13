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

import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.domain.Domain
import org.kopi.galite.field.Field
import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VTrigger

/**
 * This class represents the definition of a report field
 *
 * @param help        the help text
 * @param domain      the domain of this field
 * @param align       the alignement of the text
 * @param group       the grouping column
 * @param commands    the commands accessible in this field
 * @param triggers    the triggers executed by this field
 */
class RField<T : Comparable<T>>(override val domain: Domain<T>? = null): Field<T>(domain) {
  /** the ident of this field */
  private val ident: String get() = label

  var options: Int = 0

  var group: String? = null

  lateinit var commands: Array<VCommand>

  lateinit var triggers: Array<VTrigger>

  /**
   * Returns the parent
   */
  var report: VReport? = null

  /**
   * true if the field is hidden, false otherwise
   */
  private val isHidden: Boolean get() = options and Constants.CLO_HIDDEN > 0


  /** Field's help that describes the expected value of an input field */
  override var hidden: Boolean? = false
    set(value) {
      options = if(value == true) Constants.CLO_HIDDEN else Constants.CLO_VISIBLE
      field = value
    }

  var groupID = -1

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------
  /**
   * Generates localization for the field in the xml file
   */
  override fun genLocalization(writer: LocalizationWriter) {
    if (!isHidden) {
      (writer as ReportLocalizationWriter).genField(ident, label, help)
    }
  }
}
