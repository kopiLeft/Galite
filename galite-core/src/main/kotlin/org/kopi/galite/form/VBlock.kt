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

import java.sql.SQLException
import java.util.EventListener

import javax.swing.event.EventListenerList

import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.all
import kotlin.collections.elementAt
import kotlin.collections.filter
import kotlin.collections.find
import kotlin.collections.first
import kotlin.collections.forEach
import kotlin.collections.forEachIndexed
import kotlin.collections.indices
import kotlin.collections.isNotEmpty
import kotlin.collections.map
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.single
import kotlin.collections.toList
import kotlin.collections.toTypedArray
import kotlin.math.abs

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.EqOp
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.Join
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.compoundAnd
import org.jetbrains.exposed.sql.intLiteral
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.upperCase
import org.kopi.galite.common.Trigger
import org.kopi.galite.db.DBContext
import org.kopi.galite.db.DBContextHandler
import org.kopi.galite.db.DBDeadLockException
import org.kopi.galite.db.DBForeignKeyException
import org.kopi.galite.db.DBInterruptionException
import org.kopi.galite.db.Utils
import org.kopi.galite.form.VConstants.Companion.TRG_PREDEL
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.list.VListColumn
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.ActionHandler
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.Message
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.VActor
import org.kopi.galite.visual.VColor
import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VDatabaseUtils
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VExecFailedException

abstract class VBlock(var form: VForm) : VConstants, DBContextHandler, ActionHandler {
  /**
   * Build everything after construction
   */
  protected fun buildCstr() {
    activeCommands = ArrayList()
    if (bufferSize == 1) {
      fetchSize = displaySize
      displaySize = 1
    } else {
      fetchSize = bufferSize
    }
    mode = VConstants.MOD_QUERY
    recordInfo = IntArray(2 * bufferSize)
    fetchBuffer = IntArray(fetchSize)
    fetchCount = 0
    activeField = null
    activeRecord = if (isMulti()) -1 else 0
    currentRecord = -1
    detailMode = (!isMulti() || noChart()) && displaySize == 1
    sortedRecords = if (isMulti()) {
      IntArray(bufferSize)
    } else {
      IntArray(1)
    }
    for (i in sortedRecords.indices) {
      sortedRecords[i] = i // "default order"
    }
  }

  /**
   * @return The corresponding display associated to this model.
   */
  internal val display: UBlock?
    get() {
      var view: UBlock? = null
      val listeners = blockListener.listenerList
      var i = listeners.size - 2
      while (i >= 0 && view == null) {
        if (listeners[i] == BlockListener::class.java) {
          view = (listeners[i + 1] as BlockListener).getCurrentDisplay()
        }
        i -= 2
      }
      return view
    }

  fun getActiveCommands(): Array<VCommand?> {
    val temp: Array<VCommand?> = arrayOfNulls(activeCommands.size)

    activeCommands.toArray(temp)
    return temp
  }

  /**
   * @param page the page number of this block
   */
  fun setInfo(page: Int) {
    pageNumber = page
    setInfo()
    buildCstr()
  }

  protected open fun setInfo() {
    // Do nothing, should be redefined if some info
    // has to be set
  }

  fun build() {
    //  default does nothing
  }

  /**
   * @return true if this block follows an other block
   */
  val isFollow: Boolean
    get() = alignment != null

  val isDroppable: Boolean
    get() = dropListMap.isNotEmpty()

  fun isAccepted(flavor: String): Boolean = dropListMap.containsKey(flavor.toLowerCase())

  fun getDropTarget(flavor: String): VField? = getField(dropListMap[flavor.toLowerCase()] as? String)

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  /**
   * Localizes this block
   *
   * @param     manager         the manger to use for localization
   */
  fun localize(manager: LocalizationManager) {
    val loc = manager.getBlockLocalizer(source, name)

    title = loc.getTitle()
    help = loc.getHelp()

    if (indices != null) {
      for (i in indices!!.indices) {
        //!!! for now, overwrite ident with localized message
        //!!! inhibits relocalization of a running form
        indices!![i] = loc.getIndexMessage(indices!![i])
      }
    }
    fields.forEach {
      if (!it.isInternal()) {
        it.localize(loc)
      }
    }
  }
  // ----------------------------------------------------------------------
  // Navigation
  // ----------------------------------------------------------------------

  /**
   * Performs a void trigger
   *
   * @param     VKT_Type        the number of the trigger
   */
  override fun executeVoidTrigger(VKT_Type: Int) {
    triggers[VKT_Type]?.action?.method?.invoke()
  }

  fun executeProtectedVoidTrigger(VKT_Type: Int) {
    triggers[VKT_Type]?.action?.method?.invoke()
  }

  @Suppress("UNCHECKED_CAST")
  fun executeObjectTrigger(VKT_Type: Int): Any {
    return (triggers[VKT_Type]?.action?.method as () -> Any).invoke()
  }

  @Suppress("UNCHECKED_CAST")
  fun executeBooleanTrigger(VKT_Type: Int): Boolean {
    return (triggers[VKT_Type]?.action?.method as () -> Boolean).invoke()
  }

  @Suppress("UNCHECKED_CAST")
  open fun executeIntegerTrigger(VKT_Type: Int): Int {
    return (triggers[VKT_Type]?.action?.method as () -> Int).invoke()
  }

  /**
   * implemented for compatibility with old gui
   */
  @Deprecated("")
  fun refresh(x: Boolean) {
    fireBlockChanged()
  }

  /**
   * Sets the access of the block
   * (if isAccessible does not evaluate the
   * access of the block, this method can be made
   * public)
   */
  protected fun setAccess(access: Boolean) {
    if (blockAccess != access) {
      blockAccess = access
      // inform BlockListener
      fireAccessChanged()
    }
  }

  /**
   * Calculates the access for this block
   */
  fun updateBlockAccess() {
    // !! fix that isAccessible do not
    // calculate the access
    // !! merge with updateAcess
    isAccessible
  }

  /**
   * Returns true if the block is accessible
   */
  val isAccessible: Boolean
    get() {
      if (hasTrigger(VConstants.TRG_ACCESS)) {
        val res = try {
          callTrigger(VConstants.TRG_ACCESS)
        } catch (e: VException) {
          throw InconsistencyException(e)
        }
        if (!(res as Boolean)) {
          setAccess(false)
          return false
        }
      }

      val newAccess = getAccess() >= VConstants.ACS_VISIT || isAlwaysAccessible()
      setAccess(newAccess)
      return newAccess
    }

  /**
   * Sets the color properties of the given record.
   * @param r The record number.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  fun setColor(r: Int, foreground: VColor?, background: VColor?) {
    if (!isMulti()) {
      // give up for non multi blocks. use VField#setColor(int,VColor,VColor)
      // for simple blocks
      return
    }
    for (field in fields) {
      if (!field.isInternal()) {
        field.setColor(r, foreground, background)
      }
    }
  }

  /**
   * Resets the color properties of the given record.
   * @param r The record number.
   */
  fun resetColor(r: Int) {
    if (!isMulti()) {
      // give up for non multi blocks. use VField#resetColor(int)
      // for simple blocks
      return
    }
    for (field in fields) {
      if (!field.isInternal()) {
        field.resetColor(r)
      }
    }
  }

  /**
   * Update the color properties of the given record.
   * @param r The record number.
   */
  fun updateColor(r: Int) {
    if (!isMulti()) {
      // give up for non multi blocks. use VField#resetColor(int)
      // for simple blocks
      return
    }
    for (field in fields) {
      if (!field.isInternal()) {
        if (isRecordFilled(r)) {
          field.updateColor(r)
        } else {
          field.resetColor(r)
        }
      }
    }
  }

  /**
   * sort the records to order it by the value of the
   * given column.
   *
   * @param     column column to order or if -1 back to original order
   */
  fun sort(column: Int, order: Int) {
    if (column == -1) {
      for (i in sortedRecords.indices) {
        sortedRecords[i] = i
      }
    } else {
      sortArray(sortedRecords, column, order)
    }

    // inform blocklistener that the order of the rows is changed
    fireOrderChanged()
  }

  fun showHideFilter() {
    isFilterVisible = if (isFilterVisible) {
      filterHidden()
      false
    } else {
      filterShown()
      true
    }
  }

  private fun sortArray(array: IntArray, column: Int, order: Int) {
    mergeSort(array, column, order, 0, array.size - 1, IntArray(array.size))
  }

  private fun mergeSort(array: IntArray,
                        column: Int,
                        order: Int,
                        lo: Int,
                        hi: Int,
                        scratch: IntArray) {
    // a one-element array is always sorted
    val field = fields[column]
    if (lo < hi) {
      val mid = (lo + hi) / 2

      // split into 2 sublists and sort them
      mergeSort(array, column, order, lo, mid, scratch)
      mergeSort(array, column, order, mid + 1, hi, scratch)

      // Merge sorted sublists
      var t_lo = lo
      var t_hi = mid + 1
      for (k in lo..hi) {
        if (t_lo > mid || t_hi <= hi && field.getObject(array[t_hi]) != null && (field.getObject(array[t_lo]) == null
                        || order * compareIt(field.getObject(array[t_hi])!!, field.getObject(array[t_lo])!!) < 0)) {
          scratch[k] = array[t_hi++]
        } else {
          scratch[k] = array[t_lo++]
        }
      }

      // Copy back to array
      for (k in lo..hi) {
        array[k] = scratch[k]
      }
    }
  }

  private fun compareIt(obj1: Any, obj2: Any): Int {
    return when (obj1) {
      is Comparable<*> -> {
        obj1 as Comparable<Any>

        obj1.compareTo(obj2)
      }
      is Boolean -> {
        assert(obj2 is Boolean) { "Can't compare object (Boolean) with " + obj2.javaClass }
        when {
          obj1 == obj2 -> 0
          obj1 -> 1
          else -> -1
        }
      }
      else -> {
        throw InconsistencyException("Objects not comparable: " + obj1.javaClass + " " + obj2.javaClass)
      }
    }
  }

  fun getSortedPosition(rec: Int): Int {
    if (!isMulti()) {
      return rec
    }
    sortedRecords.forEachIndexed { index, e ->
      if (e == rec) return index
    }
    return -1
  }

  fun getDataPosition(rec: Int): Int {
    return if (!isMulti() || rec == -1) rec else sortedRecords[rec]
  }

  /**
   * nb record read (and not deleted)
   */
  val numberOfValidRecord: Int
    get() = getNumberOfValidRecord(bufferSize)

  /**
   * nb record read (and not deleted)
   */
  fun getNumberOfValidRecordBefore(recno: Int): Int = getNumberOfValidRecord(getSortedPosition(recno))

  private fun getNumberOfValidRecord(recno: Int): Int {
    // don't forget to fireValidRecordNumberChanged if
    // the valid number is changed!!
    var count = 0
    var lastFilled = 0

    for (i in 0 until recno) {
      if (!isRecordDeleted(sortedRecords[i])) {
        // && (nonEmptyReached || isRecordFilled(i))) {
        count += 1
        if (isRecordFilled(sortedRecords[i])) {
          lastFilled = count
        }
      }
    }
    // currently only used by the scrollbar.
    // make the size of the scrollbar only so big, that the top
    // most row is filled, when the scrollbar is on the bottom
    count = count.coerceAtMost(lastFilled + displaySize - 1)
    return count // $$$ May be optimised
  }

  val numberOfFilledRecords: Int
    get() {
      var count = 0

      for (i in 0 until bufferSize) {
        if (isRecordFilled(i) && !isRecordDeleted(i)) {
          count += 1
        }
      }
      return count
    }

  /**
   * enter record
   */
  protected fun enterRecord(recno: Int) {
    assert(this == form.getActiveBlock()) { name + " != " + form.getActiveBlock()!!.name }
    assert(isMulti()) { "Is not multiblock" }
    assert(activeRecord == -1) { "Is multi and activeRecord = $activeRecord" }
    assert(activeField == null) { "current field != $activeField" }

    /* activate line */
    activeRecord = recno

    currentRecord = recno
    /* calculate the access of all fields in the row */
    updateAccess(recno)

    fireBlockChanged() // cause a refresh of display
    try {
      callTrigger(VConstants.TRG_PREREC)
    } catch (e: VException) {
      throw InconsistencyException(e)
    }
  }

