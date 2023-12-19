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
package org.kopi.galite.tests.ui.vaadin.field

import java.time.LocalDate

import org.apache.commons.lang3.StringUtils

import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import org.kopi.galite.testing._enter
import org.kopi.galite.testing.click
import org.kopi.galite.testing.edit
import org.kopi.galite.testing.expectErrorNotification
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Form

class FieldConstraintsTests: GaliteVUITestBase() {

  val form = FormWithDomainConstraint().also { it.model }

  @Before
  fun `login to the App`() {
    login()

    // Open the form
    form.model.doNotModal()
  }

  @After
  fun `close pool connection`() {
    ApplicationContext.getDBConnection()?.poolConnection?.close()
  }

  @Test
  fun `test field with domain having constraints in simple block`() {
    // Int field
    form.simpleBlock._enter()
    form.simpleBlock.odd.edit(100)
    // Shows error because 100 is not odd
    expectErrorNotification("You should enter an odd number")
    // Completes Successfully because 99 is odd
    form.simpleBlock.odd.edit(99)

    // String field
    form.simpleBlock.upper.edit("abc")
    // Shows error because abc is lower
    expectErrorNotification("You should enter an uppercase string")
    // Completes Successfully because ABC is upper
    form.simpleBlock.upper.edit("ABC")

    // Date field
    form.simpleBlock.date20s.edit(LocalDate.of(1999, 1, 1))
    // Shows error because year < 2000
    expectErrorNotification("Year should be > 2000")
    // Completes Successfully because year > 2000
    form.simpleBlock.date20s.edit(LocalDate.of(2000, 1, 2))
  }

  @Test
  fun `test field with domain having constraints in multi block`() {
    // Int field
    form.multiBlock._enter()
    form.multiBlock.odd.edit(100)
    form.multiBlock.upper.click()
    // Shows error because 100 is not odd
    expectErrorNotification("You should enter an odd number")
    // Completes Successfully because 99 is odd
    form.multiBlock.odd.edit(99)

    // String field
    form.multiBlock.upper.edit("abc")
    form.multiBlock.odd.click()
    // Shows error because abc is lower
    expectErrorNotification("You should enter an uppercase string")
    // Completes Successfully because ABC is upper
    form.multiBlock.upper.edit("ABC")

    // Date field
    form.multiBlock.date20s.edit(LocalDate.of(1999, 1, 1))
    form.multiBlock.odd.click()
    // Shows error because year < 2000
    expectErrorNotification("Year should be > 2000")
    // Completes Successfully because year > 2000
    form.multiBlock.date20s.edit(LocalDate.of(2000, 1, 2))
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      org.jetbrains.exposed.sql.transactions.transaction(connection.dbConnection) {
        initModules()
      }
    }
  }
}

object Date20s : Domain<LocalDate>() {
  init {
    check(message = "Year should be > 2000") { value ->
      value.isAfter(LocalDate.of(2000, 1, 1))
    }
  }
}

object Odd : Domain<Int>(5) {
  init {
    check(message = "You should enter an odd number") { value ->
      value % 2 != 0
    }
  }
}

object Upper : Domain<String>(5) {
  init {
    check(message = "You should enter an uppercase string") { value ->
      StringUtils.isAllUpperCase(value)
    }
  }
}

class FormWithDomainConstraint: Form(title = "") {
  val action = menu("Action")
  val autoFill = actor(menu = action, label = "Autofill", help = "Autofill", command = PredefinedCommand.AUTOFILL)

  val simpleBlock = insertBlock(SimpleBlock())
  val multiBlock = insertBlock(MultiBlock())

  inner class SimpleBlock: Block("", 1, 1) {
    val odd = visit(Odd, position = at(1, 1)) {}
    val upper = visit(Upper, position = at(1, 2)) {}
    val date20s = visit(Date20s, position = at(1, 3)) {}
  }

  inner class MultiBlock: Block("", 10, 10) {
    val odd = visit(Odd, position = at(1, 1)) {}
    val upper = visit(Upper, position = at(1, 2)) {}
    val date20s = visit(Date20s, position = at(1, 3)) {}
  }
}
