/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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

import kotlin.test.assertEquals

import org.jetbrains.exposed.sql.QueryAlias
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.targetTables
import org.junit.Test
import org.kopi.galite.tests.ui.swing.JApplicationTestBase
import org.kopi.galite.database.Users

class FormWithListDomainsTests : JApplicationTestBase() {
  val FormWithListDomains = FormWithListDomains()

  @Test
  fun formWithListDomainsTests() {
    val model = FormWithListDomains.userListBlock.user.vField
    val query = Users.select { Users.id greater 0 }.alias("syn__0__")

    assertEquals(query.alias, (model.list!!.table() as QueryAlias).alias)
    assertEquals(query.columns, model.list!!.table().columns)
    assertEquals(query.targetTables(), model.list!!.table().targetTables())
  }

  @Test
  fun setValueIDTest() {
    val model = FormWithListDomains.userListBlock.user.vField

    model.setValueID(1)
    assertEquals(1, FormWithListDomains.userListBlock.user.value)

    model.setValueID(0)
    assertEquals(null as Int?, FormWithListDomains.userListBlock.user.value)
  }
}
