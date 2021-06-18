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
import com.vaadin.flow.component.HasComponents

/**
 * The block layout should provides its children
 * and set a given component in a specified position.
 */
interface BlockLayout : HasComponents {

  /**
   * Adds a component to the layout according to its constraints.
   * @param component The component to add.
   * @param constraints The constraints.
   */
  fun add(component: Component?, constraints: ComponentConstraint)

  /**
   * Adds a component to this block layout.
   *
   * @param component  The component to be added.
   * @param x          the x position.
   * @param y          The y position.
   * @param width      the column span width.
   * @param alignRight Is it right aligned ?
   * @param useAll     Use all available area ?
   */
  fun addComponent(component: Component?, x: Int, y: Int, width: Int, height: Int, alignRight: Boolean, useAll: Boolean)

  /**
   * Lays out the components added to the layout.
   */
  fun layout()

  /**
   * Clear the layout content.
   */
  fun clear()

  /**
   * Adds extra components to this layout.
   * @param component The component to add.
   * @param constraint The component constraints.
   */
  fun addAlignedComponent(component: Component, constraint: ComponentConstraint)

  /**
   * Lays out some extra components. This used to render not standard components.
   */
  fun layoutAlignedComponents()

  /**
   * Updates the layout scroll bar of it exists.
   * @param pageSize The scroll page size.
   * @param maxValue The max scroll value.
   * @param enable is the scroll bar enabled ?
   * @param value The scroll position.
   */
  fun updateScroll(pageSize: Int, maxValue: Int, enable: Boolean, value: Int)
}
