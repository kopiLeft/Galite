/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.tests.chart

import org.kopi.galite.chart.Chart
import java.util.Locale

object ChartSample: Chart()  {
  override val locale = Locale.FRANCE
  override val title = "chart for test"

  val measure1 = measure(IntTestType(10)) {
    label = "measure1"
  }

  val measure2 = measure(IntTestType(10)) {
    label = "measure2"
  }

  val city = dimension(StringTestType(10)) {
    label = "dimension"
  }

  init {
    city.add("Tunis") {
      this[measure1] = 12
      this[measure2] = 20
    }

    city.add("Bizerte") {
      this[measure1] = 45
    }

  }
}
