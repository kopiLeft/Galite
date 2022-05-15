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

import java.util.Locale

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.form.Block

object FormWithMultipleBlock : Form(title = "form for test", locale = Locale.UK) {
  val blockSample = insertBlock(BlockSample)
  val multipleBlock = insertBlock(MultipleBlock)
}

object MultipleBlock : Block("Test block", 100, 100) {
  val id = hidden(domain = INT(20)) {
    label = "id"
    help = "The user id"
  }

  val name = visit(domain = STRING(20), position = at(1, 1)) {
    label = "name"
    help = "The user name"
  }
}

fun main() {
  runForm(FormWithMultipleBlock)
}
