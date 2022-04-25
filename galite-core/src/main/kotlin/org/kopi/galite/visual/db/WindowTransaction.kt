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

import java.sql.Connection
import java.sql.SQLException

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.statements.api.ExposedConnection
import org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManager
import org.jetbrains.exposed.sql.transactions.TransactionInterface
import org.jetbrains.exposed.sql.transactions.transactionManager
import org.kopi.galite.visual.dsl.common.Window
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.visual.VWindow

/**
 * Starts a protected transaction.
 *
 * @param       connection      the database connection.
 * @param       statement       the transaction statement.
 */
fun <T> protected(connection: Connection, statement: Transaction.() -> T): T {
  val db = Database.connect({ connection })
  val outerManager = db.transactionManager
  val transaction = (outerManager as ThreadLocalTransactionManager).createTransaction(db)

  transaction.addLogger(StdOutSqlLogger)

  return transaction.statement()
}

fun ThreadLocalTransactionManager.createTransaction(db: Database): Transaction {
  return Transaction(
    ThreadLocalTransaction(
      db = db,
      transactionIsolation = defaultIsolationLevel,
    )
  ).apply {
    bindTransactionToThread(this)
  }
}

class ThreadLocalTransaction(
  override val db: Database,
  override val transactionIsolation: Int,
  override val outerTransaction: Transaction? = null
) : TransactionInterface {

  private val connectionLazy = lazy(LazyThreadSafetyMode.NONE) {
    db.connector().apply {
      autoCommit = false
      transactionIsolation = this@ThreadLocalTransaction.transactionIsolation
    }
  }
  override val connection: ExposedConnection<*>
    get() = connectionLazy.value

  override fun commit() {
    // Do nothing in nested. commit is done by Kopi
  }

  override fun rollback() {
    // Do nothing in nested. rollback is done by Kopi
  }

  override fun close() {
    // Do nothing in nested. close is done by Kopi
  }
}

/**
 * Starts a protected transaction.
 *
 * @param	message		the message to be displayed.
 * @param       db              the database to execute the statement.
 * @param       statement       the transaction statement.
 */
fun <T> Window.transaction(message: String? = null,
                           db: Database? = null,
                           statement: Transaction.() -> T): T {
  return if (isModelInitialized) {
    model.transaction(message, db, statement)
  } else {
    org.jetbrains.exposed.sql.transactions.transaction(db, statement)
  }
}

/**
 * Starts a protected transaction.
 *
 * @param	message		        the message to be displayed.
 * @param       transactionIsolation    the transaction isolation level (Connection.TRANSACTION_SERIALIZABLE,
 * TRANSACTION_READ_UNCOMMITTED, ...). See [Connection].
 * @param       repetitionAttempts      the number of retries when [SQLException] occurs.
 * @param       db                      the database to execute the statement.
 * @param       statement               the transaction statement.
 */
fun <T> Window.transaction(message: String? = null,
                           transactionIsolation: Int,
                           repetitionAttempts: Int,
                           db: Database? = null,
                           statement: Transaction.() -> T): T {
  return if (isModelInitialized) {
    model.transaction(message, transactionIsolation, repetitionAttempts, db, statement)
  } else {
    org.jetbrains.exposed.sql.transactions.transaction(db, statement)
  }
}

/**
 * Starts a protected transaction.
 *
 * @param	message		the message to be displayed.
 * @param       db              the database to execute the statement.
 * @param       statement       the transaction statement.
 */
internal fun <T> VWindow.transaction(message: String? = null,
                                     db: Database? = null,
                                     statement: Transaction.() -> T): T =
        doAndWait(message) {
          val value = org.jetbrains.exposed.sql.transactions.transaction(db, statement)

          if (this is VForm) commitTrail()
          value
        }

/**
 * Starts a protected transaction.
 *
 * @param	message		        the message to be displayed.
 * @param       transactionIsolation    the transaction isolation level (Connection.TRANSACTION_SERIALIZABLE,
 * TRANSACTION_READ_UNCOMMITTED, ...). See [Connection].
 * @param       repetitionAttempts      the number of retries when [SQLException] occurs.
 * @param       db                      the database to execute the statement.
 * @param       statement               the transaction statement.
 */
internal fun <T> VWindow.transaction(message: String? = null,
                                     transactionIsolation: Int,
                                     repetitionAttempts: Int,
                                     db: Database? = null,
                                     statement: Transaction.() -> T): T =
        doAndWait(message) {
          val value = org.jetbrains.exposed.sql.transactions.transaction(
            transactionIsolation,
            repetitionAttempts,
            db,
            statement
          )

          if (this is VForm) {
            commitTrail()
          }
          value
        }

/**
 * Display waiting message while executing the task.
 *
 * @param message the waiting message.
 * @param task    the task to execute.
 */
fun <T> VWindow.doAndWait(message: String?, task: () -> T): T {
  setWaitInfo(message)

  val returnValue = try {
    task()
  } finally {
    unsetWaitInfo()
  }

  return returnValue
}
