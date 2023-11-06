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
    const val POS_MEASURE = 0
    const val POS_DIMENSION_ROW = 1
    const val POS_DIMENSION_COL = 2

    // ---------------------------------------------------------------------
    // PIVOT TABLE RENDERER
    // ---------------------------------------------------------------------
    const val DEFAULT_RENDERER = Renderer.TABLE
    const val TABLE_BARCHART = Renderer.TABLE_BARCHART
    const val TABLE_HEATMAP = Renderer.TABLE_HEATMAP
    const val ROW_HEATMAP = Renderer.ROW_HEATMAP
    const val COL_HEATMAP = Renderer.COL_HEATMAP
    const val HORIZONTAL_BAR_CHART = Renderer.HORIZONTAL_BAR_CHART
    const val HORIZONTAL_STACKED_BAR_CHART = Renderer.HORIZONTAL_STACKED_BAR_CHART
    const val BAR_CHART = Renderer.BAR_CHART
    const val STACKED_BAR_CHART = Renderer.STACKED_BAR_CHART
    const val LINE_CHART = Renderer.LINE_CHART
    const val AREA_CHART = Renderer.AREA_CHART
    const val SCATTER_CHART = Renderer.SCATTER_CHART
    const val TSV_EXPORT = Renderer.TSV_EXPORT

    // ---------------------------------------------------------------------
    // PIVOT TABLE Aggregator
    // ---------------------------------------------------------------------
    const val DEFAULT_AGGREGATOR = Aggregator.COUNT
    const val DEFAULT_AGGREGATE_COLUMN = ""
    const val COUNT_UNIQUE_VALUES = Aggregator.COUNT_UNIQUE_VALUES
    const val LIST_UNIQUE_VALUES = Aggregator.LIST_UNIQUE_VALUES
    const val SUM = Aggregator.SUM
    const val INTEGER_SUM = Aggregator.INTEGER_SUM
    const val AVERAGE = Aggregator.AVERAGE
    const val MEDIAN = Aggregator.MEDIAN
    const val SAMPLE_VARIANCE = Aggregator.SAMPLE_VARIANCE
    const val SAMPLE_STANDARD_DEVIATION = Aggregator.SAMPLE_STANDARD_DEVIATION
    const val MINIMUM = Aggregator.MINIMUM
    const val MAXIMUM = Aggregator.MAXIMUM
    const val FIRST = Aggregator.FIRST
    const val LAST = Aggregator.LAST
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
