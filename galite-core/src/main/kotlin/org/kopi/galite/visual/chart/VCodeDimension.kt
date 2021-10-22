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

package org.kopi.galite.visual.chart

import org.kopi.galite.visual.l10n.FieldLocalizer

/**
 * Creates a chart code column.
 * @param ident   The column identifier.
 * @param format  The dimension format.
 * @param type    The column type.
 * @param source  The column localization source.
 * @param idents  The columns displayed labels.
 */
abstract class VCodeDimension(ident: String,
                              format: VColumnFormat?,
                              private val type: String,
                              private val source: String,
                              private val idents: Array<String>) : VDimension(ident, format) {
  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  override fun toString(value: Any?): String {
    return if (names != null) names!![getIndex(value)]!! else idents[getIndex(value)]
  }

  // ----------------------------------------------------------------------
  // ABSTRACT METHODS
  // ----------------------------------------------------------------------
  /**
   * Returns the index.of given object
   * @return The index.of given object
   */
  protected abstract fun getIndex(value: Any?): Int

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  override fun localize(loc: FieldLocalizer) {
    val localizer = loc.manager.getTypeLocalizer(source, type)
    names = Array(idents.size) { i ->
      localizer.getCodeLabel(idents[i])
    }
  }

  fun initLabels(labels: Array<String?>) {
    this.names = labels
  }

  protected var names: Array<String?>? = null // array of external representations
}
