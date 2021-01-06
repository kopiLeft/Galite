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

package org.kopi.galite.db

import java.sql.SQLException

/**
 * Constructor
 *
 * @param     query                   the sql query which generated the exception
 * @param     sqlException            the original SQLException
 */
abstract class DBException(query: String?, val sqlException: SQLException) : SQLException(
        sqlException.message + (if (query != null) {
          "\n---- BEGIN QUERY TRACE ----\n$query\n----  END QUERY TRACE   ----"
        } else {
          ""
        }),
        sqlException.sqlState,
        sqlException.errorCode
) {
  /**
   * Constructor
   *
   * @param        original                the original SQLException
   */
  constructor(original: SQLException) : this(null, original)

  /**
   * Get the vendor specific exception code
   *
   * @return        the vendor's error code
   */
  override fun getErrorCode(): Int {
    return sqlException.errorCode
  }

  /**
   * Get the SQLState
   *
   * @return        the SQLState value
   */
  override fun getSQLState(): String {
    return sqlException.sqlState
  }
}
