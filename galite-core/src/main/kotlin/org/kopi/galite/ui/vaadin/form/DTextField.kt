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

import org.kopi.galite.form.UTextField
import org.kopi.galite.form.VCodeField
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VDateField
import org.kopi.galite.form.VFieldUI
import org.kopi.galite.form.VFixnumField
import org.kopi.galite.form.VIntegerField
import org.kopi.galite.form.VMonthField
import org.kopi.galite.form.VStringField
import org.kopi.galite.form.VTimeField
import org.kopi.galite.form.VTimestampField
import org.kopi.galite.form.VWeekField
import org.kopi.galite.ui.vaadin.field.TextField

/**
 * The `DTextField` is the vaadin implementation
 * of the [UTextField] specifications.
 */
open class DTextField(
        model: VFieldUI,
        label: DLabel?,
        align: Int,
        options: Int,
        detail: Boolean,
) : DField(model, label, align, options, detail), UTextField {

  // --------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------
  private var field: TextField // the text component
  protected var inside = false
  protected var noEdit = false
  protected var scanner = false

  init {
    //model.model.addFieldChangeListener(this) TODO
    noEdit = options and VConstants.FDO_NOEDIT != 0
    scanner = options and VConstants.FDO_NOECHO != 0 && getModel().height > 1

    field = createFieldGUI(options and VConstants.FDO_NOECHO != 0, scanner, align)

    setContent(field)
  }

  // --------------------------------------------------
  // CREATE FIELD UI
  // --------------------------------------------------
  /**
   * Creates the field UI component.
   * @param noEcho Password field ?
   * @param scanner Scanner field ?
   * @param align The field alignment.
   * @return The [TextField] object.
   */
  private fun createFieldGUI(
          noEcho: Boolean,
          scanner: Boolean,
          align: Int,
  ): TextField {

    val textfield = TextField(getModel().width,
                              getModel().height,
                              if (getModel().height == 1) 1 else (getModel() as VStringField).getVisibleHeight(),
                              !scanner && getModel().getTypeOptions() and VConstants.FDO_DYNAMIC_NL > 0,
                              noEcho,
                              scanner,
                              noEdit,
                              align)
    textfield.autocompleteLength = getModel().getAutocompleteLength()
    textfield.hasAutocomplete = getModel().hasAutocomplete()
    textfield.hasAutofill = model.hasAutofill()

    // set field type according to the model
    // this will set the validation strategy on the client side.
    when (getModel()) {
      is VStringField -> {
        // string field
        textfield.type = TextField.Type.STRING
        textfield.convertType = getConvertType()
      }
      is VIntegerField -> {
        // integer field
        textfield.type = TextField.Type.INTEGER
        textfield.minval = (getModel() as VIntegerField).minval.toDouble()
        textfield.maxval = (getModel() as VIntegerField).maxval.toDouble()
      }
      is VMonthField -> {
        // month field
        TODO()
      }
      is VDateField -> {
        // date field
        TODO()
      }
      is VWeekField -> {
        // week field
        TODO()
      }
      is VTimeField -> {
        // time field
        TODO()
      }
      is VCodeField -> {
        // code field
        TODO()
      }
      is VFixnumField -> {
        // fixnum field
        TODO()
      }
      is VTimestampField -> {
        // timestamp field
        TODO()
      }
      else -> {
        throw IllegalArgumentException("unknown field model : " + getModel().javaClass.name)
      }
    }
    // add navigation handler TODO

    return textfield
  }

  /**
   * Returns the convert type for the string field.
   * @return The convert type for the string field.
   */
  private fun getConvertType(): TextField.ConvertType =
          when ((getModel() as VStringField).getTypeOptions() and VConstants.FDO_CONVERT_MASK) {
            VConstants.FDO_CONVERT_NONE -> TextField.ConvertType.NONE
            VConstants.FDO_CONVERT_UPPER -> TextField.ConvertType.UPPER
            VConstants.FDO_CONVERT_LOWER -> TextField.ConvertType.LOWER
            VConstants.FDO_CONVERT_NAME -> TextField.ConvertType.NAME
            else -> TextField.ConvertType.NONE
          }

  fun setContent(component: TextField) {
    addComponentAsFirst(component)
  }

  // ----------------------------------------------------------------------
  // DRAWING
  // ----------------------------------------------------------------------
  override fun updateAccess() {
    //TODO("Not yet implemented")
  }

  override fun updateText() {
    //TODO("Not yet implemented")
  }

  override fun updateColor() {
    //TODO("Not yet implemented")
  }

  override fun updateFocus() {
    //TODO("Not yet implemented")
  }

  override fun forceFocus() {
    //TODO("Not yet implemented")
  }

  //---------------------------------------------------
  // TEXTFIELD IMPLEMENTATION
  //---------------------------------------------------
  override fun getText(): String? {
    TODO("Not yet implemented")
  }

  override fun setHasCriticalValue(b: Boolean) {
    // ignore
  }

  override fun addSelectionFocusListener() {
    // ignore
  }

  override fun removeSelectionFocusListener() {
    // ignore
  }

  override fun setSelectionAfterUpdateDisabled(disable: Boolean) {
    TODO("Not yet implemented")
  }

  //---------------------------------------------------
  // DFIELD IMPLEMENTATION
  //---------------------------------------------------
  override fun getObject(): Any? {
    TODO("Not yet implemented")
  }

  override fun setBlink(blink: Boolean) {
    TODO("Not yet implemented")
  }
}
