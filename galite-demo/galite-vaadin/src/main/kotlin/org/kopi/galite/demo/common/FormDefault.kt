/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.demo.common

import java.util.Locale

import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key

class FormDefault(block: Block) : Form() {

  override val locale = Locale.UK
  override val title = "Default"

  val action = menu("Action")
  val autoFill = actor(
          ident = "Autofill",
          menu = action,
          label = "Autofill",
          help = "Autofill",
  )
  val resetBlock = actor(
          ident = "reset",
          menu = action,
          label = "break",
          help = "Reset Block",
  ) {
    key = Key.F3
    icon = Icon.BREAK
  }
  val quitForm = actor(
          ident = "quit",
          menu = action,
          label = "quit",
          help = "quit From",
  ) {
    key = Key.F4  // key is optional here
    icon = Icon.QUIT
  }
  val autofillCommand = command(item = autoFill) {}
  val resetCommand = command(item = resetBlock) {
    block.resetBlock()
  }
  val quitCommand = command(item = quitForm) {
    quitForm()
  }
}
