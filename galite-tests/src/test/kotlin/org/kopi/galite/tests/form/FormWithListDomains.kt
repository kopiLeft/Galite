/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.tests.form

import org.jetbrains.exposed.sql.select
import java.util.Locale

import org.kopi.galite.db.Users
import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.AutoComplete
import org.kopi.galite.domain.ListDomain
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock

object FormWithListDomains: Form() {
  override val locale = Locale.FRANCE
  override val title = "form to test list domains"
  val testBlock = insertBlock(UsersListBlock)
}

object UsersListBlock : FormBlock(1, 1, "UsersListBlock", "UsersListBlock") {
  val user = mustFill(domain = UsersList, position = at(1, 1)) {
    label = "user"
    help = "The user"
  }
}

object UsersList: ListDomain<Int>(20) {

  override val table = query(
          Users.select {
            Users.id greater 2
          }
  )

  override val access = {
    FormWithList
  }

  val autoComplete = complete(AutoComplete.LEFT, 10)

  init {
    this["ID"] = Users.id
    this["UC"] = Users.uc
    this["TS"] = Users.ts
    this["KURZNAME"] = Users.shortName
    this["ZEICHEN"] = Users.character
    this["TELEFON"] = Users.phone
    this["EMAIL"] = Users.email
  }
}

fun main() {
  Application.runForm(formName = FormWithListDomains)
}
