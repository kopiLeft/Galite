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
package org.kopi.galite.visual.ui.vaadin.form

import java.time.LocalDate

import org.kopi.galite.visual.base.UComponent
import org.kopi.galite.visual.form.Alignment
import org.kopi.galite.visual.form.UBlock
import org.kopi.galite.visual.form.UForm
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.form.VFieldUI
import org.kopi.galite.visual.ui.vaadin.block.Block
import org.kopi.galite.visual.ui.vaadin.block.BlockLayout
import org.kopi.galite.visual.ui.vaadin.block.SimpleBlockLayout
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VExecFailedException

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.Shortcuts
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.dependency.CssImport

/**
 * The `DBlock` is the vaadin implementation
 * of the [UBlock] specifications.
 *
 * @param parent The parent form.
 * @param model The block model.
 */

@CssImport("./styles/galite/block.css")
open class DBlock(val parent: DForm,
                  final override val model: VBlock)
  : Block(if (model.isDroppable) DBlockDropHandler(model) else null, parent.content), UBlock {

  protected val formView = parent
  protected lateinit var columnViews: Array<VFieldUI?>
  protected val maxRowPos: Int = model.maxRowPos
  protected val maxColumnPos: Int = model.maxColumnPos
  protected val displayedFields: Int = model.displayedFields
  protected val sortedRecToDisplay: IntArray
  protected val displayToSortedRec: IntArray
  protected var sortedToprec = 0 // first record displayed
  protected var currentUI: UI? = null

  /**
   * Some browsers fires extra scroll event with wrong scroll position
   * when a chart block field is clicked. This flag is used to prevent
   * these events from propagation to the block UI and thus block view refresh
   * with wrong top scroll record.
   */
  private var activeRecordSetFromDisplay = false

  init {
    model.addBlockListener(this)
    addRecordPositionPanel(parent.content)

    if (model.isMulti()) {
      sortedRecToDisplay = IntArray(model.bufferSize)
      displayToSortedRec = IntArray(model.displaySize)
    } else {
      sortedRecToDisplay = IntArray(1)
      displayToSortedRec = IntArray(1)
    }

    rebuildCachedInfos()
    createFields()

    // fire record info change event
    // this is needed to notify view side with the record
    // info changes done when the block listener is not yet
    // installed.
    for (i in 0 until model.bufferSize) {
      if (model.getRecordInfoAt(i) != 0) {
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
    val fields = model.blockFields
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
   * Rebuilds cached information
   */
  fun rebuildCachedInfos() {
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
   * Enters to the given record.
   * @param recno The new record.
   */
  protected open fun enterRecord(recno: Int) {
    refresh(true)
  }

  /**
   * Refreshes the block on screen.
   *
   * Arranges displayed lines to make sure that the current record is visible.
   * Redisplays only if forced or if the current record is off-screen.
   * If there is no current record, the first valid record is used
   */
  open fun refresh(force: Boolean) {
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
        // record should be visible => redisplay if requested
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
  }

  fun onValueChange(event: HasValue.ValueChangeEvent<Int>) {
    // when active record is set from the display. It means that
    // one block field has fired a click event. Extra scroll event
    // are blocked cause some FF version fire scroll events with
    // wrong scroll position and force UI to refresh and change its
    // content.
    if (activeRecordSetFromDisplay) {
      activeRecordSetFromDisplay = false
    } else {
      setScrollPos(event.value)
    }
  }

  fun addRecordPositionPanel(form: Form) {
    if (model.isMulti() && model.noChart()) {
      Shortcuts.addShortcutListener(
        this,
        { _ -> form.showBlockInfo() },
        Key.KEY_I,
        KeyModifier.of("Alt")
      )
    }
  }

  /**
   * Sets the block active record from a given display line.
   * @param displayLine The display line.
   */
  open fun setActiveRecordFromDisplay(displayLine: Int) {
    activeRecordSetFromDisplay = true
    fireActiveRecordChanged()
    refresh(false)
  }

  /**
   * Notifies the form that the active record of this block has changed.
   */
  protected open fun fireActiveRecordChanged() {
    form.setCurrentPosition(getSortedPosition(getCurrentRecord() - 1) + 1, model.recordCount)
  }

  /**
   * Returns the sorted position of the given record.
   * @param rec The concerned record.
   * @return The sorted position of the record.
   */
  private fun getSortedPosition(rec: Int): Int = model.getSortedPosition(rec)

  /**
   * Returns the current record.
   * @return The current record.
   */
  protected open fun getCurrentRecord(): Int {
    var current = 1
    if (model.isMulti()) {
      current = model.activeRecord + 1
    }
    return current
  }

  override fun onAttach(attachEvent: AttachEvent) {
    currentUI = attachEvent.ui
  }

  open fun fireColorChanged(
          col: Int,
          rec: Int,
          foreground: String?,
          background: String?,
  ) {
    // no client side cache
  }

  /**
   * Performs a scroll action.
   * @exception        VException an exception may be raised record.leave()
   */
  fun setScrollPos(value: Int) {
    // Can not be called in event dispatch thread
    // Scrollbar timer is not stop if you click on one of the two buttons
    var value = value
    assert(value < model.bufferSize) //getRecordSize
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
  internal fun setBorder(style: Int, title: String?, page: Page<*>?) {
    if (style != VConstants.BRD_NONE) {
      title?.let { setCaption(it, page) }
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
    } else {
      sortedRecToDisplay[pos]
    }
  }

  override fun getDisplayLine(): Int = getDisplayLine(model.activeRecord)

  override fun getRecordFromDisplayLine(line: Int): Int {
    return model.getDataPosition(displayToSortedRec[line])
  }

  override fun add(comp: UComponent?, constraints: Alignment) {
    addComponent(comp as? Component,
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
    // label + field => 2 + lines
    val layout = SimpleBlockLayout(2 * maxColumnPos, maxRowPos, this)
    if (model.alignment != null) {
      layout.setBlockAlignment(formView.getBlockView(model.alignment!!.block) as Component,
                               model.name,
                               model.alignment!!.targets,
                               model.alignment!!.isChart())
    }

    return layout
  }

  fun release() {
    columnViews.forEach {
      it?.model?.removeFieldListener(it.fieldHandler)
      it?.model?.removeFieldChangeListener(it.fieldHandler)
    }
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
    refresh(true)
  }

  override fun filterHidden() {}

  override fun filterShown() {}

  override fun refreshEntries() {}
  override fun getSelectedDate(): LocalDate? = null
  override fun goToDate(date: LocalDate) {}
  override fun enter() {}

  override fun getCurrentDisplay(): UBlock? = this
}
