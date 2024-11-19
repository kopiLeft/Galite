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

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.data.binder.Result
import com.vaadin.flow.data.binder.ValueContext
import com.vaadin.flow.data.converter.Converter
import com.vaadin.flow.data.renderer.Renderer

import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VFieldUI
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.grid.GridEditorBooleanField
import org.kopi.galite.visual.ui.vaadin.grid.GridEditorField

class DGridEditorBooleanField(
  columnView: VFieldUI,
  label: DGridEditorLabel?,
  align: Int,
  options: Int
) : DGridEditorField<Boolean?>(columnView, label, align, options),
    HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<GridEditorField<Boolean?>, Boolean?>> {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------

  private var inside = false
  private var rendrerValue: Boolean? = null

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  init {
    editor.addValueChangeListener(this)
    editor.addFocusListener {}
    (editor as GridEditorBooleanField).addKeyDownListener(gotoNext = { onGotoNextField() },
                                                          gotoPrevious = { onGotoPrevField() })
  }

  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  override fun updateText() {
    access(currentUI) {
      editor.value = getModel().getBoolean(getBlockView().getRecordFromDisplayLine(position))
    }
  }

  override fun updateFocus() {
    label!!.update(columnView, getBlockView().getRecordFromDisplayLine(position))
    if (!modelHasFocus()) {
      if (inside) {
        inside = false
      }
    } else {
      if (!inside) {
        inside = true
        enterMe()
        if (rendrerValue != null) {
          getModel().isChangedUI = true
          getModel().setBoolean(getBlockView().model.activeRecord, rendrerValue)
          rendrerValue = null
        }
      }
    }
    super.updateFocus()
  }

  override fun reset() {
    inside = false
    super.reset()
  }

  override fun updateAccess() {
    super.updateAccess()
    label!!.update(columnView, getBlockView().getRecordFromDisplayLine(position))
    access {
      (editor as GridEditorBooleanField).mandatory = getAccess() == VConstants.ACS_MUSTFILL
    }
  }

  override fun getObject(): String? = getModel().toText(editor.value)

  override fun createEditor(): GridEditorField<Boolean?> {
    return GridEditorBooleanField(trueRepresentation, falseRepresentation)
  }

  override fun createConverter(): Converter<Boolean?, Any?> {
    return object : Converter<Boolean?, Any?> {

      override fun convertToModel(value: Boolean?, context: ValueContext?): Result<Any?>? = Result.ok(value)

      override fun convertToPresentation(value: Any?, context: ValueContext?): Boolean? = value as? Boolean
    }
  }

  override fun createRenderer(): Renderer<Boolean?> {
    TODO()
  }

  override fun format(input: Any?): Any? {
    return when (input) {
      true -> trueRepresentation
      false -> falseRepresentation
      else -> input
    }
  }

  override fun valueChanged(event: AbstractField.ComponentValueChangeEvent<GridEditorField<Boolean?>, Boolean?>) {
    if (!event.isFromClient) {
      return
    }

    // ensures to get model focus to validate the field
    if (!getModel().hasFocus()) {
      getModel().block!!.gotoField(getModel())
    }
    val text = getModel().toText(event.value)
    if (getModel().checkText(text!!)) {
      getModel().isChangedUI = true
      getModel().setBoolean(getBlockView().getRecordFromDisplayLine(position), event.value)
    }
    getModel().setChanged(true)
  }

  override fun valueChanged(oldValue: String?) {
    // Nothing to do
  }

  /**
   * Returns the true representation of this boolean field.
   * @return The true representation of this boolean field.
   */
  private val trueRepresentation: String?
    get() = getModel().toText(true)

  /**
   * Returns the false representation of this boolean field.
   * @return The false representation of this boolean field.
   */
  private val falseRepresentation: String?
    get() = getModel().toText(false)

  /**
   * Gets the focus to this field.
   */
  private fun enterMe() {
    access(currentUI) {
      (editor as GridEditorBooleanField).setFocus(true)
    }
  }
}
