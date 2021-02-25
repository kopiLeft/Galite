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

import org.kopi.galite.base.UComponent
import org.kopi.galite.form.Alignment
import org.kopi.galite.form.UBlock
import org.kopi.galite.form.UForm
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VField
import org.kopi.galite.form.VFieldUI
import org.kopi.galite.ui.vaadin.block.Block
import org.kopi.galite.ui.vaadin.block.BlockLayout
import org.kopi.galite.ui.vaadin.block.SimpleBlockLayout
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VExecFailedException

import com.vaadin.flow.component.Component

/**
 * The `DBlock` is the vaadin implementation
 * of the [UBlock] specifications.
 *
 * @param parent The parent form.
 * @param model The block model.
 */
open class DBlock(val parent: DForm, override val model: VBlock) : Block(model.isDroppable), UBlock {

  protected var formView: DForm = parent
  protected lateinit var columnViews: Array<VFieldUI?>

  // protected Layout  		layout;
  protected var maxRowPos: Int = model.maxRowPos
  protected var maxColumnPos: Int = model.maxColumnPos
  protected var displayedFields: Int = model.displayedFields

  // cached infos
  protected var sortedToprec = 0 // first record displayed
  private var sortedRecToDisplay: IntArray
  private var displayToSortedRec: IntArray

  init {
    maxRowPos = model.maxRowPos
    maxColumnPos = model.maxColumnPos
    displayedFields = model.displayedFields
    formView = parent
    setBorder(model.border, model.title)
    model.addBlockListener(this)
    bufferSize = model.bufferSize
    displaySize = model.displaySize
    setSortedRecords(model.sortedRecords)
    setNoMove(model.noMove())
    noChart = model.noChart()

    if (model.isMulti()) {
      sortedRecToDisplay = IntArray(model.bufferSize)
      displayToSortedRec = IntArray(model.displaySize)
    } else {
      sortedRecToDisplay = IntArray(1)
      displayToSortedRec = IntArray(1)
    }

    rebuildCachedInfos()
    createFields()

    if (model.isDroppable) {
      TODO()
      //setDropHandler(DBlockDropHandler(model))
      //setDragStartMode(DragStartMode.HTML5)
    }

    // fire record info change event
    // this is needed to notify view side with the record
    // info changes done when the block listener is not yet
    // installed.
    for (i in 0 until model.bufferSize) {
      if (model.getRecordInfoAt(i) !== 0) {
        fireRecordInfoChanged(i, model.getRecordInfoAt(i))
      }
    }
  }

  //------------------------------------------------
  // UTILS
  //------------------------------------------------
  /**
   * Creates block fields
   */
  protected open fun createFields() {
    val fields = model.fields
    var index = 0

    columnViews = arrayOfNulls(fields.size)
    fields.forEachIndexed { i, field ->
      columnViews[i] = createFieldDisplays(index, field)
      if (columnViews[i] != null) {
        index += 1
      }
    }
  }

  /**
   * Goto the next record
   */
  fun gotoNextRecord() {
    model.gotoNextRecord()
  }

  /**
   * Goto the previous record
   */
  fun gotoPrevRecord() {
    model.gotoPrevRecord()
  }

  /**
   * Rebuilds cached information
   */
  private fun rebuildCachedInfos() {
    var cnt = 0
    var i = 0

    // sortedRecToDisplay
    while (i < sortedToprec) {
      sortedRecToDisplay[i] = -1
      i++
    }

    while (cnt < model.displaySize && i < model.bufferSize) {
      // sortedRecToDisplay: view pos not real record number
      sortedRecToDisplay[i] = if (model.isSortedRecordDeleted(i)) -1 else cnt++
      i++
    }

    while (i < model.bufferSize) {
      sortedRecToDisplay[i] = -1
      i++
    }

    // displayToSortedRec
    cnt = sortedToprec
    i = 0
    while (i < model.displaySize) {
      while (cnt < model.bufferSize && model.isSortedRecordDeleted(cnt)) {
        cnt++
      }
      // the last one can be deleted too
      if (cnt < model.bufferSize) {
        displayToSortedRec[i] = cnt++
      }
      i++
    }
  }

