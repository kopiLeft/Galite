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

import java.util.Vector
import javax.swing.event.EventListenerList

import org.kopi.galite.visual.VActor
import org.kopi.galite.visual.VCommand

abstract class VBlock {

  fun isRecordFilled(rec: Int): Boolean {
    TODO()
  }

  fun isMulti(): Boolean {
    TODO()
  }

  inner class OrderModel {
    //TODO()
  }

  fun getForm(): VForm = TODO()

  fun fetchLookup(fld: VField) {
    TODO()
  }

  companion object {
    // record info flags
    protected val RCI_FETCHED = 0x00000001
    protected val RCI_CHANGED = 0x00000002
    protected val RCI_DELETED = 0x00000004
    protected val RCI_TRAILED = 0x00000008
  }

  protected lateinit var sortedRecords: IntArray

  protected var blockAccess = false

  // prevent that the access of a field is updated
  // (performance in big charts)
  protected var ignoreAccessChange = false

  // max number of buffered records
  var bufferSize = 0

  // max number of buffered IDs
  protected var fetchSize  = 0

  // max number of displayed records
  protected var displaySize  = 0

  // page number
  protected var page = 0

  // qualified name of source file
  protected var source : String? = null

  // block name
  protected var name   : String? = null

  // block short name
  protected var shortcut  : String? = null

  // block title
  protected var title   : String? = null

  // the help on this block
  protected var align: BlockAlignment? = null

  protected var help  : String? = null

  // names of database tables
  protected lateinit var tables : Array<String>

  // block options
  protected var options = 0

  // access flags for each mode
  protected lateinit var access : IntArray

  // error messages for violated indices
  protected lateinit var indices: Array<String>

  // commands
  protected lateinit var commands : Array<VCommand>

  // actors to send to form (move to block import)
  protected lateinit var actors  : Array<VActor>

  // fields
  protected lateinit var fields : Array<VField>

  protected lateinit var VKT_Triggers: Array<IntArray>

  // dynamic data
  // current record
  var activeRecord = 0

  internal var activeField: VField? = null

  protected var detailMode = false

  // number of active records
  protected var recordCount  = 0

  // commands currently active
  protected var activeCommands : Vector<VCommand>? = null

  var currentRecord = 0

  // status vector for records
  protected lateinit var recordInfo : IntArray

  // holds Id's of fetched records
  protected lateinit var fetchBuffer : IntArray

  // # of fetched records
  protected var fetchCount  = 0

  // position of current record
  protected var fetchPosition = 0

  protected var blockListener: EventListenerList? = null

  protected var orderModel: org.kopi.galite.form.VBlock.OrderModel? = null

  protected var border = 0
  protected var maxRowPos = 0
  protected var maxColumnPos = 0
  protected var displayedFields = 0

  private val isFilterVisible = false

  protected var dropListMap: HashMap<*, *>? = null

  fun getMode(): Int = TODO()

  interface OrderListener

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------
  open fun helpOnBlock(help: VHelpGenerator) {
    TODO()
  }
}
