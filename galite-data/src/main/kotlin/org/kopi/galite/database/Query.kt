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

import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow

/**
 * Performs the given action on the single row returned by this query, or throws a DBNoRowException if the result
 * is empty or DBTooManyRowsException if the query returns more than one element.
 *
 * @param actionOnSingleRow the action to perform on the returned result row.
 */
fun Query.into(actionOnSingleRow: (ResultRow) -> Unit) {
  val resultRow = try {
    single()
  } catch (e: NoSuchElementException) {
    throw DBNoRowException()
  } catch (e: IllegalArgumentException) {
    throw DBTooManyRowsException()
  }

  actionOnSingleRow(resultRow)
}
