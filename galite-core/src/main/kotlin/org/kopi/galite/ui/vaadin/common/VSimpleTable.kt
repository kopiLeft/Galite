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
package org.kopi.galite.ui.vaadin.common

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.Tag
import com.vaadin.flow.dom.Element

@Tag("table")
open class VSimpleTable : Component(), HasComponents, HasSize, HasStyle {

  var tbody: Element = Element("tbody")
  var lastTR: Element
  var lastTd: Element? = null

  init {
    element.appendChild(tbody)
    lastTR = Element("tr")
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the amount of spacing to be added around all cells.
   *
   * @param spacing the cell spacing, in pixels
   */
  fun setCellSpacing(spacing: Int) {
    element.setProperty("cellSpacing", spacing.toDouble())
  }

  /**
   * Sets the amount of padding to be added around all cells.
   *
   * @param padding the cell padding, in pixels
   */
  fun setCellPadding(padding: Int) {
    element.setProperty("cellPadding", padding.toDouble())
  }

  /**
   * Sets the width of the table's border. This border is displayed around all
   * cells in the table.
   *
   * @param width the width of the border, in pixels
   */
  fun setBorderWidth(width: Int) {
    element.setProperty("border", "" + width)
  }

  fun addCell(newRow: Boolean, component: Component?) {
    lastTd = Element("td")
    if (component != null) {
      lastTd!!.appendChild(component.element)
    }
    if (newRow) {
      lastTR = Element("tr")
      tbody.appendChild(lastTR)
    }
    lastTR.appendChild(lastTd)
  }

  fun setTdColSpan(colSpan: Int) {
    lastTd!!.setAttribute("colSpan", colSpan.toString())
  }

  fun setTdHeight(height: String?) {
    lastTd!!.setProperty("height", height)
  }

  fun setTdWidth(width: String?) {
    lastTd!!.setProperty("width", width)
  }

  override fun add(vararg components: Component) {
    for (component in components) {
      addCell(true, component)
    }
  }
}