  /**
   * Creates the field display representation.
   * @param model The field model.
   * @return The row controller.
   */
  private fun createFieldDisplays(index: Int, model: VField): VFieldUI? {
    return if (!model.isInternal() /*hidden field */) {
      createFieldDisplay(index, model)
    } else {
      null
    }
  }

  protected open fun createFieldDisplay(index: Int, model: VField): VFieldUI {
    return DFieldUI(this, model, index)
  }

  /**
   * Refreshes the block on screen.
   *
   * Arranges displayed lines to make sure that the current record is visible.
   * Redisplays only if forced or if the current record is off-screen.
   * If there is no current record, the first valid record is used
   */
  protected open fun refresh(force: Boolean) {
    var redisplay = false
    val recno: Int // row in view

    if (!model.isMulti()) {
      return
    }
    recno = if (model.activeRecord != -1) {
      model.getSortedPosition(model.activeRecord)
    } else {
      rebuildCachedInfos()
      for (i in columnViews.indices) {
        if (columnViews[i] != null) {
          columnViews[i]!!.scrollTo(sortedToprec)
        }
      }
      return
    }

    if (recno < sortedToprec) {
      // record to be displayed is above screen => redisplay
      sortedToprec = recno

      // scroll some more, if there are some (non deleted) records
      var i = recno - 1
      var scrollMore: Int = model.displaySize / 4
      while (scrollMore > 0 && i > 0) {
        // is there a non deleted record to see?
        if (!model.isSortedRecordDeleted(i)) {
          sortedToprec -= 1
          scrollMore--
        }
        i--
      }
      redisplay = true
    } else {
      var displine = 0
      var i = sortedToprec

      while (i < recno) {
        if (!model.isSortedRecordDeleted(i)) {
          displine += 1
        }
        i += 1
      }
      if (displine < model.displaySize) {
        // record should be visible => redisplay iff requested
        redisplay = force // do nothing
      } else {
        // scroll upwards until record is visible => redisplay
        do {
          if (!model.isSortedRecordDeleted(sortedToprec)) {
            displine -= 1
          }
          sortedToprec += 1
        } while (displine >= model.displaySize)

        // scroll some more, if there are some (non deleted) records
        var i = recno + 1
        var scrollMore: Int = model.displaySize / 4
        while (scrollMore > 0 && i < model.bufferSize) {
          // is there a non deleted record to see?
          if (!model.isSortedRecordDeleted(i)) {
            sortedToprec += 1
            scrollMore--
          }
          i++
        }
        redisplay = true
      }
    }

    rebuildCachedInfos()

    if (redisplay) {
      for (i in columnViews.indices) {
        if (columnViews[i] != null) {
          columnViews[i]!!.scrollTo(sortedToprec)
        }
      }
    }
    // sends the model active record to client side.
    // BackgroundThreadHandler.access(Runnable { fireActiveRecordChanged(model.getActiveRecord()) }) TODO: Do we need BackgroundThreadHandler?
    fireActiveRecordChanged(model.activeRecord)
  }

  override fun fireValueChanged(col: Int, rec: Int, value: String?) {
    super.fireValueChanged(col, rec, value)
  }

  open fun fireColorChanged(
          col: Int,
          rec: Int,
          foreground: String?,
          background: String?,
  ) {
    TODO()
  }