  /**
   * leave record
   * @exception VException      an exception may occur in field.leave()
   */
  fun leaveRecord(check: Boolean) {
    assert(this == form.getActiveBlock()) { name + " != " + form.getActiveBlock()!!.name }
    assert(isMulti()) { "$name is not a multiblock" }
    assert(activeRecord != -1) { "Is multi and activeRecord = $activeRecord" }

    activeField?.leave(check)
    if (check) {
      callTrigger(VConstants.TRG_POSTREC)
    }
    activeRecord = -1
  }

  /**
   * GOTO FIRST RECORD
   * @exception VException      an exception may occur in record.leave()
   */
  fun gotoFirstRecord() {
    assert(this == form.getActiveBlock()) { name + " != " + form.getActiveBlock()!!.name }

    if (!isMulti()) {
      changeActiveRecord(-fetchPosition)
    } else if (noMove()) {
      throw VExecFailedException(MessageCode.getMessage("VIS-00025"))
    } else {
      var act: VField?
      assert(activeRecord != -1) { "Is multi and activeRecord = $activeRecord" }
      assert(activeField != null) { "current field $activeField" }
      act = activeField

      /* search target record */
      var i = 0

      while (i < bufferSize) {
        if (!isRecordDeleted(i)) {
          break
        }
        i += 1
      }
      if (i == bufferSize || !isRecordAccessible(i)) {
        throw VExecFailedException()
      }
      leaveRecord(true)
      enterRecord(i)
      if (activeField != null) {
        act = activeField
        activeField!!.leave(false)
      }
      act!!.enter()
      if (activeField!!.hasAction() || activeField!!.getAccess(activeRecord) < VConstants.ACS_VISIT) {
        gotoNextField()
      }
    }
  }

  /**
   * GOTO LAST RECORD
   * @exception VException      an exception may occur in record.leave()
   */
  fun gotoLastRecord() {
    assert(this == form.getActiveBlock()) { name + " != " + form.getActiveBlock()!!.name }
    if (!isMulti()) {
      if (fetchPosition >= fetchCount - 1) {
        throw VExecFailedException(MessageCode.getMessage("VIS-00015"))
      }
      changeActiveRecord(fetchCount - fetchPosition - 1)
    } else if (noMove()) {
      throw VExecFailedException(MessageCode.getMessage("VIS-00025"))
    } else {
      var act: VField?
      assert(activeRecord != -1) { "current record: $activeRecord" }
      assert(activeField != null) { "current field: $activeField" }
      act = activeField
      /* search target record */
      var i: Int = bufferSize + 1

      while (i >= 0) {
        if (isRecordFilled(i)) {
          break
        }
        i -= 1
      }
      if (i == 0 || !isRecordAccessible(i)) {
        throw VExecFailedException(MessageCode.getMessage("VIS-00015"))
      }
      leaveRecord(true)
      enterRecord(i)
      if (activeField != null) {
        act = activeField as VField
        activeField!!.leave(false)
      }
      act!!.enter()
      if (activeField!!.hasAction() || activeField!!.getAccess(activeRecord) < VConstants.ACS_VISIT) {
        gotoNextField()
      }
    }
  }

  fun isRecordInsertAllowed(rec: Int): Boolean = !(noInsert() && !isRecordFetched(rec) && !isRecordChanged(rec))

  fun isRecordAccessible(rec: Int): Boolean {
    return when {
      rec < 0 || rec >= bufferSize -> false
      !isAccessible -> false
      else -> isRecordInsertAllowed(rec)
    }
  }

  fun changeActiveRecord(record: Int) {
    assert(this == form.getActiveBlock()) { name + " != " + form.getActiveBlock()!!.name }
    if (!isMulti()) {
      var act: VField? = activeField

      if (mode != VConstants.MOD_UPDATE) {
        throw VExecFailedException(MessageCode.getMessage("VIS-00025"))
      }
      if (isChanged && !form.ask(Message.getMessage("confirm_discard_changes"))) {
        return
      }
      try {
        activeField?.leave(false)
      } catch (e: VException) {
        throw InconsistencyException()
      }
      fetchNextRecord(record)
      try {
        if (activeField != null) {
          act = activeField
          activeField!!.leave(false)
        }
        if (act == null || act.hasAction() || act.getAccess(activeRecord) < VConstants.ACS_VISIT) {
          gotoNextField()
        } else {
          act.enter()
        }
      } catch (e: VException) {
        throw e
      }
    } else if (noMove()) {
      throw VExecFailedException(MessageCode.getMessage("VIS-00025"))
    } else {
      var act: VField? = activeField
      val oldRecord: Int = activeRecord

      if (oldRecord != -1) {
        leaveRecord(true)
      }
      enterRecord(record)
      try {
        if (activeField != null) {
          act = activeField
          activeField!!.leave(false)
        }
        if (act == null || act.hasAction() || act.getAccess(activeRecord) < VConstants.ACS_VISIT) {
          gotoNextField()
        } else {
          act.enter()
        }
      } catch (e: VException) {
        leaveRecord(false)
        enterRecord(oldRecord)
        throw e
      }
    }
  }

  /**
   * GOTO NEXT RECORD OF CURRENT BLOCK
   * @exception VException      an exception may be raised bu record.leave
   */
  fun gotoNextRecord() {
    if (isMulti()) {
      var currentRec = activeRecord
      var i: Int
      assert(currentRec != -1) { " current record $activeRecord" }

      // get position in sorted order
      currentRec = getSortedPosition(currentRec)

      /* search target record*/i = currentRec + 1
      while (i < bufferSize) {
        if (!isSortedRecordDeleted(i)) {
          break
        }
        i += 1
      }
      if (i == bufferSize || !isRecordAccessible(getDataPosition(i))) {
        throw VExecFailedException(MessageCode.getMessage("VIS-00015"))
      }
      // get position in data of next record in sorted order
      changeActiveRecord(getDataPosition(i))
    } else {
      if (fetchPosition >= fetchCount - 1) {
        throw VExecFailedException(MessageCode.getMessage("VIS-00015"))
      }
      changeActiveRecord(1)
    }
  }

  /**
   * GOTO PREVIOUS RECORD
   * @exception VException      an exception may occur in record.leave()
   */
  fun gotoPrevRecord() {
    if (isMulti()) {
      var currentRec = activeRecord
      var i: Int
      assert(currentRec != -1) { " current record $activeRecord" }

      // get position in sorted order
      currentRec = getSortedPosition(currentRec)

      /* search target record*/
      i = currentRec - 1
      while (i >= 0) {
        if (!isSortedRecordDeleted(i)) {
          break
        }
        i -= 1
      }
      if (i == -1 || !isRecordAccessible(getDataPosition(i))) {
        throw VExecFailedException(MessageCode.getMessage("VIS-00015"))
      }
      // get position in data of previous record in sorted order
      changeActiveRecord(getDataPosition(i))
    } else {
      changeActiveRecord(-1)
    }
  }

  /**
   * GOTO SPECIFIED RECORD
   * @param recno               the record number
   * @exception VException      an exception may occur in record.leave()
   */
  fun gotoRecord(recno: Int) {
    assert(this == form.getActiveBlock()) {
      (name + " != "
              + (if (form.getActiveBlock() == null) "null" else form.getActiveBlock()!!.name))
    }
    if (!isMulti()) {
      changeActiveRecord(recno - fetchPosition)
      return
    }
    if (isRecordDeleted(recno)) {
      throw VExecFailedException()
    }
    if (recno >= bufferSize) {
      throw VExecFailedException()
    }
    if (noInsert() && !isRecordFetched(recno) && !isRecordChanged(recno)) {
      throw VExecFailedException()
    }
    changeActiveRecord(recno)
  }

  /**
   * Goto field in current block and in current record.
   * @exception VException      an exception may occur in record.leave()
   */
  fun gotoField(fld: VField) {
    assert(this == form.getActiveBlock()) { name + " != " + form.getActiveBlock()!!.name }
    //!!! hacheni 20171213 : the assertion is replaced by a simple test to avoid fatal error when inaccessible
//    field is to be focused. This case can happen web version due to free navigation. See ticket #1077754
//    for more details. Note that this is only a workaround to not raise a fatal error. Simply, the field
//    won't be entered when it is inaccessible. The case will be analyzed later.
//    assert !fld!!.hasAction() && fld!!.getAccess(activeRecord) >= ACS_VISIT :
//      "has action " + fld!!.hasAction()
//      + " access= " + fld!!.getAccess(activeRecord)
//      + " field=" + fld!!.name
//      + " activeREcord=" + activeRecord;
    if (fld.hasAction() || fld.getAccess(activeRecord) < VConstants.ACS_VISIT) {
      return
    }
    activeField?.leave(true)
    fld.enter()
  }

  /**
   * Goto next field in current record
   * @exception VException      an exception may occur in record.leave()
   */
  fun gotoNextField() {
    assert(this == form.getActiveBlock()) { name + " != " + form.getActiveBlock()!!.name }
    if (activeField == null) {
      return
    }
    var index = getFieldIndex(activeField)
    var target: VField? = null
    val old: VField? = activeField

    activeField!!.leave(true)

    var i = 0
    while (target == null && i < fields.size) {
      index += 1
      if (index == fields.size) {
        index = 0
      }
      if (!fields[index].hasAction() &&
              fields[index].getAccess(activeRecord) >= VConstants.ACS_VISIT &&
              (detailMode && !fields[index].noDetail() || !detailMode && !fields[index].noChart())) {
        target = fields[index]
      }
      i += 1
    }
    if (target == null) {
      old!!.enter()
      throw VExecFailedException()
    }
    target.enter()
  }

  /**
   * Goto previous field in current record
   * @exception VException      an exception may occur in field.leave()
   */
  fun gotoPrevField() {
    assert(this == form.getActiveBlock()) { name + " != " + form.getActiveBlock()!!.name }
    assert(activeField != null) { "current field $activeField" }
    var index = getFieldIndex(activeField)
    var target: VField? = null
    val old: VField? = activeField

    activeField!!.leave(true)

    var i = 0
    while (target == null && i < fields.size) {
      if (index == 0) {
        index = fields.size
      }
      index -= 1
      if (!fields[index].hasAction() &&
              fields[index].getAccess(activeRecord) >= VConstants.ACS_VISIT &&
              (detailMode && !fields[index].noDetail() || !detailMode && !fields[index].noChart())) {
        target = fields[index]
      }
      i += 1
    }
    if (target == null) {
      old!!.enter()
      throw VExecFailedException()
    }
    target.enter()
  }

  /**
   * Goto first accessible field in current record
   * @exception VException      an exception may occur in field.leave()
   */
  fun gotoFirstField() {
    assert(this == form.getActiveBlock()) { name + " != " + form.getActiveBlock()!!.name }
    assert(activeRecord != -1) {
      " current record $activeRecord" // also valid for single blocks
    }
    activeField?.leave(true)

    var target: VField? = null
    var i = 0

    while (target == null && i < fields.size) {
      if (!fields[i].hasAction() && fields[i].getAccess(activeRecord) >= VConstants.ACS_VISIT) {
        target = fields[i]
      }
      i += 1
    }

    target?.enter() ?: fireBlockChanged()
  }

  /**
   * Goto first accessible field in current record that is not fill
   * @exception VException      an exception may occur in field.leave()
   */
  fun gotoFirstUnfilledField() {
    assert(this == form.getActiveBlock()) { name + " != " + form.getActiveBlock()!!.name }
    assert(activeRecord != -1) {
      " current record $activeRecord" // also valid for single blocks
    }
    activeField?.leave(true)

    var target: VField? = null
    var i = 0

    while (target == null && i < fields.size) {
      if (!fields[i].hasAction()
              && fields[i].getAccess(activeRecord) >= VConstants.ACS_VISIT
              && fields[i].isNull(activeRecord)) {
        target = fields[i]
      }
      i += 1
    }
    target?.enter() ?: gotoFirstField()
  }

