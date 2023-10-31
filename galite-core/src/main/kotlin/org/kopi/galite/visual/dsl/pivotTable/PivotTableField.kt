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
package org.kopi.galite.visual.dsl.pivotTable

import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.dsl.common.LocalizationWriter
import org.kopi.galite.visual.dsl.field.Field
import org.kopi.galite.visual.pivotTable.VPivotTableColumn

class PivotTableField<T>(override val domain: Domain<T>,
                         val init: PivotTableField<T>.() -> Unit,
                         ident: String? = null,
                         override val source: String?) : Field<T>(domain, ident) {

  /**
   * Dimension values
   */
  var dimensionRow: Boolean? = null
  var dimensionColumn: Boolean? = null
  fun initField() {
    init()
  }

  lateinit var model: VPivotTableColumn

  fun buildPivotTableColumn(): VPivotTableColumn {
    model = domain.buildPivotTableFieldModel(this).also { column ->
      column.label = label ?: ""
      column.dimensionRow = dimensionRow
      column.dimensionColumn = dimensionColumn
    }

    return model
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------
  /**
   * Generates localization for the field in the xml file
   */
  override fun genLocalization(writer: LocalizationWriter) {
    (writer as PivotTableLocalizationWriter).genField(ident, label, help)
  }
}
