/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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

import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.type.Date
import org.kopi.galite.visual.type.Time
import org.kopi.galite.visual.type.Timestamp
import org.kopi.galite.visual.visual.VColor

data class VFullCalendarEntry(val start: Timestamp,
                              val end: Timestamp,
                              val values: MutableMap<VField, Any?>) {

  constructor(date: Date,
              start: Time,
              end: Time,
              values: MutableMap<VField, Any?>)
       : this(Timestamp.from(date, start),
              Timestamp.from(date, end),
              values)

  val startDate: Date get() = Date(start.toCalendar())

  val endDate: Date get() = Date(end.toCalendar())

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
