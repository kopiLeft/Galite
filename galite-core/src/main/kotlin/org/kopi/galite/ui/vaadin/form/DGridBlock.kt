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
package org.kopi.galite.ui.vaadin.form

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.HeaderRow
import org.kopi.galite.base.UComponent
import org.kopi.galite.form.Alignment
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VField
import org.kopi.galite.form.VFieldUI
import org.kopi.galite.ui.vaadin.block.BlockLayout

/**
 * Grid based chart block implementation.
 */
open class DGridBlock(parent: DForm, model: VBlock) : DBlock(parent, model) {

  // --------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------
  /**
   * Differently create fields for this block
   */
  override fun createFields() {
    TODO()
  }

  /**
   * Notifies the block that the UI is focused on the given record.
   * @param recno The record number
   */
  protected open fun enterRecord(recno: Int) {
    TODO()
  }

  override fun createFieldDisplay(index: Int, model: VField): VFieldUI {
    TODO()
  }

  override fun add(comp: UComponent, constraints: Alignment) {}

  override fun blockAccessChanged(block: VBlock, newAccess: Boolean) {
    TODO()
  }

  override fun createLayout(): BlockLayout {
    TODO()
  }

  override fun refresh(force: Boolean) {
    TODO()
  }

  override fun getDisplayLine(recno: Int): Int {
    return 0
  }

  override fun getRecordFromDisplayLine(line: Int): Int {
    TODO()
  }

  override fun validRecordNumberChanged() {
    TODO()
  }

  override fun filterShown() {
    TODO()
  }

  override fun filterHidden() {
    TODO()
  }

  override fun blockChanged() {
    refresh(true)
  }

  override fun blockCleared() {
    TODO()
  }

  override fun clear() {
    cancelEditor()
    scrollToStart()
  }

  /**
   * Scrolls the to beginning of the block
   */
  internal fun scrollToStart() {
    TODO()
  }

  /**
   * Clears the grid sort order
   */
  protected fun clearSortOrder() {
    TODO()
  }

  /**
   * Cancels the grid editor
   */
  protected fun cancelEditor() {
    TODO()
  }

  override fun fireValueChanged(col: Int, rec: Int, value: String?) {
    // no client side cache
  }

  override fun fireColorChanged(col: Int, rec: Int, foreground: String?, background: String?) {
    // no client side cache
  }

  override fun fireRecordInfoChanged(rec: Int, info: Int) {
    // no client side cache
  }

  override fun setSortedRecords(sortedRecords: IntArray) {
    if (!model.noDetail() && !inDetailMode()) {
      super.setSortedRecords(sortedRecords)
    }
  }

  override fun fireActiveRecordChanged(record: Int) {
    if (!model.noDetail()) {
      super.fireActiveRecordChanged(record)
    }
  }

  override fun orderChanged() {
    TODO()
  }

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  protected var grid: Grid<Any>? = null

  /*
   * We use this to fire a set item change event only when
   * the valid records is changed. It is not necessary to
   * lock the session when nothing is really changed.
   */
  private var lastValidRecords = 0

  // flag used to adapt grid width when a scroll bar is displayed
  // or when the scroll bar is removed.
  private var widthAlreadyAdapted = false

  /*
   * A workaround for a Grid behavior: If two bind request are sent
   * to the grid editor, the confirm bind callback will be done only
   * for the first bind request. This is not the expected result since
   * we expect that the last bind request will be confirmed.
   * Since the session task execution is done synchronously, this item
   * will only take the last requested item to be binded and thus we force
   * the editor to bind the last record. Typically, this is used when multiple
   * gotoRecord are called via the window action queue.
   */
  private var itemToBeEdited: Int? = null

  /*
   * A flag used to force disabling the editor cancel when scroll is fired
   * by the application and not by the user.
   * This flag is set true when application requests to edit a specific item.
   * When the item needs to be scrolled through, the editor should not be cancelled.
   * Whereas, the editor should be cancelled when the edited item is not already in
   * the port view after a scroll fired by the user.
   */
  private var doNotCancelEditor = false
  private var filterRow: HeaderRow? = null
}
