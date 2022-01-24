/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.util

/**
 * An error has occur during printing
 */
open class PrintException : Exception {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  /** The error code */
  var code: Int
    private set

  /**
   * PrintException
   *
   * @param        msg        Explanation of the error
   * @param        code        Kind of the error (see EXEC_ constants)
   */
  constructor(msg: String?, code: Int) : super(msg) {
    this.code = code
  }

  /**
   * PrintException
   *
   * @param        msg        Explanation of the error
   * @param        code        Kind of the error (see EXEC_ constants)
   */
  constructor(msg: String?, cause: Throwable?, code: Int) : super(msg, cause) {
    this.code = code
  }
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  /**
   * is this error is fatal
   */
  val isFatalError: Boolean
    get() = code == EXC_FATAL

  /**
   * should we retry
   */
  fun shouldRetry(): Boolean {
    return code == EXC_RETRY
  }

  companion object {
    // ----------------------------------------------------------------------
    // PUBLIC CONSTANTS
    // ----------------------------------------------------------------------
    const val EXC_UNKNOWN = 0
    const val EXC_FATAL = 1
    const val EXC_RETRY = 2

    private const val serialVersionUID = 0L
  }
}
