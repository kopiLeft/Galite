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

package org.kopi.galite.chart

import org.kopi.galite.visual.VRuntimeException

/**
 * Visual exception thrown when a chart did not contain any values
 * to be displayed. This will notify the user that no data was found
 * for the chart content.
 */
class VNoChartRowException : VRuntimeException {
  /**
   * Creates a new no row exception.
   */
  constructor() : super()

  /**
   * Creates a new no row exception.
   * @param message The exception message.
   */
  constructor(message: String) : super(message)

  /**
   * Creates a new no row exception.
   * @param exec The exception cause.
   */
  constructor(exec: Throwable) : super(exec)

  /**
   * Creates a new no row exception.
   * @param msg The exception message.
   * @param exc The exception cause.
   */
  constructor(msg: String, exc: Throwable) : super(msg, exc)
}
