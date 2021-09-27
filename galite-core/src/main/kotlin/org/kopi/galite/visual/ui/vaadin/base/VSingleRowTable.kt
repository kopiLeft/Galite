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
package org.kopi.galite.visual.ui.vaadin.base

import java.util.Objects

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.Tag
import com.vaadin.flow.dom.Element

/**
 * A Single row table panel.
 */
@Tag("table")
class VSingleRowTable : Component(), HasComponents {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Adds an element to the td element
   * @param elements element to add
   */
  fun add(vararg elements: Element?) {
    Objects.requireNonNull(elements, "Elements should not be null")
    for (element in elements) {
      Objects.requireNonNull(element,
                             "element to add cannot be null")
      td.appendChild(element)
    }
  }

  /**
   * Adds a component to the td element
   * @param components components to add
   */
  override fun add(vararg components: Component) {
    Objects.requireNonNull(components, "Components should not be null")
    for (component in components) {
      Objects.requireNonNull(component,
                             "Component to add cannot be null")
      td.appendChild(component.element)
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val td: Element

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates the table instance.
   */
  init {
    val body = Element("tbody")
    element.appendChild(body)
    val tr = Element("tr")
    body.appendChild(tr)
    td = Element("td")
    tr.appendChild(td)
  }
}
