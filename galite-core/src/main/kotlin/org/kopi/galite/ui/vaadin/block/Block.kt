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

import org.kopi.galite.form.BlockListener

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasEnabled

/**
 * The server side component of a simple block.
 * This UI component supports only laying components for simple
 * layout view.
 *
 * TODO: implement Block with appropriate component
 */
abstract class Block(private val droppable: Boolean) : Component(), HasEnabled {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Sets the block title.
   * @param title The block title.
   */
  fun setTitle(title: String?) {
    TODO()
  }

  /**
   * Sets the buffer size of this block.
   * @param bufferSize The block buffer size.
   */
  fun setBufferSize(bufferSize: Int) {
    TODO()
  }

  /**
   * Sets the block display size.
   * @param displaySize The display size.
   */
  fun setDisplaySize(displaySize: Int) {
    TODO()
  }

  /**
   * Sets the sorted records of this block.
   * @param sortedRecords The sorted records.
   */
  open fun setSortedRecords(sortedRecords: IntArray) {
    TODO()
  }

  /**
   * Sets the no move option for the block.
   * @param noMove The no move option.
   */
  fun setNoMove(noMove: Boolean) {
    TODO()
  }

  /**
   * Sets the block to not display the chart view even if it is a multi block.
   * @param noChart The no chart ability.
   */
  fun setNoChart(noChart: Boolean) {
    TODO()
  }

  /**
   * Switches the block view.
   * @param detail Should be switch to the detail view ?
   */
  fun switchView(detail: Boolean) {
    TODO()
  }

  /**
   * Updates the layout scroll bar of it exists.
   * @param pageSize The scroll page size.
   * @param maxValue The max scroll value.
   * @param enable is the scroll bar enabled ?
   * @param value The scroll position.
   */
  fun updateScroll(pageSize: Int, maxValue: Int, enable: Boolean, value: Int) {
    TODO()
  }

  /**
   * Sets the animation enabled for block view switch.
   * @param isAnimationEnabled Is animation enabled.
   */
  fun setAnimationEnabled(isAnimationEnabled: Boolean) {
    TODO()
  }

  /**
   * Adds a component to this block.
   * @param component The component to be added.
   * @param x the x position.
   * @param y The y position.
   * @param width the column span width.
   * @param alignRight Is it right aligned ?
   * @param useAll Use all available area ?
   */
  fun addComponent(component: Component?,
                   x: Int,
                   y: Int,
                   width: Int,
                   height: Int,
                   alignRight: Boolean,
                   useAll: Boolean) {
    layout!!.addComponent(component, x, y, width, height, alignRight, useAll)
  }

  /**
   * Registers a new block listener.
   * @param l The listener to be registered.
   */
  fun addBlockListener(l: BlockListener) {
    listeners.add(l)
  }

  /**
   * Removes a registered block listener.
   * @param l The listener to be removed.
   */
  fun removeBlockListener(l: BlockListener) {
    listeners.add(l)
  }

  /**
   * Fired when the scroll position has changed.
   * @param value The new scroll position.
   */
  protected fun fireOnScroll(value: Int) {
    TODO()
  }

  /**
   * Fired when the active record is changed from the client side.
   * @param record The new active record.
   * @param sortedTopRec The top sorted record.
   */
  protected fun fireOnActiveRecordChange(record: Int, sortedTopRec: Int) {
    TODO()
  }

  /**
   * Sends the active record to the client side.
   * @param record The new active record.
   */
  protected open fun fireActiveRecordChanged(record: Int) {
    TODO()
  }

  /**
   * Send the new records order to the client side.
   * @param sortedRecords The new record orders.
   */
  protected fun fireOrderChanged(sortedRecords: IntArray) {
    setSortedRecords(sortedRecords)
  }

  /**
   * Sends the new records info to client side.
   * @param rec The record number.
   * @param info The record info value.
   */
  protected open fun fireRecordInfoChanged(rec: Int, info: Int) {
    TODO()
  }

  /**
   * Notify the client side that the value of the record has changed.
   * @param col The column index.
   * @param rec The record number.
   * @param value The new record value.
   */
  internal open fun fireValueChanged(col: Int, rec: Int, value: String?) {
    TODO()
  }

  //---------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------
  /**
   * Creates the block layout.
   * @return The created block layout.
   */
  abstract fun createLayout(): BlockLayout?

  /**
   * Returns the block layout.
   * @return the block layout.
   */
  var layout: BlockLayout? = null
    get() {
      TODO()
    }
    private set
  private val listeners: MutableList<BlockListener> = ArrayList<BlockListener>()
}
