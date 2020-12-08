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

import org.kopi.galite.demo.desktop.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.type.Image

object FormWithFields: Form() {
  override val locale = Locale.FRANCE
  override val title = "form for test fields visibility"

  val testPage = page("test page")

  init {
    val testBlock = block(1, 1, "Test", "Test block", testPage) {
      val u = table(User)
      val i = index(message = "ID should be unique")

      val id = hidden(domain = Domain<Int>(20)) {
        label = "id"
        help = "The user id"
        columns(u.id)
      }
      val name = mustFill(domain = Domain<String>(20), position = at(1, 1)) {
        label = "name"
        help = "The user name"
        onQuerySkipped()
        columns(u.name)
      }
      val age = mustFill(domain = Domain<Int>(3), position = follow(name)) {
        label = "age"
        help = "The user age"
        onQueryHidden()
        columns(u.age) {
          index = i
          priority = 1
        }
      }
      val image = visit(domain = Domain<Image>(800,500,800), position = at(10,10)) {
        label = "image"
        help = "The user image"
      }
    }
    val testBlock2 = block(1, 1, "Test2", "Test block2", testPage) {

      val totalAge = visit(Domain<Int>(3), position = at(1, 1)) {
        label = "Total"
        help = "total user age"
        onQueryMustFill()
      }
    }

  }
}

fun main() {
  Application.runForm(formName = FormWithFields)
}
