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

package org.kopi.galite.form

import java.io.Serializable

import org.kopi.galite.form.VBlock.OrderModel
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.ActionHandler
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VExecFailedException

/**
 * This class implements all UI actions on fields
 *
 * @param blockView The block view.
 * @param model     The field model.
 * @param index     The row controller index.
 */
abstract class VFieldUI protected @JvmOverloads constructor(val blockView: UBlock,
                                              val model: VField,
                                              val index: Int = 0)
  : VConstants, ActionHandler, Serializable {

  private fun hasEditItem_S(): Boolean = model.list != null && model.list!!.hasShortcut

  // ----------------------------------------------------------------------
  // ABSTRACT METHOD
  // ----------------------------------------------------------------------
  /**
   * Creates a display widget for this row controller.
   *
   * @param label The field label.
   * @param model The field model.
   * @param detail Is this field is in detail mode ?
   * @return The [UField] display component of this field.
   */
  protected abstract fun createDisplay(label: ULabel, model: VField, detail: Boolean): UField

  /**
   * Creates a [FieldHandler] for this row controller.
   *
   * @return The created [FieldHandler].
   */
  protected abstract fun createFieldHandler(): FieldHandler

  /**
   * Creates a [ULabel] for this row controller.
   *
   * @param text The label text.
   * @param help The label help
   * @param detail Creates the label for the detail mode ?
   * @return The created [ULabel].
   */
  protected abstract fun createLabel(text: String?, help: String?, detail: Boolean): ULabel

  /**
   * Creates a [UChartLabel] for this row controller.
   *
   * @param text The label text.
   * @param help The label help.
   * @param index The chart label index.
   * @param model The chart label sort model.
   * @return The created [UChartLabel]
   */
  protected abstract fun createChartHeaderLabel(text: String?, help: String?, index: Int, model: OrderModel): UChartLabel
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  /**
   * Display error
   */
  fun displayFieldError(message: String) {
    val display = getModeDisplay()

    if (display == null) {
      model.getForm().error(message)
    } else {
      try {
        // navigates to the active record if needed
        // this is typically needed in grid based blocks
        gotoActiveRecord()
        // switch to detail view when needed
        if (getBlock().isMulti() && display == detailDisplay && !getBlock().detailMode) {
          (blockView as UMultiBlock).switchView(-1)
        }
        display.setBlink(true)
        model.getForm().error(message)
        display.setBlink(false)
        transferFocus(display)
      } catch (e: VException) {
        throw InconsistencyException()
      } finally {
        // ensure that the field gain focus again
        display.forceFocus()
      }
    }
  }

  /**
   * Returns the field display according to the model block.
   * If the block is in detail mode and the field is visible
   * in the detail mode, the detail display will be returned.
   * Otherwise, the chart display will be returned in case of
   * a valid display line
   */
  protected fun getModeDisplay(): UField? {
    val displayLine = blockView.getDisplayLine(getBlock().activeRecord)

    return when {
      model.noChart() -> detailDisplay
      model.noDetail() && displayLine != -1 -> displays[displayLine]
      !model.noChart() && !model.noDetail() -> {
        // field is visible on both views
        when {
          getBlock().isMulti() && getBlock().detailMode -> detailDisplay
          displayLine != -1 -> displays[displayLine]
          else -> null
        }
      }
      else -> null
    }
  }

  protected fun gotoActiveRecord() {
    // to be redefined by subclasses
  }

  fun resetCommands() {
    activeCommands.forEach {
      it.setEnabled(false)
    }
    activeCommands.clear()
    if (model.hasFocus()) {
      if (hasEditItem_S()) { // TRY TO REMOVE !!!!
        val command = model.getForm().cmdEditItem_S
        activeCommands.add(command)
        command.setEnabled(true)
      } else if (hasAutofill) {
        // for boolean fields, the auto fill command is not included for boolean field
        // when row controller does not allow it.
        if (model !is VBooleanField || includeBooleanAutofillCommand()) {
          val command: VCommand = model.getForm().cmdAutofill
          activeCommands.add(command)
          command.setEnabled(true)
        }
      }
      if (hasNewItem) {
        val command: VCommand = model.getForm().cmdNewItem
        activeCommands.add(command)
        command.setEnabled(true)
      }
      if (hasEditItem) {
        val command: VCommand = model.getForm().cmdEditItem
        activeCommands.add(command)
        command.setEnabled(true)
      }
      val localCommands = model.command
      localCommands?.forEachIndexed { index, localCommand ->
        if (localCommand.isActive(getBlock().getMode())) {
          val active = if (getBlock().hasTrigger(VConstants.TRG_CMDACCESS,
                          getBlock().fields.size + getBlock().commands!!.size + index + 1)) {
            try {
              (getBlock().callTrigger(VConstants.TRG_CMDACCESS,
                      getBlock().fields.size + getBlock().commands!!.size + index + 1) as Boolean)
            } catch (e: VException) {
              // consider that the command is active of any error occurs
              true
            }
          } else {
            // if no access trigger is associated with the command
            // we consider it as active command
            true
          }
          if (active) {
            activeCommands.add(localCommand)
            localCommand.setEnabled(true)
          }
        }
      }
    }
    // 20021022 laurent : do the same for increment and decrement buttons ?
    if (model.getAccess(model.block!!.activeRecord) > VConstants.ACS_SKIPPED &&
            hasAutofillCommand() &&
            !model.block!!.isChart() && display != null && display!!.getAutofillButton() != null) {
      display!!.getAutofillButton().setEnabled(autofillCommand!!.isActive(model.block!!.getMode()))
    }
  }

  fun getAllCommands(): Array<Any> {
      val cmds = arrayListOf<VCommand>()
      var i = 0
      while (commands != null && i < commands.size) {
        cmds.add(commands[i])
        i++
      }
      if (hasEditItem_S()) {
        cmds.add(model.getForm().cmdEditItem_S)
      } else if (hasAutofill) {
        if (model !is VBooleanField || includeBooleanAutofillCommand()) {
          cmds.add(model.getForm().cmdAutofill)
        }
      }
      if (hasNewItem) {
        cmds.add(model.getForm().cmdNewItem)
      }
      if (hasEditItem) {
        cmds.add(model.getForm().cmdEditItem)
      }
      return cmds.toTypedArray()
    }

  /**
   * Clears all display fields.
   */
  fun close() {
    activeCommands.forEach {
      it.setEnabled(false)
    }
    activeCommands.clear()
  }

  /**
   * resetLabel
   */
  fun resetLabel() {
    if (dl != null) {
      dl!!.init(model.label, model.toolTip)
    }
    if (dlDetail != null) {
      dl!!.init(model.label, model.toolTip)
    }
  }

  fun hasAutofill(): Boolean = hasAutofill || model.list != null || hasAutofillCommand()

  /**
   * Returns true if the field has an action trigger.
   * @return True if the field has an action trigger.
   */
  fun hasAction(): Boolean = model.hasAction()

  fun hasAutofillCommand(): Boolean = autofillCommand != null

  /**
   * Executes, if defined, the action defined by the field.
   */
  fun executeAction() {
    if (hasAction()) {
      performAsyncAction(object : Action("FIELD_ACTION") {
        override fun execute() {
          model.callTrigger(VConstants.TRG_ACTION)
        }
      })
    }
  }

  /**
   * Fill this field with an appropriate value according to present text
   * and ask the user if there is multiple choice
   * @exception        VException        an exception may occur in gotoNextField
   */
  fun fillField(): Boolean {
    model.checkType(fieldHandler.getDisplayedValue(true))
    if (hasAutofill()) {
      if (hasEditItem_S()) {
        fieldHandler.loadItem(VForm.CMD_EDITITEM)
      } else {
        model.selectFromList(false)
      }
    }
    return true
  }

  /**
   * Fill this field with an appropriate value according to present text
   * and ask the user if there is multiple choice
   * @exception        VException        an exception may occur in gotoNextField
   */
  fun autofillButton() {
    if (hasAutofillCommand()) {
      autofillCommand!!.performBasicAction()
    } else {
      fieldHandler.predefinedFill()
    }
  }

  /**
   * Fill this field with an appropriate value according to present text
   * and ask the user if there is multiple choice
   * @exception        VException        an exception may occur in gotoNextField
   */
  fun nextEntry() {
    model.checkType(fieldHandler.getDisplayedValue(true))
    if (model.hasNextPreviousEntry()) {
      model.enumerateValue(true)
    } else if (decrementCommand != null) {
      decrementCommand!!.performBasicAction()
    }
  }

  /**
   * Fill this field with an appropriate value according to present text
   * and ask the user if there is multiple choice
   * @exception        VException        an exception may occur in gotoNextField
   */
  fun previousEntry() {
    model.checkType(fieldHandler.getDisplayedValue(true))
    if (model.hasNextPreviousEntry()) {
      model.enumerateValue(false)
    } else {
      incrementCommand?.performBasicAction()
    }
  }

  /**
   * @return the associated incrementCommand
   */
  fun getIncrementCommand(): VCommand? = incrementCommand

  /**
   * @return the associated decrementCommand
   */
  fun getDecrementCommand(): VCommand? = decrementCommand

  /**
   * Returns true if the UI controller should include the auto fill
   * command for boolean fields. This is used to handle different UIs
   * for boolean field between swing and WEB implementations.
   * @return True if the auto fill command should be present for boolean fields.
   */
  protected fun includeBooleanAutofillCommand(): Boolean = true

  // ----------------------------------------------------------------------
  // PROTECTED BUILDING METHODS
  // ----------------------------------------------------------------------
  private fun buildDisplay() {
    // building
    dl = if (model.isSortable()) {
      // !!! override dl ist not good
      createChartHeaderLabel(model.label, model.toolTip, getBlock().getFieldIndex(model), getBlock().orderModel)
    } else {
      createLabel(model.label, model.toolTip, false)
    }
    if (!model.isInternal()) {
      // no hidden field (in all modes):
      if (getBlock().isMulti() && !getBlock().noChart()) {
        // add all fields in the display for one column of the chart
        // first the label
        if (!model.noChart()) {
          // is the first column filled with detailViewButton
          val leftOffset = if (getBlock().noDetail()) -1 else 0
          blockView.add(dl, Alignment(chartPos + leftOffset,
                                      0,
                                      1,
                                      1,
                                      model.align == VConstants.ALG_RIGHT))

          // the fields for the values
          displays = arrayOfNulls(getDisplaySize())
          for (i in 0 until getDisplaySize()) {
            displays[i] = createDisplay(dl, model, false)
            blockView.add(displays[i]!!, Alignment(chartPos + leftOffset, i + 1, 1, 1, false))
            displays[i]!!.setPosition(i)
          }
          scrollTo(0)
          // detail view of the chart
        }
        if (!getBlock().noDetail() && !model.noDetail()) {
          // create the second label for the detail view
          dlDetail = createLabel(model.label, model.toolTip, true)
          if (columnEnd >= 0) {
            (getBlock().display as UMultiBlock).addToDetail(dlDetail,
                    Alignment(column * 2 - 2, line - 1, 1, 1, false, true))
          }
          // field for the value in the detail view
          detailDisplay = createDisplay(dlDetail, model, true)
          (getBlock().display as UMultiBlock).addToDetail(detailDisplay,
                  Alignment(column * 2 - 1, line - 1, (columnEnd - column) * 2 + 1, (lineEnd - line) * 2 + 1, false))
          detailDisplay.setPosition(0)
          detailDisplay.setInDetail(true)
        }

        // update text
        if (!model.noChart()) {
          for (i in 0 until getDisplaySize()) {
            displays[i]!!.updateText()
          }
        }
        if (!getBlock().noDetail() && !model.noDetail()) {
          detailDisplay.updateText()
        }
      } else if (column < 0) {
        // multifields (special fields)
        // take care that in this row is only this multifield
        blockView.add(dl, MultiFieldAlignment(columnEnd * 2 - 1, line - 1, 1, 1, true))
        displays = arrayOf(createDisplay(dl, model, false))
        blockView.add(displays[0]!!, MultiFieldAlignment(columnEnd * 2 - 1,
                line,
                1,
                lineEnd - line + 1,
                false))
        displays[0]!!.setPosition(0)
        displays[0]!!.updateText()
      } else {
        displays = arrayOf(createDisplay(dl, model, false))
        if (columnEnd >= 0 && displays[0] !is UActorField) {
          // not an info field and not an actor field  => show label
          blockView.add(dl, Alignment(column * 2 - 2, line - 1, 1, 1, false, true))
        }
        if (displays[0] is UActorField) {
          // an actor field takes the label and the field space
          blockView.add(displays[0]!!, Alignment(column * 2 - 2, line - 1, (columnEnd - column) * 2 + 2, lineEnd - line + 1, false))
        } else {
          blockView.add(displays[0]!!, Alignment(column * 2 - 1, line - 1, (columnEnd - column) * 2 + 1, lineEnd - line + 1, false))
        }
        displays[0]!!.setPosition(0)
        displays[0]!!.updateText()
      }
      if (displays != null) {
        for (i in displays.indices) {
          fireAccessHasChanged(i) // update access
        }
      }
    }
    fireDisplayCreated()
    // building = false;
  }

  /**
   * Returns the displayed size of this column.
   * @return the displayed size of this column.
   */
  protected open fun getDisplaySize(): Int = getBlock().displaySize


  val display: UField?
    get() = if (blockView.getDisplayLine() == -1) {
      null
    } else {
      if (getBlock().isChart() && blockView.inDetailMode()) {
        detailDisplay
      } else {
        if (displays == null) null else displays[blockView.getDisplayLine()]
      }
    }

  // ----------------------------------------------------------------------
  // FOCUS ACCESSORS
  // ----------------------------------------------------------------------
  /**
   * Transfers focus to next accessible field (tab typed)
   * @param display The field display.
   * @exception        VException        an exception may be raised in leave()
   */
  fun transferFocus(display: UField) {
    val recno = blockView.getRecordFromDisplayLine(display.getPosition())

    // go to the correct block if necessary
    if (getBlock() != model.getForm().getActiveBlock()) {
      if (!getBlock().isAccessible) {
        throw VExecFailedException(MessageCode.getMessage("VIS-00025"))
      }
      model.getForm().gotoBlock(getBlock())
    }

    // go to the correct record if necessary
    // but only if we are in the correct block now
    if (getBlock() == model.getForm().getActiveBlock()
            && getBlock().isMulti()
            && recno != getBlock().activeRecord && getBlock().isRecordAccessible(recno)) {
      getBlock().gotoRecord(recno)
    }

    // go to the correct field if already necessary
    // but only if we are in the correct record now
    if (getBlock() == model.getForm().getActiveBlock()
            && recno == getBlock().activeRecord
            && model != getBlock().activeField
            && display.getAccess() >= VConstants.ACS_VISIT) {
      getBlock().gotoField(model)
    }
  }

  // ----------------------------------------------------------------------
  // NAVIGATING
  // ----------------------------------------------------------------------
  /**
   * Changes access dynamically, overriding mode access
   */
  fun fireAccessHasChanged(recno: Int) {
    // Comment out because:
    // update only the necessary Display in the column
    val rowInDisplay = blockView.getDisplayLine(recno)

    if (displays != null) {
      if (rowInDisplay != -1) {
        // -1 means currently not displayed
        displays[rowInDisplay]!!.updateAccess()
      }
    }
    if (detailDisplay != null && detailDisplay.getPosition() == rowInDisplay) {
      detailDisplay.updateAccess()
    }
  }

  /**
   * Changes the color properties of a field.
   */
  fun fireColorHasChanged(recno: Int) {
    val rowInDisplay = blockView.getDisplayLine(recno)

    if (displays != null) {
      if (rowInDisplay != -1) {
        // -1 means currently not displayed
        displays[rowInDisplay]!!.updateColor()
      }
    }
    if (detailDisplay != null && detailDisplay.getPosition() == rowInDisplay) {
      detailDisplay.updateColor()
    }
  }

  // ----------------------------------------------------------------------
  // DISPLAY UTILS
  // ----------------------------------------------------------------------
  /**
   * Clears all display fields.
   */
  fun scrollTo(toprec: Int) {
    if (displays != null) {
      displays.forEach {
        it!!.updateFocus()
        it.updateAccess()
        it.updateText()
        it.updateColor()
      }
    }
    if (detailDisplay != null) {
      val record: Int = blockView.getModel().activeRecord
      // is there no active line, show the same content then the first row
      // in the chart
      var dispLine = if (record >= 0) blockView.getDisplayLine(record) else 0
      // is the active line, is not in the visible part then show the same
      // content then in the first line of the chart
      if (dispLine < 0) {
        dispLine = 0
      }
      detailDisplay.setPosition(dispLine)
      detailDisplay.updateFocus()
      detailDisplay.updateAccess()
      detailDisplay.updateText()
      detailDisplay.updateColor()
    }
  }
  // ---------------------------------------------------------------------
  // IMPLEMENTATION
  // ---------------------------------------------------------------------
  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param        action                the action to perform.
   * @param        block                This action should block the UI thread ?
   */
  // USE METHOD IN FORM
  @Deprecated("use method performAsyncAction", ReplaceWith("performAsyncAction(action)"))
  override fun performAction(action: Action, block: Boolean) {
    blockView.getFormView().performAsyncAction(action)
  }

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param        action                the action to perform.
   */
  override fun performAsyncAction(action: Action) {
    blockView.getFormView().performAsyncAction(action)
  }

  /**
   * Performs the appropriate action asynchronously.
   * You can use this method to perform any operation out of the UI event process
   *
   * @param        action                the action to perform.
   */
  fun performBasicAction(action: Action) {
    blockView.getFormView().performBasicAction(action)
  }

  /**
   * Performs a void trigger
   *
   * @param        VKT_Type        the number of the trigger
   */
  override fun executeVoidTrigger(VKT_Type: Int) {
    getBlock().executeVoidTrigger(VKT_Type)
  }

  /**
   * return the block of the model
   */
  fun getBlock(): VBlock = model.block!!

  /**
   * Called when the display is created for this row controller.
   * This may be used to execute actions after creating the field
   * displays.
   */
  protected fun fireDisplayCreated() {
    // to be redefined if needed
  }

  // ----------------------------------------------------------------------
  // SNAPSHOT PRINTING
  // ----------------------------------------------------------------------
  /**
   * prepare a snapshot
   *
   * @param        fieldPos        position of this field within block visible fields
   */
  fun prepareSnapshot(fieldPos: Int, activ: Boolean) {
    displays.forEach {
      it!!.prepareSnapshot(fieldPos, activ)
    }
  }

  fun getLabel(): ULabel = dl

  fun getDetailLabel(): ULabel = dlDetail

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  val fieldHandler = createFieldHandler() // The field handler instance.

  // static (compiled) data
  private val hasAutofill: Boolean // RE
  private var hasNewItem = false // MO
  private var hasEditItem = false // VE

  //private	boolean			hasEditItem_S;	// IT !!!!
  private val commands: Array<VCommand>? // commands
  lateinit var displays: Array<UField?> // the object displayed on screen
    private set
  private lateinit var dl: ULabel // label text
  private lateinit var dlDetail: ULabel // label text (chart)
  lateinit var detailDisplay: UField // the object displayed on screen (detail)
    private set
  private var line = 0 // USE A VPosition !!!!
  private var lineEnd = 0
  private var column = 0
  private var columnEnd = 0
  private var chartPos = 0

  // dynamic data
  private val activeCommands = arrayListOf<VCommand>() // commands currently actives
  private var incrementCommand: VCommand? = null
  private var decrementCommand: VCommand? = null
  private var autofillCommand: VCommand? = null

  init {
    model.addFieldListener(fieldHandler)
    model.addFieldChangeListener(fieldHandler)
    val pos = model.position
    if (pos != null) {
      line = pos.line
      lineEnd = pos.lineEnd
      column = pos.column
      columnEnd = pos.columnEnd
      chartPos = pos.chartPos
    }
    val cmd = model.command

    cmd?.forEach {
      val commandText = it.getIdent()
      when {
        commandText == "Increment" -> incrementCommand = it
        commandText == "Decrement" -> decrementCommand = it
        commandText == "Autofill" && !model.hasAutofill() -> autofillCommand = it
      }
    }
    hasAutofill = model.hasAutofill() && !hasAutofillCommand()
    commands = cmd
    if (model.list != null) {
      if (model.list!!.newForm != null || model.list!!.action != -1) {
        hasNewItem = true
        hasEditItem = hasNewItem
      }
    }
    buildDisplay()
  }
}
