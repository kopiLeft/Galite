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
package org.kopi.galite.visual.ui.vaadin.field

import java.util.Optional

import org.kopi.galite.visual.type.Date

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.DomEvent
import com.vaadin.flow.component.EventData
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.ShortcutRegistration
import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.shared.Registration

/**
 * An Date field.
 */
class VDateField : InputTextField<DatePickerLight>(DatePickerLight()), KeyNotifier {

  fun addDateValueChangeListener(listener: (fromClient: Boolean) -> Unit) {
    internalField.addValueChangeListener {
      listener(it.isFromClient)
    }
    internalField.addPickerListener {
      if(it.value.isNotEmpty()) {
        val date = Date.parse(it.value, "yyyy-MM-dd").toString()

        if(content.value != date) {
          // Synchronize value with textfield
          content.value = date
        }
      }
      listener(it.isFromClient)
    }
  }

  override fun setPresentationValue(newPresentationValue: String?) {
    val date = TimestampValidator.parseDate(newPresentationValue.orEmpty())

    content.value = date?.toString() ?: newPresentationValue.orEmpty()
  }
}

@Tag("date-picker-light")
@JsModule("./src/date-picker-light.js")
@CssImport(value = "./styles/galite/datetime.css", themeFor = "vaadin-text-field")
class DatePickerLight : AbstractField<TextField, String>(null), HasComponents,
  Focusable<TextField>, HasSize {
  private val icon = VaadinIcon.CALENDAR_O.create()
  val textField: TextField = TextField()

  init {
    element.setProperty("attrForValue", "value")
    element.setProperty("autoOpenDisabled", true)
    textField.element.themeList.add("galite-date")
    textField.className = "input"
    textField.suffixComponent = icon
    textField.isClearButtonVisible = true
    textField.isPreventInvalidInput = true
    textField.pattern = "[0-9/\\.]*"
    textField.maxLength = 10

    icon.style["cursor"] = "pointer"
    icon.element.executeJs(
      """
              this.addEventListener("click", event => {
                    $0.opened = true;
              })
              """,
      element
    )

    add(textField)
  }

  override fun setValue(value: String?) {
    textField.value = value
  }

  override fun getValue(): String? {
    return textField.value
  }

  override fun addValueChangeListener(listener: HasValue.ValueChangeListener<in ComponentValueChangeEvent<TextField, String>>?): Registration {
    return textField.addValueChangeListener(listener)
  }

  override fun setPresentationValue(newPresentationValue: String?) {
    textField.value = newPresentationValue
  }

  override fun setReadOnly(readOnly: Boolean) {
    textField.isReadOnly = readOnly
  }

  override fun isReadOnly(): Boolean = textField.isReadOnly

  override fun setRequiredIndicatorVisible(requiredIndicatorVisible: Boolean) {
    textField.isRequiredIndicatorVisible = requiredIndicatorVisible
  }

  override fun isRequiredIndicatorVisible(): Boolean = textField.isRequiredIndicatorVisible

  override fun setTabIndex(tabIndex: Int) {
    textField.tabIndex = tabIndex
  }

  override fun getTabIndex(): Int {
    return textField.tabIndex
  }

  override fun focus() {
    textField.focus()
  }

  override fun blur() {
    textField.blur()
  }

  override fun addFocusShortcut(
    key: Key?,
    vararg keyModifiers: KeyModifier?
  ): ShortcutRegistration? {
    return textField.addFocusShortcut(key, *keyModifiers)
  }

  override fun setWidth(width: String?) {
    textField.width = width
  }

  override fun setWidth(width: Float, unit: com.vaadin.flow.component.Unit?) {
    textField.setWidth(width, unit)
  }

  override fun setMinWidth(minWidth: String?) {
    textField.minWidth = minWidth
  }

  override fun setMinWidth(minWidth: Float, unit: com.vaadin.flow.component.Unit?) {
    textField.setMinWidth(minWidth, unit)
  }

  override fun setMaxWidth(maxWidth: String?) {
    textField.maxWidth = maxWidth
  }

  override fun setMaxWidth(maxWidth: Float, unit: com.vaadin.flow.component.Unit?) {
    textField.setMaxWidth(maxWidth, unit)
  }

  override fun getWidth(): String? = textField.width

  override fun getMinWidth(): String? = textField.minWidth

  override fun getMaxWidth(): String? = textField.maxWidth

  override fun getWidthUnit(): Optional<com.vaadin.flow.component.Unit?>? = textField.widthUnit

  override fun setHeight(height: String?) {
    textField.height = height
  }

  override fun setHeight(height: Float, unit: com.vaadin.flow.component.Unit?) {
    textField.setHeight(height, unit)
  }

  override fun setMinHeight(minHeight: String?) {
    textField.minHeight = minHeight
  }

  override fun setMinHeight(minHeight: Float, unit: com.vaadin.flow.component.Unit?) {
    textField.setMinHeight(minHeight, unit)
  }

  override fun setMaxHeight(maxHeight: String?) {
    textField.setMaxHeight(maxHeight)
  }

  override fun setMaxHeight(maxHeight: Float, unit: com.vaadin.flow.component.Unit?) {
    textField.setMaxHeight(maxHeight, unit)
  }

  override fun getHeight(): String? = textField.height

  override fun getMinHeight(): String? = textField.minHeight

  override fun getMaxHeight(): String? = textField.maxHeight

  override fun getHeightUnit(): Optional<com.vaadin.flow.component.Unit?>? = textField.heightUnit

  override fun setSizeFull() {
    textField.setSizeFull()
  }

  override fun setWidthFull() {
    textField.setWidthFull()
  }

  override fun setHeightFull() {
    textField.setHeightFull()
  }

  override fun setSizeUndefined() {
    textField.setSizeUndefined()
  }

  @DomEvent("value-changed")
  class ValueChanged(source: DatePickerLight,
                     fromClient: Boolean,
                     @EventData("event.detail.value") val value: String)
    : ComponentEvent<DatePickerLight>(source, fromClient)

  fun addPickerListener(listener: ComponentEventListener<ValueChanged>): Registration {
    return addListener(ValueChanged::class.java, listener)
  }
}
