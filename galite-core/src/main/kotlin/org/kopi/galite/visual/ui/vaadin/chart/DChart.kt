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
package org.kopi.galite.visual.ui.vaadin.chart

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import org.kopi.galite.visual.VWindow
import org.kopi.galite.visual.chart.ChartTypeFactory
import org.kopi.galite.visual.chart.UChart
import org.kopi.galite.visual.chart.UChartType
import org.kopi.galite.visual.chart.VChart
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.visual.DWindow

/**
 * Creates a new chart view from its model.
 * @param model The chart model.
 */
class DChart(model: VWindow) : DWindow(model), UChart {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val content = HorizontalLayout()
  private var type: UChartType? = null

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun run() {
    (getModel() as VChart).initChart()
    (getModel() as VChart).setMenu()
    focus()
  }

  override fun refresh() {
    // TODO()
  }

  override fun build() {}

  override fun typeChanged() {
//    TODO()
  }

  override fun getType(): UChartType? {
    return type
  }

  override fun setType(type: UChartType?) {
    access {
      if (this.type != null && type != null) {
        content.remove(this.type as Component)
      }
      if (type != null) {
        this.type = type
        type.build()
        content.add(type as Component)
      }
    }
  }

  companion object {
    init {
      ChartTypeFactory.setChartTypeFactory(VChartTypeFactory())
    }
  }
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  init {
    add(content)
  }
}
