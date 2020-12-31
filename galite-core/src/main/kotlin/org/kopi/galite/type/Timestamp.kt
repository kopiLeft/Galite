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

package org.kopi.galite.type

import java.text.SimpleDateFormat

open class Timestamp {

  fun compareTo(other: Timestamp): Int = TODO()

  fun add(millis: Long): NotNullTimestamp = TODO()

  /**
   * Represents the value in sql
   */
  open fun toSql(): String? {

    val micro: String = (timestamp!!.nanos / 1000).toString()

    val tmp = buildString {
      append("00000".substring(0, 6 - micro.length))
      append(micro)
    }
    return SimpleDateFormat("'{ts '''yyyy'-'MM'-'dd' 'HH':'mm':'ss'.$tmp''}'").format(timestamp)
  }

  companion object {
    fun now(): NotNullTimestamp = TODO()
    fun parse(input: String, format: String): NotNullTimestamp = TODO()
  }

  private val timestamp: java.sql.Timestamp? = null
}
