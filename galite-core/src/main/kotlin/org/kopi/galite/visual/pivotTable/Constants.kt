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

package org.kopi.galite.visual.pivotTable

import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.Constants
import org.vaadin.addons.componentfactory.PivotTable.Renderer
import org.vaadin.addons.componentfactory.PivotTable.Aggregator

/**
 * Collects some constants for the pivot table implementation
 */
interface Constants : Constants {
  companion object {
    // ---------------------------------------------------------------------
    // PIVOT TABLE POSITION
    // ---------------------------------------------------------------------
    const val DIMENSION_NO_POSITION = 0
    const val DIMENSION_ROW = 1
    const val DIMENSION_COLUMN = 2

    // ---------------------------------------------------------------------
    // PIVOT TABLE Aggregator
    // ---------------------------------------------------------------------
    const val SUM_OVER_SUM = "Sum Over Sum"
    const val UPPER_BOUND = "80% Upper Bound"
    const val LOWER_BOUND = "80% Lower Bound"
    const val SUM_FRACTION_TOTALS = "Sum as Fraction of Total"
    const val SUM_FRACTION_ROWS = "Sum as Fraction of Rows"
    const val SUM_FRACTION_COLUMNS = "Sum as Fraction of Columns"
    const val COUNT_FRACTION_TOTALS = "Count as Fraction of Total"
    const val COUNT_FRACTION_ROWS = "Count as Fraction of Rows"
    const val COUNT_FRACTION_COLUMNS = "Count as Fraction of Columns"

    // ---------------------------------------------------------------------
    // PIVOT TABLE MODE
    // ---------------------------------------------------------------------
    const val MODE_INTERACTIVE = 0
    const val MODE_NONINTERACTIVE = 1

    // ---------------------------------------------------------------------
    // TRIGGERED EVENTS
    // ---------------------------------------------------------------------
    const val TRG_INIT = 0
    const val TRG_PRE_PIVOT_TABLE = 1
    const val TRG_POST_PIVOT_TABLE = 2
    const val TRG_VOID = VConstants.TRG_VOID
    const val TRG_OBJECT = VConstants.TRG_OBJECT

    // ---------------------------------------------------------------------
    // TRIGGER INFO
    // ---------------------------------------------------------------------
    val TRG_NAMES = arrayOf(
            "TRG_INIT",
            "TRG_PRE_PIVOT_TABLE",
            "TRG_POST_PIVOT_TABLE",
    )
    val TRG_TYPES = intArrayOf(
            TRG_VOID,
            TRG_VOID,
            TRG_VOID
    )
  }
}
