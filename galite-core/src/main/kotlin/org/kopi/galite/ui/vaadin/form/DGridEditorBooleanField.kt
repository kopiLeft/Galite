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

import com.vaadin.flow.component.HasValue
import com.vaadin.flow.data.binder.Result
import com.vaadin.flow.data.binder.ValueContext
import com.vaadin.flow.data.converter.Converter
import com.vaadin.flow.data.renderer.Renderer
import org.kopi.galite.form.UTextField
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VFieldUI
import org.kopi.galite.ui.vaadin.grid.GridEditorBooleanField
import org.kopi.galite.ui.vaadin.grid.GridEditorField

class DGridEditorBooleanField(
        columnView: VFieldUI,
        label: DGridEditorLabel?,
        align: Int,
        options: Int
) : DGridEditorField<Boolean?>(columnView, label, align, options), UTextField {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var inside = false
  private var rendrerValue: Boolean? = null

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  init {
    // editor.setLabel(label.text) TODO
    // editor.addValueChangeListener(this) TODO
  }
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  override fun updateText() {
    //BackgroundThreadHandler.access(Runnable { TODO
    editor.value = getModel().getBoolean(getBlockView().getRecordFromDisplayLine(position))
    //})
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
    /*BackgroundThreadHandler.access(Runnable { TODO
      editor.setLabel(label.text)
      if (getAccess() == VConstants.ACS_MUSTFILL) {
        editor.setMandatory(true)
      } else {
        editor.setMandatory(false)
      }
    })*/
  }

  override fun getObject(): String? = getText()

  override fun createEditor(): GridEditorField<Boolean?> {
    return GridEditorBooleanField(trueRepresentation, falseRepresentation)
  }

  override fun createConverter(): Converter<Boolean?, Any?> {
    return object : Converter<Boolean?, Any?> {

      override fun convertToModel(value: Boolean?, context: ValueContext?): Result<Any?>? = Result.ok(value)

      override fun convertToPresentation(value: Any?, context: ValueContext?): Boolean? = value as Boolean

      val modelType: Class<Any>
        get() = Any::class.java
      val presentationType: Class<Boolean>
        get() = Boolean::class.java
    }
  }

  override fun createRenderer(): Renderer<Boolean?> {
    TODO()
  }

  override fun getText(): String? = getModel().toText(editor.value)

  override fun setHasCriticalValue(b: Boolean) {}

  override fun addSelectionFocusListener() {}

  override fun removeSelectionFocusListener() {}

  override fun setSelectionAfterUpdateDisabled(disable: Boolean) {}

  fun valueChange(event: HasValue.ValueChangeEvent<Boolean?>) { // TODO: link to listener
    // ensures to get model focus to validate the field
    if (!getModel().hasFocus()) {
      getModel().block!!.activeField = getModel()
    }
    val text = getModel().toText(event.value)
    if (getModel().checkText(text!!)) { // TODO:nullable?
      getModel().isChangedUI = true
      getModel().setBoolean(getBlockView().getRecordFromDisplayLine(position), event.value)
    }
    getModel().setChanged(true)
  }

  /**
   * Returns the true representation of this boolean field.
   * @return The true representation of this boolean field.
   */
  protected val trueRepresentation: String?
    get() = getModel().toText(java.lang.Boolean.TRUE)

  /**
   * Returns the false representation of this boolean field.
   * @return The false representation of this boolean field.
   */
  protected val falseRepresentation: String?
    get() = getModel().toText(java.lang.Boolean.FALSE)

  /**
   * Gets the focus to this field.
   */
  protected fun enterMe() {
    /*BackgroundThreadHandler.access(Runnable {  TODO
      getEditor().focus()
    })*/
  }
}
