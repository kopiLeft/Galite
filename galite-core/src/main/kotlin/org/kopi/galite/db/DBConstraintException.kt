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
 * @param     original                the original SQLException
 * @param     constraintName          the violated constraint
 **/
open class DBConstraintException(query: String?, original: SQLException,
                                 val constraint: String = "unspecified") : DBException(query, original) {

  constructor(original: SQLException) : this(null, original, "unspecified")

  constructor(original: SQLException, constraintName: String) : this(null, original, constraintName)

  /**
   * Returns the index name
   */
  val description: String
    get() = "DBConstraintException: '$constraint'"
}
