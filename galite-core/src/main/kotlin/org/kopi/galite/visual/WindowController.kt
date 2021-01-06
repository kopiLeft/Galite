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

import org.kopi.galite.common.Window
import java.io.Serializable

import org.kopi.galite.util.base.InconsistencyException

/**
 * Creates the GUI, opens the window, form, ...
 * Handles actions initialized by the user
 */
abstract class WindowController : Serializable {
  //------------------------------------------------------------------
  // UTILS
  //------------------------------------------------------------------
  /**
   * Registers [WindowBuilder] for a given model type.
   * @param type The model type.
   * @param uiBuilder The given WindowBuilder.
   * @return The old registered [WindowBuilder] for the model type.
   */
  fun registerWindowBuilder(type: Int, uiBuilder: WindowBuilder): WindowBuilder? {
    val old = builder[type]
    builder[type] = uiBuilder
    return old
  }

  /**
   * Returns the [WindowBuilder] registered to a given model.
   * @param model The window model.
   * @return The corresponding WindowBuilder
   */
  protected fun getWindowBuilder(model: VWindow): WindowBuilder? =
          if (model.getType() > builder.size || builder[model.getType()] == null) {
            // program should never reach here.
            Thread.dumpStack()
            throw InconsistencyException("WindowController: WindowBuilder not found")
          } else {
            builder[model.getType()]
          }

  //------------------------------------------------------------------
  // ABSTRACT METHODS
  //------------------------------------------------------------------
  /**
   * Shows the [UWindow] and block the executing thread. The model should
   * wait until the view is closed.
   * @param model The [UWindow] model.
   */
  abstract fun doModal(model: VWindow): Boolean

  /**
   * Shows the [UWindow] without blocking the executing thread.
   * @param model The [UWindow] model.
   */
  abstract fun doNotModal(model: VWindow)

  /**
   * Shows the [UWindow] without blocking the executing thread.
   * @param model The [UWindow] model.
   */
  abstract fun doNotModal(model: Window)

  //------------------------------------------------------------------
  // DATA MEMBERS
  //------------------------------------------------------------------
  private val builder = arrayOfNulls<WindowBuilder>(256)

  companion object {
    /**
     * The `WindowController` instance.
     */
    lateinit var windowController: WindowController
  }
}
