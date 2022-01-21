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

import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.datetimepicker.DateTimePicker
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.timepicker.TimePicker

/**
 * A timestamp field.
 */
@CssImport.Container(value = [
  CssImport(value = "./styles/galite/datetime.css", themeFor = "vaadin-date-time-picker-date-text-field"),
  CssImport(value = "./styles/galite/datetime.css", themeFor = "vaadin-date-time-picker-time-text-field"),
  CssImport(value = "./styles/galite/datetime.css", themeFor = "vaadin-date-time-picker-date-picker"),
  CssImport(value = "./styles/galite/datetime.css", themeFor = "vaadin-date-time-picker-time-picker"),
  CssImport(value = "./styles/galite/datetime.css", themeFor = "vaadin-date-time-picker")
])
class VTimeStampField : InputTextField<DateTimePicker>(DateTimePicker()), KeyNotifier {

  init {
    internalField.isAutoOpen = false

    // Workaround for autoselection on focus
    val children = internalField.children.toArray()
    val datePicker = (children.single { it is DatePicker } as DatePicker)
    val timePicker = (children.single { it is TimePicker } as TimePicker)

    element.themeList.add("galite-timestamp")

    datePicker.element.executeJs(
      """
              this.addEventListener("focus", event => {
                    this.focusElement.inputElement.select()
              })
      """
    )
    timePicker.element.executeJs(
      """
              this.addEventListener("focus", event => {
                    this.focusElement.inputElement.select()
              })
      """
    )
  }

  override fun setPresentationValue(newPresentationValue: String?) {
    content.value = TimestampValidator
      .parseTimestamp(newPresentationValue.orEmpty())
      ?.sqlTimestamp
      ?.toLocalDateTime()
  }
}
