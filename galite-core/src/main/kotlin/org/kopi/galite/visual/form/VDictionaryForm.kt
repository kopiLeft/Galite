/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.form

import org.kopi.galite.visual.Message
import org.kopi.galite.visual.MessageCode

import org.kopi.galite.visual.VExecFailedException
import org.kopi.galite.visual.VRuntimeException
import org.kopi.galite.visual.chart.VChart
import org.kopi.galite.visual.database.transaction
import org.kopi.galite.visual.form.VConstants.Companion.MOD_UPDATE
import org.kopi.galite.visual.fullcalendar.VFullCalendarBlock
import org.kopi.galite.visual.pivottable.VPivotTable
import org.kopi.galite.visual.report.VNoRowException
import org.kopi.galite.visual.report.VReport

abstract class VDictionaryForm protected constructor(source: String? = null) : VForm(source), VDictionary {

  // ----------------------------------------------------------------------
  // QUERY SEARCH
  // ----------------------------------------------------------------------

  private var newRecord = false
  private var lookup = false
  private var closeOnSave = false
  private var savedData: ArrayList<Any?>? = null
  private var savedState: ArrayList<Int>? = null
  private var block: VBlock? = null
  /**
   * The id of selected or new record
   */
  var iD = -1
    private set

  private var editID = -1

  var isRecursiveQuery = false
    private set

  var isMenuQuery = false

  /**
   * This is a modal call. Used in eg. PersonKey.k in some packages
   *
   * @exception        org.kopi.galite.visual.VException        an exception may be raised by triggers
   */
  fun editWithID(id: Int): Int {
    editID = id
    doModal()
    newRecord = false
    editID = -1
    return iD
  }

  /**
   * This is a modal call. Used in eg. PersonKey.k in some packages
   *
   * @exception        org.kopi.galite.visual.VException        an exception may be raised by triggers
   */
  fun openForQuery(): Int {
    lookup = true
    doModal()
    lookup = false
    return iD
  }

  /**
   * create a new record and returns id
   * @exception        org.kopi.galite.visual.VException        an exception may be raised by triggers
   */
  fun newRecord(): Int {
    newRecord = true
    doModal()
    newRecord = false
    return iD
  }

  override fun prepareForm() {
    block = getBlock(0)
    assert(!block!!.isMulti() || block is VFullCalendarBlock) { threadInfo() }

    if (newRecord) {
      if (getBlock(0) == null) {
        gotoBlock(block!!)
      }
      Commands.insertMode(block!!)
    } else if (editID != -1) {
      newRecord = true
      fetchBlockRecord(0, editID)
      getBlock(0).setMode(MOD_UPDATE)
    }
    super.prepareForm()
  }

  /**
   * close the form
   */
  override fun close(code: Int) {
    assert(!getBlock(0).isMulti()) { threadInfo() }
    val id = getBlock(0).getFieldID()
    iD = if (id != null) {
      val i = id.getInt(0)
      i ?: -1
    } else {
      -1
    }
    super.close(code)
  }

  // ----------------------------------------------------------------------
  // VDICTIONARY IMPLEMENTATION
  // ----------------------------------------------------------------------
  override fun search(): Int {
    return openForQuery()
  }

  override fun edit(id: Int): Int {
    return editWithID(id)
  }

  override fun add(): Int {
    return newRecord()
  }

  fun saveFilledField() {
    isRecursiveQuery = true
    savedData = arrayListOf()
    savedState = arrayListOf()
    val fields = block!!.fields
    fields.forEach { field ->
      savedData!!.add(field.getObject(0))
    }
    fields.forEach { field ->
      savedState!!.add(field.getSearchOperator())
    }
  }

  private fun retrieveFilledField() {
    isRecursiveQuery = false
    super.reset()
    val fields = block!!.fields
    for (i in fields.indices) {
      fields[i].setObject(0, savedData!!.elementAt(i))
    }
    block!!.setRecordChanged(0, false)
    for (i in fields.indices) {
      fields[i].setSearchOperator(savedState!!.elementAt(i).toInt())
    }
  }

  /**
   *
   * @exception        org.kopi.galite.visual.VException        an exception may be raised by triggers
   */
  override fun reset() {
    if (isRecursiveQuery) {
      retrieveFilledField()
    } else {
      super.reset()
    }
    isRecursiveQuery = false
  }

  /**
   *
   */
  fun interruptRecursiveQuery() {
    isRecursiveQuery = false
  }

  fun isNewRecord(): Boolean {
    return newRecord || lookup || closeOnSave
  }

  fun setCloseOnSave() {
    closeOnSave = true
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  private fun fetchBlockRecord(block: Int, record: Int) {
    try {
      transaction {
        getBlock(block).fetchRecord(record)
      }
    } catch (e: Throwable) {
      if (e is VSkipRecordException) {
        throw VExecFailedException()
      }
      throw VRuntimeException(e)
    }
  }

  /**
   * Implements interface for COMMAND CreateReport
   */
  fun createReport(b: VBlock, reportBuilder: () -> VReport) {
    b.validate()
    try {
      setWaitInfo(Message.getMessage("report_generation"))
      val report = reportBuilder()
      report.doNotModal()
      unsetWaitInfo()
    } catch (e: VNoRowException) {
      unsetWaitInfo()
      error(MessageCode.getMessage("VIS-00057"))
    }
    b.setRecordChanged(0, false)
  }

  /**
   * Implements interface for COMMAND CreateChart
   */
  fun createChart(b: VBlock, chartBuilder: () -> VChart) {
    b.validate()
    try {
      setWaitInfo(Message.getMessage("chart_generation"))
      val chart = chartBuilder()
      chart.doNotModal()
      unsetWaitInfo()
    } catch (e: VNoRowException) {
      unsetWaitInfo()
      error(MessageCode.getMessage("VIS-00057"))
    }
    b.setRecordChanged(0, false)
  }

  /**
   * Implements interface for COMMAND CreatePivotTable
   */
  fun createPivotTable(b: VBlock, pivotTableBuilder: () -> VPivotTable) {
    b.validate()
    try {
      setWaitInfo(Message.getMessage("pivotTable_generation"))
      val pivotTable = pivotTableBuilder()
      pivotTable.doNotModal()
      unsetWaitInfo()
    } catch (e: VNoRowException) {
      unsetWaitInfo()
      error(MessageCode.getMessage("VIS-00057"))
    }
    b.setRecordChanged(0, false)
  }

  companion object {
    /**
     * static call to createReport.
     */
    fun createReport(report: VReport, b: VBlock) {
      b.validate()
      try {
        report.setWaitInfo(Message.getMessage("report_generation"))
        report.doNotModal()
        report.unsetWaitInfo()
      } catch (e: VNoRowException) {
        report.unsetWaitInfo()
        report.error(MessageCode.getMessage("VIS-00057"))
      }
      b.setRecordChanged(0, false)
    }
  }
}
