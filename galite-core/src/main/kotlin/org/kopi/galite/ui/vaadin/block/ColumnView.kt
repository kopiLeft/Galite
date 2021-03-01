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

import org.kopi.galite.ui.vaadin.field.Field
import org.kopi.galite.ui.vaadin.label.Label

/**
 * A column view represents the display entity of a field for each record
 * in a block model. This will collect all field connector that represents
 * the same field but in different records.
 */
class ColumnView(val block: Block) {

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
   * Navigates to the next field in container block.
   */
  fun gotoNextField() {
    if (block != null) {
      // block.gotoNextField() TODO
    }
  }

  /**
   * Navigates to the previous field in container block.
   */
  fun gotoPrevField() {
    if (block != null) {
      // block.gotoPrevField() TODO
    }
  }

  /**
   * Navigates to next empty must fill field in container block.
   */
  fun gotoNextEmptyMustfill() {
    if (block != null) {
      // block.gotoNextEmptyMustfill() TODO
    }
  }

  /**
   * Navigates to the next record in container block.
   */
  fun gotoNextRecord() {
    if (block != null) {
      // block.gotoNextRecord() TODO
    }
  }

  /**
   * Navigates to the previous record in container block.
   */
  fun gotoPrevRecord() {
    if (block != null) {
      // block.gotoPrevRecord() TODO
    }
  }

  /**
   * Navigates to the first record in container block.
   */
  fun gotoFirstRecord() {
    if (block != null) {
      // block.gotoFirstRecord() TODO
    }
  }

  /**
   * Navigates to the last record in container block.
   */
  fun gotoLastRecord() {
    if (block != null) {
      // block.gotoLastRecord() TODO
    }
  }

  /**
   * Sets the given record to be changed.
   * @param rec The record number.
   * @param val The change value.
   */
  fun setRecordChanged(rec: Int, `val`: Boolean) {
    block.setRecordChanged(rec, `val`)
  }

  /**
   * Updates the current block display line field value
   * @param record The record number.
   */
  fun updateValue(record: Int) {
    val displayLine: Int = block.getDisplayLine(record)

    if (displayLine != -1) {
      displays[displayLine]!!.updateValue()
    }
    // update detail display value
    // update detail display value
    if (detailDisplay != null) {
      detailDisplay!!.updateValue()
    }
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
   * Returns the record for the given display line.
   * @param displayLine The display line.
   * @return The record number.
   */
  fun getRecordFromDisplayLine(displayLine: Int): Int {
    return block.getRecordFromDisplayLine(displayLine)
  }
}
