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

import org.kopi.galite.type.Timestamp

/**
 * The user data.
 *
 * @param userName      the user name.
 * @param os            the operating system used by the user.
 * @param application   the application used by the user.
 * @param address       the IP address of the user.
 * @param tabCount      the number of opened tabs.
 * @param loggedin      is the user already logged in?
 */
data class UserData(var userName: String,
                    var os: String,
                    var application: Application,
                    var address: String,
                    var tabCount: Int = 1,
                    var loggedin: Boolean = false) {
  /**
   * Time of the the access to the application.
   */
  val sessionStart: Timestamp = Timestamp.now()

  /**
   * Time of the the login to the application.
   */
  var loginTime: Timestamp? = null
}
