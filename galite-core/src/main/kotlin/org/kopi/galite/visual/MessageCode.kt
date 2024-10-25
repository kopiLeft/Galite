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

package org.kopi.galite.visual

import java.util.Locale
import java.util.regex.Pattern

import org.kopi.galite.visual.base.ExtendedMessageFormat
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.util.base.InconsistencyException

/**
 * This class handles localized messages.
 */
object MessageCode {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private val keyPattern = Pattern.compile("^[A-Z][A-Z][A-Z]-\\d\\d\\d\\d\\d$")

  /**
   * Returns a message (convenience routine).
   *
   * @param     key             the message ke
   * @param     param1          the first message parameter
   * @param     param1          the second message parameter
   * @return    the requested message
   */
  fun getMessage(key: String, param1: Any, param2: Any): String =
          getMessage(key = key, params = arrayOf(param1, param2))

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
  @Suppress("UNCHECKED_CAST")
  @JvmOverloads
  fun getMessage(key: String, params: Any? = null, withKey: Boolean = true): String {
    val params = when (params) {
      is Array<*> -> params.map { it }.toTypedArray() // Convert to Array<Any?>
      else -> arrayOf(params)
    }

    if (!keyPattern.matcher(key).matches()) {
      throw InconsistencyException("Malformed message key '$key'")
    }
    val domain = key.substring(0, 3)
    val ident = key.substring(4, 9)
    val src = ApplicationContext.getRegistry().getMessageSource(domain)
            ?: throw InconsistencyException("No message source found for module '"
                                                    + domain + "'")
    return try {
      val manager = LocalizationManager(ApplicationContext.getDefaultLocale(), Locale.getDefault())

      // Within a String, "''" represents a single quote in java.text.MessageFormat.
      val format = manager.getMessageLocalizer(src, ident).getText().replace("'", "''")
      val messageFormat = ExtendedMessageFormat(format, ApplicationContext.getDefaultLocale())
      (if (withKey) "$key: " else "") + messageFormat.formatMessage(params)
    } catch (e: InconsistencyException) {
      ApplicationContext.reportTrouble(
        "localize MessageCode",
        "org.kopi.galite.visual.MessageCode.getMessage(String key, Object[] params, boolean withKey)",
        e.message,
        e
      )
      System.err.println("ERROR: " + e.message)
      "$key: message for !$key! not found!"
    }
  }
}
