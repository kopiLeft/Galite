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

import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.Constants

/**
 * Collects some constants for the pivot table implementation
 */
interface Constants : Constants {
  companion object {
    // ---------------------------------------------------------------------
    // PIVOT TABLE RENDERER
    // ---------------------------------------------------------------------
    const val DEFAULT_RENDERER = "Table"
    const val TABLE_BARCHART = "Table Barchart"
    const val TABLE_HEATMAP = "Heatmap"
    const val ROW_HEATMAP = "Row Heatmap"
    const val COL_HEATMAP = "Col Heatmap"
    const val HORIZONTAL_BAR_CHART = "Horizontal Bar Chart"
    const val HORIZONTAL_STACKED_BAR_CHART = "Horizontal Stacked Bar Chart"
    const val BAR_CHART = "Bar Chart"
    const val STACKED_BAR_CHART = "Stacked Bar Chart"
    const val LINE_CHART = "Line Chart"
    const val AREA_CHART = "Area Chart"
    const val SCATTER_CHART = "Scatter Chart"
    const val TSV_EXPORT = "TSV Export"

    // ---------------------------------------------------------------------
    // PIVOT TABLE Aggregator
    // ---------------------------------------------------------------------
    const val DEFAULT_AGGREGATOR = "Count"
    const val DEFAULT_AGGREGATE_COLUMN = ""
    const val COUNT_UNIQUE_VALUES = "Count Unique Values"
    const val LIST_UNIQUE_VALUES = "List Unique Values"
    const val SUM = "Sum"
    const val INTEGER_SUM = "Integer Sum"
    const val AVERAGE = "Average"
    const val MEDIAN = "Median"
    const val SAMPLE_VARIANCE = "Sample Variance"
    const val SAMPLE_STANDARD_DEVIATION = "Sample Standard Deviation"
    const val MINIMUM = "Minimum"
    const val MAXIMUM = "Maximum"
    const val FIRST = "First"
    const val LAST = "Last"
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