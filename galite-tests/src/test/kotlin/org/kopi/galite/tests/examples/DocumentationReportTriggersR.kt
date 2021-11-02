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
package org.kopi.galite.tests.examples

import java.util.Locale

import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.report.FieldAlignment
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.report.Triggers
import org.kopi.galite.visual.type.Decimal

/**
 * test format & align
 * test field triggers [compute]
 * report trigger
 *
 */
class DocumentationReportTriggersR : Report() {
  override val locale = Locale.UK

  override val title = "Clients_Report"

  //test hiiden field
  val hiddenField = field(STRING(10)) {
    label = "hidden Field"
    hidden
  }

  // test to upper Case format + align left
  val name = field(STRING(25)) {
    label = "Name"
    help = "The name"
    align = FieldAlignment.LEFT
    format { value ->
      value.toUpperCase()
    }
  }

  // test to lower Case format + align right
  val name2 = field(STRING(25)) {
    label = "Name 2"
    help = "The name 2"
    align = FieldAlignment.RIGHT
    format { value ->
      value.toLowerCase()
    }
  }

  //test avgInteger
  val age = field(INT(25)) {
    label = "age with avg"
    help = "age"
    compute {
      // Computes the average of ages
      Triggers.avgInteger(this)
    }
  }

  //test sumInteger
  val age2 = field(INT(25)) {
    label = "age2 with sum"
    help = "age2"
    compute {
      // Computes the sum of ages
      Triggers.sumInteger(this)
    }
  }

  //test sumDecimal
  val salary = field(DECIMAL(20, 10)) {
    label = "salary with sum"
    help = "salary"
    compute {
      // Computes the sum of salary
      Triggers.sumDecimal(this)
    }
  }

  //test avgDecimal
  val salary2 = field(DECIMAL(20, 10)) {
    label = "salary with avg"
    help = "salary"
    compute {
      // Computes the sum of salary2
      Triggers.avgDecimal(this)
    }
  }

  //test CodeDomain
  val codeDomain = field(Days) {
    label = "code domain"
    help = "code domain"
  }

  object Days: CodeDomain<Int>() {
    init {
      "Sunday" keyOf 1
      "Monday" keyOf 2
      "Tuesday" keyOf 3
      "Wednesday" keyOf 4
      "Thursday" keyOf 5
      "Friday" keyOf 6
      "Saturday" keyOf 7
    }
  }

  /** report trigger **/
  val preReport = trigger(PREREPORT) {
    println("---------PREREPORT TRIGGER-------------")
  }

  val postReport = trigger(POSTREPORT) {
    println("---------POSTREPORT TRIGGER-------------")
  }

  /** Report data initialization **/
  init {
    add {
      this[name] = "Sami"
      this[name2] = "Sami"
      this[age] = 20
      this[age2] = 40
      this[salary] = Decimal("2000.50")
      this[salary2] = Decimal("4000.50")
      this[codeDomain] = Days.keyOf("Monday")
    }
    add {
      this[name] = "Ahmed"
      this[name2] = "Ahmed"
      this[age] = 40
      this[age2] = 20
      this[salary] = Decimal("4000.50")
      this[salary2] = Decimal("2000.50")
      this[codeDomain] = Days.keyOf("Sunday")
    }
  }
}
