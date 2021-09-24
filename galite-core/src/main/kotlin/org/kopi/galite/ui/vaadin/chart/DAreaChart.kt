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
package org.kopi.galite.ui.vaadin.chart

import org.kopi.galite.chart.VDataSeries

import com.github.appreciated.apexcharts.config.chart.Type
/**
 * Creates a new area chart from a given data series model.
 * @param title The chart title.
 * @param dataSeries The data series model.
 */
class DAreaChart(title: String?, dataSeries: Array<VDataSeries>) : DAbstractChartType(Type.area, title, dataSeries) {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun createChartData(name: String?) {
    TODO()
  }

  override fun isEnabled(): Boolean {
    TODO("Not yet implemented")
  }

  override fun setEnabled(enabled: Boolean) {
    TODO("Not yet implemented")
  }

  override fun isVisible(): Boolean {
    TODO("Not yet implemented")
  }

  override fun setVisible(visible: Boolean) {
    TODO("Not yet implemented")
  }
}
