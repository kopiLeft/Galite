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

import org.jetbrains.exposed.sql.DatabaseConnectionAutoRegistration
import org.jetbrains.exposed.sql.statements.api.ExposedConnection
import org.jetbrains.exposed.sql.statements.api.ExposedSavepoint
import org.jetbrains.exposed.sql.statements.jdbc.JdbcConnectionImpl

class DatabaseConnectionImpl : DatabaseConnectionAutoRegistration {
  override fun invoke(connection:  Connection) = CustomJdbcConnectionImpl(JdbcConnectionImpl(connection))
}

class CustomJdbcConnectionImpl(val connectionImpl: JdbcConnectionImpl) : ExposedConnection<Connection> by connectionImpl {
  override fun commit() {}

  override fun rollback() {}

  override fun close() {}

  override fun rollback(savepoint: ExposedSavepoint) {}
}
