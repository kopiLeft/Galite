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

package org.kopi.galite.base

import java.sql.Connection

import org.jetbrains.exposed.sql.Database

class DBContext {

  /**
   * Create a connection with Exposed. Connects to database and logs on.
   *
   * @param     driverName      the class name of the JDBC driver to register.
   * @param     url             the URL of the database to connect to
   * @param     user            the name of the database user
   * @param     password        the password of the database user
   * @param     lookupUserId    lookup user id in table KOPI_USERS ?
   * @param     schema          the current database schema
   */
  fun createConnection(
    driverName: String,
    url: String,
    user: String,
    password: String,
    lookupUserId: Boolean, // TODO
    schema: String? // TODO
  ): Database {
    val database = Database.connect(url = url, driver = driverName, user = user, password = password)
    connections.add(database)
    return database
  }

  /**
   * Creates a connection from JDBC Connection
   *
   * @param     connection      the JDBC connection
   * @param     lookupUserId    lookup user id in table KOPI_USERS ?
   * @param     schema          the current database schema
   */
  fun createConnection(
    connection: Connection,
    lookupUserId: Boolean, // TODO
    schema: String? // TODO
  ): Database {
    val database = Database.connect({ connection })
    connections.add(database)
    return database
  }

  /**
   * Connects to database and logs on.
   *
   * @param     driverName      the class name of the JDBC driver to register.
   * @param     url             the URL of the database to connect to
   * @param     user            the name of the database user
   * @param     password        the password of the database user
   */
  fun createConnection(
    driverName: String,
    url: String,
    user: String,
    password: String
  ): Database {
    val database = createConnection(driverName, url, user, password, true, null)
    connections.add(database)
    return database
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  /** All connections currently opened */
  var connections = arrayListOf<Database>()
    private set

  /**
   * The underlying default connection.
   */
  var defaultConnection: Database? = null

  /**
   * If there is a transaction started
   */
  var isInTransaction = false
    private set
}
