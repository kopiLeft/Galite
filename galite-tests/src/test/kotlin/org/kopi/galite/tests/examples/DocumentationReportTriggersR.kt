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
package org.kopi.galite.tests.examples

import java.math.BigDecimal
import java.util.Locale

import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.report.Report
import org.jetbrains.exposed.sql.insert
import org.kopi.galite.visual.database.transaction
import org.kopi.galite.visual.Icon
import org.kopi.galite.visual.report.triggers.avgDecimal
import org.kopi.galite.visual.report.triggers.avgInteger
import org.kopi.galite.visual.report.triggers.sumDecimal
import org.kopi.galite.visual.report.triggers.sumInteger

/**
 * test field triggers [compute]
 * report trigger
 */
class DocumentationReportTriggersR : Report(title = "Report to test triggers", locale = Locale.UK) {

  val action = menu("Action")

  val quit = actor(
    menu = action,
    label = "Quit",
    help = "Quit",
  ) {
    key = Key.ESCAPE
    icon = Icon.QUIT
  }

  val quitCmd = command(item = quit) {
    model.close()
  }

  val name = field(STRING(25)) {
    label = "Name"
    help = "The name"
  }

  val lastName = field(STRING(25)) {
    label = "last Name"
    help = "The last name"
  }

  // test avgInteger
  val age = field(INT(25)) {
    label = "age with avg"
    help = "age"
    compute {
      // Computes the average of ages
      avgInteger()
    }
  }

  // test sumInteger
  val age2 = field(INT(25)) {
    label = "age2 with sum"
    help = "age2"
    compute {
      // Computes the sum of ages
      sumInteger()
    }
  }

  // test sumDecimal
  val salary = field(DECIMAL(5, 2)) {
    label = "salary with sum"
    help = "salary"
    compute {
      // Computes the sum of salary
      sumDecimal()
    }
  }

  // test avgDecimal
  val salary2 = field(DECIMAL(5, 2)) {
    label = "salary with avg"
    help = "salary"
    compute {
      // Computes the sum of salary2
      avgDecimal()
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
    transaction {
      initDocumentationData()
      TestTriggers.insert {
        it[id] = 5
        it[INS] = "PREREPORT Trigger"
      }
    }
  }

  val postReport = trigger(POSTREPORT) {
    transaction {
      initDocumentationData()
      TestTriggers.insert {
        it[id] = 5
        it[INS] = "POSTREPORT Trigger"
      }
    }
  }

  /** Report data initialization **/
  init {
    add {
      this[name] = "Sami"
      this[lastName] = "Malouli"
      this[age] = 20
      this[age2] = 40
      this[salary] = BigDecimal("20.50")
      this[salary2] = BigDecimal("40.50")
      this[codeDomain] = Days.keyOf("Monday")
    }
    add {
      this[name] = "Ahmed"
      this[lastName] = "Mouelhi"
      this[age] = 40
      this[age2] = 20
      this[salary] = BigDecimal("40.50")
      this[salary2] = BigDecimal("20.50")
      this[codeDomain] = Days.keyOf("Sunday")
    }
  }
}
