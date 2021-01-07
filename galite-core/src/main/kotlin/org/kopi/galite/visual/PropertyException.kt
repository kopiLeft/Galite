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

/**
 */
class PropertyException : Exception {
  /**
   * Constructs an exception with no message.
   */
  constructor() : super()

  /**
   * Constructs an exception with a message.
   *
   * @param        message                the associated message
   */
  constructor(message: String) : super(message)

  /**
   * Constructs a new exception with the specified cause and a detail message
   *
   * @param     cause           the cause  (null value permitted)
   */
  constructor(cause: Throwable) : super(cause)

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param        message                the associated message
   * @param     cause           the cause  (null value permitted)
   */
  constructor(message: String, cause: Throwable) : super(message, cause)

  companion object {
    private const val serialVersionUID = 0L
  }
}
