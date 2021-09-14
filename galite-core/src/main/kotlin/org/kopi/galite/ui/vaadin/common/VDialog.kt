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
package org.kopi.galite.ui.vaadin.common

import java.util.Objects

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H3

@CssImport("./styles/galite/dialog.css")
open class VDialog  : Dialog() {

  protected val headerContainer = Div()
  protected val dialogContent = Div()
  protected val footerContainer = Div()

  init {
    setId("v-dialog")
    super.add(headerContainer)
    super.add(dialogContent)
    super.add(footerContainer)
  }

  /**
   * Creates a {@code<span>} element with given text, and sets this component
   * as dialog header
   *
   * @param headerText
   * dialog title
  </span> */
  open fun setHeader(headerText: String?) {
    setHeader(H3(headerText))
  }

  /**
   * Replaces the header section with given components
   *
   * @param components
   * components for header section
   */
  open fun setHeader(vararg components: Component) {
    headerContainer.removeAll()
    for (component in components) {
      addToHeader(component)
    }
  }

  /**
   * Adds given components to header section
   *
   * @param components
   * components for header section
   */
  open fun addToHeader(vararg components: Component) {
    Objects.requireNonNull(components, "Components should not be null")
    for (component in components) {
      Objects.requireNonNull(component, "Component to add cannot be null")
      headerContainer.add(component)
    }
  }

  /**
   * Replaces the footer section with given components
   *
   * @param components
   * components for footer section
   */
  open fun setFooter(vararg components: Component) {
    footerContainer.removeAll()
    for (component in components) {
      addToFooter(component)
    }
  }

  /**
   * Adds given components to footer section
   *
   * @param components
   * components for footer section
   */
  open fun addToFooter(vararg components: Component) {
    Objects.requireNonNull(components, "Components should not be null")
    for (component in components) {
      Objects.requireNonNull(component, "Component to add cannot be null")
      footerContainer.add(component)
    }
  }

  /**
   * Replaces the content of dialog with given components
   *
   * @param components
   * components to add
   */
  open fun setContent(vararg components: Component?) {
    removeAll()
    add(*components)
  }

  /**
   * Adds given components to the dialog content
   *
   * @param components
   * components to add
   */
  override fun add(vararg components: Component?) {
    dialogContent.add(*components)
  }

  override fun remove(vararg components: Component) {
    Objects.requireNonNull(components, "Components should not be null")
    for (component in components) {
      Objects.requireNonNull(component, "Component to remove cannot be null")
      if (dialogContent.element == component.element.parent) {
        dialogContent.element.removeChild(component.element)
      } else {
        throw IllegalArgumentException("The given component ($component) is not a child of this component")
      }
    }
  }

  override fun removeAll() {
    dialogContent.removeAll()
  }

  /**
   * Adds given components to the dialog content at the given index.
   *
   * @param index
   * the index, where the component will be added.
   *
   * @param component
   * the component to add
   */
  override fun addComponentAtIndex(index: Int, component: Component) {
    Objects.requireNonNull(component, "Component should not be null")
    require(index >= 0) { "Cannot add a component with a negative index" }
    // The case when the index is bigger than the children count is handled
    // inside the method below
    dialogContent.element.insertChild(index, component.element)
  }
}
