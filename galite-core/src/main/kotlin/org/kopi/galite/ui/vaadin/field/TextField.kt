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

import org.kopi.galite.form.VCodeField
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VDateField
import org.kopi.galite.form.VField
import org.kopi.galite.form.VFixnumField
import org.kopi.galite.form.VMonthField
import org.kopi.galite.form.VStringField
import org.kopi.galite.form.VTimeField
import org.kopi.galite.form.VTimestampField
import org.kopi.galite.form.VWeekField

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.customfield.CustomField
import com.vaadin.flow.data.binder.BeanValidationBinder

/**
 * A text field component.
 *
 * @param model           The field model.
 * @param noEcho          Is it a password field ?
 * @param scanner         Is it a scanner field ?
 * @param noEdit          Is it a no edit field.
 * @param align           The field text alignment.
 * @param hasAutofill     Tells if the field has an autofill command
 */
class TextField(val model: VField,
                val noEcho: Boolean,
                val scanner: Boolean,
                val noEdit: Boolean,
                val align: Int,
                val hasAutofill: Boolean)
  : CustomField<Any?>(), HasStyle {

  val field: AbstractField<*, out Any?>

  /**
   * The column number.
   */
  val col: Int

  /**
   * The row number.
   */
  val rows: Int

  /**
   * The visible rows
   */
  val visibleRows: Int

  /**
   * Use default line transformer in multiple line field ? Dynamic new line means that
   * we use '\n' for line break. Fixed new line means that we complete the messing field columns with space
   * character instead of using line separator.
   */
  val dynamicNewLine: Boolean

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
   * The field enumeration for code fields.
   */
  var enumerations: Array<String>? = null

  /**
   * If the field has the auto complete feature.
   */
  var hasAutocomplete = false

  /**
   * The auto complete minimum length to begin querying for suggestions
   */
  var autocompleteLength = 0

  /**
   * The convert type to be applied to the field.
   */
  var convertType = ConvertType.NONE

  /**
   * The text validator for this field.
   */
  var validator: TextValidator? = null

  val listeners = mutableListOf<HasValue.ValueChangeListener<HasValue.ValueChangeEvent<*>>>()

  init {
    col = model.width
    rows = model.height
    visibleRows = if (model.height == 1) 1 else (model as VStringField).getVisibleHeight()
    dynamicNewLine = !scanner && model.getTypeOptions() and VConstants.FDO_DYNAMIC_NL > 0
    autocompleteLength = model.getAutocompleteLength()
    hasAutocomplete = model.hasAutocomplete()
    setFieldType()

    field = createTextField()
    field.isEnabled = enabled
    add(field)
    if (hasAutofill) {
      //TODO("AUTOFILL")
    }
    setValidator()
  }

  val maxLength: Int get() = col * rows

  override fun onAttach(attachEvent: AttachEvent) {
    listeners.forEach {
      field.addValueChangeListener(it)
    }
  }

  fun setFieldType() {
    // set field type according to the model
    when (model) {
      is VStringField -> {
        // string field
        type = Type.STRING
        convertType = _getConvertType()
      }
      is org.kopi.galite.form.VIntegerField -> {
        // integer field
        type = Type.INTEGER
        if(model.minValue != null) {
          minval = model.minValue.toDouble()
        }
        if(model.maxValue != null) {
          maxval = model.maxValue.toDouble()
        }
      }
      is VMonthField -> {
        // month field
        type = Type.MONTH
      }
      is VDateField -> {
        // date field
        type = Type.DATE
      }
      is VWeekField -> {
        // week field
        type = Type.WEEK
      }
      is VTimeField -> {
        // time field
        type = Type.TIME
      }
      is VCodeField -> {
        // code field
        type = Type.CODE
        enumerations = model.labels
      }
      is VFixnumField -> {
        // fixnum field
        type = Type.DECIMAL
        if(model.minValue != null) {
          minval = model.minValue.toDouble()
        }
        if(model.maxValue != null) {
          maxval = model.maxValue.toDouble()
        }
        maxScale = model.maxScale
        fraction = model.isFraction

      }
      is VTimestampField -> {
        // timestamp field
        type = Type.TIMESTAMP
      }
      else -> {
        throw IllegalArgumentException("unknown field model : " + model.javaClass.name)
      }
    }
    // add navigation handler TODO
  }

  /**
   * Returns the convert type for the string field.
   * @return The convert type for the string field.
   */
  private fun _getConvertType(): ConvertType =
          when ((model as VStringField).getTypeOptions() and VConstants.FDO_CONVERT_MASK) {
            VConstants.FDO_CONVERT_NONE -> ConvertType.NONE
            VConstants.FDO_CONVERT_UPPER -> ConvertType.UPPER
            VConstants.FDO_CONVERT_LOWER -> ConvertType.LOWER
            VConstants.FDO_CONVERT_NAME -> ConvertType.NAME
            else -> ConvertType.NONE
          }

  /**
   * Sets the validator of a text field.
   */
  fun setValidator() {
    val binder = BeanValidationBinder(String::class.java)
    val bindingBuilder = binder.forField(field)

    val validator = when (type) {
      Type.STRING -> StringValidator(col, rows, !dynamicNewLine, convertType, maxLength)
      Type.INTEGER -> IntegerValidator(minval, maxval, maxLength)
      Type.DECIMAL -> DecimalValidator(maxScale, fraction, col, minval, maxval, maxLength)
      Type.DATE -> DateValidator(maxLength)
      Type.TIME -> TimeValidator(maxLength)
      Type.MONTH -> MonthValidator(maxLength)
      Type.WEEK -> WeekValidator(maxLength)
      Type.TIMESTAMP -> TimestampValidator(maxLength)
      Type.CODE -> EnumValidator(enumerations, maxLength)
      else -> AllowAllValidator(maxLength)
    }

    this.validator = validator

    bindingBuilder.withValidator(validator)
            .bind({ TODO() }, { _, _ -> TODO() })
  }

  /**
   * Creates the attached text field component.
   * @return the attached text field component.
   */
  private fun createTextField(): AbstractField<*, out Any?> {
    val text = createFieldComponent()
    if (noEdit) {
      text.isReadOnly = true
    }
    // TODO
    return text
  }

  /**
   * Creates the input component according to field state.
   * @return the input component
   */
  protected fun createFieldComponent(): AbstractField<*, out Any?> {
    var col = col
    val text = if (noEcho && rows == 1) {
      VPasswordField(col)
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
    } else if(type == Type.INTEGER) {
      VIntegerField(col, minval!!.toInt(), maxval!!.toInt())
    } else if(isDecimal()) {
      VFixnumField(col, maxScale, minval, maxval, fraction)
    } else if(type == Type.CODE) {
      VCodeField(enumerations)
    }  else if(type == Type.TIME) {
      VTimeField()
    } else if(type == Type.TIMESTAMP) {
      VTimeStampField()
    } else if(isDate()) {
      VDateField()
    } else {
      VTextField(col).also {
        if(type == Type.WEEK) {
          it.setInputType("week")
        } else if (type == Type.MONTH) {
          it.setInputType("month")
        }
      }
      // TODO
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
    text.setMaxLength(maxLength)
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
   * Returns true if the field should contains only date.
   * @return True if the field should contains only date.
   */
  private fun isDate(): Boolean {
    return type == Type.DATE
  }

  /**
   * Sets the the blink state of the field.
   * @param blink The blink state.
   */
  fun setBlink(blink: Boolean) {
    if (blink) {
      classNames.add("text-field-blink")
    } else {
      classNames.remove("text-field-blink")
    }
  }

  /**
   * Returns if he field is read only.
   */
  override fun isReadOnly(): Boolean {
    return field.isReadOnly
  }

  override fun setPresentationValue(newPresentationValue: Any?) {
    field.value = newPresentationValue
  }

  override fun generateModelValue(): Any? = field.value

  /**
   * Registers a text change listener
   * @param l The text change listener.
   */
  fun addTextValueChangeListener(l: HasValue.ValueChangeListener<HasValue.ValueChangeEvent<*>>) {
    listeners.add(l)
  }

  /**
   * Communicates the widget text to server side.
   */
  internal fun sendTextToServer() {
    // TODO
  }

  /**
   * Sends the dirty values to the server side.
   * @param values The field values per record.
   */
  internal fun sendDirtyValuesToServer(values: Map<Int?, String?>?) {
    // TODO
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
