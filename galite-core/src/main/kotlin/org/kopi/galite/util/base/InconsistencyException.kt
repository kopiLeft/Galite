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

package org.kopi.galite.util.base

/**
 * An InconsistencyException indicates that an inconsistent internal state has
 * been discovered, usually due to incorrect program logic.
 */
class InconsistencyException : RuntimeException {
  /**
   * Constructs am InconsistencyException with no specified detail message.
   */
  @Deprecated("Use the constructor with the message or build it with the exception which cause this case.",
          ReplaceWith("InconsistencyException(message = errorMessage)"))
  constructor() : super()

  /**
   * Constructs am InconsistencyException with the specified detail message.
   *
   * @param        message                the detail message
   */
  constructor(message: String) : super(message)

  /**
   * Constructs am InconsistencyException with the specified detail message.
   *
   * @param        message                the detail message
   */
  constructor(message: Throwable) : super(message)

  /**
   * Constructs am InconsistencyException with the specified detail message.
   *
   * @param        message                the detail message
   */
  constructor(message: String, throwable: Throwable) : super(message, throwable)
}
