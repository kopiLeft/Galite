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

package org.kopi.galite.form

import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VHelpGenerator

open class VHelpGenerator : VHelpGenerator() {

  open fun helpOnField(blockTitle: String?,
                       pos: Int,
                       label: String,
                       anchor: String?,
                       help: String?) {
    TODO()
  }

 open fun helpOnType(modeName: String,
                     modeDesc: String,
                     typeName: String,
                     typeDesc: String,
                     names: Array<String>?) {
    TODO()
 }

  open fun helpOnFieldCommand(commands: Array<VCommand>?) {
    TODO()
  }
  open fun helpOnForm(name: String,
                 commands: Array<VCommand>?,
                 blocks: Array<VBlock>?,
                 title: String,
                 help: String,
                 code: String): String? {

    TODO()
  }
  open fun helpOnBlock(replace: String,
                       title: String,
                       help: String,
                       commands: Array<VCommand>?,
                       fields: Array<VField?>?,
                       b: Boolean) {}
}
