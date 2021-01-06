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
package org.kopi.galite.ui.vaadin.chart

import org.kopi.galite.chart.CConstants
import org.kopi.galite.chart.ChartTypeFactory
import org.kopi.galite.chart.UChartType
import org.kopi.galite.chart.VChartType
import org.kopi.galite.util.base.InconsistencyException

class VChartTypeFactory : ChartTypeFactory() {
  override fun createTypeView(title: String, model: VChartType): UChartType {
    val view = when (model.ordinal) {
      CConstants.TYPE_PIE -> DPieChart(title, model.getDataSeries())
      CConstants.TYPE_COLUMN -> DColumnChart(title, model.getDataSeries())
      CConstants.TYPE_BAR -> DBarChart(title, model.getDataSeries())
      CConstants.TYPE_LINE -> DLineChart(title, model.getDataSeries())
      CConstants.TYPE_AREA -> DAreaChart(title, model.getDataSeries())
      else -> throw InconsistencyException("NO UI IMPLEMENTATION FOR CHART TYPE " + model.name)
    }
    model.setDisplay(view)

    return view
  }
}
