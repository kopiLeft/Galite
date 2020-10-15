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

import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.visual.VActor

abstract class VBlock {

  fun getForm(): VForm = TODO()

  fun fetchLookup(fld: VField) {
    TODO()
  }

  var bufferSize = 0 // max number of buffered records

  // dynamic data
  // current record
  var activeRecord = 0

  fun getMode(): Int = TODO()

  interface OrderListener

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------

  fun gotoNextField() {
    TODO()
  }

  fun executeObjectTrigger(VKT_Type: Int?): Any = TODO()

  fun isChart(): Boolean = TODO()

  open fun helpOnBlock(help: VHelpGenerator) {
    TODO()
  }

  internal var activeField: VField? = null
  internal var source // qualified name of source file
          : String? = null
  internal var shortcut // block short name
          : String? = null
  internal var title // block title
          : String? = null
  internal var align: BlockAlignment? = null
  internal var help // the help on this block
          : String? = null
  internal lateinit var tables // names of database tables
          : Array<String>
  internal var options // block options
          = 0
  internal lateinit var access // access flags for each mode
          : IntArray
  internal lateinit var indices // error messages for violated indices
          : Array<String>
  internal lateinit var name: String // block name
  var isChanged = false
  var pageNumber: Int = 0

  /**
   * Returns true if the block is accessible
   */
  open fun isAccessible(): Boolean {
    TODO()
  }
  open fun updateBlockAccess() {
    TODO()
  }
  open fun checkBlock() {
    TODO()
  }
  open fun getActors(): Array<VActor> {
    TODO()
  }
  open fun initialise() {
    TODO()
  }
  open fun initIntern() {
    TODO()
  }
  open fun close() {
    TODO()
  }
  open fun setCommandsEnabled(enable: Boolean) {
    TODO()
  }

  fun leave(b: Boolean) {
    TODO()
  }

  fun clear(){
    TODO()
  }
  fun setMode(modQuery: Int){
    TODO()
  }
  open fun singleMenuQuery(showSingleEntry: Boolean): Int {
    TODO()
  }

  fun enter() {
    TODO()
  }

  abstract fun localize(manager: LocalizationManager)
}
