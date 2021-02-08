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
import org.kopi.galite.form.UBlock
import org.kopi.galite.form.UField
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VField
import org.kopi.galite.form.VFieldUI
import org.kopi.galite.form.VForm
import org.kopi.galite.ui.vaadin.field.Field
import org.kopi.galite.ui.vaadin.field.FieldListener
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.VColor

/**
 * The `DField` is the vaadin [UField] implementation.
 *
 * @param model The field model.
 * @param label The field label.
 * @param align The field alignment.
 * @param options The field options.
 * @param detail Is it a detail view ?
 */
abstract class DField(internal val model: VFieldUI,
                      internal var label: DLabel?,
                      internal var align: Int,
                      internal var options: Int,
                      private var inDetail: Boolean)
  : Field(model.getIncrementCommand() != null,
          model.getDecrementCommand() != null), UField, FieldListener {

  //----------------------------------------------------------------------
  // UI MANAGEMENT
  // ----------------------------------------------------------------------
  /**
   * Enters this field.
   * @param refresh Should we refresh GUI ?
   */
  open fun enter(refresh: Boolean) {
    updateFocus()
  }

  /**
   * Leaves this field.
   */
  open fun leave() {
    updateFocus()
  }

  //-------------------------------------------------
  // ACCESSORS
  //-------------------------------------------------
  /**
   * Field cell renderer
   */
  override fun setPosition(pos: Int) {
    // TODO()
  }

  /**
   * Field cell renderer
   * @return the position in chart (0..nbDisplay)
   */
  override fun getPosition(): Int {
    return pos
  }

  /**
   * Returns the alignment.
   * @return the alignment.
   */
  open fun getAlign(): Int? {
    return align
  }

  override fun getModel(): VField {
    return model.model
  }

  override fun setInDetail(detail: Boolean) {
    inDetail = detail
    label!!.isInDetail = detail
  }

  /**
   * Returns `true` is the field belongs to the detail view.
   * @return `true` is the field belongs to the detail view.
   */
  open fun isInDetail(): Boolean {
    return inDetail
  }

  override fun getAutofillButton(): UComponent? {
    return null
  }

  /**
   * Returns the row controller.
   * @return The row controller.
   */
  open fun getRowController(): VFieldUI {
    return model
  }

  //-------------------------------------------------
  // UTILS
  //-------------------------------------------------
  /**
   * This method is called after an action of the user, object should
   * be redisplayed accordingly to changes.
   */
  open fun update() {
    // overridden in subclasses
  }

  override fun updateText() {
    // overridden in subclasses
  }

  override fun updateFocus() {
    if (modelHasFocus()) {
      val form: VForm = getModel().getForm()
      form.setInformationText(getModel().toolTip)
      form.setFieldSearchOperator(getModel().getSearchOperator())
    }
  }

  override fun forceFocus() {
    // to be implemented by subclasses
  }

  override fun updateAccess() {
    TODO()
  }

  //-------------------------------------------------
  // ABSTRACT METHODS
  //-------------------------------------------------
  /**
   * Returns the object associed to record r
   *
   * @param        r the position of the record
   * @return        the displayed value at this position
   */
  abstract override fun getObject(): Any?

  /**
   * set blink state
   * @param blink The blink ability.
   */
  abstract override fun setBlink(blink: Boolean)

  //-------------------------------------------------
  // PROTECTED UTILS
  //-------------------------------------------------
  /**
   * Returns `true` if the field model is focused.
   * @return `true` if the field model is focused.
   */
  protected fun modelHasFocus(): Boolean {
    if (getModel() == null) {
      return false
    }
    val block = getModel().block
    return getModel().hasFocus() && block!!.activeRecord == getBlockView().getRecordFromDisplayLine(pos)
  }

  /**
   * Returns `true` if the field is in skipped mode.
   * @return `true` if the field is in skipped mode.
   */
  protected fun isSkipped(): Boolean {
    val block = getModel().block
    return (getAccess() == VConstants.ACS_SKIPPED
            || !block!!.isRecordAccessible(getBlockView().getRecordFromDisplayLine(getPosition())))
  }

  override fun getAccess(): Int {
    return getAccessAt(getPosition())
  }

  /**
   * Returns the field access at a given line.
   * @param at The desired line.
   * @return The field access.
   */
  protected fun getAccessAt(at: Int): Int {
    return if (getModel() != null) {
      getModel().getAccess(getBlockView().getRecordFromDisplayLine(at))
    } else {
      VConstants.ACS_SKIPPED
    }
  }

  /**
   * Returns the field foreground color.
   * @param at The desired line.
   * @return The foreground color.
   */
  protected fun getForegroundAt(at: Int): VColor? {
    return if (model != null) {
      getModel().getForeground(getBlockView().getRecordFromDisplayLine(at))
    } else {
      null
    }
  }

  /**
   * Returns the field background color.
   * @param at The desired line.
   * @return The background color.
   */
  protected fun getBackgroundAt(at: Int): VColor? {
    return if (model != null) {
      getModel().getBackground(getBlockView().getRecordFromDisplayLine(at))
    } else {
      null
    }
  }

  /**
   * Returns the foreground color of the current data position.
   * @return The foreground color of the current data position.
   */
  fun getForeground(): VColor? {
    return getForegroundAt(getPosition())
  }

  /**
   * Returns the background color of the current data position.
   * @return The background color of the current data position.
   */
  fun getBackground(): VColor? {
    return getBackgroundAt(getPosition())
  }

  // ----------------------------------------------------------------------
  // SNAPSHOT PRINTING
  // ----------------------------------------------------------------------
  /**
   * prepare a snapshot
   * @param fieldPos position of this field within block visible fields
   */
  override fun prepareSnapshot(fieldPos: Int, activ: Boolean) {
    label!!.prepareSnapshot(activ)
  }

  //-------------------------------------------------
  // FIELD IMPLEMENTATION
  //-------------------------------------------------
  override fun getBlockView(): UBlock {
    return model.blockView
  }

  //-------------------------------------------------
  // FIELD LISTENER IMPLEMENTATION
  //-------------------------------------------------
  override fun onIncrement() {
    model.getIncrementCommand()!!.performAction()
  }

  override fun onDecrement() {
    model.getDecrementCommand()!!.performAction()
  }

  override fun onClick() {
    if (!modelHasFocus()) {
      // an empty row in a chart has not calculated
      // the access for each field (ACCESS Trigger)
      if (model.getBlock().isMulti()) {
        val recno: Int = getBlockView().getRecordFromDisplayLine(getPosition())
        if (!model.getBlock().isRecordFilled(recno)) {
          model.getBlock().updateAccess(recno)
        }
      }
      if (!model.getBlock().isMulti()
              || model.getBlock().detailMode == isInDetail() || model.getBlock().noChart()) {
        val action: Action = object : Action("mouse1") {
          override fun execute() {
            model.transferFocus(this@DField) // use here a mouse transferfocus
          }
        }
        // execute it as model transforming thread
        // it is not allowed to execute it not with
        // the method performAsync/BasicAction.
        model.performAsyncAction(action)
      }
    }
  }

  override fun fireAction() {
    TODO()
  }

  /**
   * !!! We never change transfer a focus to a field that belongs
   * to another block than this field model block. If we do it, it
   * can cause assertion errors when validating blocks caused by
   * actors actions.
   */
  override fun transferFocus() {
    TODO()
  }

  override fun gotoNextField() {
    TODO()
  }

  override fun gotoPrevField() {
    TODO()
  }

  override fun gotoNextEmptyMustfill() {
    TODO()
  }

  override fun gotoPrevRecord() {
    TODO()
  }

  override fun gotoNextRecord() {
    TODO()
  }

  override fun gotoFirstRecord() {
    TODO()
  }

  override fun gotoLastRecord() {
    TODO()
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  protected var state = 0 // Display state
  protected var pos = 0
  internal var access = 0 // current access of field
  protected var isEditable = false // is this field editable
  protected var mouseInside = false // private events
}