  /**
   * Goto next accessible field in current record that is not fill
   * @exception VException      an exception may occur in field.leave()
   */
  fun gotoNextEmptyMustfill() {
    assert(this == form.getActiveBlock()) { name + " != " + form.getActiveBlock()!!.name }
    assert(activeRecord != -1) {
      " current record $activeRecord" // also valid for single blocks
    }
    val current: VField? = activeField

    if (activeField != null)
      activeField?.leave(true) ?: run {
        gotoFirstUnfilledField()
        return
      }


    var target: VField? = null

    // found field
    var i = 0
    while (i < fields.size && fields[i] !== current) {
      i += 1
    }
    assert(i < fields.size) { "i: " + i + "  fields.length" + fields.size }
    i += 1

    // walk next to next
    while (target == null && i < fields.size) {
      if (!fields[i].hasAction() &&
              fields[i].getAccess(activeRecord) == VConstants.ACS_MUSTFILL &&
              fields[i].isNull(activeRecord)) {
        target = fields[i]
      }
      i += 1
    }

    // redo from start
    i = 0
    while (target == null && i < fields.size) {
      if (!fields[i].hasAction() &&
              fields[i].getAccess(activeRecord) == VConstants.ACS_MUSTFILL &&
              fields[i].isNull(activeRecord)) {
        target = fields[i]
      }
      i += 1
    }
    target?.enter() ?: gotoFirstField()
  }

  /**
   * Goto last accessible field in current record.
   * @exception VException      an exception may occur in field.leave()
   */
  fun gotoLastField() {
    assert(this == form.getActiveBlock()) { name + " != " + form.getActiveBlock()!!.name }
    assert(activeRecord != -1) {
      " current record $activeRecord" // also valid for single blocks
    }
    activeField?.leave(true)

    var target: VField? = null
    var i = fields.size - 1

    while (i >= 0) {
      if (!fields[i].hasAction() && fields[i].getAccess(activeRecord) >= VConstants.ACS_VISIT) {
        target = fields[i]
      }
      i -= 1
    }
    target?.enter()
  }

  /**
   * Returns true if the block has changed wrt the database.
   */
  val isChanged: Boolean
    get() {
      if (hasTrigger(VConstants.TRG_CHANGED)) {
        val res: Any?
        try {
          res = callTrigger(VConstants.TRG_CHANGED)
        } catch (e: VException) {
          throw InconsistencyException(e)
        }
        return (res as Boolean)
      } else {
        for (i in 0 until bufferSize) {
          if (isRecordChanged(i)) {
            return true
          }
        }
        return false
      }
    }

  /**
   * enter a new block
   */
  fun enter() {
    assert(form.getActiveBlock() == null) { "current block = " + form.getActiveBlock() }
    if (isMulti()) {
      activeRecord = -1
    }

    //    needResetCommands = true;
    form.setActiveBlock(this)
    try {
      callTrigger(VConstants.TRG_PREBLK)
    } catch (e: VException) {
      // a pre block trigger must not fail => chg compiler
      throw InconsistencyException(e)
    }
    if (isMulti()) {
      // find a valid record
      if (activeRecord == -1) {
        var recno = -1
        var i = 0
        while (i < bufferSize && recno == -1) {
          if (!isRecordDeleted(i)) {
            recno = i
          }
          i++
        }
        assert(recno < bufferSize) { "reno: $recno< buffer size:$bufferSize" }
        enterRecord(recno)
      }
    } else {
      // if the block is not a multiblock
      // the record is not entered
      // so the update of the access must be done here
      updateAccess(0) // There is only and always record 0
    }
    if (activeRecord != -1) {
      // SOME PREREC TRIGGERS MIGHT SET CURRENT FIELD, BUT STILL NEED TO
      // BE FORCED-REFRESHED, LEMI 06/08/00, LEMI 03/09/00
      fireBlockChanged()
      if (activeField == null) {
        try {
          gotoFirstField()
        } catch (e: VException) {
          // should only be raised when leaving a field
          throw InconsistencyException()
        }
      }
    }
  }

  /**
   * exit block
   * @exception VException      an exception may be raised by record.leave
   */
  fun leave(check: Boolean): Boolean {
    assert(this == form.getActiveBlock()) { name + " != " + form.getActiveBlock()!!.name }
    if (check) {
      validate()
    } else {
      if (isMulti()) {
        if (activeRecord != -1) {
          leaveRecord(false)
        }
      } else {
        activeField?.leave(false)
      }
    }

    //    needResetCommands = true;
    if (check) {
      callTrigger(VConstants.TRG_POSTBLK)
    }
    form.setActiveBlock(null)

    // lackner 2003.07.31 setMode only if check is true
    if (check) {
      setMode(mode)
    }
    return true
  }

  /**
   * Validate current block.
   * @exception VException      an exception may be raised by triggers
   */
  fun validate() {
    //!!! hacheni 20171213 : force the form active block to this block to avoid a known fatal error raised due
    //    to free navigation implementation in WEB context. It can be that the gotoBlock was not fired when
    //    the block was entered when one of the block fields was focused. This stays a workaround and does not
    //    solve the source of the problem but get rid of the fatal error. The problem will be analyzed later.
    //    see #1078473 & #1079026 for more details.
    //    assert this == form.getActiveBlock() : this.name + " != " + ((form.getActiveBlock() == null) ? "null" : form.getActiveBlock().name);
    if (form.getActiveBlock() !== this) {
      form.gotoBlock(this)
    }

    var lastRecord = activeRecord

    try {
      if (!isMulti()) {
        activeField?.leave(true)
        checkMustfillFields()
      } else {
        var j: Int

        if (activeRecord != -1) {
          leaveRecord(true)
        }

        for (i in 0 until bufferSize) {
          /* check if record is empty */
          activeRecord = i
          lastRecord = i

          if (isRecordChanged(i)) {
            j = 0
            while (j < fields.size) {
              val fld = fields[j]
              if (fld.getAccess(activeRecord) >= VConstants.ACS_VISIT && !fld.isNull(i)) {
                break
              }
              j++
            }
            if (j == fields.size && !noDelete()) {
              if (!isRecordFetched(i)) {
                setRecordChanged(i, false)
              } else {
                setRecordDeleted(i, true)
              }
            }
          }

          if (isRecordFilled(i)) {
            checkMustfillFields()
            callTrigger(VConstants.TRG_VALREC)
          }
          activeRecord = -1
          lastRecord = -1
        }
      }
      callTrigger(VConstants.TRG_VALBLK)
    } catch (exc: VFieldException) {
      throw exc
    } catch (exc: VException) {
      if (lastRecord != -1) {
        if (isMulti()) {
          // chart
          gotoRecord(lastRecord)
        } else {
          // single block
          gotoFirstField()
        }
      } else {
        // leave it on the hard way to be able to enter
        // it again
        form.setActiveBlock(null)
        // reenter the block
        enter()
      }
      throw exc
    } finally {
      fireBlockChanged()
    }
  }

  val record: Int
    get() {
      var current = 1
      var count = 0
      if (isMulti()) {
        current = activeRecord + 1
      } else {
        for (i in 0 until fetchCount) {
          if (fetchBuffer[i] != -1) {
            count++
            if (i == fetchPosition) {
              current = count
              break
            }
          }
        }
      }
      return current
    }