  /**
   * Performs a scroll action.
   * @exception        VException an exception may be raised record.leave()
   */
  fun setScrollPos(value: Int) {
    // Can not be called in event dispatch thread
    // Scrollbar timer is not stop if you click on one of the two buttons
    var value = value
    assert(value < model.bufferSize //getRecordSize
    )
    if (sortedToprec != value) {
      var recno = 0 //temp sortedToprec
      while (value > 0) {
        if (!model.isSortedRecordDeleted(recno)) {
          value--
        }
        recno++
      }
      if (model.activeField != null) {
        var lastVisibleRec = recno
        var nbDisplay: Int = model.displaySize - 1
        val activeRecord: Int = model.activeRecord
        var inside = false // is active record still in the shown rows
        while (nbDisplay > 0) {
          if (!model.isSortedRecordDeleted(lastVisibleRec)) {
            nbDisplay--
          }
          if (activeRecord == model.getDataPosition(lastVisibleRec)) {
            // active record is still in the shown rows
            inside = true
          }
          lastVisibleRec += 1
        }
        sortedToprec = recno
        if (inside) {
          if (model.activeField != null) {
            model.activeField!!.updateText()
          }
          blockChanged()
        } else {
          val nextRec = if (model.getSortedPosition(model.activeRecord) < recno) {
            model.getDataPosition(recno)
          } else {
            model.getDataPosition(lastVisibleRec)
          }
          if (model.noMove() || !model.isRecordAccessible(nextRec)) {
            throw VExecFailedException()
          }
          model.changeActiveRecord(nextRec)
        }
      } else {
        if (model.noMove() || model.isRecordDeleted(model.getDataPosition(recno))) {
          // || !model.isRecordAccessible(model.getDataPosition(recno))) {
          throw VExecFailedException()
        }
        sortedToprec = recno
        blockChanged()
        if (model !== model.form.getActiveBlock()) {  // NOT CURRENT BLOCK -> BACK TO -1
          model.activeRecord = -1
        }
      }
    }
  }

  /**
   * Clears the block content.
   */
  override fun clear() {
    sortedToprec = 0
    refresh(true)
  }

  /**
   * Sets the block border.
   * @param style The border style.
   * @param title The block title.
   */
  private fun setBorder(style: Int, title: String?) {
    if (style != VConstants.BRD_NONE) {
      title?.let { setCaption(it) }
    }
  }

  //---------------------------------------------------
  // UBLOCK IMPLEMENTATION
  //---------------------------------------------------

  override fun getFormView(): UForm {
    return formView
  }

  override fun getDisplayLine(recno: Int): Int {
    if (recno < 0) {
      return -1
    }
    val pos: Int = model.getSortedPosition(recno)
    return if (pos < 0) {
      -1
    } else sortedRecToDisplay[pos]
  }

  override fun getDisplayLine(): Int = getDisplayLine(model.activeRecord)

  override fun getRecordFromDisplayLine(line: Int): Int {
    return model.getDataPosition(displayToSortedRec[line])
  }

  override fun add(comp: UComponent, constraints: Alignment) {
    addComponent(comp as Component,
                 constraints.x,
                 constraints.y,
                 constraints.width,
                 constraints.height,
                 constraints.alignRight,
                 constraints.useAll)
  }

  override fun getColumnPos(x: Int): Int {
    return 0
  }

  override fun inDetailMode(): Boolean {
    return false
  }

  override fun createLayout(): BlockLayout {
    // label + field => fldNumber + lines
    val layout = SimpleBlockLayout(DFieldUI.fldNumber * maxColumnPos, maxRowPos)
    if (model.alignment != null) {
      layout.setBlockAlignment(formView.getBlockView(model.alignment!!.block) as Component,
                               model.alignment!!.targets,
                               model.alignment!!.isChart())
    }

    return layout
  }

  //---------------------------------------------------
  // BLOCKLISTENER IMPLEMENTATION
  //---------------------------------------------------
  override fun blockClosed() {}

  override fun blockChanged() {
    refresh(true)
  }

  override fun blockCleared() {
    clear()
  }

  override fun blockAccessChanged(block: VBlock, newAccess: Boolean) {}

  override fun blockViewModeLeaved(block: VBlock, activeField: VField?) {}

  override fun blockViewModeEntered(block: VBlock, activeField: VField?) {}

  override fun validRecordNumberChanged() {}

  override fun recordInfoChanged(rec: Int, info: Int) {}

  override fun orderChanged() {
    TODO()
  }

  override fun filterHidden() {}

  override fun filterShown() {}

  override fun getCurrentDisplay(): UBlock? = this
}
