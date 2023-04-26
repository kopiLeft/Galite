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

package org.kopi.galite.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.util.base.InconsistencyException
import java.sql.SQLException

/**
 * A connection maintain information about current context, underlying
 * JDBC connection, driver and user.
 */
class Connection {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  val url: String
  val userName: String
  val password: String?
  var dbConnection: Database
//  var conn: java.sql.Connection? = null
  var user: Int = 0

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
  private constructor(connection: java.sql.Connection,
                      lookupUserId: Boolean = true,
                      schema: Schema? = null,
                      traceLevel: Int? = null,
                      isolationLevel: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE) {
    val configuration = databaseConfig(schema, traceLevel, isolationLevel)

//    conn = connection
    dbConnection = Database.connect({ connection }, databaseConfig = configuration)
    url = dbConnection.url
    userName = connection.metaData.userName
    password = null // already authenticated
    user = if (!lookupUserId) USERID_NO_LOOKUP else USERID_TO_DETERMINE
    setUserID()
  }

  /**
   * Creates a connection with Exposed and opens it.
   *
   * @param        url             the URL of the database to connect to
   * @param        userName        the name of the database user
   * @param        password        the password of the database user
   * @param        lookupUserId    lookup user id in table of users ?
   * @param        schema          the database schema to set as current schema
   */
  private constructor(url: String,
                      driver: String,
                      userName: String,
                      password: String,
                      lookupUserId: Boolean = true,
                      schema: String? = null,
                      traceLevel: Int? = null,
                      isolationLevel: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE)
      : this(url, driver, userName, password, lookupUserId, schema?.let { Schema(schema) }, traceLevel, isolationLevel)

  /**
   * Creates a connection with Exposed and opens it.
   *
   * @param        url             the URL of the database to connect to
   * @param        userName        the name of the database user
   * @param        password        the password of the database user
   * @param        lookupUserId    lookup user id in table of users ?
   * @param        schema          the database schema to set as current schema
   */
  private constructor(url: String,
                      driver: String,
                      userName: String,
                      password: String,
                      lookupUserId: Boolean = true,
                      schema: Schema? = null,
                      traceLevel: Int? = null,
                      isolationLevel: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE) {
    val configuration = databaseConfig(schema, traceLevel, isolationLevel)

    dbConnection = Database.connect(url = url,
                                    driver = driver,
                                    user = userName,
                                    password = password,
                                    databaseConfig = configuration)
    this.url = url
    this.userName = userName
    this.password = password
    this.user = if (!lookupUserId) USERID_NO_LOOKUP else USERID_TO_DETERMINE
    setUserID()
  }

  /**
   * Creates a connection with Exposed from a datasource.
   *
   * @param     dataSource      the dataSource
   * @param     lookupUserId    lookup user id in table of users ?
   * @param     schema          the current database schema
   */
  private constructor(dataSource: javax.sql.DataSource,
                      lookupUserId: Boolean = true,
                      schema: Schema? = null,
                      traceLevel: Int? = null,
                      isolationLevel: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE) {
    val configuration = databaseConfig(schema, traceLevel, isolationLevel)

    dbConnection = Database.connect(dataSource, databaseConfig = configuration)
    url = dbConnection.url
    userName = dataSource.connection.metaData.userName.orEmpty()
    this.user = if (!lookupUserId) USERID_NO_LOOKUP else USERID_TO_DETERMINE
    password = null // already authenticated
  }

  /**
   * Returns the user ID
   */
  fun getUserID(): Int {
    when (user) {
      USERID_NO_LOOKUP -> throw InconsistencyException("user id must not be queried")
      USERID_TO_DETERMINE -> throw InconsistencyException("user id not yet determined")
    }

    return user
  }

