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
 * Interface for object that executes transactions and query to databases.
 */
interface DBContextHandler {
  /**
   * Returns the database context for this object.
   */
  fun getDBContext(): DBContext

  /**
   * Sets the database context for this object
   *
   * @param        context                a database context
   */
  fun setDBContext(context: DBContext)

  /**
   * Starts a protected transaction.
   *
   * @param        message                the message to be displayed
   */
  fun startProtected(message: String)

  /**
   * Commits a protected transaction.
   */
  fun commitProtected()

  /**
   * Aborts a protected transaction.
   *
   * @param        interrupt       should interrupt the connection if true
   */
  fun abortProtected(interrupt: Boolean)

  /**
   * Returns true if the exception allows a retry of the
   * transaction, false in the other case.
   *
   * @param reason the reason of the transaction failure
   * @return true if a retry is possible
   */
  fun retryableAbort(reason: Exception): Boolean

  /**
   * Asks the user, if she/he wants to retry the exception
   *
   * @return true, if the transaction should be retried.
   */
  fun retryProtected(): Boolean

  /**
   * Returns whether this object handles a transaction at this time.
   */
  fun inTransaction(): Boolean
}
