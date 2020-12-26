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
 * $Id: DLineChart.java 34963 2016-11-08 10:13:45Z hacheni $
 */
package org.kopi.galite.ui.vaadin.chart

import org.kopi.galite.chart.VDataSeries

/**
 * Creates a line area chart from a given data series model.
 * @param title The chart title.
 * @param dataSeries The data series model.
 */
class DLineChart(title: String?, dataSeries: Array<VDataSeries>) : DAbstractChartType(title, dataSeries) {
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