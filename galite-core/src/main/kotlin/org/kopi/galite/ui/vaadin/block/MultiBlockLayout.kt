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
package org.kopi.galite.ui.vaadin.block

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Div

/**
 * The multiple block layout widget composed of a simple
 * block layout and a chart block layout.
 */
class MultiBlockLayout : AbstractBlockLayout() {

  private var pane: Div? = null
  private val chartLayout: ChartBlockLayout? = null

  /**
   * Switch from the chart view to the detail view and vis versa.
   * Switch is only performed when it is a multi block.
   * @param detail Should we switch to detail view ?
   */
  fun switchView(detail: Boolean) {
    // TODO
  }

  override fun initSize() {
    TODO("Not yet implemented")
  }

  override fun add(component: Component?, constraints: ComponentConstraint) {
    chartLayout!!.add(component, constraints)
  }

  override fun addComponent(
          component: Component?, x: Int, y: Int, width: Int, height: Int, alignRight: Boolean,
          useAll: Boolean,
  ) {
    TODO("Not yet implemented")
  }

  override fun layout() {
    TODO("Not yet implemented")
  }

  override fun clear() {
    TODO("Not yet implemented")
  }

  override fun layoutAlignedComponents() {
    TODO("Not yet implemented")
  }

  override fun updateScroll(pageSize: Int, maxValue: Int, enable: Boolean, value: Int) {
    TODO("Not yet implemented")
  }
}
