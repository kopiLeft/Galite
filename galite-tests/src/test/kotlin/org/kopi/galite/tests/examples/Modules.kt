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
package org.kopi.galite.tests.examples

import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.demo.command.CommandForm
import org.kopi.galite.demo.insertIntoModule
import org.kopi.galite.tests.form.FormWithReport

fun initModules() {
  transaction {
    insertIntoModule("1000", "org/kopi/galite/test/Menu", 0)
    insertIntoModule("1001", "org/kopi/galite/test/Menu", 1, "1000", FormWithReport::class)
    insertIntoModule("2000", "org/kopi/galite/test/Menu", 100)
    insertIntoModule("2001", "org/kopi/galite/test/Menu", 101, "2000", CommandForm::class)
    insertIntoModule("2002", "org/kopi/galite/test/Menu", 102, "2000", FormExample::class)
    insertIntoModule("2003", "org/kopi/galite/test/Menu", 103, "2000", MultipleBlockForm::class)
  }
}
