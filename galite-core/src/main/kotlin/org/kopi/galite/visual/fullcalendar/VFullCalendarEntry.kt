/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.visual.fullcalendar

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

import org.kopi.galite.visual.VColor
import org.kopi.galite.visual.form.VField

/**
 * Represents a full calendar record.
 *
 * @param start     the timestamp as start of the entry
 * @param end       the timestamp as end of the entry
 * @param values    the values of the form fields except the [start] and [end].
 */
data class VFullCalendarEntry(val start: LocalDateTime,
                              val end: LocalDateTime,
                              val values: MutableMap<VField, Any?>) {

  constructor(date: LocalDate,
              start: LocalTime,
              end: LocalTime,
              values: MutableMap<VField, Any?>)
       : this(LocalDateTime.of(date, start),
              LocalDateTime.of(date, end),
              values)

  val startDate: LocalDate get() = LocalDate.from(start)

  val endDate: LocalDate get() = LocalDate.from(end)

  val description: String?
    get() {
      val fields = values.keys
      var priority = 0
      var descriptionField: VField = fields.first()

      for (field in fields) {
        if(field.getPriority() > priority) {
          priority = field.getPriority()
          descriptionField = field
        }
      }

      return values[descriptionField]?.toString()
    }

  fun getColor(record: Int) : VColor = colors[record % colors.size]

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is VFullCalendarEntry) return false

    if (start != other.start || end != other.end) return false
    if (values.size != other.values.size) return false
    for (key in values.keys) {
      if(values[key] != other.values[key]) {
        return false
      }
    }

    return true
  }

  override fun hashCode(): Int {
    var result = start.hashCode()
    result = 31 * result + end.hashCode()
    result = 31 * result + values.hashCode()
    return result
  }

  fun copy(start: LocalDateTime, end: LocalDateTime): VFullCalendarEntry = VFullCalendarEntry(start, end, values)

  companion object {
    val colors = listOf(
      VColor(255, 99, 71), // tomato
      VColor(255,165,0), // orange
      VColor(30,144,255), // dodger blue
      VColor(60,179,113), // medium sea green
      VColor(128,128,128), // gray
      VColor(106,90,205), // slate blue
      VColor(238,130,238) // violet
    )
  }
}
