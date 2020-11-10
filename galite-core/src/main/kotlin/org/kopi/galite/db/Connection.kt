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

import java.sql.Connection

import org.jetbrains.exposed.sql.Database
import org.kopi.galite.util.base.InconsistencyException

/**
 * A connection maintain information about current context, underlying
 * JDBC connection, driver and user.
 */
class Connection {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------
  /**
   * Creates a connection with Exposed from JDBC Connection
   *
   * @param     connection          the JDBC connection
   * @param     lookupUserId        lookup user id in table of Users ?
   * @param     schema              the database schema to set as current schema
   */
  constructor(connection: Connection,
              lookupUserId: Boolean = true, // TODO
              schema: String? = null) { // TODO
    dbConnection = Database.connect({ connection })
    url = dbConnection.url
    userName = connection.metaData.userName
    password = null // already authenticated
    setUserID()
  }

  /**
   * Creates a connection with Exposed and opens it.
   *
   * @param        url             the URL of the database to connect to
   * @param        user            the name of the database user
   * @param        pass            the password of the database user
   * @param        lookupUserId    lookup user id in table of users ?
   * @param        schema          the database schema to set as current schema
   */
  constructor(url: String,
              driver: String,
              userName: String,
              password: String,
              lookupUserId: Boolean = true, // TODO
              schema: String? = null) { // TODO
    dbConnection = Database.connect(url = url, driver = driver, user = userName, password = password)
    this.url = url
    this.userName = userName
    this.password = password
    //setUserID() TODO
  }

  /**
   * Returns the user ID
   */
  fun getUserID(): Int {
    when(user) {
      USERID_NO_LOOKUP -> throw InconsistencyException("user id must not be queried")
      USERID_TO_DETERMINE -> throw InconsistencyException("user id not yet determined")
    }

    return user
  }

  /**
   * Retrieves the user ID of the current user
   */
  private fun setUserID() {
    TODO()
  }

  companion object {
    // -1 not yet determined
    private const val USERID_TO_DETERMINE = -1

    // -2 do not lookup user ID
    private const val USERID_NO_LOOKUP = -2
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  val url: String
  val userName: String
  val password: String?
  var dbConnection: Database
  val user: Int = 0
}
