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
  inner class OrderModel {}
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

  // static (compiled) data
  protected var form // enclosing form
          : VForm? = null

  var bufferSize // max number of buffered records
          = 0
  protected var fetchSize // max number of buffered IDs
          = 0
  protected var displaySize // max number of displayed records
          = 0
  protected var page // page number
          = 0

  protected var source // qualified name of source file
          : String? = null
  protected var name // block name
          : String? = null
  protected var shortcut // block short name
          : String? = null
  protected var title // block title
          : String? = null
  protected var align: BlockAlignment? = null
  protected var help // the help on this block
          : String? = null
  protected lateinit var tables // names of database tables
          : Array<String>
  protected var options // block options
          = 0
  protected lateinit var access // access flags for each mode
          : IntArray
  protected lateinit var indices // error messages for violated indices
          : Array<String>

  protected lateinit var commands // commands
          : Array<VCommand>
  protected lateinit var actors // actors to send to form (move to block import)
          : Array<VActor>
  protected lateinit var fields // fields
          : Array<VField>
  protected lateinit var VKT_Triggers: Array<IntArray>

  // dynamic data
  var activeRecord // current record
          = 0
  protected var activeField: VField? = null
  protected var detailMode = false
  protected var recordCount // number of active records
          = 0
  protected var activeCommands // commands currently active
          : Vector<VCommand>? = null

  protected var currentRecord = 0

  protected var mode // current mode
          = 0
  protected lateinit var recordInfo // status vector for records
          : IntArray
  protected lateinit var fetchBuffer // holds Id's of fetched records
          : IntArray
  protected var fetchCount // # of fetched records
          = 0
  protected var fetchPosition // position of current record
          = 0

  protected var blockListener: EventListenerList? = null
  protected var orderModel: org.kopi.galite.form.VBlock.OrderModel? = null

  protected var border = 0
  protected var maxRowPos = 0
  protected var maxColumnPos = 0
  protected var displayedFields = 0

  private val isFilterVisible = false

  protected var dropListMap: HashMap<*, *>? = null
}