  /**
   * Retrieves the user ID of the current user
   */
  private fun setUserID() {

    if (user != USERID_NO_LOOKUP) {
      if (userName == "root" || userName == "lgvplus" || userName == "tbadmin" || userName == "dba") {
        user = USERID_NO_LOOKUP
      } else {
        try {
          transaction {
            user = Users.slice(Users.id).select {
              Users.shortName eq userName
            }.single()[Users.id]
          }
        } catch (e: NoSuchElementException) {
          throw SQLException("user unknown")
        } catch (e: IllegalArgumentException) {
          throw SQLException("different users with same name")
        } catch (e: SQLException) {
          throw InconsistencyException(e.message!!)
        }
      }
    }
  }

//  /**
//   * Closes the connection to the database.
//   */
//  @Throws(SQLException::class)
//  fun close() {
//    conn = try {
//      conn!!.rollback()
//      conn!!.close()
//      null
//    } catch (e: SQLException) {
//      throw e
//    }
//  }

  companion object {

    /**
     * Creates a connection with Exposed from JDBC Connection
     *
     * @param     connection          the JDBC connection
     * @param     lookupUserId        lookup user id in table of Users ?
     * @param     schema              the database schema to set as current schema
     */
    fun createConnection(connection: java.sql.Connection,
                         lookupUserId: Boolean = true,
                         schema: Schema? = null,
                         traceLevel: Int? = null,
                         isolationLevel: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE): Connection {
      return Connection(connection, lookupUserId, schema, traceLevel, isolationLevel)
    }

    /**
     * Creates a connection with Exposed and opens it.
     *
     * @param        url             the URL of the database to connect to
     * @param        userName        the name of the database user
     * @param        password        the password of the database user
     * @param        lookupUserId    lookup user id in table of users ?
     * @param        schema          the database schema to set as current schema
     */
    fun createConnection(url: String,
                         driver: String,
                         userName: String,
                         password: String,
                         lookupUserId: Boolean = true,
                         schema: String? = null,
                         traceLevel: Int? = null,
                         isolationLevel: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE): Connection {
      return Connection(url, driver, userName, password, lookupUserId, schema, traceLevel, isolationLevel)
    }


    /**
     * Creates a connection with Exposed and opens it.
     *
     * @param        url             the URL of the database to connect to
     * @param        userName        the name of the database user
     * @param        password        the password of the database user
     * @param        lookupUserId    lookup user id in table of users ?
     * @param        schema          the database schema to set as current schema
     */
    fun createConnection(url: String,
                         driver: String,
                         userName: String,
                         password: String,
                         lookupUserId: Boolean = true,
                         schema: Schema? = null,
                         traceLevel: Int? = null): Connection {
      return Connection(url, driver, userName, password, lookupUserId, schema, traceLevel)
    }

    /**
     * Creates a connection with Exposed from a datasource.
     *
     * @param     dataSource      the dataSource
     * @param     lookupUserId    lookup user id in table of users ?
     * @param     schema          the current database schema
     */
    fun createConnection(dataSource: javax.sql.DataSource,
                         lookupUserId: Boolean = true,
                         schema: Schema? = null,
                         traceLevel: Int? = null,
                         isolationLevel: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE): Connection {
      return Connection(dataSource, lookupUserId, schema, traceLevel, isolationLevel)
    }

    // -1 not yet determined
    private const val USERID_TO_DETERMINE = -1

    // -2 do not lookup user ID
    private const val USERID_NO_LOOKUP = -2
  }
}

fun databaseConfig(schema: Schema?, traceLevel: Int? = null, isolationLevel: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE): DatabaseConfig = DatabaseConfig {
  sqlLogger = Slf4jSqlInfoLogger(traceLevel)
  defaultSchema = schema // Feature added in https://github.com/JetBrains/Exposed/pull/1367
  defaultIsolationLevel = isolationLevel
}

class Slf4jSqlInfoLogger(private val traceLevel: Int? = null) : SqlLogger {
  override fun log(context: StatementContext, transaction: Transaction) {
    if (exposedLogger.isInfoEnabled && (traceLevel == null || traceLevel > 0)) {
      exposedLogger.info("${System.currentTimeMillis()} ${context.expandArgs(TransactionManager.current())}")
    }
  }
}
