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
package org.kopi.galite.monitoring

/**
 * Data relative to the application used by the user (example: web browser).
 *
 * @param name          the name of the application used by the user.
 * @param majorVersion  the major version of the application the user is using.
 * @param minorVersion  the minor version of the application the user is using.
 */
data class Application(val name: String, val majorVersion: Int, val minorVersion: Int) {

  val version: String get() = "$majorVersion.$minorVersion"

  override fun toString(): String {
    return if(majorVersion == -1 || minorVersion == -1) {
      "$name $version"
    } else {
      name
    }
  }
}
