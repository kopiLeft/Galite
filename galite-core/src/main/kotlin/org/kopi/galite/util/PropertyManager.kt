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

package org.kopi.galite.util

import java.util.ResourceBundle
import java.util.MissingResourceException
import java.util.StringTokenizer
import java.util.Hashtable

/**
 * This class is used to set a hierarchical structure for handling properties:
 * On a properties' file we can define the "parent" property which contains
 * one or a space separated list of parents.
 *
 * @param resourcesFileName               the properties file name
 */
class PropertyManager(val resourcesFileName: String) {

  /**
   * Gets a string for the given key from the resource bundle of this property manager
   * or one of its parents.
   * @param key                the key for the desired string
   * @return                   the string for the given key
   */
  fun getString(key: String): String? {
    return getString(resourcesFileName, ArrayList(), key)
  }

  /**
   * Gets a string for the given key from the resource bundle of this property manager
   * or one of its parents.
   * @param resourceName       the current resource to look into
   * @param visitedResources   list of visited resource files (used to avoid circular dependencies)
   * @param key                the key for the desired string
   * @return                   the string for the given key
   */
  fun getString(resourceName: String?, visitedResources: ArrayList<String>, key: String): String? {
    var resource: ResourceBundle?

    when {
      resourceName == null -> {
        return null
      }
      resources.containsKey(resourceName) -> {
        resource = if (resources[resourceName] is ResourceBundle) {
          resources[resourceName] as ResourceBundle?
        } else {
          return null
        }
      }
      else -> {
        try {
          // create a new bundle and store it on the table
          // to avoid creating a new one for each lookup
          resource = ResourceBundle.getBundle(resourceName)
          resources[resourceName] = resource
        } catch (mre: MissingResourceException) {
          System.err.println("$resourceName.properties not found, will use default properties")
          resources[resourceName] = ""
          resource = null
        }
      }
    }

    return if (resource == null) {
      null
    } else {
      var value = try {
        resource.getString(key)
      } catch (e: Exception) {
        null
      }

      if (value != null) {
        return value
      } else {
        // lookup on parents resources
        var p: String

        // mark the current resource as visited
        visitedResources.add(resourceName)

        val st = try {
          StringTokenizer(resource.getString("parent"))
        } catch (e: Exception) {
          return null
        }

        while (value == null && st.hasMoreTokens()) {
          p = st.nextToken()

          // check this resource (p) if not already visited
          if (!visitedResources.contains(p)) {
            value = try {
              getString(p, visitedResources, key)
            } catch (e: Exception) {
              null
            }
          }
        }
      }
      value
    }
  }

  companion object {
    private val resources = Hashtable<String, Any>()
  }
}
