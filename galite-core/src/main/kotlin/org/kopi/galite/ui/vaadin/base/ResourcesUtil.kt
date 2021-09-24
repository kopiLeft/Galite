/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.ui.vaadin.base

/**
 * Resources utilities.
 */
object ResourcesUtil {
  //---------------------------------------------------
// IMPLEMENTATIONS
//---------------------------------------------------
  /**
   * Returns the resource simple name from its application URI.
   * A theme resource URI have this general form : ${APPLICATION_PATH}/VAADIN/themes/${theme}/name.extension.
   * What we want is to retrieve the resource name.
   * @param uri The resource application URI.
   * @return The simple name of the resource.
   */
  fun getResourceName(uri: String): String {
    // look for the last /
    val lastSlashIndex = uri.lastIndexOf('/')
    // look for the last .
    val lastDotIndex = uri.lastIndexOf('.')
    return when {
      lastDotIndex == -1 && lastSlashIndex == -1 -> uri
      lastDotIndex != -1 && lastSlashIndex != 1 ->
        // found it is OK now, the resource name is between the the / and the .
        uri.substring(lastSlashIndex + 1, lastDotIndex)
      lastDotIndex != -1 && lastSlashIndex == 1 ->
        // no / found, the resource name starts from the beginning.
        uri.substring(0, lastDotIndex)
      else ->
        // no . found take it until the end
        uri.substring(lastSlashIndex)
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private const val THEME_RESOURCE_PREFIX = "theme://resource/"
}