  protected fun fireAccessChanged() {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == BlockListener::class.java) {
        (listeners[i + 1] as BlockListener).blockAccessChanged(this, blockAccess)
      }
      i -= 2
    }
  }

  protected fun fireViewModeEntered(block: VBlock, field: VField?) {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2
    while (i >= 0) {
      if (listeners[i] == BlockListener::class.java) {
        (listeners[i + 1] as BlockListener).blockViewModeEntered(block, field)
      }
      i -= 2
    }
  }

  protected fun fireViewModeLeaved(block: VBlock, field: VField?) {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == BlockListener::class.java) {
        (listeners[i + 1] as BlockListener).blockViewModeLeaved(block, field)
      }
      i -= 2
    }
  }

  protected fun fireRecordCountChanged() {
    val record = record
    val localRecordCount = recordCount
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == BlockRecordListener::class.java) {
        (listeners[i + 1] as BlockRecordListener).blockRecordChanged(getSortedPosition(record - 1) + 1,
                                                                     localRecordCount)
      }
      i -= 2
    }
  }

  /**
   * check that user has proper UI with focus on a field on the good page
   */
  fun checkBlock() {
    if (form.getActiveBlock() == this) {
      if (activeField == null) {
        try {
          if (activeRecord == -1 || isRecordDeleted(activeRecord)) {
            var i = 0
            while (i < bufferSize) {
              if (!isRecordDeleted(i)) {
                break
              }
              i += 1
            }
            activeRecord = i
          }
          gotoFirstField()
          // lackner 2003.07.31
          // - inserted to get information about the usage of this code
          // - can be removed if the method checkBlock is removed
          if (ApplicationContext.getDefaults() != null
                  && ApplicationContext.getDefaults().isDebugModeEnabled()) {
            if ((form.getDisplay() as UForm).getRuntimeDebugInfo() != null) {
              (form.getDisplay() as UForm).getRuntimeDebugInfo()!!.printStackTrace()
            }
            println("INFO: VBlock checkBlock " + Thread.currentThread())
          }
        } catch (f: VException) {
          throw InconsistencyException()
        }
      }
      fireRecordCountChanged()
    } else {
      setCommandsEnabled(false)
    }
  }

  /**
   * Checks that all mustfill fields are filled.
   */
  protected fun checkMustfillFields() {
    fields.forEach { field ->
      if (field.getAccess(activeRecord) == VConstants.ACS_MUSTFILL && field.isNull(activeRecord)) {
        // !!! lackner 04.10.2003 I don't know if it is really necessary here
        fireBlockChanged()
        throw VFieldException(field, MessageCode.getMessage("VIS-00023"))
      }
    }
  }

  /**
   * Clears the entire block.
   */
  fun clear() {
    if (this == form.getActiveBlock()) {
      if (!isMulti()) {
        if (activeField != null) {
          try {
            activeField!!.setNull()
            activeField!!.leave(false)
          } catch (e: VException) {
            throw InconsistencyException()
          }
        }
      } else {
        if (activeRecord != -1) {
          try {
            leaveRecord(false)
          } catch (e: VException) {
            throw InconsistencyException()
          }
        }
      }
    }
    for (i in fields.indices) {
      fields[i].setSearchOperator(VConstants.SOP_EQ)
    }
    if (!noChart() && detailMode) {
      detailMode = false
    }
    setAccess(VConstants.ACS_MUSTFILL)
    for (i in 0 until bufferSize) {
      clearRecordImpl(i)
    }
    fetchPosition = -1

    // clear sorting
    for (i in sortedRecords.indices) {
      sortedRecords[i] = i // "default order"
    }
    fireBlockCleared()
  }

  /**
   * Sets defaults for block.
   */
  fun setDefault() {
    try {
      callTrigger(VConstants.TRG_DEFAULT)
    } catch (e: VException) {
      if (e.message != null) {
        form.notice(e.message!!)
      }
    }
    for (i in 0 until bufferSize) {
      activeRecord = i // also valid for single blocks
      for (j in fields.indices) {
        fields[j].setDefault()
      }
      setRecordChanged(i, false)
    }
    if (isMulti()) {
      activeRecord = -1
    }
    fireBlockCleared()
  }

  /**
   * Sets visibility of block.
   */
  fun setAccess(value: Int) {
    assert(this !== form.getActiveBlock() || activeField == null) { "current block: " + form.getActiveBlock().toString() + "; current field: " + activeField }
    for (i in fields.indices) {
      fields[i].setAccess(value)
    }
  }

  /**
   * Returns true if field is never displayed.
   */
  val isInternal: Boolean
    get() = (access[VConstants.MOD_QUERY] == VConstants.ACS_HIDDEN) &&
            (access[VConstants.MOD_INSERT] == VConstants.ACS_HIDDEN) &&
            (access[VConstants.MOD_UPDATE] == VConstants.ACS_HIDDEN)

  /**
   * Clears given record.
   */
  fun clearRecord(recno: Int) {
    clearRecordImpl(recno)
    fireBlockChanged()
  }

  /**
   * Clears given record.
   */
  protected fun clearRecordImpl(recno: Int) {
    assert(this !== form.getActiveBlock() || isMulti() && recno != activeRecord
                   || !isMulti() && activeField == null) {
      ("activeBlock " + form.getActiveBlock()
              .toString() + " recno " + recno.toString() + " current record " + activeRecord
              .toString() + " isMulti? " + isMulti().toString() + " current field " + activeField)
    }

    // backups the records if it is called in a
    // transaction
    setRecordDeleted(recno, false)

    // don't update access
    ignoreAccessChange = true
    for (i in fields.indices) {
      fields[i].clear(recno)
    }
    setRecordFetched(recno, false)
    setRecordChanged(recno, false)
    // update access again
    ignoreAccessChange = false
    // done in setMode(...)
    // updateAccess(recno);
  }

  /**
   * Inserts an empty record at current position.
   * @exception VException      an exception may be raised by triggers
   */
  fun insertEmptyRecord(recno: Int) {
    assert(isMulti()) { "$name is not a multiblock" }
    assert(activeRecord == -1) { " current record $activeRecord" }

    // search first free record starting at current position
    var i: Int = recno

    while (i < bufferSize) {
      if (!isRecordFetched(i) && !isRecordChanged(i)) {
        break
      }
      i++
    }

    // already new && unchanged
    if (i == recno) {
      return
    }

    // nothing is free
    if (i == bufferSize) {
      throw VExecFailedException()
    }

    // shift from i down to current record */
    while (i > recno) {
      copyRecord(i - 1, i, true)
      i -= 1
    }
    clearRecord(recno)
  }
  // ----------------------------------------------------------------------
  // Interface bd/Triggers
  // ----------------------------------------------------------------------
  /**
   * Loads block from database.
   * @exception VException      an exception may be raised by triggers
   */
  fun load() {
    // get select condition from first record in block
    if (isMulti()) {
      activeRecord = 0
    }
    callProtectedTrigger(VConstants.TRG_PREQRY)

    // create database query
    val columns = getSearchColumns()
    val table = getSearchTables_()
    val condition = getSearchConditions_()
    val orderBy = getSearchOrder_()

    if (isMulti()) {
      activeRecord = -1
      activeField = null
    }

    // clear block: it will only hold the retrieved tuples
    clear()

    // get index of id field in BLOCK
    val idfld: Int = getFieldIndex(idField)

    // get index of id field in QUERY
    var idqry = 0

    for (i in 0 until idfld) {
      if (fields[i].getColumnCount() > 0) {
        idqry += 1
      }
    }

    // open database query, fetch tuples
    val query = if (condition != null) {
      table!!.slice(columns).select(condition).orderBy(*orderBy.toTypedArray())
    } else {
      table!!.slice(columns).selectAll().orderBy(*orderBy.toTypedArray())
    }

    fetchCount = 0

    for (result in query) {
      if (fetchCount >= fetchSize) {
        break
      }

      if (result[columns[idqry]] == 0) {
        continue
      }

      fetchBuffer[fetchCount] = result[columns[idqry]] as Int

      if (fetchCount >= bufferSize) {
        fetchCount += 1
      } else {
        fields.forEachIndexed { index, field ->
          if (field.getColumnCount() > 0) {
            field.setQuery_(fetchCount, result, columns[index])
          }
        }

        setRecordFetched(fetchCount, true)
        setRecordChanged(fetchCount, false)
        setRecordDeleted(fetchCount, false)

        try {
          if (isMulti()) {
            activeRecord = fetchCount
          }
          callProtectedTrigger(VConstants.TRG_POSTQRY)
          if (isMulti()) {
            activeRecord = -1
          }

          fetchCount += 1
        } catch (e: VException) {
          if (isMulti()) {
            activeRecord = -1
          }

          if (e is VSkipRecordException) {
            clearRecordImpl(fetchCount)
          } else {
            clear()
            throw e
          }
        } catch (t: Throwable) {
          t.printStackTrace()
        }
      }
    }

    fetchPosition = 0
    // !!! REMOVE setActiveRecord(0);
    if (!isMulti() && fetchCount == 0) {
      throw VQueryNoRowException(MessageCode.getMessage("VIS-00022"))
    } else if (!isMulti()) {
      setMode(VConstants.MOD_UPDATE)
    }
    fireBlockChanged()
  }

  /**
   * Fetches record with given ID from database.
   * @exception VException      an exception may be raised by triggers
   */
  fun fetchRecord(id: Int) {
    val columns = getSearchColumns()
    val table = getSearchTables_()
    val condition = mutableListOf<Op<Boolean>>()

    condition.add(Op.build { idColumn eq id })
    if (VBlockDefaultOuterJoin.getFetchRecordCondition(fields) != null) {
      condition.add(VBlockDefaultOuterJoin.getFetchRecordCondition(fields)!!)
    }

    try {
      val result = table!!.slice(columns).select(condition.compoundAnd()).single()

      /* set values */
      var j = 0
      fields.forEach { field ->
        if (field.getColumnCount() > 0) {
          field.setQuery_(result, columns[j])
          j += 1
        }
      }
    } catch (noSuchElementException: NoSuchElementException) {
      /* Record does not exist anymore: it was deleted by another user */
      throw VSkipRecordException()
    } catch (illegalArgumentException: IllegalArgumentException) {
      assert(false) { "too many rows" }
    }

    setRecordFetched(activeRecord, true)
    setRecordChanged(activeRecord, false)
    setRecordDeleted(activeRecord, false)
    callProtectedTrigger(VConstants.TRG_POSTQRY)
    setMode(VConstants.MOD_UPDATE)
  }

  /**
   * Fetches next record (in given direction) in fetch buffer.
   * @exception VException      an exception may be raised by triggers
   */
  fun fetchNextRecord(incr: Int) {
    assert(!isMulti()) { "$name is a multi block" }
    var pos: Int = fetchPosition + incr

    while (pos in 0 until fetchCount) {
      if (fetchBuffer[pos] == -1) {
        pos += incr
        continue
      }
      try {
        try {
          fetchPosition = pos
          fetchRecord(fetchBuffer[pos])
          return
        } catch (e: VException) {
          try {
          } catch (abortEx: VException) {
            throw abortEx
          }
        } catch (e: SQLException) {
          try {
          } catch (abortEx: DBDeadLockException) {
            throw VExecFailedException(MessageCode.getMessage("VIS-00058"))
          } catch (abortEx: DBInterruptionException) {
            throw VExecFailedException(MessageCode.getMessage("VIS-00058"))
          } catch (abortEx: SQLException) {
            throw VExecFailedException(abortEx)
          }
        } catch (e: Error) {
          try {
          } catch (abortEx: Error) {
            throw VExecFailedException(abortEx)
          }
        } catch (e: RuntimeException) {
          try {
          } catch (abortEx: RuntimeException) {
            throw VExecFailedException(abortEx)
          }
        }
      } catch (e: VException) {
        if (e !is VSkipRecordException) {
          throw e
        }
        fetchBuffer[pos] = -1
      }
      pos += incr
    }
    throw VExecFailedException()
  }

  /**
   * Saves changes in block to database.
   * @exception VException      an exception may be raised by triggers
   * @exception SQLException            an exception may be raised DB access
   */
  fun save() {
    assert(!isMulti() || activeRecord == -1) { "Is multi and activeRecord = $activeRecord" }
    try {
      callProtectedTrigger(VConstants.TRG_PRESAVE)
    } catch (e: VException) {
      throw InconsistencyException()
    }
    if (!isMulti()) {
      when (getMode()) {
        VConstants.MOD_INSERT -> insertRecord(0, -1)
        VConstants.MOD_UPDATE -> updateRecord(0)
        else -> throw InconsistencyException()
      }
    } else {
      if (isIndexed()) {
        /* first delete all deleted and changed old records */
        for (i in 0 until bufferSize) {
          if (isRecordFetched(i)) {
            if (isRecordChanged(i)) {
              tables!![0].deleteWhere { idColumn eq idField.getInt(i)!! }
            } else if (isRecordDeleted(i)) {
              deleteRecord(i)
            }
          }
        }
      }
      for (i in 0 until bufferSize) {
        if (isRecordDeleted(i)) {
          if (!isRecordFetched(i)) {
            clearRecordImpl(i)
          } else {
            // IF INDEX UPDATE SET THEN RECORD ALREADY DELETED
            if (!isIndexed()) {
              deleteRecord(i)
            }
          }
        } else if (isRecordChanged(i)) {
          try {
            if (!isRecordFetched(i)) {
              insertRecord(i, -1)
            } else {
              if (isIndexed()) {
                // !!! update with ID
                insertRecord(i, idField.getInt(i)!!)
              } else {
                updateRecord(i)
              }
            }
          } catch (doNothing: VSkipRecordException) {
            activeRecord = -1
          }
        }
      }
    }
  }

  /**
   * Deletes in database
   * @exception VException      an exception may be raised by triggers
   * @exception SQLException    an exception may be raised DB access
   */
  fun delete() {
    if (this == form.getActiveBlock()) {
      if (!isMulti()) {
        if (activeField != null) {
          try {
            activeField!!.leave(false)
          } catch (e: VException) {
            throw InconsistencyException()
          }
        }
      } else {
        if (activeRecord != -1) {
          try {
            leaveRecord(false)
          } catch (e: VException) {
            throw InconsistencyException()
          }
        }
      }
    }
    if (!isMulti()) {
      if (!isRecordFetched(0)) {
        clearRecord(0)
      } else {
        deleteRecord(0)
      }
    } else {
      for (i in 0 until bufferSize) {
        if (!isRecordFetched(i)) {
          clearRecord(i)
        } else {
          deleteRecord(i)
        }
      }
    }
    callProtectedTrigger(VConstants.TRG_POSTDEL)
  }

  /**
   * Searches the field holding the ID of the block's base table.
   * May be overridden by actual form.
   */
  val idField: VField
    get() {
      return getBaseTableField("ID") ?: throw InconsistencyException()
    }

  /**
   * Returns the name of the DB column of the ID field.
   */
  @Suppress("UNCHECKED_CAST")
  val idColumn: Column<Int>
    get() {
      return idField.lookupColumn(0) as? Column<Int> ?: throw InconsistencyException()
    }

  // laurent : return f even if it's null until we add this field in
  // all the forms. After we can throw an Exception if the field UC
  // of the block base table is not present.
  /**
   * Searches field holding UC of block base table
   */
  val ucField: VField?
    get() {

      // laurent : return f even if it's null until we add this field in
      // all the forms. After we can throw an Exception if the field UC
      // of the block base table is not present.
      return getBaseTableField("UC")
    }

  /**
   * Searches field holding TS of block base table
   */
  fun getTsField(): VField? {
    return getBaseTableField("TS")
  }

  /**
   * Searches a field of block base table
   *
   * @param     field   the name of the field to search for
   * @return    the field if found, otherwise null
   */
  protected fun getBaseTableField(field: String): VField? {
    for (i in fields.indices) {
      val column = fields[i].lookupColumn(0)
      if (column != null && column.name == field) {
        return fields[i]
      }
    }
    return null
  }

  /**
   * Returns the database columns of block.
   */
  fun getReportSearchColumns(): String? {
    var result: String?
    result = null

    // take all visible fields with database access
    fields.forEach { field ->
      // image fields cannot be handled in a report.
      if (field !is VImageField
              && !field.isInternal()
              && field.getColumnCount() > 0) {
        if (result == null) {
          result = ""
        } else {
          result += ", "
        }
        result += field.getColumn(0)!!.getQualifiedName()
      }
    }

    // add ID field AT END if it exists and not already taken
    for (field in fields) {
      //!!! graf 20080329: should we replace fld!!.name.equals("ID") by fld == getIdField() ?
      if (field.isInternal() && field.name == idField.name && field.getColumnCount() > 0) {
        if (result == null) {
          result = ""
        } else {
          result += ", "
        }
        result += field.getColumn(0)!!.getQualifiedName()
        break
      }
    }
    return result
  }

  /**
   * Returns the database columns of block.
   */
  fun getSearchColumns(): List<Column<*>> =
          fields.filter { it.getColumnCount() > 0 }
                  .map { it.getColumn(0)!!.column }

  /**
   * Checks which outer join syntax (JDBC or Oracle) should be used.
   *
   * @return    true iff Oracle outer join syntax should be used.
   */
  private fun useOracleOuterJoinSyntax(): Boolean {
    TODO()
  }

  /**
   * Tests whether the specified table has nullable columns.
   */
  fun hasNullableColumns(table: Int): Boolean {
    fields.forEach { field ->
      if (field.fetchColumn(table) != -1 && field.isInternal()
              && field.getColumn(field.fetchColumn(table))!!.nullable) {
        return true
      }
    }
    return false
  }

  /**
   * Tests whether this table has only internal fields.
   */
  @Deprecated("use hasOnlyInternalFields(table: Table)")
  fun hasOnlyInternalFields(table: Int): Boolean {
    fields.forEach { field ->
      if (field.fetchColumn(table) != -1 && !field.isInternal()) {
        return false
      }
    }
    return true
  }

  /**
   * Tests whether this table has only internal fields.
   */
  fun hasOnlyInternalFields(table: Table): Boolean = fields.all { it.fetchColumn(table) == -1 || it.isInternal() }

  /**
   * Returns the tables for database query, with outer joins conditions.
   */
  @Deprecated("use getSearchTables_()")
  fun getSearchTables(): String {
    TODO()
  }

  /**
   * Returns the tables for database query, with outer joins conditions.
   */
  fun getSearchTables_(): Join? {
    return VBlockDefaultOuterJoin.getSearchTables(this)
  }

  /**
   * Returns the search conditions for database query.
   */
  @Deprecated("use getSearchConditions_()")
  fun getSearchConditions(): String? {
    TODO()
  }

  /**
   * Returns the search conditions for database query.
   */
  fun getSearchConditions_(): Op<Boolean>? {
    val conditionList: MutableList<Op<Boolean>> = mutableListOf()

    fields.forEach { field ->
      if (field.getColumnCount() > 0) {
        val condColumn = field.getColumn(0)!!.column as Column<String>
        val searchColumn = when (field.options and VConstants.FDO_SEARCH_MASK) {

          VConstants.FDO_SEARCH_NONE -> condColumn
          VConstants.FDO_SEARCH_UPPER -> condColumn.upperCase()
          VConstants.FDO_SEARCH_LOWER -> condColumn.lowerCase()
          else -> throw InconsistencyException("FATAL ERROR: bad search code: $options")
        }

        val condition = field.getSearchCondition_(searchColumn)

        condition?.let {
          conditionList.add(condition)
        }
      }
    }
    return if (conditionList.isEmpty()) {
      null
    } else {
      conditionList.compoundAnd()
    }
  }

  /**
   * Returns the search order for database query.
   */
  fun getSearchOrder(): String? {
    TODO()
  }

  /**
   * Returns the search order for database query.
   */
  open fun getSearchOrder_(): MutableList<Pair<Column<*>, SortOrder>> {
    val columns = mutableListOf<Column<*>>()
    val priorities = IntArray(fields.size)
    val sizes = IntArray(fields.size)
    var elems = 0

    // get the fields connected to the database with their priorities
    fields.forEach { field ->
      if (field.getColumnCount() != 0 && field.getPriority() != 0) {
        // this is a field connected to the database
        columns.add(field.getColumn(0)!!.column)
        priorities[elems] = field.getPriority()
        sizes[elems] = field.width * field.height
        elems += 1
      }
    }

    // (bubble) sort the fields with respect to their priorities
    for (i in elems - 1 downTo 1) {
      var swapped = false

      for (j in 0 until i) {
        if (abs(priorities[j]) < abs(priorities[j + 1])) {
          columns[j] = columns[j + 1].also { columns[j + 1] = columns[j] }
          priorities[j] = priorities[j + 1].also { priorities[j + 1] = priorities[j] }
          sizes[j] = sizes[j + 1].also { sizes[j + 1] = sizes[j] }
          swapped = true
        }
      }
      if (!swapped) {
        break
      }
    }

    // build the order by query
    val orderBy = mutableListOf<Pair<Column<*>, SortOrder>>()
    var size = 0
    // val maxCharacters: Int = form.dBContext.defaultConnection.getMaximumCharactersCountInOrderBy()  //TODO
    // val maxColumns: Int = form.dBContext.defaultConnection.getMaximumColumnsInOrderBy() //TODO

    for (i in 0 until elems) {

      // control the size (nbr of columns and size of characters in an "order by" clause)
      /*  if (size + sizes[i] > maxCharacters || i > maxColumns) { //TODO
        break
      }*/
      size += sizes[i]
      if (priorities[i] < 0) {
        orderBy.add(columns[i] to SortOrder.DESC)
      } else {
        orderBy.add(columns[i] to SortOrder.ASC)
      }
    }
    return orderBy
  }

  /**
   * Fetches lookup fields with key
   * if there are more than one column specified, it takes the fist column
   * @exception VException      an exception may be raised by triggers
   */
  fun fetchLookupFirst(fld: VField?) {
    assert(fld != null) { "fld = $fld" }
    assert(this == form.getActiveBlock()) {
      (name + " != "
              + (if ((form.getActiveBlock() == null)) "null" else form.getActiveBlock()!!.name))
    }
    val table: Int = fld!!.getColumn(0)!!.getTable()

    fetchLookup(table, fld)
  }

  /**
   * Fetches lookup fields with key
   * @exception VException      an exception may be raised by triggers
   */
  fun fetchLookup(fld: VField?) {
    assert(fld != null) { "fld = $fld" }
    assert(this == form.getActiveBlock()) {
      (name + " != "
              + (if ((form.getActiveBlock() == null)) "null" else form.getActiveBlock()!!.name))
    }
    assert(fld!!.getColumnCount() == 1) { "column count: " + fld.getColumnCount() }
    val table: Int = fld.getColumn(0)!!.getTable()
    fetchLookup(table, fld)
  }

  protected fun fetchLookup(tableIndex: Int, currentField: VField) {
    val table = object : Table(tables!![tableIndex].tableName) {}
    val columns = mutableListOf<Column<*>>()
    val conditions = mutableListOf<Op<Boolean>>()

    for (i in fields.indices) {
      val f = fields[i]

      if (f !== currentField && f.lookupColumn(tableIndex) != null && !f.isLookupKey(tableIndex)) {
        f.setNull(activeRecord)
      }
    }

    for (i in fields.indices) {
      val column = fields[i].lookupColumn(tableIndex)

      if (column != null) {
        columns.add(column)
      }

      if (fields[i] == currentField || fields[i].isLookupKey(tableIndex)) {
        val condition = fields[i].getSearchCondition()

        // TODO: 10/12/2020 FIX this !
        if (condition == null || fields[i].lookupColumn(tableIndex)!!.condition() !is EqOp) {
          // at least one key field is not completely specified
          // no guarantee that a unique value will be fetched
          // end processing - non-key fields have already been cleared
          return
        }

        // TODO: 11/12/2020 FIX this !
        if (condition != null) {
          conditions.add(fields[i].lookupColumn(tableIndex)!!.condition())
        }
      }
    }

    try {
      transaction {
        val condition: Op<Boolean> = conditions.compoundAnd()
        val query = table.slice(columns).select(condition)

        if (query.toList().isEmpty()) {
          throw VExecFailedException(MessageCode.getMessage("VIS-00016", arrayOf(tables!![tableIndex])))

        } else {
          var j = 0

          fields.forEach {
            if (it.lookupColumn(tableIndex) != null) {
              it.setQuery_(query.first(), it.getColumn(1 + j)!!.column)
              j += 1
            }
          }
          if (query.toList().isNotEmpty()) {
            throw VExecFailedException(MessageCode.getMessage("VIS-00020", arrayOf(tables!![tableIndex])))
          }
        }
      }
    } catch (e: SQLException) {
      throw VExecFailedException("XXXX !!!!" + e.message)
    }
  }

  fun refreshLookup(record: Int) {
    clearLookups(record)
    selectLookups(record)
  }
  // ----------------------------------------------------------------------
  // BUILD A MENU FROM DB RECORDS MATCHING BLOCK SEARCH CONDITIONS
  // ----------------------------------------------------------------------
  /**
   * Selects ID from block query menu
   *
   * @param     showSingleEntry         display menu even if there is only one element
   * @return    ID of selected record
   */
  fun singleMenuQuery(showSingleEntry: Boolean): Int {
    assert(!isMulti()) { "$name is a multi block" }
    var dialog: VListDialog? = null

    try {
      try {
        dialog = transaction {
          callProtectedTrigger(VConstants.TRG_PREQRY)
          buildQueryDialog()
        }
      } catch (e: VException) {
        try {
        } catch (abortEx: VException) {
          throw abortEx
        }
      } catch (e: SQLException) {
        try {
        } catch (abortEx: DBDeadLockException) {
          throw VExecFailedException(MessageCode.getMessage("VIS-00058"))
        } catch (abortEx: DBInterruptionException) {
          throw VExecFailedException(MessageCode.getMessage("VIS-00058"))
        } catch (abortEx: SQLException) {
          throw VExecFailedException(abortEx)
        }
      } catch (e: Error) {
        try {
        } catch (abortEx: Error) {
          throw VExecFailedException(abortEx)
        }
      } catch (e: RuntimeException) {
        try {
        } catch (abortEx: RuntimeException) {
          throw VExecFailedException(abortEx)
        }
      }
    } catch (e: Exception) {
      if (e.message != null) {
        form.error(e.message!!)
      }
      return -1
    }
    return if (dialog == null) {
      form.error(MessageCode.getMessage("VIS-00022"))
      -1
    } else {
      // !! jdk 1.4.1 lackner 07.08.2003
      // if the second parameter is null, it is slower
      dialog.selectFromDialog(form, null, showSingleEntry)
    }
  }

  /**
   * Builds the query dialog that shows the list of data rows from database.
   *
   * Warning, you should use this method inside a transaction
   */
  fun Transaction.buildQueryDialog(): VListDialog? {
    val query_tab = arrayOfNulls<VField>(fields.size)
    var query_cnt = 0

    /* get the fields to be displayed in the dialog */
    for (field in fields) {

      /* skip fields not related to the database */
      if (field.getColumnCount() == 0) {
        continue
      }

      /* skip fields we don't want to show */
      if (field.getPriority() == 0) {
        continue
      }

      /* skip fields with fixed value */
      if (!field.isNull(activeRecord) &&
              field.getSearchOperator() == VConstants.SOP_EQ &&
              !field.getSql(activeRecord)!!.contains('*')) {
        continue
      }
      query_tab[query_cnt++] = field
    }

    /* (bubble) sort fields wrt priorities */
    for (i in query_cnt - 1 downTo 1) {
      var swapped = false

      for (j in 0 until i) {
        if (abs(query_tab[j]!!.getPriority()) < abs(query_tab[j + 1]!!.getPriority())) {
          val tmp = query_tab[j]

          query_tab[j] = query_tab[j + 1]
          query_tab[j + 1] = tmp
          swapped = true
        }
      }
      if (!swapped) {
        break
      }
    }

    /* build query: first rows to select ... */
    val columns = mutableListOf<Column<*>>()

    for (i in 0 until query_cnt) {
      columns.add(query_tab[i]!!.getColumn(0)!!.column)
    }

    /* add the DB column of the ID field. */
    columns.add(idColumn)

    /* ... and now their order */
    var orderSize = 0
    //val maxCharacters: Int = form.getDBContext().getDefaultConnection().getMaximumCharactersCountInOrderBy() TODO
    //val maxColumns: Int = form.getDBContext().getDefaultConnection().getMaximumColumnsInOrderBy() TODO

    val orderBys = mutableListOf<Pair<Column<*>, SortOrder>>()

    for (i in 0 until query_cnt) {
      // control the size (nbr of columns and size of characters in an "order by" clause)
      val size = query_tab[i]!!.width * query_tab[i]!!.height
      /*if (orderSize + size > maxCharacters || i > maxColumns) {
        break  // TODO
      }*/
      orderSize += size

      if (query_tab[i]!!.getPriority() < 0) {
        orderBys.add(columns[i] to SortOrder.DESC)
      } else {
        orderBys.add(columns[i] to SortOrder.ASC)
      }
    }

    /* query from where ? */
    val tables = getSearchTables_()
    val conditions = getSearchConditions_()

    val values = Array(query_cnt) { arrayOfNulls<Any>(fetchSize) }
    val ids = IntArray(fetchSize)
    var rows = 0

    val query = if (conditions == null) {
      tables!!.slice(columns).selectAll().orderBy(*orderBys.toTypedArray())
    } else {
      tables!!.slice(columns).select(conditions).orderBy(*orderBys.toTypedArray())
    }
    for (result in query) {
      if (rows == fetchSize) {
        break
      }

      /* don't show record with ID = 0 */
      if (result[idColumn] == 0) {
        continue
      }

      ids[rows] = result[idColumn]
      for (i in 0 until query_cnt) {
        values[i][rows] = query_tab[i]!!.retrieveQuery_(result, columns[i])
      }
      rows += 1
    }

    return if (rows == 0) {
      null
    } else {
      val cols = arrayOfNulls<VListColumn>(query_cnt)

      for (i in cols.indices) {
        cols[i] = query_tab[i]!!.getListColumn()
      }
      val dialog = VListDialog(cols, values, ids, rows)
      if (rows == fetchSize) {
        dialog.setTooManyRows()
      }
      dialog
    }
  }
  // ----------------------------------------------------------------------
  // SETS/GETS INFORMATION ABOUT THE BLOCK
  // ----------------------------------------------------------------------

  /**
   * Returns the record info value for the given record.
   * @param rec The record number.
   * @return The record info value.
   */
  fun getRecordInfoAt(rec: Int): Int {
    return recordInfo[rec]
  }

  /**
   * Returns true iff at least one record is filled
   */
  fun isFilled(): Boolean {
    var i = 0

    while (i < bufferSize) {
      if (isRecordFilled(i)) {
        return true
      }
      i += 1
    }
    return false
  }

  /**
   * Returns true iff the record is filled
   */
  fun isRecordFilled(rec: Int): Boolean {
    return !isRecordDeleted(rec) && (isRecordFetched(rec) || isRecordChanged(rec))
  }

  /**
   * Returns true iff the specified record has been fetched from the database
   */
  fun isRecordFetched(rec: Int): Boolean = recordInfo[rec] and RCI_FETCHED != 0

  /**
   * Returns true iff the specified record has been changed
   */
  fun isRecordChanged(rec: Int): Boolean = recordInfo[rec] and RCI_CHANGED != 0

  /**
   * Returns true iff the specified record has been deleted
   */
  fun isRecordDeleted(rec: Int): Boolean = recordInfo[rec] and RCI_DELETED != 0

  /**
   * Returns true iff the specified record has been deleted
   */
  fun isSortedRecordDeleted(sortedRec: Int): Boolean = recordInfo[sortedRecords[sortedRec]] and RCI_DELETED != 0

  /**
   * Returns true iff the specified record is trailed
   */
  fun isRecordTrailed(rec: Int): Boolean = recordInfo[rec] and RCI_TRAILED != 0

  /**
   * Returns true iff the current record is filled
   */
  fun isCurrentRecordFilled(): Boolean = !isCurrentRecordDeleted() && (isCurrentRecordFetched() || isCurrentRecordChanged())

  /**
   * Returns true iff the current record has been fetched from the database
   */
  fun isCurrentRecordFetched(): Boolean = recordInfo[currentRecord] and RCI_FETCHED != 0

  /**
   * Returns true iff the current record has been changed
   */
  fun isCurrentRecordChanged(): Boolean = recordInfo[currentRecord] and RCI_CHANGED != 0

  /**
   * Returns true iff the current record has been deleted
   */
  fun isCurrentRecordDeleted(): Boolean = recordInfo[currentRecord] and RCI_DELETED != 0

  /**
   * Returns true iff the current record is trailed
   */
  fun isCurrentRecordTrailed(): Boolean = recordInfo[currentRecord] and RCI_TRAILED != 0

  /**
   * Returns the current block access.
   */
  fun getAccess(): Int {
    for (i in fields.indices) {
      if (fields[i].getAccess(activeRecord) >= VConstants.ACS_VISIT) {
        return VConstants.ACS_VISIT
      }
    }
    return VConstants.ACS_SKIPPED
  }

  /**
   * Updates current access of block fields in the current Record.
   */
  @JvmOverloads
  fun updateAccess(record: Int = activeRecord) {
    for (i in fields.indices) {
      if (!fields[i].isInternal()) {
        // internal fields are always hidden
        // no need for an update
        fields[i].updateAccess(record)
      }
    }
  }

  fun setRecordFetched(rec: Int, value: Boolean) {
    val oldValue = recordInfo[rec]
    val newValue: Int

    // calculate new value
    newValue = if (value) {
      oldValue or RCI_FETCHED
    } else {
      oldValue and RCI_FETCHED.inv()
    }
    if (newValue != oldValue) {
      // backup record before we changed it
      trailRecord(rec)
      // set record info
      recordInfo[rec] = newValue
      if (!ignoreAccessChange) {
        updateAccess(rec)
      }
      // inform listener that the number of rows changed
      fireValidRecordNumberChanged()
      // inform that the record info has changed
      fireRecordInfoChanged(rec, newValue)
    } else {
      // a value changed - access can change
      if (!ignoreAccessChange) {
        updateAccess(rec)
      }
    }
  }

  /**
   * Use the default record
   */
  fun setRecordFetched(value: Boolean) {
    setRecordFetched(activeRecord, value)
  }

  fun setRecordChanged(rec: Int, value: Boolean) {
    val oldValue = recordInfo[rec]
    val newValue: Int

    // calculate new value
    newValue = if (value) {
      oldValue or RCI_CHANGED
    } else {
      oldValue and RCI_CHANGED.inv()
    }
    if (newValue != oldValue) {
      // backup record before we change it
      trailRecord(rec)
      if (!value && activeField != null && activeField!!.changed) {
        activeField!!.setChanged(false)
      }
      recordInfo[rec] = newValue
      if (!ignoreAccessChange) {
        updateAccess(rec)
      }

      // inform listener that the number of rows changed
      fireValidRecordNumberChanged()
      // inform that the record info has changed
      fireRecordInfoChanged(rec, newValue)
    } else {
      // a value changed - access can change
      if (!ignoreAccessChange) {
        updateAccess(rec)
      }
    }
  }

  /**
   * Use the default record
   */
  fun setRecordChanged(value: Boolean) {
    setRecordChanged(activeRecord, value)
  }

  /**
   *
   */
  fun setRecordDeleted(rec: Int, value: Boolean) {
    val oldValue = recordInfo[rec]
    val newValue: Int

    // calculate new value
    newValue = if (value) {
      oldValue or RCI_DELETED
    } else {
      oldValue and RCI_DELETED.inv()
    }
    if (newValue != oldValue) {
      // backup record before we change it
      trailRecord(rec)
      recordInfo[rec] = newValue
      if (!ignoreAccessChange) {
        updateAccess(rec)
      }
      // inform listener that the number of rows changed
      fireValidRecordNumberChanged()
      // inform that the record info has changed
      fireRecordInfoChanged(rec, newValue)
    } else {
      // a value changed - access can change
      if (!ignoreAccessChange) {
        updateAccess(rec)
      }
    }
  }

  /**
   * Use the default record
   */
  fun setRecordDeleted(value: Boolean) {
    setRecordDeleted(activeRecord, value)
  }

  /**
   *
   */
  fun setRecordTrailed(rec: Int, value: Boolean) {
    if (value) {
      recordInfo[rec] = recordInfo[rec] or RCI_TRAILED
    } else {
      recordInfo[rec] = recordInfo[rec] and RCI_TRAILED.inv()
    }
  }

  /**
   * Use the default record
   */
  fun setRecordTrailed(value: Boolean) {
    setRecordTrailed(activeRecord, value)
  }

  /**
   * COPY RECORD IN BLOCK
   */
  fun copyRecord(from: Int, to: Int, trail: Boolean) {
    if (trail) {
      trailRecord(to)
    }
    recordInfo[to] = recordInfo[from]
    for (i in fields.indices) {
      fields[i].copyRecord(from, to)
    }
  }

  /**
   * Initialises the block.
   * @exception VException      an exception may be raised by triggers
   */
  fun initialise() {
    callTrigger(VConstants.TRG_INIT)
  }

  fun initIntern() {
    for (i in fields.indices) {
      fields[i].block = this
    }
    build()
    for (i in fields.indices) {
      fields[i].build()
    }
  }
  // ----------------------------------------------------------------------
  // Utils
  // ----------------------------------------------------------------------

  /**
   *
   */
  open fun getActor(i: Int): VActor = form.getActor(i)

  /**
   * Returns true iff this block can display more than one record.
   */
  @Deprecated("This method is replaced by noChart()")
  fun isChart(): Boolean = !noChart()

  /**
   * Returns true iff this block can display more than one record.
   */
  fun isMulti(): Boolean = bufferSize > 1

  /**
   * nb field on this block
   */
  fun getFieldCount(): Int = fields.size

  /**
   * Returns a field from its name
   *
   * @param     name    the name of the field
   * @return the field or null if no field with that name has been found
   */
  fun getField(name: String?): VField? {
    return fields.find { name == it.name }
  }

  fun getFieldID(): VField? {
    return getField("ID")
  }

  /**
   * Returns the index of field in block
   */
  fun getFieldIndex(fld: VField?): Int {
    for (i in fields.indices) {
      if (fld == fields[i]) {
        return i
      }
    }
    throw InconsistencyException()
  }

  /*
   * Will empty records not be deleted automatically ?
   */
  protected fun noDelete(): Boolean = options and VConstants.BKO_NODELETE != 0

  /*
   * Are empty records inaccessible ?
   */
  protected fun noInsert(): Boolean = options and VConstants.BKO_NOINSERT != 0

  /*
   * Is navigation between records disabled ?
   */
  fun noMove(): Boolean = options and VConstants.BKO_NOMOVE != 0

  /*
   * Should saving delete and reinsert modified records ?
   */
  protected fun isIndexed(): Boolean = options and VConstants.BKO_INDEXED != 0

  /*
   * Are empty records inaccessible ?
   */
  fun noDetail(): Boolean = options and VConstants.BKO_NODETAIL != 0

  /*
   * Are empty records inaccessible ?
   */
  fun noChart(): Boolean = options and VConstants.BKO_NOCHART != 0

  /*
   * Is this block accessible even when no fields are accessible ?
   */
  protected fun isAlwaysAccessible(): Boolean = options and VConstants.BKO_ALWAYS_ACCESSIBLE != 0

  /*
   * Is this block accessible even when no fields are accessible ?
   */
  protected fun isAlwaysSkipped(): Boolean {
    return access[VConstants.MOD_QUERY] <= VConstants.ACS_SKIPPED &&
            access[VConstants.MOD_UPDATE] <= VConstants.ACS_SKIPPED &&
            access[VConstants.MOD_INSERT] <= VConstants.ACS_SKIPPED
  }

  // ----------------------------------------------------------------------
  // TRAILING
  // ----------------------------------------------------------------------
  /**
   * Sets block untrailed (commits changes).
   */
  fun commitTrail() {
    for (i in 0 until bufferSize) {
      setRecordTrailed(i, false)
    }
  }

  /**
   * Restore trailed information.
   */
  fun abortTrail() {
    var foundTrailed = false

    for (i in 0 until bufferSize) {
      if (isRecordTrailed(i)) {
        copyRecord(i + bufferSize, i, false)
        setRecordTrailed(i, false)
        foundTrailed = true
      }
    }
    if (foundTrailed) {
      fireValidRecordNumberChanged()
    }
  }

  /**
   * Returns a list of filled records
   */
  fun getFilledRecords(): IntArray {
    var count = 0

    run {
      var i = 0

      while (i < bufferSize) {
        if (isRecordFilled(i)) {
          count++
        }
        i += 1
      }
    }
    val elems = IntArray(count)
    count = 0
    var i = 0

    while (i < bufferSize) {
      if (isRecordFilled(i)) {
        elems[count++] = i
      }
      i += 1
    }
    return elems
  }
  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  /**
   * Enables/disables block-level commands
   */
  fun setCommandsEnabled(enable: Boolean) {
    if (enable) {
      if (commands != null) {
        if (activeCommands.size > 0) {
          // remove all commands currently in the list
          setCommandsEnabled(false)
        }
        // add active commands to the list
        for (i in commands!!.indices) {
          // look command access only when the command
          // is active for the block mode.
          if (commands!![i].isActive(mode)) {
            val active: Boolean = if (hasTrigger(VConstants.TRG_CMDACCESS, fields.size + i + 1)) {
              try {
                (callTrigger(VConstants.TRG_CMDACCESS, fields.size + i + 1) as Boolean)
              } catch (e: VException) {
                // consider that the command is active of any error occurs
                true
              }
            } else {
              // if no access trigger is associated with the command
              // we consider it as active command
              true
            }
            activeCommands.add(commands!![i])
            commands!![i].setEnabled(active)
          }
        }
      }
    } else {
      for (i in activeCommands.indices) {
        val cmd: VCommand = activeCommands.elementAt(i)

        cmd.setEnabled(false)
      }
      activeCommands.clear()
    }
  }

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param     action          the action to perform.
   * @param     block           This action should block the UI thread ?
   */
  @Deprecated("Use method performAsyncAction without bool parameter",
              ReplaceWith("performAsyncAction(action)"))
  override fun performAction(action: Action, block: Boolean) {
    form.performAsyncAction(action)
  }

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param     action          the action to perform.
   */
  override fun performAsyncAction(action: Action) {
    form.performAsyncAction(action)
  }

  /**
   * Trails information about the record.
   * This copy (backup) is used if the transaction is aborted
   * to rollback the form the the correct point.
   */
  fun trailRecord(rec: Int) {
    // check if trailing needed
    if (!form.inTransaction() || isRecordTrailed(rec)) {
      return
    }

    // copy record to trail area
    copyRecord(rec, bufferSize + rec, false)
    setRecordTrailed(rec, true)
  }
  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  fun callProtectedTrigger(event: Int, index: Int = 0): Any? {
    currentRecord = activeRecord
    executeProtectedVoidTrigger(VKT_Triggers[index][event])
    return null
  }
  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  fun callTrigger(event: Int, index: Int = 0): Any? {
    val returnValue: Any?

    // do not use getCurrentRecord because getCurrentRecord throws an
    // exception if currentRecord is null.
    val oldCurrentRecord: Int = _currentRecord
    returnValue = try {
      currentRecord = activeRecord
      when (VConstants.TRG_TYPES[event]) {
        VConstants.TRG_VOID -> {
          executeVoidTrigger(VKT_Triggers[index][event])
          null
        }
        VConstants.TRG_BOOLEAN -> executeBooleanTrigger(VKT_Triggers[index][event])
        VConstants.TRG_INT -> executeIntegerTrigger(VKT_Triggers[index][event])
        VConstants.TRG_OBJECT -> executeObjectTrigger(VKT_Triggers[index][event])
        else -> throw InconsistencyException("BAD TYPE" + VConstants.TRG_TYPES.get(event))
      }
    } finally {
      // Triggers like ACCESS or VALUE trigger can be called anywhere
      // but should not change the currentRecord for further calculations.
      currentRecord = oldCurrentRecord
    }
    return returnValue
  }

  /**
   * Returns true iff there is trigger associated with given event.
   */
  fun hasTrigger(event: Int): Boolean = hasTrigger(event, 0)

  /**
   * Returns true iff there is trigger associated with given event.
   */
  fun hasTrigger(event: Int, index: Int): Boolean = VKT_Triggers[index][event] != 0

  /*
   * Clears all hidden lookup fields.
   */
  protected fun clearLookups(recno: Int) {
    tables?.forEach { table ->
      fields.forEach { field ->
        if (field.isInternal() && field.lookupColumn(table) != null && field.eraseOnLookup()) {
          field.setNull(recno)
        }
      }
    }
  }

  /*
   * For each lookup-table of block check that record exists and is unique
   *
   * Selects a record from a lookup table
   * Checks that record exists and is unique
   */
  protected fun selectLookups(recno: Int) {
    if (tables != null) {
      for (i in 1 until tables!!.size) {
        selectLookup(tables!![i], recno)
      }
    }
  }

  private fun isNullReference(table: Table, recno: Int): Boolean {
    var nullReference: Boolean

    // check if this lookup table has not only internal fields
    if (hasOnlyInternalFields(table)) {
      nullReference = false
    } else {
      // check if all lookup fields for this table are null.
      nullReference = true

      for (field in fields) {
        if (!nullReference) {
          break
        }
        if (field.fetchColumn(table) != -1
                && !field.isInternal()
                && !field.isNull(recno)) {
          nullReference = false
        }
      }
    }

    // this test is useful since we use outer join only for nullable columns.
    for (field in fields) {
      if (!nullReference) {
        break
      }
      if (field.isInternal()
              && field.fetchColumn(0) != -1
              && field.fetchColumn(table) != -1
              && !(field.getColumn(field.fetchColumn(table))!!.nullable ||
                      field.getColumn(field.fetchColumn(0))!!.nullable)) {
        nullReference = false
      }
    }
    return nullReference
  }

  /*
   *
   */
  protected fun selectLookup(table: Table, recno: Int) {
    val columns = mutableListOf<Column<*>>()
    val conditions = mutableListOf<Op<Boolean>>()

    // set internal fields to null (null reference)
    if (isNullReference(table, recno)) {
      fields.forEach { field ->
        if (field.isInternal() && field.lookupColumn(table) != null) {
          field.setNull(recno)
        }
      }
    } else {
      fields.forEach { field ->
        val column = field.lookupColumn(table) as Column<Any>?

        if (column != null) {
          columns.add(column)
          if (!field.isInternal() || !field.isNull(recno)) {
            val sql = field.getSql(recno)

            if (sql != "?") { // dont lookup for blobs...
              if (field.getSql(recno).equals(Utils.NULL_LITERAL)) {
                conditions.add(Op.build { column.isNull() })
              } else {
                conditions.add(Op.build { column eq field.getSql(recno)!! })
              }
            }
          }
        }
      }
      if (conditions.isEmpty()) {
        throw InconsistencyException("no conditions for table ${table.tableName}")
      }

      try {
        val result = table.slice(columns).select(conditions.compoundAnd()).single()

        fields.forEachIndexed { index, field ->
          if (field.lookupColumn(table) != null) {
            field.setQuery_(recno, result, columns[index])
          }
        }
      } catch (noSuchElementException :NoSuchElementException) {
        activeRecord = recno
        throw VExecFailedException(MessageCode.getMessage("VIS-00016", arrayOf(table.tableName)))
      } catch (illegalArgumentException: IllegalArgumentException) {
        activeRecord = recno
        throw VExecFailedException(MessageCode.getMessage("VIS-00020", arrayOf(table.tableName)))
      }
    }
  }

  /*
   * Checks unique index constraints
   * @exception VException      an exception may be raised by triggers
   */
  fun checkUniqueIndices(recno: Int) {
    if (indices != null) {
      val id = if (isRecordFetched(recno)) idField.getInt(recno) else -1

      for (i in indices!!.indices) {
        checkUniqueIndex(i, recno, id!!)
      }
    }
  }

  /*
   * Checks unique index constraints
   */
  protected open fun checkUniqueIndex(index: Int, recno: Int, id: Int) {
    val condition = mutableListOf<Op<Boolean>>()

    for (field in fields) {
      val column  = if (field.isNull(recno) || !field.hasIndex(index)) {
        null
      } else {
        @Suppress("UNCHECKED_CAST")
        field.lookupColumn(0) as? Column<Any>
      }
      if (column != null) {
        condition.add(Op.build { column eq field.getSql(recno)!! })
      }
    }

    if (condition.isNotEmpty()) {
      try {
        val result = tables!![0].slice(idColumn).select{ condition.compoundAnd() }.single()

        if (result[idColumn] != id) {
          form.setActiveBlock(this@VBlock)
          activeRecord = recno
          gotoFirstField()
          throw VExecFailedException(MessageCode.getMessage("VIS-00014", arrayOf<Any>(indices!![index])))
        }
      } catch (illegalArgumentException: IllegalArgumentException) {
        error("too many rows")
      }
    }
  }

  /**
   * Inserts the specified record of given block into database.
   *
   * @param     recno           the index of the record to insert
   * @param     id              the ID to give to the record or -1 if next available to get
   *
   * @exception VException      an exception may be raised by triggers
   */
  protected fun insertRecord(recno: Int, id: Int) {
    TODO()
  }

  /**
   *
   */
  protected fun fillIdField(recno: Int, id: Int) {
    TODO()
  }

  /**
   * Updates current record of given block in database.
   */
  protected fun updateRecord(recno: Int) {
    TODO()
  }

  /**
   * Deletes current record of given block from database.
   */
  fun deleteRecord(recno: Int) {
    try {
      assert(!isMulti() || activeRecord == -1) { "isMulti? " + isMulti() + " current record " + activeRecord }
      if (isMulti()) {
        activeRecord = recno
      }
      callProtectedTrigger(TRG_PREDEL)
      fields.forEach {
        it.callProtectedTrigger(TRG_PREDEL)
      }
      if (isMulti()) {
        activeRecord = -1
      }
      val id = idField.getInt(recno)!!

      if (id == 0) {
        activeRecord = recno
        throw VExecFailedException(MessageCode.getMessage("VIS-00019"))
      }
      VDatabaseUtils.checkForeignKeys_(form, id, tables!![0])

      /* verify that the record has not been changed in the database */
      checkRecordUnchanged(recno)
      try {
        tables!![0].deleteWhere { idColumn eq id }
      } catch (e: DBForeignKeyException) {
        activeRecord = recno // also valid for single blocks
        throw convertForeignKeyException(e)
      }
      clearRecord(recno)
    } catch (e: VException) {
      if (isMulti() && form.getActiveBlock() != this) {
        activeRecord = -1
      }
      throw e
    }
  }

  /**
   * Check whether the given record has been modified (deleted or updated) in the
   * database.
   */
  protected fun checkRecordUnchanged(recno: Int) {
    // Assertion enabled only for tables with ID
    if (!blockHasNoUcOrTsField()) {
      val idFld: VField = idField
      val ucFld: VField? = ucField
      val tsFld = getTsField()
      val table = tables!![0]
      val value = idFld.getInt(recno)

      assert(ucFld != null || tsFld != null) { "UC or TS field must exist (Block = $name)." }

      val ucColumn = if (ucFld == null) {
        intLiteral(-1)
      } else {
        Column(table, "UC", IntegerColumnType())
      }

      val tsColumn = if (tsFld == null) {
        intLiteral(-1)
      } else {
        Column(table, "UC", IntegerColumnType())
      }

      val query = table.slice(ucColumn, tsColumn)
              .select { idColumn eq value!! }

      if (query.empty()) {
        activeRecord = recno
        throw VExecFailedException(MessageCode.getMessage("VIS-00018"))
      } else {
        var changed = false

        transaction {
          if (ucFld != null) {
            changed = changed or (ucFld.getInt(recno) != query.first()[ucColumn])
          }
          if (tsFld != null) {
            changed = changed or (tsFld.getInt(recno) != query.first()[tsColumn])
          }

          if (changed) {
            // record has been updated
            activeRecord = recno // also valid for single blocks
            throw VExecFailedException(MessageCode.getMessage("VIS-00017"))
          }
        }
      }
    }
  }

  /**
   * Returns true iff this block has no UC and no TS field.
   * May be overridden in subclasses eg actual blocks. Note: In this case,
   * conflicting deletes or updates of a record being edited, are impossible to
   * detect.
   */
  protected fun blockHasNoUcOrTsField(): Boolean {
    return false
  }

  /**
   * Converts a [DBForeignKeyException] to visual exception with a comprehensive
   * message including the tow table in relation.
   * If the referenced and the referencing table are not provided, we will use the
   * exception message. Note, that this should not happen if FK exception are handled
   * in driver interfaces.
   */
  protected fun convertForeignKeyException(exception: DBForeignKeyException): VExecFailedException {
    val referenced: String? = exception.referencedTable
    val referencing: String? = exception.referencingTable
    return if (referenced == null || referencing == null) {
      // use the original exception in this case
      VExecFailedException(exception)
    } else VExecFailedException(MessageCode.getMessage("VIS-00021", arrayOf(referencing, referenced)))
    // create a visual exception
  }

  /**
   * Checks if a foreign key is referenced in the view SYSTEMREFERENZEN
   * TODO: Remove this cause it is depending on SYSTEMREFERENZEN table.
   */
  @Deprecated("")
  protected fun convertForeignKeyException(name: String?): VExecFailedException {
    TODO()
  }
  // ----------------------------------------------------------------------
  // ACTOR HANDLING (TBC)
  // ----------------------------------------------------------------------

  fun close() {
    setCommandsEnabled(false)
    if (activeField != null) {
      // !!! TO DO
      //      activeField.getUI().close();
    }
  }

  override var dBContext: DBContext?
    get() = form.dBContext
    set(value) = throw InconsistencyException("CALL IT ON FORM")

  override fun retryableAbort(reason: Exception): Boolean = form.retryableAbort(reason)

  override fun retryProtected(): Boolean = form.retryProtected()

  override fun inTransaction(): Boolean = form.inTransaction()

  // ----------------------------------------------------------------------
  // LISTENER
  // ----------------------------------------------------------------------
  fun addBlockListener(bl: BlockListener?) {
    blockListener.add(BlockListener::class.java, bl)
  }

  fun removeBlockListener(bl: BlockListener?) {
    blockListener.remove(BlockListener::class.java, bl)
  }

  fun addBlockRecordListener(bl: BlockRecordListener?) {
    blockListener.add(BlockRecordListener::class.java, bl)
  }

  fun removeBlockRecordListener(bl: BlockRecordListener?) {
    blockListener.remove(BlockRecordListener::class.java, bl)
  }

  protected fun fireBlockChanged() {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == BlockListener::class.java) {
        (listeners[i + 1] as BlockListener).blockChanged()
      }
      i -= 2
    }
  }

  protected fun fireValidRecordNumberChanged() {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == BlockListener::class.java) {
        (listeners[i + 1] as BlockListener).validRecordNumberChanged()
      }
      i -= 2
    }
  }

  protected fun fireRecordInfoChanged(rec: Int, info: Int) {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == BlockListener::class.java) {
        (listeners[i + 1] as BlockListener).recordInfoChanged(rec, info)
      }
      i -= 2
    }
  }

  protected fun fireBlockCleared() {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == BlockListener::class.java) {
        (listeners[i + 1] as BlockListener).blockCleared()
      }
      i -= 2
    }
  }

  protected fun fireOrderChanged() {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == BlockListener::class.java) {
        (listeners[i + 1] as BlockListener).orderChanged()
      }
      i -= 2
    }
  }

  protected fun filterShown() {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == BlockListener::class.java) {
        (listeners[i + 1] as BlockListener).filterShown()
      }
      i -= 2
    }
  }

  protected fun filterHidden() {
    val listeners = blockListener.listenerList
    var i = listeners.size - 2

    while (i >= 0) {
      if (listeners[i] == BlockListener::class.java) {
        (listeners[i + 1] as BlockListener).filterHidden()
      }
      i -= 2
    }
  }

  // ----------------------------------------------------------------------
  // SORTING AND LABELS
  // ----------------------------------------------------------------------

  interface OrderListener : EventListener {
    fun orderChanged()
  }

  inner class OrderModel {
    private fun setState(i: Int) {
      when (i) {
        Companion.STE_INC -> {
          sortOrder = 1
          state = Companion.STE_INC
        }
        Companion.STE_DESC -> {
          sortOrder = -1
          state = Companion.STE_DESC
        }
        Companion.STE_UNORDERED -> {
          sortedColumnIndex = -1
          sortOrder = 1
          state = Companion.STE_UNORDERED
        }
      }
    }

    fun sortColumn(index: Int) {
      if (sortedColumnIndex == index) {
        when (state) {
          Companion.STE_INC -> setState(Companion.STE_DESC)
          Companion.STE_DESC -> setState(Companion.STE_UNORDERED)
          Companion.STE_UNORDERED -> setState(Companion.STE_INC)
          else -> setState(Companion.STE_INC)
        }
      } else {
        sortedColumnIndex = index
        setState(Companion.STE_INC)
      }
      sort(sortedColumnIndex, sortOrder)
      fireOrderChanged()
    }

    fun getColumnOrder(index: Int): Int {
      return if (index == sortedColumnIndex) state else Companion.STE_UNORDERED
    }

    private fun fireOrderChanged() {
      val listeners = orderListener.listenerList
      var i = listeners.size - 2

      while (i >= 0) {
        if (listeners[i] == OrderListener::class.java) {
          (listeners[i + 1] as OrderListener).orderChanged()
        }
        i -= 2
      }
    }

    fun addSortingListener(sl: OrderListener?) {
      orderListener.add(OrderListener::class.java, sl)
    }

    private var sortedColumnIndex: Int
    private var state = 0
    private var sortOrder = 0
    private val orderListener: EventListenerList

    init {
      sortedColumnIndex = -1 // no column is sorted
      orderListener = EventListenerList()
    }
  }

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------
  fun helpOnBlock(help: VHelpGenerator) {
    if (!isAlwaysSkipped()) {
      help.helpOnBlock(form.javaClass.name.replace('.', '_'),
                       title,
                       this.help,
                       commands,
                       fields,
                       form.blocks.size == 1)
    }
  }

  fun getFieldPos(field: VField): Int {
    var count = 1

    for (i in fields.indices) {
      if (field == fields[i]) {
        return count
      }
      if (fields[i].getDefaultAccess() != VConstants.ACS_HIDDEN) {
        count++
      }
    }
    return 0
  }

  // ----------------------------------------------------------------------
  // SNAPSHOT PRINTING
  // ----------------------------------------------------------------------
  /**
   * prepare a snapshot of all fields
   */
  fun prepareSnapshot(active: Boolean) {
    // set background ???
    if (commands != null) {
      for (i in commands!!.indices) {
        commands!![i].setEnabled(true)
      }
    }
    var count = 1

    for (i in fields.indices) {
      if (fields[i].getDefaultAccess() != VConstants.ACS_HIDDEN) {
        fields[i].prepareSnapshot(count++, active)
      }
    }
  }

  override fun toString(): String {
    return buildString {
      try {
        append("\n-----------------------------------------------\nBLOCK ")
        append(name)
        append(" Shortcut: ")
        append(shortcut)
        append(" Title: ")
        append(title)
        append("\n")
        append("bufferSize: ")
        append(bufferSize)
        append("; fetchSize: ")
        append(fetchSize)
        append("; displaySize: ")
        append(displaySize)
        append("; page: ")
        append(pageNumber)
        append("\n")
        append("mode: ")
        append(mode)
        append("\n")
        append("activeRecord :")
        append(activeRecord)
        append("; activeField ")
        if (activeField == null) {
          append(": null")
        } else {
          append("\n")
          append(activeField!!.toString())
        }
        append("\n")
        append("fetchCount: ")
        append(fetchCount)
        append("; recordCount: ")
        append(recordCount)
        append("; fetchPosition: ")
        append(fetchPosition)
        append("\n")
        append("CURRENT RECORD:\n")
        if (fields != null) {
          for (i in fields.indices) {
            if (fields[i] != null) {
              append(fields[i].toString())
            } else {
              append("Field ")
              append(i)
              append(" is null\n")
            }
          }
        } else {
          append("No information about fields available.\n")
        }
      } catch (e: Exception) {
        append("Exception while retrieving bock information. \n")
      }
    }
  }


  /**
   * Returns the sorted records array.
   * @return The sorted records array.
   */
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  lateinit var sortedRecords: IntArray
    protected set

  protected var blockAccess = false

  // prevent that the access of a field is updated
  // (performance in big charts)
  protected var ignoreAccessChange = false

  // max number of buffered records
  var bufferSize = 0

  // max number of buffered IDs
  protected var fetchSize = 0

  // max number of displayed records
  var displaySize = 0

  /** The page number of this block */
  var pageNumber = 0 // page number
  protected lateinit var source: String // qualified name of source file
  lateinit var name: String // block name
  protected lateinit var shortcut: String // block short name
  var title: String = "" // block title
  var alignment: BlockAlignment? = null
  protected var help: String? = null // the help on this block
  internal var tables: Array<Table>? = null // names of database tables
  protected var options = 0 // block options
  protected lateinit var access: IntArray // access flags for each mode
  protected var indices: Array<String>? = null // error messages for violated indices
  internal var commands: Array<VCommand>? = null // commands
  open var actors: Array<VActor>? = null // actors to send to form (move to block import)
    get(): Array<VActor>? {
      val temp: Array<VActor>? = field
      field = null
      return temp
    }

  lateinit var fields: Array<VField> // fields
  protected var VKT_Triggers = mutableListOf(IntArray(VConstants.TRG_TYPES.size))
  protected val triggers = mutableMapOf<Int, Trigger>()

  // dynamic data
  var activeRecord = 0 // current record
    get() {
      return if (field in 0 until bufferSize) field else -1
    }
    set(rec) {
      assert(isMulti() || rec == 0) { "multi? " + isMulti() + "rec: " + rec }
      field = rec
    }

  var activeField: VField? = null
  var detailMode = false
    set(mode: Boolean) {
      if (mode != field) {
        // remember field to enter it in the next view
        val vField = activeField
        fireViewModeLeaved(this, vField)
        field = mode
        fireViewModeEntered(this, vField)
      }
    }

  // number of active records
  val recordCount
    get(): Int {
      var count = 0
      if (isMulti()) {
        for (i in 0 until bufferSize) {
          if (isRecordFetched(i) || isRecordChanged(i)) {
            count++
          }
        }
      } else {
        for (i in 0 until fetchCount) {
          if (fetchBuffer[i] != -1) {
            count++
          }
        }
      }
      return count
    }

  protected lateinit var activeCommands: ArrayList<VCommand> // commands currently active
  private var _currentRecord = 0
  var currentRecord
    get(): Int {
      return if (!isMulti()) {
        0
      } else {
        assert(_currentRecord in 0 until bufferSize) { "Bad currentRecord $_currentRecord" }
        _currentRecord
      }
    }
    set(rec) {
      if (isMulti()) {
        _currentRecord = rec
      }
    }

  fun getMode(): Int = mode

  fun setMode(mode: Int) {
    if (this !== form.getActiveBlock()) {
      this.mode = mode
      for (i in fields.indices) {
        fields[i].updateModeAccess()
      }
    } else {
      // is this restriction acceptable ?
      assert(!isMulti()) { "Block $name is a multiblock." }
      val act = activeField
      act?.leave(true)
      this.mode = mode
      for (i in fields.indices) {
        fields[i].updateModeAccess()
      }
      if (act != null && !act.hasAction() && act.getAccess(activeRecord) >= VConstants.ACS_VISIT) {
        act.enter()
      }
    }
  }

  // current mode
  private var mode = 0
  protected lateinit var recordInfo: IntArray // status vector for records
  protected lateinit var fetchBuffer: IntArray // holds Id's of fetched records
  protected var fetchCount = 0 // # of fetched records
  protected var fetchPosition = 0 // position of current record
  protected var blockListener = EventListenerList()
  internal var orderModel = OrderModel()
  var border = 0
  var maxRowPos = 0
  var maxColumnPos = 0
  var displayedFields = 0
  private var isFilterVisible = false
  protected var dropListMap = HashMap<Any?, Any?>()

  companion object {
    // record info flags
    protected const val RCI_FETCHED = 0x00000001
    protected const val RCI_CHANGED = 0x00000002
    protected const val RCI_DELETED = 0x00000004
    protected const val RCI_TRAILED = 0x00000008

    //Inner class Order Model constants
    const val STE_UNORDERED = 1
    const val STE_INC = 2
    const val STE_DESC = 4
  }
}
