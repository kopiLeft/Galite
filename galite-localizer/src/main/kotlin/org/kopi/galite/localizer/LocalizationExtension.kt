/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.localizer

import java.util.Locale

import org.gradle.api.Project

class LocalizationExtension {
  /**
   * Database URL.
   */
  lateinit var url: String

  /**
   * JDBC driver
   */
  lateinit var driver: String

  /**
   * Database username.
   * Responds to the `-Pusername` property.
   */
  internal lateinit var username: String

  /**
   * User password
   * Responds to the `-Ppassword` property.
   */
  internal lateinit var password: String

  var locales: List<Locale> = listOf()

  var schema: String? = null

  var output: String? = null

  internal fun configure(project: Project) {
    val username = project.getProperty("username")
    if (username != null) {
      this.username = username
    }

    val password = project.getProperty("password")
    if (password != null) {
      this.password = password
    }
  }

  private fun Project.getProperty(propertyName: String) : String? {
    var value: String? = null

    if (System.getProperty(propertyName) != null) {
      value = System.getProperty(propertyName)
    }

    if (value == null) {
      if (project.hasProperty(propertyName)) {
        value = project.property(propertyName) as String
      }
    }

    logger.info("Property $propertyName is set to $value")

    return value
  }
}
