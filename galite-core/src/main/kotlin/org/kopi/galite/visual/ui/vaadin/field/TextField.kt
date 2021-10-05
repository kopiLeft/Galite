/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.field

import org.kopi.galite.visual.form.VCodeField
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VDateField
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.form.VFixnumField
import org.kopi.galite.visual.form.VMonthField
import org.kopi.galite.visual.form.VStringField
import org.kopi.galite.visual.form.VTimeField
import org.kopi.galite.visual.form.VTimestampField
import org.kopi.galite.visual.form.VWeekField
import org.kopi.galite.visual.ui.vaadin.base.Styles
import org.kopi.galite.visual.ui.vaadin.event.TextFieldListener
import org.kopi.galite.visual.ui.vaadin.form.DTextField
import org.kopi.galite.visual.ui.vaadin.form.KeyNavigator

import com.flowingcode.vaadin.addons.ironicons.IronIcons
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.icon.IronIcon
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder

/**
 * A text field component.
 *
 * @param model           The field model.
 * @param noEcho          Is it a password field ?
 * @param scanner         Is it a scanner field ?
 * @param noEdit          Is it a no edit field.
 * @param align           The field text alignment.
 * @param hasAutofill     Tells if the field has an autofill command
 * @param fieldParent     parent of this text field
 */
