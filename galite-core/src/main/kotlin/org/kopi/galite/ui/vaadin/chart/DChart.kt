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

import org.kopi.galite.chart.ChartTypeFactory
import org.kopi.galite.chart.UChart
import org.kopi.galite.chart.UChartType
import org.kopi.galite.chart.VChart
import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.ui.vaadin.visual.DWindow
import org.kopi.galite.visual.VWindow

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

/**
 * Creates a new chart view from its model.
 * @param model The chart model.
 */
class DChart(model: VWindow) : DWindow(model), UChart {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val content = HorizontalLayout()

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

  override fun setEnabled(enabled: Boolean) {
    TODO("Not yet implemented")
  }

  override fun isEnabled(): Boolean {
    TODO("Not yet implemented")
  }

  override fun focus() {
//    TODO()
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  override var type: UChartType? = null
    set(newType) {
      access {
        if (field != null && newType != null) {
          content.remove(field as Component)
        }
        if (newType != null) {
          field = newType
          newType.build()
          content.add(newType as Component)
        }
      }
    }

  companion object {
    init {
      ChartTypeFactory.chartTypeFactory = VChartTypeFactory()
    }
  }
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  init {
    add(content)
  }
}
