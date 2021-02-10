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
import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.form.Page

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H4

/**
 * The component of a simple block.
 * This UI component supports only laying components for simple
 * layout view.
 */
abstract class Block(private val droppable: Boolean) : Div(), HasEnabled {

  /** The block layout. */
  private var layout: BlockLayout? = null
  var caption: H4? = null
  private val listeners = mutableListOf<BlockListener>()
  private var fields = mutableListOf<ColumnView>()

  /**
   * The block buffer size.
   */
  var bufferSize = 0

  /**
   * The block display size.
   */
  var displaySize = 0

  /**
   * No chart option. Sets the block to not display the chart view even if it is a multi block.
   */
  var noChart = false

  /**
   * The block fields values per record.
   */
  private val cachedValues = mutableListOf<CachedValue>()

  init {
    className = Styles.BLOCK
  }

  override fun onAttach(attachEvent: AttachEvent?) {
    setCachedValues()
  }

  /**
   * Sets the block title.
   * @param title The block title.
   */
  override fun setTitle(title: String?) {
    super.setTitle(title)
    // TODO
  }

  /**
   * Sets the sorted records of this block.
   * @param sortedRecords The sorted records.
   */
  open fun setSortedRecords(sortedRecords: IntArray) {
    // TODO
  }

  /**
   * Sets the no move option for the block.
   * @param noMove The no move option.
   */
  fun setNoMove(noMove: Boolean) {
    // TODO
  }

  /**
   * Switches the block view.
   * Switch is only performed when it is a multi block.
   * @param detail Should be switch to the detail view ?
   */
  fun switchView(detail: Boolean) {
    if (layout is MultiBlockLayout) {
      (layout as MultiBlockLayout).switchView(detail)
    }
  }

  /**
   * Updates the layout scroll bar of it exists.
   * @param pageSize The scroll page size.
   * @param maxValue The max scroll value.
   * @param enable is the scroll bar enabled ?
   * @param value The scroll position.
   */
  fun updateScroll(pageSize: Int, maxValue: Int, enable: Boolean, value: Int) {
    // TODO
  }

  /**
   * Sets the animation enabled for block view switch.
   * @param isAnimationEnabled Is animation enabled.
   */
  fun setAnimationEnabled(isAnimationEnabled: Boolean) {
    // TODO
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
  fun addComponent(
          component: Component?,
          x: Int,
          y: Int,
          width: Int,
          height: Int,
          alignRight: Boolean,
          useAll: Boolean,
  ) {
    buildLayout()
    layout!!.addComponent(component, x, y, width, height, alignRight, useAll)
  }

  /**
   * Builds the block layout.
   */
  fun buildLayout() {
    if (layout == null) {
      layout = createLayout()
      if (droppable) {
        //dndWrapper = DragAndDropWrapper(layout) TODO
        //dndWrapper.setImmediate(true)
        //setContent(dndWrapper)
      } else {
        setContent(layout as Component)
      }
    }
  }


  /**
   * Appends the given field to the block field list.
   * @param field The field to be added to block fields.
   */
  open fun addField(field: ColumnView) {
    if (field.index != -1) {
      if (field.index > fields.size) {
        fields.add(field)
      } else {
        fields.add(field.index, field)
      }
    }
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
    // TODO
  }

  /**
   * Fired when the active record is changed from the client side.
   * @param record The new active record.
   * @param sortedTopRec The top sorted record.
   */
  protected fun fireOnActiveRecordChange(record: Int, sortedTopRec: Int) {
    // TODO
  }

  /**
   * Sends the active record to the client side.
   * @param record The new active record.
   */
  protected open fun fireActiveRecordChanged(record: Int) {
    // TODO
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
    // TODO
  }

  /**
   * Notify the client side that the value of the record has changed.
   * @param col The column index.
   * @param rec The record number.
   * @param value The new record value.
   */
  internal open fun fireValueChanged(col: Int, rec: Int, value: String?) {
    val existingValue: CachedValue?

    val cachedValue = CachedValue(col, rec, value)
    existingValue = isAlreadyCached(cachedValue)
    if (existingValue != null) {
      cachedValues.remove(existingValue)
    }
    cachedValues.add(cachedValue)
  }

  /**
   *
   * @param cachedValue : cached value
   * @return the existing cached value if it exists
   */
  protected open fun isAlreadyCached(cachedValue: CachedValue?): CachedValue? {
    for (value in cachedValues) {
      if (value.hasSameKey(cachedValue!!)) {
        return value
      }
    }
    return null
  }

  /**
   * Sets the cached values of the block fields.
   */
  private fun setCachedValues() {
    for (cachedValue in cachedValues) {
      setCachedValue(cachedValue.col, cachedValue.rec, cachedValue.value)
    }
  }

  /**
   * Sets the cached values for the given column.
   * @param column The column view number.
   * @param rec The record number.
   * @param value The column value.
   */
  private fun setCachedValue(column: Int, rec: Int, value: String) {
    val field = fields[column]
    if (field != null) {
      field.setValueAt(rec, value)
      field.updateValue(rec)
    }
  }

  /**
   * Sets the record to be changed.
   * @param rec The record number.
   * @param val The change value.
   */
  open fun setRecordChanged(rec: Int, `val`: Boolean) {
    // TODO
  }


  /**
   * Returns the display line for the given record.
   * @param recno The record number.
   * @return The display line.
   */
  open fun getDisplayLine(recno: Int): Int {
    // TODO
    return 0
  }

  /**
   * Sets the block active record from a given display line.
   * @param displayLine The display line.
   */
  open fun getRecordFromDisplayLine(displayLine: Int): Int {
    // return getDataPosition(displayToSortedRec.get(displayLine)) TODO
    return 0
  }

  /**
   * Sets the block content.
   * @param content The block content.
   */
  protected open fun setContent(content: Component) {
    removeAll()
    add(content)
  }

  /**
   * Sets the block widget layout.
   * @param layout The widget layout.
   */
  protected open fun setLayout(layout: BlockLayout) {
    this.layout = layout
  }

  /**
   * Sets the block caption.
   * @param caption The block caption.
   * @param maxColumnPos The maximum column position.
   */
  protected open fun setCaption(caption: String?) {
    if (caption == null || caption.isEmpty()) {
      return
    }
    val page: Page<*>? = parentPage
    this.caption = H4(caption)
    this.caption!!.className = "block-title"
    page?.setCaption(this)
  }

  /**
   * The parent block page.
   */
  var parentPage: Page<*>? = null

  /**
   * Layout components Creates the content of the block.
   */
  protected open fun layout() {
    // create detail block view.
    layout?.layout()
  }

  open fun clear() {
    super.removeAll()
    if (layout != null) {
      try {
        layout!!.removeAll()
      } catch (e: IndexOutOfBoundsException) {
        // ignore cause it can be cleared before
      }
      layout = null
    }
    caption = null
  }

  //---------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------
  /**
   * Creates the block layout.
   * @return The created block layout.
   */
  abstract fun createLayout(): BlockLayout?
}
