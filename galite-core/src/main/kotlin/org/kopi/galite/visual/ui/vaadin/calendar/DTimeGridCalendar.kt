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
import java.time.DayOfWeek
import java.time.LocalTime

import org.vaadin.stefan.fullcalendar.CalendarViewImpl
import org.vaadin.stefan.fullcalendar.Entry

/**
 * Creates a new time grid full calendar
 * @param dataSeries The type of the full calendar.
 */
class DTimeGridCalendar (private val dataSeries: Array<Array<*>>) : DAbstractFullCalendar(CalendarViewImpl.TIME_GRID_WEEK) {
  init {
    calendar.setNowIndicatorShown(true)
    calendar.setFirstDay(DayOfWeek.MONDAY)
  }
  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun build() {
    val data = arrayOf(Task(LocalDate.of(2021, 11, 17), LocalDate.of(2021, 11, 17), LocalTime.of(10, 0, 0), LocalTime.of(10, 30, 0), "desc 1", "desc 2"),
                       Task(LocalDate.of(2021, 11, 17), LocalDate.of(2021, 11, 17), LocalTime.of(17, 0, 0), LocalTime.of(17, 30, 0), "desc 1", "desc 2"),
                       Task(LocalDate.of(2021, 11, 19), LocalDate.of(2021, 11, 19), LocalTime.of(16, 0, 0), LocalTime.of(17, 30, 0), "desc 1", "desc 2"),
    )
    // to change with dataSeries !!
    data.forEach { task ->

      val entry = Entry()
      entry.title = task.description1 + "-" + task.description2
      entry.setStart(task.startDate.atTime(task.startTime), calendar.timezone)
      entry.setEnd(task.endDate.atTime(task.endTime), calendar.timezone)
      entry.color = "#ff3333"

      calendar.addEntries(entry)
    }
  }
}

class Task(val startDate: LocalDate,
           val endDate: LocalDate,
           val startTime : LocalTime,
           val endTime : LocalTime,
           val description1 : String,
           val description2 : String)
