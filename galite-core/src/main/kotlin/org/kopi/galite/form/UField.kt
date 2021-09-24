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

package org.kopi.galite.form

import org.kopi.galite.base.UComponent

/**
 * `UField` is the top-level interface that must be implemented
 * by all fields. It is the visual component of the [VField] model.
 */
interface UField : UComponent {

  /**
   * Returns the field model.
   * @return the field model.
   */
  fun getModel(): VField

  /**
   * Returns the field block view.
   * @return The [UBlock] containing this view.
   */
  fun getBlockView(): UBlock

  /**
   * Sets the blink state of the field.
   * @param blink the blink state.
   */
  fun setBlink(blink: Boolean)

  /**
   * Returns the field access.
   * @return the field access.
   */
  fun getAccess(): Int

  /**
   * represents the field position in the chart.
   */
  var position: Int

  /**
   * Returns the auto fill button.
   * @return The auto fill button.
   */
  fun getAutofillButton(): UComponent?

  /**
   * Sets is the field is in detail view.
   * @param detail The detail model state.
   */
  fun setInDetail(detail: Boolean)

  /**
   * Updates the field text
   */
  fun updateText()

  /**
   * Updates the field access
   */
  fun updateAccess()

  /**
   * Updates the field focus
   */
  fun updateFocus()

  /**
   * Forces this field to get the focus in window.
   */
  fun forceFocus()

  /**
   * Updates the field color properties (background and foreground).
   */
  fun updateColor()

  /**
   * Prepares the field snapshot.
   * @param fieldPos The field position.
   * @param active Is the field active.
   */
  fun prepareSnapshot(fieldPos: Int, active: Boolean)

  /**
   * Returns the field content.
   * @return The field value.
   */
  fun getObject(): Any?
}