@CssImport.Container(value = [
  CssImport("./styles/galite/textfield.css"),
  CssImport(value = "./styles/galite/textfield.css", themeFor = "vaadin-text-field")
])
class TextField(val model: VField,
                val noEcho: Boolean,
                val scanner: Boolean,
                val noEdit: Boolean,
                val align: Int,
                val hasAutofill: Boolean,
                val fieldParent: DTextField
) : AbstractField<Any?>(), HasStyle {

  val inputField: InputTextField<*>

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

  private var autofill: IronIcon? = null

  internal var lastCommunicatedValue = ""

  private val textFieldListeners = mutableListOf<TextFieldListener>()

  init {
    className = Styles.TEXT_FIELD
    col = model.width
    rows = model.height
    visibleRows = if (model.height == 1) 1 else (model as VStringField).getVisibleHeight()
    dynamicNewLine = !scanner && model.getTypeOptions() and VConstants.FDO_DYNAMIC_NL > 0
    autocompleteLength = model.getAutocompleteLength()
    hasAutocomplete = model.hasAutocomplete()
    setFieldType()

    inputField = createTextField()
    inputField.isEnabled = enabled
    add(inputField)
    if (hasAutofill) {
      autofill = IronIcons.ARROW_DROP_DOWN.create()
      autofill!!.style["cursor"] = "pointer" // TODO: move to css
      autofill!!.addClickListener {
        fireAutofill()
      }
      inputField.suffixComponent = autofill
      autofill!!.isVisible = false
      var focused = false
      inputField.addFocusListener {
        focused = true
        autofill?.isVisible = true
      }
      inputField.addBlurListener {
        focused = false
        autofill?.isVisible = false
      }
      inputField.element.addEventListener("mouseover") {
        autofill?.isVisible = true
      }
      inputField.element.addEventListener("mouseout") {
        if (!focused) {
          autofill?.isVisible = false
        }
      }
    }
  }

  val maxLength: Int get() = col * rows

  fun setFieldType() {
    // set field type according to the model
    when (model) {
      is VStringField -> {
        // string field
        type = Type.STRING
        convertType = _getConvertType()
      }
      is org.kopi.galite.visual.form.VIntegerField -> {
        // integer field
        type = Type.INTEGER
        if (model.minValue != null) {
          minval = model.minValue.toDouble()
        }
        if (model.maxValue != null) {
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
        if (model.minValue != null) {
          minval = model.minValue.toDouble()
        }
        if (model.maxValue != null) {
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
  fun setValidator(field: InputTextField<*>) {
    val binder = Binder(String::class.java)
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

    bindingBuilder.withValidator(validator)
            .bind({ TODO() }, { _, _ -> TODO() })

    this.validator = validator
    field.setTextValidator(validator)
  }

  /**
   * Sets the text transformation applied to the text widget according to the convert text
   * @param text The text widget.
   */
  private fun setTextTransform(text: InputTextField<*>) {
    when (convertType) {
      ConvertType.UPPER -> text.addClassName("transform-uppercase")
      ConvertType.LOWER -> text.addClassName("transform-lowercase")
      ConvertType.NAME -> text.addClassName("transform-capitalize")
      ConvertType.NONE -> text.addClassName("transform-none")
    }
  }

  /**
   * Creates the attached text field component.
   * @return the attached text field component.
   */
  private fun createTextField(): InputTextField<*> {
    val text = createFieldComponent()

    setValidator(text)
    setTextTransform(text)
    if (noEdit) {
      text.setTextValidator(NoeditValidator(maxLength))
      text.isReadOnly = true
    }

    return text
  }

  /**
   * Creates the input component according to field state.
   * @return the input component
   */
  private fun createFieldComponent(): InputTextField<*> {
    var col = col
    val size = getFieldSize()
    val text = if (noEcho && rows == 1) {
      VPasswordField(col)
    } else if (rows > 1) {
      if (scanner) {
        col = 40
      }
      VTextAreaField().also {
        it.setRows(rows, visibleRows)
        it.width = (col * 10).toString() + "px"
        it.setWordwrap(true)
        // if fixed new line mode is used, we remove scroll bar from text area
        it.setFixedNewLine(!dynamicNewLine)
      }
    } else if(!fieldParent.hasAction) {
      when (type) {
        Type.INTEGER -> VIntegerField(col, minval!!, maxval!!)
        Type.DECIMAL -> VFixnumField(col, maxScale, minval, maxval, fraction)
        Type.CODE -> VCodeField(enumerations)
        Type.TIME -> VTimeField()
        Type.TIMESTAMP -> VTimeStampField()
        Type.DATE -> VDateField()
        else -> InputTextField(TextField())
      }
    } else {
      VInputButtonField(size)
    }

    text.size = size
    text.setMaxLength(maxLength)
    text.width = "" + size + "ch" // TODO: temporary styling
    text.element.style["box-sizing"] = "unset"
    text.setHasAutocomplete(model.hasAutocomplete())
    // add navigation handler.
    TextFieldNavigationHandler.createNavigator(text, rows > 1)
    textFieldListeners.add(KeyNavigator(model, text))
    return text
  }

  private fun getFieldSize(): Int {
    var size = col

    // upper characters take wider place
    if (convertType == ConvertType.UPPER) {
      size += 2
    }

    size += when (type) {
      Type.TIMESTAMP -> {
        6
      }
      Type.TIME -> {
        5
      }
      Type.DATE -> {
        7
      }
      else -> {
        3
      }
    }

    // let the place to the autofill icon
    if (hasAutofill) {
      size += 1
    }

    return 1.coerceAtLeast(size)
  }

  /**
   * Returns true if the field should contains only digits.
   * @return True if the field should contains only digits.
   */
  private fun isNumeric(): Boolean {
    return type != Type.STRING && type != Type.CODE
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
    return inputField.isReadOnly
  }

  override fun setPresentationValue(newPresentationValue: Any?) {
    inputField.value = newPresentationValue.toString()
  }

  override fun getValue(): String? {
    return inputField.value
  }

  override fun setValue(value: Any?) {
    inputField.setValue(value)
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

  /**
   * Marks the connector to be dirty for the given record.
   * This means that before performing any action, the value of this field
   * for the given record should be communicated to the server.
   * @param rec The active record.
   * @param value The new field value.
   */
  internal fun markAsDirty(rec: Int, value: String?) {
    (parent.get() as Field).markAsDirty(rec, value)
  }

  /**
   * Returns `true` if the last communicated value is different from the widget value.
   * @return `true` if the last communicated value is different from the widget value.
   */
  internal fun needsSynchronization(): Boolean {
    return lastCommunicatedValue != value
  }

  /**
   * Fires a print form event on this text field.
   */
  internal fun firePrintForm() {
    for (l in textFieldListeners) {
      l.printForm()
    }
  }

  /**
   * Fires a previous entry event on this text field.
   */
  internal fun firePreviousEntry() {
    for (l in textFieldListeners) {
      l.previousEntry()
    }
  }

  /**
   * Fires a next entry event on this text field.
   */
  internal fun fireNextEntry() {
    for (l in textFieldListeners) {
      l.previousEntry()
    }
  }

  /**
   * Fires a goto next block event on this text field.
   */
  internal fun fireGotoNextBlock() {
    for (l in textFieldListeners) {
      l.gotoNextBlock()
    }
  }

  private fun fireAutofill() {
    for (l in textFieldListeners) {
      l.autofill()
    }
  }

  override fun focus() {
    inputField.parentWindow?.lasFocusedField = this
    super.focus()
  }

  override fun addFocusListener(function: () -> Unit) {
    inputField.addFocusListener {
      function()
    }
  }

  override fun getContent(): Component = inputField

  /**
   * Sets the field color properties.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  fun setColor(foreground: String?, background: String?) {
    inputField.setColor(foreground, background)
  }

  /**
   * Checks if the content of this field is empty.
   * @return `true` if this field is empty.
   */
  override val isNull get(): Boolean = this.inputField.isNull

  /**
   * Checks the value of this text field.
   * @param rec The active record.
   * @throws CheckTypeException When field content is not valid
   */
  override fun checkValue(rec: Int) {
    inputField.checkValue(rec)
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
