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
import java.util.Calendar

import org.kopi.galite.visual.fullcalendar.VFullCalendarBlock
import org.kopi.galite.visual.fullcalendar.VFullCalendarEntry
import org.kopi.galite.visual.type.Date
import org.kopi.galite.visual.type.Timestamp
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.base.Utils
import org.kopi.galite.visual.visual.Action
import org.kopi.galite.visual.visual.MessageCode
import org.kopi.galite.visual.visual.VExecFailedException
import org.vaadin.stefan.fullcalendar.CalendarViewImpl
import org.vaadin.stefan.fullcalendar.Entry
import org.vaadin.stefan.fullcalendar.FullCalendar
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout

/**
 * Creates a new abstract full calendar
 * @param type The data series model.
 */
@CssImport("./styles/galite/fullcalendar.css")
open class DAbstractFullCalendar protected constructor(protected val model: VFullCalendarBlock,
                                                       private val type: CalendarViewImpl)
  : VerticalLayout() {

  protected val calendar: FullCalendar = FullCalendarBuilder.create().build()
  protected val datePicker = DatePicker(LocalDate.now())
  protected val header = HorizontalLayout()
  protected var currentUI: UI? = null

  init {
    width = "150vh"
    height = "70vh"
    calendar.setSizeFull()
    calendar.changeView(type)
    // adding data to full calendar
    updateEntries()
    // adding header to full calendar
    setHeader()
    // adding full calendar to layout
    add(calendar)

    // adding listener
    setDatePickerListeners()
    addEntryListeners()
  }

  /**
   * Refresh full calendar data.
   */
  fun refreshEntries() {
    removeAllEntries()
    updateEntries()
  }

  fun getSelectedDate() : Date {
    return Date(datePicker.value)
  }

  fun goToDate(date: Date) {
    access(currentUI) {
      calendar.gotoDate(LocalDate.of(date.year, date.month, date.day))
    }
  }

  fun enter() {
    access(currentUI) {
      datePicker.focus()
    }
  }

  private fun updateEntries() {
    model.form.performAsyncAction(object : Action("Fetch entries") {
      override fun execute() {
        val queryList = model.fetchEntries(Date(datePicker.value))
        updateEntries(queryList)
      }
    })
  }

  private fun updateEntries(queryList: List<VFullCalendarEntry>) {
    val entries = queryList.map { fcEntry ->
      val record = fcEntry.values[model.idField] as Int
      val entry = FullCalendarEntry(record)
      val start = fcEntry.start.sqlTimestamp.toLocalDateTime()
      val end = fcEntry.end.sqlTimestamp.toLocalDateTime()

      entry.title = fcEntry.description
      entry.setStart(start, calendar.timezone)
      entry.setEnd(end, calendar.timezone)
      entry.color = Utils.toString(fcEntry.getColor(record))

      entry
    }

    access(currentUI) {
      calendar.addEntries(entries)
    }
  }

  /**
   * Adding button that redirect user to current day in the full calendar
   * Adding date picker that allow user to choice date
   */
  private fun setHeader() {
    header.setId("fullCalendar-header")
    datePicker.setId("fullCalendar-date-picker")

    header.add(datePicker)
    super.add(header)
  }

  /**
   * Adding listener on date picker allow user to navigation to a specific date
   */
  private fun setDatePickerListeners() {
    datePicker.addValueChangeListener { event ->
      if (event.isFromClient) {
        model.form.performAsyncAction(object : Action("Selected date changed") {
          override fun execute() {
            model.dateChanged(Date(event.oldValue), Date(event.value))
          }
        })
      }
    }
  }

  private fun addEntryListeners() {
    // Edit entry
    calendar.addEntryClickedListener {
      model.form.performAsyncAction(object : Action("entry clicked") {
        override fun execute() {
          model.openForEdit((it.entry as FullCalendarEntry).record)
        }
      })
    }
    calendar.addEntryResizedListener {
      model.form.performAsyncAction(object : Action("entry time edited") {
        override fun execute() {
          val newEntry = it.applyChangesOnEntry()
          val newStart = Timestamp.from(newEntry.start)
          val newEnd = Timestamp.from(newEntry.end)

          check(newStart, newEnd)
          model.openForEdit((it.entry as FullCalendarEntry).record, newStart, newEnd)
        }
      })
    }
    calendar.addEntryDroppedListener {
      model.form.performAsyncAction(object : Action("entry time edited") {
        override fun execute() {
          val newEntry = it.applyChangesOnEntry()
          val newStart = Timestamp.from(newEntry.start)
          val newEnd = Timestamp.from(newEntry.end)

          check(newStart, newEnd)
          model.openForEdit((it.entry as FullCalendarEntry).record, newStart, newEnd)
        }
      })
    }

    // Insert new entry
    calendar.addTimeslotsSelectedListener {
      model.form.performAsyncAction(object : Action("new entry") {
        override fun execute() {
          val start = Timestamp.from(it.startDateTime)
          val end = Timestamp.from(it.endDateTime)

          check(start, end)
          model.openForEdit(start, end)
        }
      })
    }
  }

  @Deprecated("to be removed when dateField is not supported")
  private fun check(startDateTime: Timestamp, endDateTime: Timestamp) {
    val start = startDateTime.toCalendar()
    val end = endDateTime.toCalendar()

    if(model.dateField != null && start.get(Calendar.DAY_OF_WEEK) != end.get(Calendar.DAY_OF_WEEK)) {
      updateEntries()
      throw VExecFailedException(MessageCode.getMessage("VIS-00070"))
    }
  }

  private fun removeAllEntries() {
    access(currentUI) {
      calendar.removeAllEntries()
    }
  }

  override fun onAttach(attachEvent: AttachEvent) {
    currentUI = attachEvent.ui
  }

  class FullCalendarEntry(val record: Int) : Entry()
}
