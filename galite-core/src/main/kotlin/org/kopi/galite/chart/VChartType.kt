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

import org.kopi.galite.base.UComponent
import org.kopi.galite.visual.VModel

/**
 * Supported chart types.
 *
 * @param ordinal The type ordinal.
 * @param name The type name.
 */
open class VChartType protected constructor(val ordinal: Int, val name: String) : Serializable, CConstants, VModel {
  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------

  override fun setDisplay(display: UComponent) {
    assert(display is UChartType) { "The display should be a chart type view" }
    this.display = display as UChartType
  }

  override fun getDisplay(): UComponent = display

  /**
   * Creates the data series objects from the chart model.
   * @param chart The chart model.
   */
  fun createDataSeries(chart: VChart) {
    dataSeries.clear()
    chart.getRows().forEach { row ->
      val data = VDataSeries(createDimensionData(chart, row))
      // fill the measures data for the dimension data.
      fillMeasures(data, chart, row)
      dataSeries.add(data)
    }
  }

  /**
   * Creates the dimension data for a given data row.
   * This will fill all the measures for the first dimension existing in the data row.
   * It is like parsing a table row, the first column is the dimension and the other
   * columns are its measures.
   * @param chart The chart model.
   * @param row The data row.
   * @return The created dimension data.
   */
  internal fun createDimensionData(chart: VChart, row: VRow): VDimensionData =
          VDimensionData(chart.getDimension(0).label,
                  chart.getDimension(0).format(row.getDimensionAt(0)))

  /**
   * Fills the measure data for the given data series.
   * @param dataSeries The data series.
   * @param chart The chart model.
   * @param row The data row.
   */
  internal fun fillMeasures(dataSeries: VDataSeries, chart: VChart, row: VRow) {
    for (i in 0 until row.getMeasuresCount()) {
      val vMeasureData = VMeasureData(chart.getMeasure(i).label, chart.getMeasure(i).toNumber(row.getMeasureAt(i)))
      dataSeries.measures.add(vMeasureData)
    }
  }

  /**
   * Returns the data series.
   * @return the data series.
   */
  fun getDataSeries(): Array<VDataSeries> {
    return dataSeries.toTypedArray()
  }

  private lateinit var display: UChartType
  internal var dataSeries: MutableList<VDataSeries> = mutableListOf()

  companion object {
    // constants
    val PIE = VChartType(CConstants.TYPE_PIE, "Pie")
    val COLUMN = VChartType(CConstants.TYPE_COLUMN, "Column")
    val BAR = VChartType(CConstants.TYPE_BAR, "Bar")
    val LINE = VChartType(CConstants.TYPE_LINE, "Line")
    val AREA = VChartType(CConstants.TYPE_AREA, "Area")
    val DEFAULT = COLUMN
  }
}
