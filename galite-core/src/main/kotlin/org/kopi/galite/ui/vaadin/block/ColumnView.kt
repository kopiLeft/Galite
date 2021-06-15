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
package org.kopi.galite.ui.vaadin.block

import org.kopi.galite.ui.vaadin.base.VConstants
import org.kopi.galite.ui.vaadin.field.Field
import org.kopi.galite.ui.vaadin.field.FieldListener
import org.kopi.galite.ui.vaadin.form.DBlock
import org.kopi.galite.ui.vaadin.label.Label

/**
 * A column view represents the display entity of a field for each record
 * in a block model. This will collect all field connector that represents
 * the same field but in different records.
 */
class ColumnView(val block: DBlock) {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  /** Sets the label associated with this column view. */
  var label: Label? = null

  /** The detail label of this column view. */
  var detailLabel: Label? = null
  private var detailDisplay: Field? = null
  private var displays = arrayOfNulls<Field>(block.displaySize)
  private var values = arrayOfNulls<String>(block.bufferSize)
  private var fgColors = arrayOfNulls<String>(block.bufferSize)
  private var bgColors = arrayOfNulls<String>(block.bufferSize)

  /** The column view index */
  var index: Int = -1
    private set

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the detail display of this column view.
   * @param detailDisplay The detail display field.
   */
  fun setDetailDisplay(detailDisplay: Field) {
    this.detailDisplay = detailDisplay
    this.detailDisplay!!.columnView = this
    if (index == -1) {
      index = detailDisplay.index
    }
  }

  /**
   * Appends the given field to the column view collector.
   * @param field The field to be added.
   */
  fun addField(field: Field) {
    displays[field.position] = field
    field.columnView = this
    index = field.index
  }

  /**
   * Returns the field for the given display line.
   * @param displayLine The display line.
   * @return The field object.
   */
  fun getField(displayLine: Int): Field? {
    return if (displayLine != -1) {
      displays[displayLine]
    } else {
      null
    }
  }

  /**
   * Returns `true` if this column view has a PREFLD trigger.
   * @return `true` if this column view has a PREFLD trigger.
   */
  fun hasPreFieldTrigger(): Boolean {
    if (block.inDetailMode()) {
      if (detailDisplay != null) {
        return detailDisplay!!.hasPreFieldTrigger
      }
    } else {
      if (block.getDisplayLine() != -1) {
        return displays[block.getDisplayLine()]!!.hasPreFieldTrigger
      }
    }
    return false
  }

  /**
   * Returns the access of the column view for the block display line.
   * @return The access of the column view.
   */
  val access: Int
    get() {
      if (block.inDetailMode()) {
        if (detailDisplay != null) {
          return detailDisplay!!.getAccess(block.activeRecord)
        }
      } else {
        if (block.getDisplayLine() != -1) {
          return displays[block.getDisplayLine()]!!.getAccess(block.activeRecord)
        }
      }
      return VConstants.ACS_HIDDEN
    }

  /**
   * Returns `true` if the column view has an ACTION trigger defined.
   * @return `true` if the column view has an ACTION trigger defined.
   */
  fun hasAction(): Boolean {
    if (block.inDetailMode()) {
      if (detailDisplay != null) {
        return detailDisplay!!.hasAction
      }
    } else {
      if (block.getDisplayLine() != -1) {
        return displays[block.getDisplayLine()]!!.hasAction
      }
    }
    return false
  }

  /**
   * Checks if the column view is no chart for the block display line.
   * @return `true` if the field in the display line has no chart option.
   */
  fun noChart(): Boolean {
    return if (block.getDisplayLine() != -1) {
      displays[block.getDisplayLine()]!!.noChart
    } else {
      false
    }
  }

  /**
   * Checks if the column view is no detail for the block display line.
   * @return `true` if the field in the display line has no detail option.
   */
  fun noDetail(): Boolean {
    return if (block.getDisplayLine() != -1) {
      displays[block.getDisplayLine()]!!.noDetail
    } else {
      false
    }
  }

