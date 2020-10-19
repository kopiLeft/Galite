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
import java.text.MessageFormat

import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.l10n.LocalizationManager

/**
 * This class handles localized properties
 */
object VlibProperties {

  // ----------------------------------------------------------------------
  // STATIC METHODS
  // ----------------------------------------------------------------------

  /**
   * Returns the value of the property for the given key.
   *
   * @param     key             the property key
   * @return    the requested property value
   */
  fun getString(key: String): String = getString(key, null)

  /**
   * Returns the value of the property for the given key.
   *
   * @param     key             the property key
   * @param     param1          the first property parameter
   * @param     param1          the second property parameter
   * @return    the requested property value
   */
  fun getString(key: String, param1: Any, param2: Any): String {
    return getString(key, arrayOf(param1, param2))
  }

  fun getString(key: String, params: Any?): String {
    val format: String
    val manager: LocalizationManager = if (ApplicationContext.applicationContext.getApplication() != null) {
      ApplicationContext.getLocalizationManager()
    } else {
      LocalizationManager(Locale.getDefault(), null)
    }
    return try {
      // Within a String, "''" represents a single quote in java.text.MessageFormat.
      format = manager.getPropertyLocalizer(VLIB_PROPERTIES_RESOURCE_FILE, key).getValue().replace("'", "''")

      if (params is Array<*>) {
        MessageFormat.format(format, *params)
      } else {
        MessageFormat.format(format, params)
      }

    } catch (e: InconsistencyException) {
      ApplicationContext.reportTrouble("localize Property",
                                       "org.kopi.galite.visual.VlibProperties.getString(key: String, params: Any?)",
                                       e.message,
                                       e)
      System.err.println("ERROR: ${e.message}")
      "!$key!"
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private const val VLIB_PROPERTIES_RESOURCE_FILE ="resource/org/kopi/galite/VlibProperties"
}
