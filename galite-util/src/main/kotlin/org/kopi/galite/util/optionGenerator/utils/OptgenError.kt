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

package org.kopi.galite.util.optionGenerator.utils

import org.galite.kopi.util.base.FormattedException
import org.kopi.galite.util.base.Message
import org.kopi.galite.util.base.MessageDescription

/**
 * Error thrown on problems encountered while running the program.
 */
class OptgenError : FormattedException {
  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * An exception with a formatted message as argument
   * @param    message        the formatted message
   */
  constructor(message: Message) : super(message)

  /**
   * Primary constructor for creating an exception with a Message
   * An exception with an arbitrary number of parameters
   * @param    description    the message description
   * @param    parameters    the array of parameters
   */
  constructor(description: MessageDescription, vararg parameters: Any?) : this(Message(description, parameters))

  companion object {
    /**
     * Comment for `serialVersionUID`
     */
    private const val serialVersionUID = -1940949686284187233L
  }
}