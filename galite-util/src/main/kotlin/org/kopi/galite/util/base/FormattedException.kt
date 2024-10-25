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

package org.galite.kopi.util.base

import org.kopi.galite.util.base.Message
import org.kopi.galite.util.base.MessageDescription

/**
 * This class defines exceptions formatted using message descriptions.
 */
open class FormattedException(private val messageDetail: Message) : Exception(messageDetail.description.format) {

  /**
   * An exception with an arbitrary number of parameters
   * @param description the message description
   * @param parameters the array of parameters
   */
  constructor(description: MessageDescription, vararg parameters: Any) : this(Message(description, parameters))

  /**
   * An exception with no parameters
   * @param description the message description
   */
  constructor(description: MessageDescription) : this(description, *emptyArray())

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  /**
   * Returns a string explaining the exception.
   */
  fun getFormattedMessage(): String {
    return messageDetail.description.format // Returning the formatted message
  }
  /**
   * Returns the formatted message.
   */
  val formattedMessage: Message
    get() = messageDetail

  /**
   * Returns true if the error has the specified description.
   */
  fun hasDescription(description: MessageDescription): Boolean {
    return messageDetail.description === description
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  override val message: String?
    get() = messageDetail.description.format // Return the formatted message as String

  companion object {
    /**
     * Comment for `serialVersionUID`
     */
    private const val serialVersionUID = 6590654347028052938L
  }
}
