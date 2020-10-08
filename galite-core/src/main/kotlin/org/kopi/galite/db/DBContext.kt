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

/**
 * The database context
 *
 * @param defaultConnection The underlying default connection.
 */
class DBContext(var defaultConnection: Connection) {

  /**
   * Create a connection. Connects to database and logs on.
   *
   * @param     driverName      the class name of the JDBC driver to register.
   * @param     url             the URL of the database to connect to
   * @param     user            the name of the database user
   * @param     password        the password of the database user
   * @param     lookupUserId    lookup user id in table of users ?
   * @param     schema          the current database schema
   */
  fun createConnection(
          driverName: String,
          url: String,
          user: String,
          password: String,
          lookupUserId: Boolean = true, // TODO
          schema: String? = null // TODO
  ): Connection {
    this.connection = Connection(url = url,
            driver = driverName,
            userName = user,
            password = password,
            lookupUserId = lookupUserId,
            schema = schema)
    return this.connection
  }

  /**
   * Creates a connection from JDBC Connection
   *
   * @param     connection      the JDBC connection
   * @param     lookupUserId    lookup user id in table of users ?
   * @param     schema          the current database schema
   */
  fun createConnection(
          connection: java.sql.Connection,
          lookupUserId: Boolean, // TODO
          schema: String? // TODO
  ): Connection {
    this.connection = Connection(connection = connection,
            lookupUserId = lookupUserId,
            schema = schema)
    return this.connection
  }


  fun abortWork() {
    TODO()
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  /** Connection currently opened */
  lateinit var connection: Connection
    private set
}
