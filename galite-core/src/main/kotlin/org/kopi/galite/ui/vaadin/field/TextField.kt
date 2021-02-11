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
package org.kopi.galite.ui.vaadin.field

import com.vaadin.flow.component.AbstractSinglePropertyField
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.data.binder.BeanValidationBinder

/**
 * A text field component.
 *
 * @param col             The column number.
 * @param rows            The row number.
 * @param visibleRows     The visible rows
 * @param dynamicNewLine  Use default line transformer in multiple line field ? Dynamic new line means that
 * we use '\n' for line break. Fixed new line means that we complete the messing field columns with space
 * character instead of using line separator.
 * @param noEcho          Is it a password field ?
 * @param scanner         Is it a scanner field ?
 * @param noEdit          Is it a no edit field.
 * @param align           The field text alignment.
 */
class TextField(
        val col: Int,
        val rows: Int,
        val visibleRows: Int,
        val dynamicNewLine: Boolean,
        val noEcho: Boolean,
        val scanner: Boolean,
        val noEdit: Boolean,
        val align: Int,
) : AbstractField() {

  lateinit var field: AbstractSinglePropertyField<*, out Any?>

  /**
   * Is this text field enabled?
   */
  private val enabled = true

  /**
   * The field type: integer, decimal, date, time, timestamp, ...
   */
  lateinit var type: Type

  /**
   * The minimum value to be accepted by the field.
   */
  var minval: Double? = null

  /**
   * The maximum value to be accepted by the field.
   */
  var maxval: Double? = null

  /**
   * The max scale to be used with this field if it is a decimal one.
   */
  var maxScale = 0

  /**
   * Is this field a fraction one ?
   */
  var fraction = false

  /**
   * The value in the field
   */
  override var value: Any?
    get() = this.field.value
    set(value) {
      this.field.value = value
    }

  /**
   * The field enumeration for code fields.
   */
  var enumerations: Array<String>? = null

  /**
   * If the field has the auto complete feature.
   */
  var hasAutocomplete = false

  /**
   * Tells if the field has an autofill command
   */
  var hasAutofill = false

  /**
   * The auto complete minimum length to begin querying for suggestions
   */
  var autocompleteLength = 0

  /**
   * The convert type to be applied to the field.
   */
  var convertType = ConvertType.NONE

  /**
   * Sets the text field component.
   */
  override fun onAttach(attachEvent: AttachEvent) {
    field = createTextField()
    field.isEnabled = enabled
    add(field)
    if (hasAutofill) {
      TODO("AUTOFILL")
    }
    setValidationStrategy()
  }

  /**
   * Sets the validation strategy of a text field.
   */
  fun setValidationStrategy() {
    val binder = BeanValidationBinder(Any::class.java)
    val bindingBuilder = binder.forField(field)

    when (type) {
      Type.STRING -> bindingBuilder.withValidator(StringValidator(col, rows, !dynamicNewLine, convertType)).bind(
              { TODO() }, { _, _ -> TODO() })
      Type.INTEGER -> bindingBuilder.withValidator(IntegerValidator(minval, maxval)).bind({ TODO() },
                                                                                          { _, _ -> TODO() })
      Type.DECIMAL -> TODO()
      Type.DATE -> TODO()
      Type.TIME -> TODO()
      Type.MONTH -> TODO()
      Type.WEEK -> TODO()
      Type.TIMESTAMP -> TODO()
      Type.CODE -> TODO()
      else -> TODO()
    }
  }

  /**
   * Creates the attached text field component.
   * @return the attached text field component.
   */
  private fun createTextField(): AbstractSinglePropertyField<*, out Any?> {
    val text = createFieldComponent()
    // TODO
    return text
  }

  /**
   * Creates the input component according to field state.
   * @return the input component
   */
  protected fun createFieldComponent(): AbstractSinglePropertyField<*, out Any?> {
    var col = col
    val text = if (noEcho && rows == 1) {
      VTextField(col)
    } else if (rows > 1) {
      if (scanner) {
        col = 40
      }
      VTextAreaField().also {
        it.setRows(rows, visibleRows)
        it.cols = col
        it.setWordwrap(true)
        // if fixed new line mode is used, we remove scroll bar from text area
        it.setFixedNewLine(!dynamicNewLine)
      }
    } else if(isNumber()) {
      VIntegerField()
    } else {
      VTextField(col) // TODO
    }

    // TODO()
    var size = col
    // numeric fields are considered as monospaced fields
    if (isNumeric()) {
      size -= 1
    } else {
      // upper characters take wider place
      if (convertType == ConvertType.UPPER) {
        size += 2
      }
    }
    // let the place to the autofill icon
    if (hasAutofill) {
      size += 1
    }
    text.size = 1.coerceAtLeast(size)
    text.setMaxLength(col * rows)
    text.maxWidth = "" + col + "em" // TODO: temporary styling
    // add navigation handler.
    // text.addKeyDownHandler(TextFieldNavigationHandler.newInstance(this, text, rows > 1)) TODO
    return text
  }

  /**
   * Returns true if the field should contains only digits.
   * @return True if the field should contains only digits.
   */
  private fun isNumeric(): Boolean {
    return type != Type.STRING && type != Type.CODE
  }

  /**
   * Returns true if the field should contains only decimals.
   * @return True if the field should contains only decimals.
   */
  private fun isDecimal(): Boolean {
    return type == Type.DECIMAL
  }

  /**
   * Returns true if the field should contains only integers.
   * @return True if the field should contains only integers.
   */
  private fun isNumber(): Boolean {
    return type == Type.INTEGER
  }

  //---------------------------------------------------
  // CONVERT TYPE
  //---------------------------------------------------
  /**
   * The convert type to be applied to this text field.
   * The convert type can be to upper case, to lower case or to name.
   */
  enum class ConvertType {
    /**
     * no conversion.
     */
    NONE,

    /**
     * upper case conversion.
     */
    UPPER,

    /**
     * lower case conversion.
     */
    LOWER,

    /**
     * name conversion.
     */
    NAME
  }

  //---------------------------------------------------
  // FIELD TYPE
  //---------------------------------------------------
  /**
   * Text field types.
   */
  enum class Type {
    /**
     * String field
     */
    STRING,

    /**
     * Integer field
     */
    INTEGER,

    /**
     * Decimal field.
     */
    DECIMAL,

    /**
     * Date field.
     */
    DATE,

    /**
     * Time field.
     */
    TIME,

    /**
     * Month field.
     */
    MONTH,

    /**
     * Week field
     */
    WEEK,

    /**
     * Timestamp field.
     */
    TIMESTAMP,

    /**
     * Code field
     */
    CODE
  }
}
