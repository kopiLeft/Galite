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
import org.kopi.galite.visual.ui.vaadin.base.Utils
import org.kopi.galite.visual.visual.Color
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

  companion object {
    object ColorsIterator: Iterator<String> {
      val list = listOf("tomato", "orange", "dodgerblue", "mediumseagreen", "gray", "slateblue", "violet")

      //"#4285F4", "#1967D2", "#E6705C", "#FBBC04", "#F72A25", "#34A853", "#E6E434", "#188038", "#C3CFE6", "#E66CDF")
      val size = list.size
      var i = 0
      override fun hasNext(): Boolean {
        return i < size
      }

      override fun next(): String {
        i++
        if (i == size) {
          i = 0
        }
        return list[i]
      }
    }

    val color: String get() = ColorsIterator.next()
  }
}
