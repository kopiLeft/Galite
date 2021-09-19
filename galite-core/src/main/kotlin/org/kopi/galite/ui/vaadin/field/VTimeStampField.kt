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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.datetimepicker.DateTimePicker
import com.vaadin.flow.component.dependency.CssImport

/**
 * A timestamp field.
 */
@CssImport.Container(value = [
  CssImport(value = "./styles/galite/datetime.css", themeFor = "vaadin-date-time-picker-date-text-field"),
  CssImport(value = "./styles/galite/datetime.css", themeFor = "vaadin-date-time-picker-time-text-field"),
  CssImport(value = "./styles/galite/datetime.css", themeFor = "vaadin-date-time-picker-date-picker"),
  CssImport(value = "./styles/galite/datetime.css", themeFor = "vaadin-date-time-picker-time-picker")
])
class VTimeStampField : InputTextField<DateTimePicker>(DateTimePicker()), KeyNotifier {

  init {
    internalField.isAutoOpen = false
  }

  override fun setPresentationValue(newPresentationValue: String?) {
    content.value = if(newPresentationValue != null && newPresentationValue.isNotEmpty()) {
      val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
      LocalDateTime.parse(newPresentationValue, formatter)
    } else {
      null
    }
  }
}
