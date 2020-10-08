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

package org.kopi.galite.visual

import java.util.regex.Pattern

/**
 * This class handles localized messages.
 */
//TODO
object MessageCode {
  /**
   * Returns a message (convenience routine).
   *
   * @param     key             the message key
   * @return    the requested message
   */
  fun getMessage(key: String): String = getMessage(key, null)


  fun getMessage(key: String, withKey: Boolean): String = getMessage(key, null, withKey)


  /**
   * Returns a message (convenience routine).
   *
   * @param     key             the message key
   * @param     param           a message parameter
   * @return    the requested message
   */
  fun getMessage(key: String, param: Any?): String = TODO()

  fun getMessage(key: String, param: Any, withKey: Boolean): String = TODO()

  /**
   * Returns a message (convenience routine).
   *
   * @param     key             the message ke
   * @param     param1          the first message parameter
   * @param     param1          the second message parameter
   * @return    the requested message
   */
  fun getMessage(key: String, param1: Any, param2: Any): String = TODO()

  fun getMessage(key: String, param1: Any, param2: Any, withKey: Boolean): String = TODO()

  /**
   * Returns a message (convenience routine).
   * key must be of the form CCC-DDDDD (exp: KOP-00001)
   * where CCC is a 3 capital character module id
   * and DDDDD a 5 digit message id
   *
   * @param     key             the message key
   * @param     params          the array of message parameters
   * @return    the requested message
   */
  fun getMessage(key: String, params: Array<Any>?, withKey: Boolean): String = TODO()

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private val keyPattern = Pattern.compile("^[A-Z][A-Z][A-Z]-\\d\\d\\d\\d\\d$")
}
