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
package org.kopi.galite.visual.db

import java.lang.RuntimeException
import java.sql.SQLException

open class DBRuntimeException : RuntimeException {

  /**
   * Constructs an exception with a message.
   *
   * @param        message                the associated message
   */
  constructor(message: String?) : super(message)

  /**
   * Constructs an exception with a message.
   *
   * @param     query           the sql query which generated the exception
   * @param     message         the associated message
   */
  constructor(
    query: String?,
    message: String
  ) : super(message + if (query != null) "\n---- BEGIN QUERY TRACE ----\n$query\n----  END QUERY TRACE   ----" else "")

  /**
   * Constructs an exception with a message.
   *
   * @param        exc                the cause exception
   */
  constructor(exc: SQLException?) : super(exc)

  /**
   * Constructs an exception with a message.
   *
   * @param        message                the associated message
   */
  constructor(message: String?, exc: SQLException?) : super(message, exc)

  /**
   * Constructs an exception with a message.
   *
   * @param     query           the sql query which generated the exception
   * @param     message         the associated message
   */
  constructor(query: String?, message: String, exc: SQLException?) : super(
    message + if (query != null) "\n---- BEGIN QUERY TRACE ----\n$query\n----  END QUERY TRACE   ----" else "",
    exc
  )
}
