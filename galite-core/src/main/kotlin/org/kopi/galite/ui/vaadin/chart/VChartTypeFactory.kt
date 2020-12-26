/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: VChartTypeFactory.java 34961 2016-11-04 17:20:49Z hacheni $
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