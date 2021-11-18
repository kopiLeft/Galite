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
package org.kopi.galite.visual.ui.vaadin.calendar

import java.time.LocalDate

import org.vaadin.stefan.fullcalendar.CalendarViewImpl
import org.vaadin.stefan.fullcalendar.FullCalendar
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * Creates a new abstract full calendar
 * @param type The data series model.
 */
@CssImport("./styles/galite/fullcalendar.css")
abstract class DAbstractFullCalendar protected constructor(private val type: CalendarViewImpl) : VerticalLayout() {

  protected val calendar: FullCalendar = FullCalendarBuilder.create().build()
  private val datePicker = DatePicker()
  protected val header = HorizontalLayout()

  init {
    super.setSizeFull()
    calendar.setSizeFull()
    calendar.changeView(type)
    // adding data to full calendar
    build()
    // adding header to full calendar
    setHeader()
    // adding full calendar to layout
    super.add(calendar)

    // adding listener
    setDateListener()
  }

  /**
   * Adding button that redirect user to current day in the full calendar
   * Adding date picker that allow user to choice date
   */
  fun setHeader() {
    header.setId("fullCalendar-header")
    datePicker.setId("fullCalendar-date-picker")

    header.add(datePicker)
    super.add(header)
  }

  /**
   * Adding listener on date picker allow user to navigation to a specific date
   */
  fun setDateListener() {
    datePicker.addValueChangeListener { event: AbstractField.ComponentValueChangeEvent<DatePicker?, LocalDate?> ->
      if (event.value != null) {
        calendar.gotoDate(event.value)
      }
    }
  }

  fun addEntryListener() {
    calendar.addEntryClickedListener {
      // TODO
    }
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  abstract fun build()
}
