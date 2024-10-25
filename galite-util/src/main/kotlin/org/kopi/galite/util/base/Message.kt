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

package org.kopi.galite.util.base

class Message(val description: MessageDescription, vararg parameters: Any?) {
  /**
   * An exception with an arbitrary number of parameters
   * @param description the message description
   * @param parameters the vararg parameters
   */
  val params: Array<Any?> = arrayOf(*parameters)

  /**
   * An exception with no parameters
   * @param description the message description
   */
  constructor(description: MessageDescription) : this(description, *emptyArray())

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  val severityLevel: Int
    /**
     * Returns the severity level
     */
    get() = description.level

  val message: String
    /**
     * Returns the string explaining the error
     */
    get() = description.format(this.params)

}