  /**
   * Checks if the navigation from this column view should be delegated to server.
   * @return `true` if the navigation should be delegated to server.
   */
  fun delegateNavigationToServer(): Boolean {
    return true // FIXME: always delegate navigation to server. remove useless code.
    if (block.inDetailMode()) {
      if (detailDisplay != null) {
        return detailDisplay!!.delegateNavigationToServer()
      }
    } else {
      if (block.getDisplayLine() != -1) {
        return displays[block.getDisplayLine()]!!.delegateNavigationToServer()
      }
    }
    return false
  }

  /**
   * Tells the server side that the focused field is the active one
   * in the client side.
   */
  fun transferFocus() {
    if (getFieldListener() != null) {
      getFieldListener()!!.transferFocus()
    }
  }

  /**
   * Returns the field Listener
   */
  fun getFieldListener(): FieldListener? {
      if (block.inDetailMode()) {
        if (detailDisplay != null) {
          return detailDisplay
        }
      } else {
        if (block.getDisplayLine() != -1) {
          return displays[block.getDisplayLine()]
        }
      }
      return null
    }

  /**
   * Leaves the field in the given display line.
   * @param rec The concerned record.
   * @throws CheckTypeException  When type check fails.
   */
  fun leave(rec: Int) {
    if (block.inDetailMode()) {
      if (detailDisplay != null) {
        detailDisplay!!.leave(rec)
      }
    } else {
      if (block.getDisplayLine() != -1) {
        displays[block.getDisplayLine()]!!.leave(rec)
      }
    }
  }

  /**
   * Enters to this column view.
   * This will gain the focus to the display line field in this column view.
   */
  fun enter() {
    if (block.inDetailMode()) {
      if (detailDisplay != null) {
        detailDisplay!!.enter()
      }
    } else {
      if (block.getDisplayLine() != -1) {
        displays[block.getDisplayLine()]!!.enter()
      }
    }
  }

  /**
   * Checks if the current display line is null for this column view.
   * @return `true` when the current display line is null for this column view.
   */
  val isNull: Boolean
    get() = if (block.getDisplayLine() != -1) {
      displays[block.getDisplayLine()]!!.isNull()
    } else {
      true
    }

  /**
   * Cleans the dirty values of this column view.
   */
  fun cleanDirtyValues() {
    if (displays == null) {
      return
    }
    for (field in displays) {
      if (field != null && field.isDirty) {
        field.cleanDirtyValues()
      }
    }
    if (detailDisplay != null && detailDisplay!!.isDirty) {
      detailDisplay!!.cleanDirtyValues()
    }
  }

  /**
   * Returns `true` if the column view has at least one dirty field.
   * @return `true` if the column view has at least one dirty field.
   */
  val isDirty: Boolean
    get() {
      if (displays == null) {
        return false
      }
      for (field in displays) {
        if (field != null && field.isDirty) {
          return true
        }
      }
      return detailDisplay != null && detailDisplay!!.isDirty
    }

  /**
   * Updates the current block display line field value
   */
  fun updateValue() {
    if (block.getDisplayLine() != -1) {
      displays[block.getDisplayLine()]!!.updateValue()
    }
    // update detail display value
    detailDisplay?.updateValue()
  }

  /**
   * Updates the current block display line field color
   */
  protected fun updateColor() {
    if (block.getDisplayLine() != -1) {
      displays[block.getDisplayLine()]!!.updateColor()
    }
    // update detail display color
    detailDisplay?.updateColor()
  }

  /**
   * Updates the current block display line field value
   * @param record The record number.
   */
  fun updateValue(record: Int) {
    val displayLine = block.getDisplayLine(record)
    if (displayLine != -1) {
      displays[displayLine]!!.updateValue()
    }
    // update detail display value
    detailDisplay?.updateValue()
  }

  /**
   * Updates the current block display line field color.
   * @param record The record number.
   */
  fun updateColor(record: Int) {
    val displayLine = block.getDisplayLine(record)
    if (displayLine != -1) {
      displays[displayLine]!!.updateColor()
    }
    // update detail display color
    detailDisplay?.updateColor()
  }

