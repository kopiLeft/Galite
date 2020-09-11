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
import org.jetbrains.exposed.sql.statements.api.ExposedConnection
import org.jetbrains.exposed.sql.transactions.TransactionManager

class DBContext {

  /**
   * Create a connection with Exposed. Connects to database and logs on.
   *
   * @param     driverName            The class name of the JDBC driver to register.
   * @param     url             the URL of the database to connect to
   * @param     user            the name of the database user
   * @param     password        the password of the database user
   * @param     lookupUserId    lookup user id in table KOPI_USERS ?
   * @param     schema          the current database schema
   * @return    a new exposed connection
   */
  fun createConnection(driverName: String,
                       url: String,
                       user: String,
                       password: String,
                       lookupUserId: Boolean,
                       schema: String?): ExposedConnection<*> {
    Database.connect(url = url, driver = driverName, user = user, password = password)
    connections.add(TransactionManager.current().connection)
    return TransactionManager.current().connection
  }

  /**
   * Creates a connection from JDBC Connection
   *
   * @param     connection                the JDBC connection
   * @param     lookupUserId    lookup user id in table KOPI_USERS ?
   * @param     schema          the current database schema
   */
  fun createConnection(connection: Connection,
                       lookupUserId: Boolean,
                       schema: String?): ExposedConnection<*> {
    Database.connect({connection})
    connections.add(TransactionManager.current().connection)
    return TransactionManager.current().connection
  }

  /**
   * Connects to database and logs on.
   *
   * @param     driverName            The class name of the JDBC driver to register.
   * @param     url             the URL of the database to connect to
   * @param     user            the name of the database user
   * @param     password            the password of the database user
   * @return    a new exposed connection
   */
  fun createConnection(driverName: String,
                       url: String,
                       user: String,
                       password: String): ExposedConnection<*> {
    return createConnection(driverName, url, user, password, true, null)
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  /** All connections currently opened */
  var connections = arrayListOf<ExposedConnection<*>>()
    private set

  /**
   * The underlying default JDBC connection.
   */
  var defaultConnection: ExposedConnection<*>? = null

  /**
   * If there is a transaction started
   */
  var isInTransaction = false
    private set
}