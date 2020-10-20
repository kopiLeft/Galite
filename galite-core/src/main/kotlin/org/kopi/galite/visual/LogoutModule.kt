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

import org.kopi.galite.db.DBContext

/**
 * A logout module that simply call [Application.logout]
 */
class LogoutModule : Executable {

  /**
   * MenuTree sets the context of new executable to the default connection
   */
  override var dBContext: DBContext? = null

  /**
   * The start method called every time the user launch this app from menu
   * it should be not modal
   * @exception VException      an exception may be raised by your app
   */
  override fun doNotModal() {
    ApplicationContext.applicationContext.getApplication().logout()
  }
}
