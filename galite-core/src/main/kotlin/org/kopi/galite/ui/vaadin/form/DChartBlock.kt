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
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VConstants
import org.kopi.galite.ui.vaadin.block.BlockLayout
import org.kopi.galite.ui.vaadin.block.BlockListener
import org.kopi.galite.ui.vaadin.block.ChartBlockLayout
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VExecFailedException

import com.vaadin.flow.component.Component

/**
 * The `DChartBlock` is a [DBlock] representing
 * a chart block where data is represented in a data grid.
 *
 * @param parent The parent form.
 * @param model The block model.
 */
open class DChartBlock(parent: DForm, model: VBlock) : DBlock(parent, model), BlockListener {

  //-------------------------------------------------
  // DATA MEMBERS
  //-------------------------------------------------
  private var init = false

  /*
   * This flag was added to avoid mutual communication
   * between client and server side when the scroll bar
   * position is changed from the client side.
   * Thus, scroll bar position is not changed by server
   * side when it is changed from the client side
   * @see {@link #refresh(boolean)
   */
  private var scrolling = false

  init {
    if (model.displaySize < model.bufferSize) {
      addBlockListener(this)
    }
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun add(comp: UComponent?, constraints: Alignment) {
    addComponent(comp as? Component,
                 constraints.x,
                 constraints.y,
                 constraints.width,
                 constraints.height,
                 constraints.alignRight,
                 constraints.useAll)
  }

  override fun createLayout(): BlockLayout {
    val layout = ChartBlockLayout(displayedFields, model.displaySize + 1)

    layout.hasScroll = model.displaySize < model.bufferSize
    return layout
  }

  override fun validRecordNumberChanged() {
    if (model.displaySize < model.bufferSize) {
      updateScrollbar()
    }
  }

  override fun recordInfoChanged(rec: Int, info: Int) {
    // send the new records info
    fireRecordInfoChanged(rec, info)
    model.updateColor(rec)
  }

  override fun refresh(force: Boolean) {
    super.refresh(force)
    // don't update scroll bar when the scroll position
    // is being changing from the client side.
    if (!scrolling) {
      updateScrollbar()
    }
  }

  override fun onScroll(value: Int) {
    if ((getFormView() as DForm).inAction) {
      // do not change the rows if there is currently a
      // another command executed
      return
    }
    if (!init) {
      init = true // on initialization, we do not scroll.
    } else {
      try {
        scrolling = true
        setScrollPos(value)
        scrolling = false
      } catch (e: VException) {
        e.printStackTrace()
      }
    }
  }

  override fun onActiveRecordChange(record: Int, sortedTopRec: Int) {
    if (record != model.activeRecord) {
      model.form.performAsyncAction(object : Action() {
        override fun execute() {
          // go to the correct block if necessary
          if (model !== model.form.getActiveBlock()) {
            if (!model.isAccessible) {
              throw VExecFailedException(MessageCode.getMessage("VIS-00025"))
            }
            model.form.gotoBlock(model)
          }
          if (fireTriggers()) {
            sortedToprec = sortedTopRec
            model.gotoRecord(record)
          } else {
            model.activeRecord = record
            model.currentRecord = record
            sortedToprec = sortedTopRec
            refresh(false)
          }
        }

        /**
         * Returns `true` if there some triggers that should be fired
         * when changing the active record of the block model.
         * @return `true` if there some triggers that should be fired.
         * @throws VException Visual errors.
         */
        private fun fireTriggers(): Boolean {
          return isActiveBlock && (fireTriggerOnBlock() || fireTriggersOnActiveField())
        }

        /**
         * Returns `true` if this block is the active block in the form.
         * @return `true` if this block is the active block in the form.
         * @throws VException Visual errors.
         */
        private val isActiveBlock: Boolean
          get() = (model.form.getActiveBlock() != null
                  && model.form.getActiveBlock() == model)

        /**
         * Returns `true` is there are trigger that should be fired on this block.
         * @return `true` is there are trigger that should be fired on this block.
         * @throws VException Visual errors.
         */
        private fun fireTriggerOnBlock(): Boolean {
          return (model.hasTrigger(VConstants.TRG_PREREC)
                  || model.hasTrigger(VConstants.TRG_VALREC))
        }

        /**
         * Returns `true` is there are trigger that should be fired on the active field.
         * @return `true` is there are trigger that should be fired on the active field.
         * @throws VException Visual errors.
         */
        private fun fireTriggersOnActiveField(): Boolean {
          return if (model.activeField != null) {
            (model.activeField!!.hasTrigger(VConstants.TRG_POSTCHG)
                    || model.activeField!!.hasTrigger(VConstants.TRG_VALFLD)
                    || model.activeField!!.hasTrigger(VConstants.TRG_FORMAT)
                    || model.activeField!!.hasTrigger(VConstants.TRG_PREVAL)
                    || model.activeField!!.hasTrigger(VConstants.TRG_POSTFLD))
          } else {
            false
          }
        }
      })
    }
  }

  /**
   * Updates the scroll bar position.
   */
  override fun updateScrollbar() {
    //BackgroundThreadHandler.access(Runnable { TODO
      val validRecords = model.numberOfValidRecord
      val dispSize = model.displaySize

      updateScroll(dispSize,
                   validRecords,
                   validRecords > dispSize,
                   model.getNumberOfValidRecordBefore(getRecordFromDisplayLine(0)))
    //})
  }
}
