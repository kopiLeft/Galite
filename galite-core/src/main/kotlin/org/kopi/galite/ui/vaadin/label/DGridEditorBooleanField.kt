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
package org.kopi.galite.ui.vaadin.label

import org.kopi.galite.form.UTextField
import org.kopi.galite.form.VFieldUI
import java.util.*

class DGridEditorBooleanField(
        columnView: VFieldUI,
        label: DGridEditorLabel,
        align: Int,
        options: Int
) : DGridEditorField<Boolean>(columnView, label, align, options), UTextField, ValueChangeListener {
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  override fun updateText() {
    BackgroundThreadHandler.access(Runnable {
      getEditor().setValue(
              model.getBoolean(blockView.getRecordFromDisplayLine(position)))
    })
  }

  override fun updateFocus() {
    label!!.update(columnView, blockView.getRecordFromDisplayLine(position))
    if (!modelHasFocus()) {
      if (inside) {
        inside = false
      }
    } else {
      if (!inside) {
        inside = true
        enterMe()
        if (rendrerValue != null) {
          model.setChangedUI(true)
          model.setBoolean(blockView.getModel().getActiveRecord(), rendrerValue)
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
    BackgroundThreadHandler.access(Runnable {
      getEditor().setLabel(label.text)
      if (getAccess() == VConstants.ACS_MUSTFILL) {
        getEditor().setMandatory(true)
      } else {
        getEditor().setMandatory(false)
      }
    })
  }

  val `object`: Any
    get() = text

  override fun createEditor(): GridEditorField<Boolean> {
    return GridEditorBooleanField(trueRepresentation, falseRepresentation)
  }

  override fun createConverter(): Converter<Boolean, Any?> {
    return object : Converter<Boolean?, Any?>() {
      @Throws(ConversionException::class)
      fun convertToModel(value: Boolean, targetType: Class<out Any?>?, locale: Locale?): Any {
        return value
      }

      @Throws(ConversionException::class)
      fun convertToPresentation(value: Any?, targetType: Class<out Boolean?>?, locale: Locale?): Boolean? {
        return value as Boolean?
      }

      val modelType: Class<Any>
        get() = Any::class.java
      val presentationType: Class<Boolean>
        get() = Boolean::class.java
    }
  }

  override fun createRenderer(): Renderer<Boolean> {
    val renderer: BooleanRenderer
    renderer = BooleanRenderer(trueRepresentation, falseRepresentation)
    renderer.addValueChangeListener(object : ValueChangeListener() {
      fun valueChange(event: ValueChangeEvent) {
        rendrerValue = event.getValue()
      }
    })
    return renderer
  }

  val text: String
    get() = model.toText(getEditor().getValue())

  override fun getEditor(): GridEditorBooleanField {
    return super.getEditor() as GridEditorBooleanField
  }

  fun setHasCriticalValue(b: Boolean) {}
  fun addSelectionFocusListener() {}
  fun removeSelectionFocusListener() {}
  fun setSelectionAfterUpdateDisabled(disable: Boolean) {}
  fun valueChange(event: ValueChangeEvent) {
    val text: String

    // ensures to get model focus to validate the field
    if (!model.hasFocus()) {
      model.getBlock().setActiveField(model)
    }
    text = model.toText(event.getValue())
    if (model.checkText(text)) {
      model.setChangedUI(true)
      model.setBoolean(blockView.getRecordFromDisplayLine(position), event.getValue())
    }
    model.setChanged(true)
  }

  /**
   * Returns the true representation of this boolean field.
   * @return The true representation of this boolean field.
   */
  protected val trueRepresentation: String
    protected get() = model.toText(java.lang.Boolean.TRUE)

  /**
   * Returns the false representation of this boolean field.
   * @return The false representation of this boolean field.
   */
  protected val falseRepresentation: String
    protected get() = model.toText(java.lang.Boolean.FALSE)

  /**
   * Gets the focus to this field.
   */
  protected fun enterMe() {
    BackgroundThreadHandler.access(Runnable { getEditor().focus() })
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var inside = false
  private var rendrerValue: Boolean? = null

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  init {
    getEditor().setLabel(label.text)
    getEditor().addValueChangeListener(this)
  }
}