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

package org.kopi.galite.visual.chart

/**
 * The chart type view factory.
 */
abstract class ChartTypeFactory {
  /**
   * Creates the chart type view according to the given model.
   * @param model The chart type model.
   * @return The corresponding chart type view.
   */
  abstract fun createTypeView(title: String, model: VChartType): UChartType

  companion object {
    /**
     * The `ChartTypeFactory` instance.
     */
    private lateinit var chartTypeFactory: ChartTypeFactory

    //---------------------------------------------------------------------
    // ACCESSORS
    //---------------------------------------------------------------------
    /**
     * Returns the `ChartTypeFactory` instance.
     * @return The ChartTypeFactory instance.
     */
    fun getChartTypeFactory(): ChartTypeFactory {
      return chartTypeFactory
    }

    /**
     * Sets the `ChartTypeFactory` instance.
     * @param factory The ChartTypeFactory instance.
     */
    fun setChartTypeFactory(factory: ChartTypeFactory) {
      chartTypeFactory = factory
    }
  }
}
