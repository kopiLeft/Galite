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

import java.util.Locale

import org.jetbrains.exposed.sql.Table

import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.type.Image

object Users : Table() {
  val name = varchar("name", 10)
  val image = blob("image")
}

object FormWithSpecialTypes : Form() {
  override val locale = Locale.FRANCE
  override val title = "form for test"

  val blockWithSpecialTypes = insertBlock(BlockWithSpecialTypes())
}

class BlockWithSpecialTypes : FormBlock(1, 1, "Test block") {
  val u = table(Users)

  val name = mustFill(domain = Domain<String>(width = 10), position = at(1, 1)) {
    label = "name"
    help = "The user name"
    columns(u.name)
  }

  val image = visit(domain = Domain<Image>(width = 100, height = 100 ), position = at(1, 2)) {
    label = "image"
    help = "The user image"
    columns(u.image)
  }

}

fun main() {
  Application.runForm(formName = FormWithSpecialTypes)
}
