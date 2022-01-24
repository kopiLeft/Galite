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

import java.time.LocalTime

import com.vaadin.flow.component.KeyNotifier
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.timepicker.TimePicker

/**
 * A time field.
 */
@CssImport(value = "./styles/galite/datetime.css", themeFor = "vaadin-time-picker-text-field")
class VTimeField : InputTextField<TimePicker>(TimePicker()), KeyNotifier {

  init {
    internalField.isAutoOpen = false
    internalField.element.setProperty("pattern", "[0-9:]*")
    internalField.element.setProperty("preventInvalidInput", true)
    element.themeList.add("galite-time")

    // Workaround for autoselection on focus
    element.executeJs(
      """
              this.addEventListener("focus", event => {
                    this.focusElement.inputElement.select()
              })
      """
    )
  }

  override fun setPresentationValue(newPresentationValue: String?) {
    val time = TimestampValidator.parseTime(newPresentationValue.orEmpty())

    content.value = if(time != null) {
      LocalTime.of(time.hours, time.minutes, time.seconds)
    } else {
      null
    }
  }
}