  /**
   * Checks the value of the current display.
   * @param rec The concerned record.
   * @throws CheckTypeException  When type check fails.
   */
  fun checkValue(rec: Int) {
    if (block.inDetailMode()) {
      if (detailDisplay != null) {
        detailDisplay!!.checkValue(rec)
      }
    } else {
      if (block.getDisplayLine() != -1) {
        if (displays[block.getDisplayLine()]!!.isChanged) {
          displays[block.getDisplayLine()]!!.checkValue(rec)
        }
      }
    }
  }

  /**
   * Checks if the current field of this column view has a dirty value.
   * @param rec The current record number.
   */
  fun maybeHasDirtyValues(rec: Int) {
    if (block.inDetailMode()) {
      detailDisplay?.markAsDirty(rec)
    } else {
      if (block.getDisplayLine() != -1) {
        if (displays[block.getDisplayLine()]!!.isChanged) {
          displays[block.getDisplayLine()]!!.markAsDirty(rec)
        }
      }
    }
  }

  /**
   * Sets the value of this column view in the given record number.
   * @param record The record number.
   * @param newValue The record value.
   */
  fun setValueAt(record: Int, newValue: String?) {
    var newValue = newValue
    var oldValue: String?
    oldValue = values[record]
    if (oldValue == null) {
      oldValue = ""
    }
    if (newValue == null) {
      newValue = ""
    }
    if (newValue != oldValue) {
      values[record] = newValue
      setRecordChanged(record, true)
    }
  }

  /**
   * Sets the foreground color of this column view.
   * @param record The record number.
   * @param color The color value.
   */
  fun setForegroundColorAt(record: Int, color: String?) {
    fgColors[record] = color
  }

  /**
   * Sets the background color of this column view.
   * @param record The record number.
   * @param color The color value.
   */
  fun setBackgroundColorAt(record: Int, color: String?) {
    bgColors[record] = color
  }

  /**
   * Returns the value at the given display line.
   * @param displayLine The record number.
   * @return The value at the given display line.
   */
  fun getValueAt(displayLine: Int): String? {
    return values[getRecordFromDisplayLine(displayLine)]
  }

  /**
   * Returns the foreground color at the given display line.
   * @param displayLine The record number.
   * @return The foreground color at the given display line.
   */
  fun getForegroundColorAt(displayLine: Int): String? {
    return fgColors[getRecordFromDisplayLine(displayLine)]
  }

  /**
   * Returns the background color at the given display line.
   * @param displayLine The record number.
   * @return The background color at the given display line.
   */
  fun getBackgroundColorAt(displayLine: Int): String? {
    return bgColors[getRecordFromDisplayLine(displayLine)]
  }

  /**
   * Returns the record for the given display line.
   * @param displayLine The display line.
   * @return The record number.
   */
  fun getRecordFromDisplayLine(displayLine: Int): Int {
    return if (block != null) {
      block.getRecordFromDisplayLine(displayLine)
    } else {
      -1
    }
  }

  /**
   * Returns the column view value at the given record.
   * @param record The record number.
   * @return The column view value.
   */
  fun getRecordValueAt(record: Int): String? {
    return values!![record]
  }

  /**
   * Scrolls to the given to record.
   * @param toprec The record to be scrolled to
   */
  fun scrollTo(toprec: Int) {
    if (displays == null) {
      return
    }
    for (field in displays) {
      if (field != null) {
        field.updateValue()
        field.updateColor()
      }
    }
    if (detailDisplay != null) {
      val record = block.activeRecord
      // is there no active line, show the same content then the first row
      // in the chart
      var dispLine = if (record >= 0) block.getDisplayLine(record) else 0
      // is the active line, is not in the visible part then show the same
      // content then in the first line of the chart
      if (dispLine < 0) {
        dispLine = 0
      }
      detailDisplay!!.position = dispLine
      detailDisplay!!.updateValue()
      detailDisplay!!.updateColor()
    }
  }

  /**
   * Sets this column view as the active field.
   */
  fun setAsActiveField() {
    setAsActiveField(-1)
  }

  /**
   * Sets this column view as the active field.
   * @param rec The record of the active field
   */
  fun setAsActiveField(rec: Int) {
    if (block != null) {
      block.activeField = this
      if (rec != -1) {
        block.activeRecord = rec
      }
    }
  }

