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
 * This class is the root of all visual kopi exception
 * According to type and message, this exception will be handled
 * in different part of the action processing. For instance, VFieldException
 * will be handled by VForm that will display an error message if the message
 * is not null and it will put the focus on the caller field
 * Warning: All exceptions occuring in commands that do not herit from VException
 * (or SQLException in proteced statement) will generate a FATAL ERROR and close
 * the current form
 */
abstract class VException : Exception {
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
   * @param     cause           the cause  (null value permited
   */
  constructor(cause: Throwable) : super(cause)

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param     message         the associated message
   * @param     cause           the cause  (null value permited
   */
  constructor(message: String, cause: Throwable) : super(message, cause)
}
