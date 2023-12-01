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

import com.vaadin.flow.component.UI
import com.vaadin.flow.data.converter.Converter
import com.vaadin.flow.data.renderer.Renderer
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.VColor
import org.kopi.galite.visual.base.UComponent
import org.kopi.galite.visual.form.UField
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.form.VFieldUI
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.field.TextField.ConvertType
import org.kopi.galite.visual.ui.vaadin.grid.GridEditorField

/**
 * An UField associated with the grid block in inline edit.
 */
abstract class DGridEditorField<T>(
        internal var columnView: VFieldUI,
        val label: DGridEditorLabel?,
        protected var align: Int,
        protected var options: Int
) : UField,
        GridEditorField.NavigationListener,
        GridEditorField.AutofillListener {

  internal var access = 0 // current access of field
  protected var isEditable = options and VConstants.FDO_NOEDIT == 0 // is this field editable

  /**
   * Returns the field editor hold by this component.
   */
  val editor: GridEditorField<T> by lazy {
    createEditor().also { editor ->
      editor.dGridEditorField = this
      editor.addClickListener {
        onClick()
      }
      editor.addAttachListener {
        currentUI = it.ui
      }
    }
  }

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  /**
   * Creates a new instance of the grid editor field.
   */
  init {
    editor.setConverter(createConverter())
    // editor.addNavigationListener(this) TODO
    // editor.addAutofillListener(this) TODO
    setLabelAlignment()
  }
  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  override fun getModel(): VField = columnView.model

  abstract fun valueChanged(oldValue: String?)

  override fun getBlockView(): DGridBlock = columnView.blockView as DGridBlock

  override fun getAccess(): Int {
    return getModel().getAccess(getBlockView().getRecordFromDisplayLine(position))
  }

  override fun setBlink(blink: Boolean) {
    /*BackgroundThreadHandler.access(Runnable { TODO
      editor.setBlink(blink)
    })*/
  }

  override var position: Int
    get() = 0
    set(position) {}

  override fun getAutofillButton(): UComponent? = null

  override fun setInDetail(detail: Boolean) {}

  override fun forceFocus() {
    access(currentUI) {
      editor.focus()
    }
  }

  override fun updateColor() {
    editor.setColor(getModel().align, foreground, background)
  }

  var currentUI: UI? = null

  override fun prepareSnapshot(fieldPos: Int, active: Boolean) {}

  /**
   * Sets the label alignment according to the editor alignment.
   */
  protected fun setLabelAlignment() {
    if (label != null) {
      /*when (getModel().align) { TODO
        VConstants.ALG_LEFT -> label.addStyleName("v-align-left")
        VConstants.ALG_RIGHT -> label.addStyleName("v-align-right")
        VConstants.ALG_CENTER -> label.addStyleName("v-align-center")
        else -> label.addStyleName("v-align-left")
      }*/
    }
  }

  // ----------------------------------------------------------------------
  // UCOMPONENT
  // ----------------------------------------------------------------------

  override fun isEnabled(): Boolean {
    return editor.isEnabled
  }

  override fun setEnabled(enabled: Boolean) {
    editor.isEnabled = enabled
  }

  override fun isVisible(): Boolean = editor.isVisible

  override fun setVisible(visible: Boolean) {
    editor.isVisible = visible
  }

  override fun updateFocus() {
    if (modelHasFocus()) {
      val form = getModel().getForm()
      form.setInformationText(getModel().toolTip)
      form.setFieldSearchOperator(getModel().getSearchOperator())
    }
  }

  override fun updateAccess() {
    label?.update(columnView, getBlockView().getRecordFromDisplayLine(position))
    access(currentUI) {
      access = getAccess()
      setEnabled(access > VConstants.ACS_SKIPPED)
      setVisible(access != VConstants.ACS_HIDDEN)
      updateLabel()
    }
  }

  /**
   * Updates a given field label.
   */
  protected fun updateLabel() {
    if (label != null) {
      val was: Boolean = label.isEnabled
      val will: Boolean = access >= VConstants.ACS_VISIT
      if (was != will) {
        label.isEnabled = will
      }
    }
  }

  /**
   * Returns the null representation of this editor.
   * @return The null representation of this editor.
   */
  protected open val nullRepresentation: T?
    get() = null

  /**
   * Performs a reset operation on this editor.
   */
  open fun reset() {
    editor.value = nullRepresentation
  }

  // ----------------------------------------------------------------------
  // CLICK
  // ----------------------------------------------------------------------
  open fun onClick() {
    if (!modelHasFocus()) {
      val recno: Int = getBlockView().getRecordFromDisplayLine(position)
      if (!columnView.getBlock().isRecordFilled(recno)) {
        getModel().block!!.updateAccess(recno)
      }

      columnView.performAsyncAction(object : Action("mouse1") {
        override fun execute() {
          try {
            columnView.transferFocus(this@DGridEditorField) // use here a mouse transferfocus
          } catch (exception: Exception) {
            // Workaround to avoid closing editor on application errors.
            access(currentUI) {
              getBlockView().editor.editItem(getBlockView().editor.item)
            }
            throw exception
          }
        }
      })
    }
  }

  override fun onAutofill() {
    performAutoFillAction()
  }

  // ----------------------------------------------------------------------
  // NAVIGATION
  // ----------------------------------------------------------------------
  override fun onGotoNextField() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_TAB") {
      override fun execute() {
        getModel().block!!.form.getActiveBlock()!!.gotoNextField()
      }
    })
  }

  override fun onGotoPrevField() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_STAB") {
      override fun execute() {
        getModel().block!!.form.getActiveBlock()!!.gotoPrevField()
      }
    })
  }

  override fun onGotoNextBlock() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_BLOCK") {
      override fun execute() {
        getModel().block!!.form.gotoNextBlock()
      }
    })
  }

  override fun onGotoPrevRecord() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_REC_UP") {
      override fun execute() {
        getModel().block!!.form.getActiveBlock()!!.gotoPrevRecord()
      }
    })
  }

  override fun onGotoNextRecord() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_REC_DOWN") {
      override fun execute() {
        getModel().block!!.form.getActiveBlock()!!.gotoNextRecord()
      }
    })
  }

  override fun onGotoFirstRecord() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_REC_FIRST") {
      override fun execute() {
        getModel().block!!.form.getActiveBlock()!!.gotoFirstRecord()
      }
    })
  }

  override fun onGotoLastRecord() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_REC_LAST") {
      override fun execute() {
        getModel().block!!.form.getActiveBlock()!!.gotoLastRecord()
      }
    })
  }

  override fun onGotoNextEmptyMustfill() {
    getModel().getForm().performAsyncAction(object : Action("keyKEY_ALTENTER") {
      override fun execute() {
        getModel().block!!.form.getActiveBlock()!!.gotoNextEmptyMustfill()
      }
    })
  }

  // ----------------------------------------------------------------------
  // UTIL
  // ----------------------------------------------------------------------
  /**
   * Sets this editor as the focused one in the browser window
   */
  fun enter() {
    updateFocus()
  }

  /**
   * Leaves this editor in the browser window.
   */
  fun leave() {
    updateFocus()
  }

  /**
   * Creates the editor field associated with this component.
   * @return The created editor field.
   */
  protected abstract fun createEditor(): GridEditorField<T>

  /**
   * Creates the conversion engine for grid data rendering.
   * @return The data converter.
   */
  abstract fun createConverter(): Converter<T, Any?>

  /**
   * Creates the editor field renderer
   * @return The editor renderer.
   */
  abstract fun createRenderer(): Renderer<T>

  open fun format(input: Any?): Any? = input

  /**
   * Performs the auto fill action.
   */
  fun performAutoFillAction() {
    getModel().getForm().performAsyncAction(object : Action("autofill") {
      override fun execute() {
        columnView.transferFocus(this@DGridEditorField)
        columnView.autofillButton()
      }
    })
  }

  /**
   * Performs the field action trigger
   */
  fun performFieldAction() {
    if (columnView.hasAction()) {
      getModel().getForm().performAsyncAction(object : Action("TRG_ACTION") {
        override fun execute() {
          columnView.transferFocus(this@DGridEditorField)
          getModel().callTrigger(VConstants.TRG_ACTION)
        }
      })
    }
  }

  /**
   * Returns the field foreground color.
   * @param at The desired line.
   * @return The foreground color.
   */
  protected fun getForegroundAt(at: Int): VColor? {
    return getModel().getForeground(getBlockView().getRecordFromDisplayLine(at))
  }

  /**
   * Returns the field background color.
   * @param at The desired line.
   * @return The background color.
   */
  protected fun getBackgroundAt(at: Int): VColor? {
    return getModel().getBackground(getBlockView().getRecordFromDisplayLine(at))
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

  /**
   * Returns `true` if the field model is focused.
   * @return `true` if the field model is focused.
   */
  fun modelHasFocus(): Boolean {
    val block = getModel().block
    return getModel().hasFocus() && block!!.activeRecord == getBlockView().getRecordFromDisplayLine(position)
  }

  /**
   * Returns the convert type for the string field.
   * @return The convert type for the string field.
   */
  protected fun getConvertType(model: VField?): ConvertType {
    return when (model!!.getTypeOptions() and VConstants.FDO_CONVERT_MASK) {
      VConstants.FDO_CONVERT_NONE -> ConvertType.NONE
      VConstants.FDO_CONVERT_UPPER -> ConvertType.UPPER
      VConstants.FDO_CONVERT_LOWER -> ConvertType.LOWER
      VConstants.FDO_CONVERT_NAME -> ConvertType.NAME
      else -> ConvertType.NONE
    }
  }
}
