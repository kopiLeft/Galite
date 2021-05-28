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
import org.kopi.galite.form.dsl.FormBlock

object FormWithMultipleBlock : Form() {
  override val locale = Locale.UK
  override val title = "form for test"
  val blockSample = insertBlock(BlockSample)
  val multipleBlock = insertBlock(MultipleBlock)
}

object MultipleBlock : FormBlock(100, 100, "Test block") {
  val id = hidden(domain = Domain<Int>(20)) {
    label = "id"
    help = "The user id"
  }

  val name = visit(domain = Domain<String>(20), position = at(1, 1)) {
    label = "name"
    help = "The user name"
  }
}

fun main() {
  Application.runForm(FormWithMultipleBlock)
}
