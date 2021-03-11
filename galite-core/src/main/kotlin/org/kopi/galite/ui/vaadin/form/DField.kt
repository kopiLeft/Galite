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
import org.kopi.galite.form.VImageField
import org.kopi.galite.form.VStringField
import org.kopi.galite.ui.vaadin.actor.Actor
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
 * @param inDetail Is it a detail view ?
 */
abstract class DField(internal val model: VFieldUI,
                      internal var label: DLabel?,
                      internal var align: Int,
                      internal var options: Int,
                      private var inDetail: Boolean)
  : Field(model.getIncrementCommand() != null,
          model.getDecrementCommand() != null), UField, FieldListener {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  protected var state = 0 // Display state
  internal var access = 0 // current access of field
  protected var isEditable = options and VConstants.FDO_NOEDIT == 0 // is this field editable
  protected var mouseInside = false // private events

  init {
    addFieldListener(this)
    visibleHeight = when {
      getModel() is VStringField -> {
        (getModel() as VStringField).getVisibleHeight()
      }
      getModel() is VImageField -> {
        /*
         * Sets the visible height of the image field to allow row span in simple block layouts.
         * We estimate that a row height is ~ 20 px
         */
        (getModel() as VImageField).iconHeight / 20
      }
      else -> {
        getModel().height
      }
    }
    hasAction = model.hasAction()
    label!!.hasAction = model.hasAction()
    noChart = getModel().noChart()
    noDetail = getModel().noDetail()
    navigationDelegationMode = _getNavigationDelegationMode()
    defaultAccess = getModel().getDefaultAccess()
    index = model.index
    hasPreFieldTrigger = getModel().hasTrigger(VConstants.TRG_PREFLD)
    addActors(getActors())
  }

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
   * @return the position in chart (0..nbDisplay)
   */
  override var position: Int = 0

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

  override fun getAutofillButton(): UComponent? = null

  /**
   * Returns the row controller.
   * @return The row controller.
   */
  open fun getRowController(): VFieldUI = model

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
    // access { TODO: access from thread!!
      access = getAccess()
      dynAccess = access
      updateStyles(access)
      isVisible = access != VConstants.ACS_HIDDEN
      isActionEnabled = access >= VConstants.ACS_VISIT
      update(label)
    //}
  }

  /**
   * Updates a given field label.
   * @param label The label to be updated.
   */
  private fun update(label: DLabel?) {
    if (label != null) {
      val was = label.isEnabled
      val will = access >= VConstants.ACS_VISIT
      if (was != will) {
        label.isEnabled = will
      }
    }
  }

  /**
   * Update field style according to its access.
   * @param access The field access.
   */
  private fun updateStyles(access: Int) {
    classNames.remove("visit")
    classNames.remove("skipped")
    classNames.remove("mustfill")
    classNames.remove("hidden")
    when (access) {
      VConstants.ACS_VISIT ->       classNames.add("visit")
      VConstants.ACS_SKIPPED ->     classNames.add("skipped")
      VConstants.ACS_MUSTFILL ->    classNames.add("mustfill")
      VConstants.ACS_HIDDEN ->      classNames.add("hidden")
      else ->                       classNames.add("visit")
    }
  }


  /**
   * Returns the navigation delegation to server mode.
   * For POSTFLD AND PREFLD triggers we always delegate the navigation to server.
   * For POSTCHG, PREVAL, VALFLD and FORMAT triggers we delegate the navigation to server if
   * the field value has changed.
   * @return The navigation delegation to server mode.
   */
  private fun _getNavigationDelegationMode(): NavigationDelegationMode {
    return when {
      getModel().hasTrigger(VConstants.TRG_POSTFLD) -> {
        NavigationDelegationMode.ALWAYS
      }
      getModel().hasTrigger(VConstants.TRG_PREFLD) -> {
        NavigationDelegationMode.ALWAYS
      }
      getModel().block!!.hasTrigger(VConstants.TRG_PREREC) -> {
        NavigationDelegationMode.ALWAYS
      }
      getModel().block!!.hasTrigger(VConstants.TRG_POSTREC) -> {
        NavigationDelegationMode.ALWAYS
      }
      getModel().block!!.hasTrigger(VConstants.TRG_VALREC) -> {
        NavigationDelegationMode.ALWAYS
      }
      getModel().list != null -> {
        NavigationDelegationMode.ONVALUE
      }
      getModel().hasTrigger(VConstants.TRG_POSTCHG) -> {
        NavigationDelegationMode.ONCHANGE
      }
      getModel().hasTrigger(VConstants.TRG_PREVAL) -> {
        NavigationDelegationMode.ONCHANGE
      }
      getModel().hasTrigger(VConstants.TRG_VALFLD) -> {
        NavigationDelegationMode.ONCHANGE
      }
      getModel().hasTrigger(VConstants.TRG_FORMAT) -> {
        NavigationDelegationMode.ONCHANGE
      }
      else -> {
        NavigationDelegationMode.NONE
      }
    }
  }

  /**
   * Returns the actors associated with this field.
   * @return The actors associated with this field.
   */
  private fun getActors(): Collection<Actor> {
    val actors: MutableSet<Actor>
    actors = HashSet<Actor>()
    for (cmd in model.getAllCommands()) {
      if (cmd != null) {
        // for field commands this is needed to have the actor model instance
        cmd.setEnabled(false)
        if (cmd.actor != null) {
          actors.add(cmd.actor!!.getDisplay() as Actor)
        }
      }
    }
    return actors
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
    return getModel().hasFocus() && block!!.activeRecord == getBlockView().getRecordFromDisplayLine(position)
  }

  /**
   * Returns `true` if the field is in skipped mode.
   * @return `true` if the field is in skipped mode.
   */
  protected fun isSkipped(): Boolean {
    val block = getModel().block
    return (getAccess() == VConstants.ACS_SKIPPED
            || !block!!.isRecordAccessible(getBlockView().getRecordFromDisplayLine(position)))
  }

  override fun getAccess(): Int {
    return getAccessAt(position)
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
  val foreground: VColor?
    get() = getForegroundAt(position)

  /**
   * Returns the background color of the current data position.
   * @return The background color of the current data position.
   */
  val background: VColor?
    get() = getBackgroundAt(position)
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
        val recno: Int = getBlockView().getRecordFromDisplayLine(position)
        if (!model.getBlock().isRecordFilled(recno)) {
          model.getBlock().updateAccess(recno)
        }
      }
      if (!model.getBlock().isMulti()
              || model.getBlock().isDetailMode == isInDetail() || model.getBlock().noChart()) {
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
    model.executeAction()
  }

  /**
   * !!! We never change transfer a focus to a field that belongs
   * to another block than this field model block. If we do it, it
   * can cause assertion errors when validating blocks caused by
   * actors actions.
   */
  override fun transferFocus() {
    if (!modelHasFocus()) {
      // an empty row in a chart has not calculated
      // the access for each field (ACCESS Trigger)
      if (model.getBlock().isMulti()) {
        val recno: Int = getBlockView().getRecordFromDisplayLine(position)
        if (!model.getBlock().isRecordFilled(recno)) {
          model.getBlock().updateAccess(recno)
        }
      }
      if (!model.getBlock().isMulti()
              || model.getBlock().isDetailMode == isInDetail() || model.getBlock().noChart()) {
        val action: Action = object : Action("mouse1") {
          override fun execute() {
            // proceed only of we are in the same block context.
            if (getModel().block == getModel().getForm().getActiveBlock()) {
              val recno: Int = getBlockView().getRecordFromDisplayLine(position)

              // go to the correct record if necessary
              // but only if we are in the correct block now
              if (getModel().block!!.isMulti()
                      && recno != getModel().block!!.activeRecord && getModel().block!!.isRecordAccessible(
                              recno)) {
                getModel().block!!.gotoRecord(recno)
              }

              // go to the correct field if already necessary
              // but only if we are in the correct record now
              if (recno == getModel().block!!.activeRecord && getModel() != getModel().block!!.activeField && getAccess() >= VConstants.ACS_VISIT) {
                getModel().block!!.gotoField(getModel())
              }
            }
          }
        }
        // execute it as model transforming thread
        // it is not allowed to execute it not with
        // the method performAsync/BasicAction.
        model.performAsyncAction(action)
      }
    }
  }

  override fun gotoNextField() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_TAB") {
      override fun execute() {
        if (getModel() != null) {
          getModel().block!!.form.getActiveBlock()!!.gotoNextField()
        }
      }
    })
  }

  override fun gotoPrevField() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_STAB") {
      override fun execute() {
        if (getModel() != null) {
          getModel().block!!.form.getActiveBlock()!!.gotoPrevField()
        }
      }
    })
  }

  override fun gotoNextEmptyMustfill() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_ALTENTER") {
      override fun execute() {
        if (getModel() != null) {
          getModel().block!!.form.getActiveBlock()!!.gotoNextEmptyMustfill()
        }
      }
    })
  }

  override fun gotoPrevRecord() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_REC_UP") {
      override fun execute() {
        if (getModel() != null) {
          getModel().block!!.gotoPrevRecord()
        }
      }
    })
  }

  override fun gotoNextRecord() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_REC_DOWN") {
      override fun execute() {
        if (getModel() != null) {
          getModel().block!!.gotoNextRecord()
        }
      }
    })
  }

  override fun gotoFirstRecord() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_REC_FIRST") {
      override fun execute() {
        if (getModel() != null) {
          getModel().block!!.form.getActiveBlock()!!.gotoFirstRecord()
        }
      }
    })
  }

  override fun gotoLastRecord() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_REC_LAST") {
      override fun execute() {
        if (getModel() != null) {
          getModel().block!!.form.getActiveBlock()!!.gotoLastRecord()
        }
      }
    })
  }

  /**
   * Performs the auto fill action.
   */
  fun performAutoFillAction() {
    getModel().getForm().performAsyncAction(object : Action("autofill") {
      override fun execute() {
        model.transferFocus(this@DField)
        model.autofillButton()
      }
    })
  }

  /**
   * Performs the field action trigger
   */
  fun performFieldAction() {
    if (model.hasAction()) {
      getModel().getForm().performAsyncAction(object : Action("TRG_ACTION") {
        override fun execute() {
          model.transferFocus(this@DField)
          getModel().callTrigger(VConstants.TRG_ACTION)
        }
      })
    }
  }
}
