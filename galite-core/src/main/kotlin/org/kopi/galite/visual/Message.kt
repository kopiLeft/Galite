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

import java.util.Locale

import org.kopi.galite.base.ExtendedMessageFormat
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.util.base.InconsistencyException

/**
 * This class handles localized messages
 */
object Message {
  /**
   * Returns a message (convenience routine).
   *
   * @param     ident             the message ident
   * @return    the requested message
   */
  fun getMessage(ident: String): String {
    return getMessage(ident = ident, params = null as Any?)
  }

  /**
   * Returns a message (convenience routine).
   *
   * @param     ident             the message ident
   * @param     param1          the first message parameter
   * @param     param1          the second message parameter
   * @return    the requested message
   */
  fun getMessage(source: String, ident: String, param1: Any, param2: Any): String {
    return getMessage(source = source, ident = ident, params = arrayOf(param1, param2))
  }

  /**
   * Returns the formatted message identified by its unique key and formatted
   * using the given parameters objects.
   *
   * @param source The localization XML source.
   * @param ident The message identifiers.
   * @param params The message parameters.
   * @return The formatted message.
   */
  fun getMessage(source: String = VISUAL_KOPI_MESSAGES_LOCALIZATION_RESOURCE,
                 ident: String,
                 params: Any? = null): String {
    val params = if (params is Array<*>?) params as Array<Any?>? else arrayOf(params)

    val manager = if (ApplicationContext.applicationContext != null
            && ApplicationContext.applicationContext.getApplication() != null) {
      ApplicationContext.getLocalizationManager()
    } else {
      LocalizationManager(Locale.getDefault(), null)
    }
    return try {
      //   Within a String, "''" represents a single quote in java.text.MessageFormat.
      val format = manager!!.getMessageLocalizer(source, ident).getText().replace("'", "''")
      ExtendedMessageFormat.formatMessage(format, params)
    } catch (e: InconsistencyException) {
      System.err.println("ERROR: " + e.message)
      "!$ident!"
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private const val VISUAL_KOPI_MESSAGES_LOCALIZATION_RESOURCE = "org/kopi/galite/VKMessages"
}
