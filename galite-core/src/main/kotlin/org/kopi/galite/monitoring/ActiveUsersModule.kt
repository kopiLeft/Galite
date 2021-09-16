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

import java.util.Locale

import org.kopi.galite.domain.BOOL
import org.kopi.galite.domain.INT
import org.kopi.galite.domain.STRING
import org.kopi.galite.domain.TIMESTAMP
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.dsl.DictionaryForm
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.visual.ApplicationContext

class ActiveUsersModule : DictionaryForm() {
  override val locale = Locale.UK
  override val title = "Active users"
  val action = menu("Action")
  val autoFill = actor(
    ident = "Autofill",
    menu = action,
    label = "Autofill",
    help = "Autofill",
  )

  val users = insertBlock(Users())

  inner class Users : FormBlock(20, 20, "Active users") {

    val userName = visit(domain = STRING(10), position = at(1, 1)) {
      label = "Name"
      help = "The user name"
    }
    val address = visit(domain = STRING(15), position = at(2, 1)) {
      label = "IP address"
      help = "The IP address"
    }
    val application = visit(domain = STRING(30), position = at(3, 1)) {
      label = "Application/Browser"
      help = "The application used by the user"
    }
    val os = visit(domain = STRING(17), position = at(4, 1)) {
      label = "Operating System"
      help = "The OS used by the user"
    }
    val loggedin = visit(domain = BOOL, position = at(5, 1)) {
      label = "Logged in?"
      help = "Is the user logged in?"
    }
    val sessionStart = visit(domain = TIMESTAMP, position = at(6, 1)) {
      label = "Session start"
      help = "The session start timestamp"
    }
    val loginTimestamp = visit(domain = TIMESTAMP, position = at(7, 1)) {
      label = "Login time"
      help = "The user login time"
    }
    val tabs = visit(domain = INT(2), position = at(8, 1)) {
      label = "Tabs count "
      help = "The number of opened tabs"
    }

    init {
      border = VConstants.BRD_LINE

      val activeUsers = ApplicationContext.applicationContext.getActiveUsers()
      activeUsers.forEachIndexed { index, userData ->
        userName[index] = userData.userName
        address[index] = userData.address
        application[index] = userData.application.toString()
        os[index] = userData.os
        loggedin[index] = userData.loggedin
        sessionStart[index] = userData.sessionStart
        loginTimestamp[index] = userData.loginTime
        tabs[index] = userData.tabCount
      }
    }
  }
}
