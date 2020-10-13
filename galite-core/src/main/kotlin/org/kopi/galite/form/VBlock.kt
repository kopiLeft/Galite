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

import org.kopi.galite.visual.VActor

abstract class VBlock(form: VForm) {

  var bufferSize = 0 // max number of buffered records

  // dynamic data
  // current record
  var activeRecord = 0

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------
  open fun helpOnBlock(help: VHelpGenerator) {
    TODO()
  }

  abstract fun isChart(): Boolean

  fun isRecordFilled(rec:Int):Boolean =TODO()

  open var actors: Array<VActor>? = null
    internal set
}
