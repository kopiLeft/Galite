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
}
