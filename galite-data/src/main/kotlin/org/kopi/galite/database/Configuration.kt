/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.database

import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

class Configuration(var configBundle: Properties = Properties()) {
  companion object {
    /**
     * Get default configuration
     */
    fun getDefault(): Configuration? {
      return instance
    }

    /**
     * Get property value
     */
    fun getString(key: String): String? {
      return try {
        if (instance == null) {
          instance = Configuration()
          instance!!.load()
        }

        instance!![key] ?: resourceBundle?.getString(key)
      } catch (e: IOException) {
        if (resourceBundle != null && resourceBundle!!.containsKey(key)) resourceBundle!!.getString(key) else null
      } catch (e: MissingResourceException) {
        null
      }
    }

    /**
     * Get resource bundle
     */
    fun loadResourceBundle() : ResourceBundle? {
      return try {
        ResourceBundle.getBundle(RESOURCE_FILE)
      } catch (e: MissingResourceException) {
        null
      }
    }

    // ------------------------------------------------------------------
    // DATA MEMBERS
    // ------------------------------------------------------------------

    var                         instance: Configuration? = null
    var                         resourceBundle: ResourceBundle? = loadResourceBundle()

    // The application server (TOMCAT) is started from the tomcat instance root
    // directory which is "/usr/share/instance_name". The service configuration
    // is located in the "/usr/share/instance_name/conf/config.properties"
    private const val           CONFIGURATION_FILE = "conf/config.properties"
    private const val           RESOURCE_FILE: String = "config"

  }

  fun load() {
    var input: InputStream? = null

    try {
      println("Working Directory = ${System.getProperty("user.dir")}")
      input = FileInputStream(CONFIGURATION_FILE)
      configBundle.load(input)
    } finally {
      input?.close()
    }
  }

  operator fun get(key: String): String? {
    return configBundle.getProperty(key)
  }

  fun getConfigurationBundle(): Properties {
    return configBundle
  }

  init {
    instance = this
    resourceBundle = loadResourceBundle()
  }
}
