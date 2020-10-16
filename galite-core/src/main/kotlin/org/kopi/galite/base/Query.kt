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

import org.kopi.galite.type.NotNullDate
import org.kopi.galite.type.NotNullFixed
import java.sql.Blob
import java.sql.SQLException

class Query {

  fun isNull(column: Int): Boolean = TODO()

  fun getDate(pos: Int): NotNullDate = TODO()

  fun getBoolean(column: Int): Boolean = TODO()

  fun getObject(pos: Int): Any = TODO()

  fun getFixed(pos: Int): NotNullFixed = TODO()

  fun getByteArray(pos: Int): ByteArray = TODO()

  fun getBlob(pos: Int): Blob? = TODO()
}