  /**
   * Tells the block that this column is not the active field anymore.
   */
  fun unsetAsActiveField() {
    if (block != null) {
      block.activeField = null
    }
  }

  /**
   * Checks if this column view is the active block field.
   * @return `true` if this column view is the block active field.
   */
  val isBlockActiveField: Boolean
    get() = block.activeField == this

  /**
   * Checks if the active field of the block containing this field view is `null`.
   * @return `true` if the active field of the block containing this field view is `null`.
   */
  val isBlockActiveFieldNull: Boolean
    get() = block.activeField == null

  /**
   * Returns the block active record.
   * @return The block active record.
   */
  val blockActiveRecord: Int
    get() = block.activeRecord

  /**
   * Returns the block old active record.
   * @return The block old active record.
   */
  val blockOldActiveRecord: Int
    get() = block.getOldActiveRecord()

  /**
   * Navigates to the next field in container block.
   */
  fun gotoNextField() {
    if (block != null) {
      block.gotoNextField()
    }
  }

  /**
   * Navigates to the previous field in container block.
   */
  fun gotoPrevField() {
    if (block != null) {
      block.gotoPrevField()
    }
  }

  /**
   * Navigates to next empty must fill field in container block.
   */
  fun gotoNextEmptyMustfill() {
    if (block != null) {
      block.gotoNextEmptyMustfill()
    }
  }

  /**
   * Navigates to the next record in container block.
   */
  fun gotoNextRecord() {
    if (block != null) {
      block.gotoNextRecord()
    }
  }

  /**
   * Navigates to the previous record in container block.
   */
  fun gotoPrevRecord() {
    if (block != null) {
      block.gotoPrevRecord()
    }
  }

  /**
   * Navigates to the first record in container block.
   */
  fun gotoFirstRecord() {
    if (block != null) {
      block.gotoFirstRecord()
    }
  }

  /**
   * Navigates to the last record in container block.
   */
  fun gotoLastRecord() {
    if (block != null) {
      block.gotoLastRecord()
    }
  }

  /**
   * Sets the given record to be changed.
   * @param rec The record number.
   * @param val The change value.
   */
  fun setRecordChanged(rec: Int, `val`: Boolean) {
    if (block != null) {
      block.setRecordChanged(rec, `val`)
    }
  }

  /**
   * Sets the given record to be fetched.
   * @param rec The record number.
   * @param val The fetch value.
   */
  fun setRecordFetched(rec: Int, `val`: Boolean) {
    if (block != null) {
      block.setRecordFetched(rec, `val`)
    }
  }

  /**
   * Sets the block active record from a given display line.
   * @param displayLine The display line.
   */
  fun setBlockActiveRecordFromDisplayLine(displayLine: Int) {
    if (block != null) {
      block.setActiveRecordFromDisplay(displayLine)
    }
  }

  /**
   * Returns `true` if we are in multiple block context.
   * @return `true` if we are in multiple block context.
   */
  val isMultiBlock: Boolean
    get() = if (block == null) false else block.isMulti()

  /**
   * Returns `true` if this column view has auto fill feature.
   * @return `true` if this column view has auto fill feature.
   */
  fun hasAutofill(): Boolean {
    var hasAutofill: Boolean
    hasAutofill = false
    if (label != null) {
      hasAutofill = hasAutofill or label!!.hasAction
    }
    if (detailLabel != null) {
      hasAutofill = hasAutofill or detailLabel!!.hasAction
    }
    return hasAutofill
  }

  /**
   * Sets the actors ability of this column view.
   * @param enabled The actors ability.
   */
  fun setActorsEnabled(enabled: Boolean) {
    if (displays == null) {
      return
    }
    for (display in displays) {
      display?.isActionEnabled = enabled
    }
    detailDisplay?.isActionEnabled = enabled
  }

  /**
   * Disables all block column view actors.
   */
  fun disableBlockActors() {
    if (block != null) {
      block.setColumnViewsActorsEnabled(false)
    }
  }

  /**
   * Disables all blocks actors
   */
  fun disableAllBlocksActors() {
    (block.parent.content).disableAllBlocksActors()
  }
}
