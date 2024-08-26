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

import org.kopi.galite.visual.UWindow

/**
 * The `UChart` is the view part of a chart component.
 * A chart is a separate window where we can display a chart
 * component like pie chart, bar chart, column chart and line chart.
 */
interface UChart : UWindow {
  /**
   * Redisplays the chart content.
   */
  fun refresh()

  /**
   * Builds the chart display.
   */
  fun build()

  /**
   * Fired when the chart type has been changed.
   */
  fun typeChanged()

  /**
   * Sets the chart type representation.
   * @param newType The chart type view
   */
  fun setType(newType: UChartType?)

  /**
   * Returns the chart type.
   * @return The chart type.
   */
  fun getType(): UChartType?
}
