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

class DBForeignKeyException : DBConstraintException {
  //---------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------
  /**
   * Constructor
   *
   * @param        original                the original SQLException
   * @param        constraint              the violated constraint
   */
  constructor(original: SQLException, constraint: String) : super(original, constraint)

  /**
   * Constructor
   *
   * @param     query                   the sql query which generated the exception
   * @param     original                the original SQLException
   * @param     constraint              the violated constraint
   */
  constructor(query: String, original: SQLException, constraint: String) : super(query, original, constraint)

  /**
   * Creates a new `DBForeignKeyException` that indicates the tables in relation.
   *
   * @param     query                   the sql query which generated the exception
   * @param     original                the original SQLException
   * @param     constraint              the violated constraint
   * @param     referenced              the referenced table
   * @param     referencing             the referencing table
   */
  constructor(query: String,
              original: SQLException,
              constraint: String,
              referenced: String,
              referencing: String) : super(query, original, constraint) {
    referencedTable = referenced
    referencingTable = referencing
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  var referencedTable: String? = null // The referenced table
    private set

  /**
   * Returns the referencing table in this FK exception.
   */
  var referencingTable: String? = null // The referencing table
    private set

}
