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
import org.kopi.galite.visual.VCommand

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

  open fun getTitle(): String? {
    TODO()
  }

  // ----------------------------------------------------------------------
  // LISTENER
  // ----------------------------------------------------------------------
  open fun addBlockListener(bl: BlockListener?) {
    TODO()
  }
  // ----------------------------------------------------------------------
  // UI
  // ----------------------------------------------------------------------
  open fun getBorder(): Int {
    TODO()
  }
  open fun getMaxRowPos(): Int {
    TODO()
  }

  open fun getMaxColumnPos(): Int {
    TODO()
  }

  open fun getDisplayedFields(): Int {
    TODO()
  }

  open fun isMulti(): Boolean {
    TODO()
  }

  open fun getDisplaySize(): Int {
    TODO()
  }

  open fun isDroppable(): Boolean {
    TODO()
  }

  open fun getFields(): Array<VField?>? {
   TODO()
  }

  open fun getAlignment(): BlockAlignment? {
    TODO()
  }

  open fun gotoPrevRecord() {
    TODO()
  }

  open fun isRecordAccessible(rec: Int): Boolean {
    TODO()
  }

  open fun isSortedRecordDeleted(sortedRec: Int): Boolean {
    TODO()
  }

  open fun getSortedPosition(rec: Int): Int {
    TODO()
  }

  open fun isRecordDeleted(rec: Int): Boolean {
    TODO()
  }

  open fun isAccepted(flavor: String): Boolean {
    TODO()
  }

  open fun changeActiveRecord(record: Int) {
    TODO()
  }

  open fun getDropTarget(flavor: String): VField? {
    TODO()
  }

  open fun noMove(): Boolean {
    TODO()
  }

  open fun getDataPosition(rec: Int): Int {
    TODO()
  }

  open fun gotoNextRecord() {
    TODO()
  }

  open fun gotoRecord(recno: Int) {
    TODO()
  }

  open fun getActiveField(): VField? {
    TODO()
  }

  fun executeObjectTrigger(VKT_Type: Int?): Any = TODO()

  fun isChart(): Boolean = TODO()

  open fun helpOnBlock(help: VHelpGenerator) {
    TODO()
  }

  internal var activeField: VField? = null

  var currentRecord = 0

  // qualified name of source file
  internal var source : String? = null

  // block short name
  internal var shortcut : String? = null

  // block title
  internal var title : String? = null

  internal var align: BlockAlignment? = null

  // the help on this block
  internal var help : String? = null

  // names of database tables
  internal lateinit var tables : Array<String>

  // block options
  internal var options = 0

  // access flags for each mode
  internal lateinit var access : IntArray

  // error messages for violated indices
  internal lateinit var indices : Array<String>

  // block name
  internal lateinit var name: String

  var isChanged = false

  var pageNumber: Int = 0

  protected lateinit var sortedRecords: IntArray

  protected var blockAccess = false

  // prevent that the access of a field is updated
  // (performance in big charts)
  protected var ignoreAccessChange = false

  // max number of buffered IDs
  protected var fetchSize  = 0

  // commands
  protected lateinit var commands : Array<VCommand>

  // actors to send to form (move to block import)
 internal lateinit var actors  : Array<VActor>

  protected lateinit var VKT_Triggers: Array<IntArray>

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
  fun isRecordFilled(rec: Int): Boolean {
    TODO()
  }

  inner class OrderModel {
    //TODO()
  }
  companion object {
    // record info flags
    protected val RCI_FETCHED = 0x00000001
    protected val RCI_CHANGED = 0x00000002
    protected val RCI_DELETED = 0x00000004
    protected val RCI_TRAILED = 0x00000008
  }
}
