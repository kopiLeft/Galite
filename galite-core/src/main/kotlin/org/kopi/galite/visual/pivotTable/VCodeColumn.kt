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

import kotlin.math.max

import org.kopi.galite.visual.l10n.FieldLocalizer
import org.kopi.galite.visual.l10n.TypeLocalizer

/**
 * Represents a report column description
 *
 * @param     ident           The column identifier
 * @param     options         The column options as bitmap
 * @param     align           The column alignment
 * @param     groups          The index of the column grouped by this one or -1
 * @param     function        An (optional) summation function
 */
abstract class VCodeColumn(ident: String?,
                           private val type: String?,
                           private val source: String?,
                           options: Int, align: Int,
                           groups: Int, function: VCalculateColumn?,
                           width: Int,
                           format: VCellFormat?,
                           private val idents: Array<String>)
           : VReportColumn(ident,
                           options,
                           align,
                           groups,
                           function,
                           width,
                           1,
                           format) {

  protected var names: Array<String?>? = null // array of external representations

  /**
   * Compares two objects.
   *
   * @param    object1    the first operand of the comparison
   * @param    object2    the second operand of the comparison
   * @return    -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  abstract override fun compareTo(object1: Any, object2: Any): Int

  /**
   * Return a string representation.
   */
  override fun format(o: Any?): String {
    return if (isFolded || o == null) {
      ""
    } else {
      format?.format(o) ?: if (names != null) names!![getIndex(o)]!! else idents[getIndex(o)]
    }
  }

  /**
   * Get the index of the value.
   */
  abstract fun getIndex(value: Any): Int

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  /**
   * Localizes this field
   *
   * @param     parentLocalizer         the caller localizer
   */
  override fun localize(parentLocalizer: FieldLocalizer) {
    val loc: TypeLocalizer = parentLocalizer.manager.getTypeLocalizer(source, type)
    names = Array(idents.size) { i ->
      val label = loc.getCodeLabel(idents[i])
      this.width = max(this.width, label.length)
      label
    }
  }

  fun initLabels(labels: Array<String>) {
    names = labels.map {
      this.width = max(this.width, it.length)
      it
    }.toTypedArray()
  }
}
