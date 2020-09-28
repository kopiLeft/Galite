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

package org.kopi.galite.visual

import org.kopi.galite.base.UComponent
import org.kopi.galite.report.VReportCommand

import java.io.Serializable

/**
 * `VModel` is the top level interface that all model classes should implement.
 * This interface is used in [UIFactory] to create model displays.
 *
 * @see UIFactory
 *
 * @see WindowBuilder
 */
interface VModel : Serializable {
  /**
   * Sets the model display.
   * @param display The model display.
   */
  fun setDisplay(display: UComponent)

  /**
   * Returns the model display.
   * @return The model display
   */
  fun getDisplay(): UComponent?
  abstract fun setNumber(trigger: Int)
  abstract fun setHandler(vReportCommand: VReportCommand)
  abstract fun getNumber(): Int
  abstract fun getActorIdent(): String
}
