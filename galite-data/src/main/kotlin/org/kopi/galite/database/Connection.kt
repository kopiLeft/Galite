/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2024 kopiRight Managed Solutions GmbH, Wien AT
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

import java.sql.SQLException

import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*

import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.util.base.InconsistencyException

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
  var poolConnection: HikariDataSource
  var user: Int = 0

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Creates a connection with Exposed and opens it.
   *
   * @param     url             the URL of the database to connect to
   * @param     driver          the JDBC driver to use to access the database
   * @param     userName        the name of the database user
   * @param     password        the password of the database user
   * @param     lookupUserId    lookup user id in table of users ?
   * @param     schema          the database schema to set as current schema
   * @param     traceLevel      the trace level to print database queries before execution (0: none, 1: all)
   * @param     isolationLevel  the transaction isolation level
   * @param     maxRetries      the number of maximum retries if a transaction fails
   * @param     waitMin         the minimum number (inclusive) of milliseconds to wait before retrying a transaction after it has aborted
   * @param     waitMax         the maximum number (inclusive) of milliseconds to wait before retrying a transaction after it has aborted
   * @param     logger          the SQL logger
   */
  private constructor(url: String,
                      driver: String? = null,
                      userName: String,
                      password: String,
                      lookupUserId: Boolean = true,
                      schema: String? = null,
                      traceLevel: Int? = null,
                      isolationLevel: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE,
                      maxRetries: Int? = null,
                      waitMin: Long? = null,
                      waitMax: Long? = null,
                      logger: SqlLogger? = null) {
    poolConnection = HikariDataSource().apply {
      this.jdbcUrl = url
      driver?.let { this.driverClassName = it }
      schema?.let { this.schema = it }
      this.username = userName
      this.password = password
      this.maximumPoolSize = 1
      this.transactionIsolation = ISOLATION_LEVELS[isolationLevel]
    }
    dbConnection = Database.connect(datasource = poolConnection,
                                    databaseConfig = databaseConfig(traceLevel = traceLevel,
                                                                    isolationLevel = isolationLevel,
                                                                    maxRetries = maxRetries,
                                                                    waitMin = waitMin,
                                                                    waitMax = waitMax,
                                                                    logger = logger))
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
   * @param     traceLevel      the trace level to print database queries before execution (0: none, 1: all)
   * @param     isolationLevel  the transaction isolation level
   * @param     maxRetries      the number of maximum retries if a transaction fails
   * @param     waitMin         the minimum number (inclusive) of milliseconds to wait before retrying a transaction after it has aborted
   * @param     waitMax         the maximum number (inclusive) of milliseconds to wait before retrying a transaction after it has aborted
   * @param     logger          the SQL logger
   */
  private constructor(dataSource: javax.sql.DataSource,
                      lookupUserId: Boolean = true,
                      schema: String? = null,
                      traceLevel: Int? = null,
                      isolationLevel: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE,
                      maxRetries: Int? = null,
                      waitMin: Long? = null,
                      waitMax: Long? = null,
                      logger: SqlLogger? = null) {
    poolConnection = HikariDataSource().apply {
      this.dataSource = dataSource
      schema?.let { this.schema = it }
      this.maximumPoolSize = 1
      this.transactionIsolation = ISOLATION_LEVELS[isolationLevel]
    }
    dbConnection = Database.connect(datasource = poolConnection,
                                    databaseConfig = databaseConfig(traceLevel = traceLevel,
                                                                    isolationLevel = isolationLevel,
                                                                    maxRetries = maxRetries,
                                                                    waitMin = waitMin,
                                                                    waitMax = waitMax,
                                                                    logger = logger))
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
          transaction(db = dbConnection) {
            user = Users.select(Users.id).where {
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

  companion object {
    /**
     * Creates a connection with Exposed and opens it.
     *
     * @param   url             the URL of the database to connect to
     * @param   driver          the JDBC driver to use to access the database
     * @param   userName        the name of the database user
     * @param   password        the password of the database user
     * @param   lookupUserId    lookup user id in table of users ?
     * @param   schema          the database schema to set as current schema
     * @param   traceLevel      the trace level to print database queries before execution (0: none, 1: all)
     * @param   isolationLevel  the transaction isolation level
     * @param   maxRetries      the number of maximum retries if a transaction fails
     * @param   waitMin         the minimum number (inclusive) of milliseconds to wait before retrying a transaction after it has aborted
     * @param   waitMax         the maximum number (inclusive) of milliseconds to wait before retrying a transaction after it has aborted
     * @param   logger          the SQL logger
     */
    fun createConnection(url: String,
                         driver: String? = null,
                         userName: String,
                         password: String,
                         lookupUserId: Boolean = true,
                         schema: String? = null,
                         traceLevel: Int? = null,
                         isolationLevel: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE,
                         maxRetries: Int? = null,
                         waitMin: Long? = null,
                         waitMax: Long? = null,
                         logger: SqlLogger? = null): Connection {
      return Connection(url,
                        driver,
                        userName,
                        password,
                        lookupUserId,
                        schema,
                        traceLevel,
                        isolationLevel,
                        maxRetries,
                        waitMin,
                        waitMax,
                        logger)
    }

    /**
     * Creates a connection with Exposed from a datasource.
     *
     * @param   dataSource      the dataSource
     * @param   lookupUserId    lookup user id in table of users ?
     * @param   schema          the current database schema
     * @param   traceLevel      the trace level to print database queries before execution (0: none, 1: all)
     * @param   isolationLevel  the transaction isolation level
     * @param   maxRetries      the number of maximum retries if a transaction fails
     * @param   waitMin         the minimum number (inclusive) of milliseconds to wait before retrying a transaction after it has aborted
     * @param   waitMax         the maximum number (inclusive) of milliseconds to wait before retrying a transaction after it has aborted
     * @param   logger          the SQL logger
     */
    fun createConnection(dataSource: javax.sql.DataSource,
                         lookupUserId: Boolean = true,
                         schema: String? = null,
                         traceLevel: Int? = null,
                         isolationLevel: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE,
                         maxRetries: Int? = null,
                         waitMin: Long? = null,
                         waitMax: Long? = null,
                         logger: SqlLogger? = null): Connection {
      return Connection(dataSource,
                        lookupUserId,
                        schema,
                        traceLevel,
                        isolationLevel,
                        maxRetries,
                        waitMin,
                        waitMax,
                        logger)
    }

    // -1 not yet determined
    private const val USERID_TO_DETERMINE = -1

    // -2 do not lookup user ID
    private const val USERID_NO_LOOKUP = -2
  }

  // Isolation levels Hashmap : Links java.sql isolation levels (Int) to hikariCP isolation levels (String)
  val ISOLATION_LEVELS = hashMapOf(java.sql.Connection.TRANSACTION_NONE             to "TRANSACTION_NONE",
                                   java.sql.Connection.TRANSACTION_READ_UNCOMMITTED to "TRANSACTION_READ_UNCOMMITTED",
                                   java.sql.Connection.TRANSACTION_READ_COMMITTED   to "TRANSACTION_READ_COMMITTED",
                                   java.sql.Connection.TRANSACTION_REPEATABLE_READ  to "TRANSACTION_REPEATABLE_READ",
                                   java.sql.Connection.TRANSACTION_SERIALIZABLE     to "TRANSACTION_SERIALIZABLE")
}

@OptIn(ExperimentalKeywordApi::class)
fun databaseConfig(schema: Schema? = null,
                   traceLevel: Int? = null,
                   isolationLevel: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE,
                   maxRetries: Int? = null,
                   waitMin: Long? = null,
                   waitMax: Long? = null,
                   logger: SqlLogger? = null)
    : DatabaseConfig = DatabaseConfig {
  sqlLogger = logger ?: Slf4jSqlInfoLogger(traceLevel)
  schema?.let { defaultSchema = it } // Feature added in https://github.com/JetBrains/Exposed/pull/1367
  defaultIsolationLevel = isolationLevel
  defaultMaxAttempts = maxRetries ?: 5
  defaultMinRetryDelay = waitMin ?: 0L
  defaultMaxRetryDelay = waitMax ?: 0L
  preserveKeywordCasing = false
}

class Slf4jSqlInfoLogger(private val traceLevel: Int? = null) : SqlLogger {
  override fun log(context: StatementContext, transaction: Transaction) {
    if (exposedLogger.isInfoEnabled && (traceLevel == null || traceLevel > 0)) {
      exposedLogger.info("${System.currentTimeMillis()} ${context.expandArgs(TransactionManager.current())}")
    }
  }
}
