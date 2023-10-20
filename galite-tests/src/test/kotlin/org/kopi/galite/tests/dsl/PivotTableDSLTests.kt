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
package org.kopi.galite.tests.dsl

import java.util.Locale

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.pivotTable.PivotTable

class PivotTableDSLTests : VApplicationTestBase() {

  @Test
  fun `test generated model from a basic pivot table`() {
    val pivotTable = BasicPivotTable()
    val model = pivotTable.model

    assertEquals(pivotTable.locale, model.locale)
    assertEquals(pivotTable.title, model.getTitle())
    assertEquals(pivotTable.help, model.help)
  }

  @Test
  fun `test pivot table fields`() {
    val pivotTable = PivotTableWithData()
    val pivotTableModel = pivotTable.model

    assertEquals(3, pivotTableModel.model.columns.size)
    println(pivotTableModel.model.columns[0]!!.label)
    println(pivotTableModel.model.columns[1]!!.label)
    println(pivotTableModel.model.columns[2]!!.label)
    assertEquals(pivotTable.firstName.label, pivotTableModel.model.columns[0]!!.label)
    assertEquals(pivotTable.addressClt.label, pivotTableModel.model.columns[1]!!.label)
    assertEquals(pivotTable.ageClt.label, pivotTableModel.model.columns[2]!!.label)
  }
}

class BasicPivotTable : PivotTable(
  title = "Clients Pivot Table",
  help = "This is a pivot table that contains information about clients",
  locale = Locale.UK
)

class PivotTableWithData : PivotTable(title = "Clients Pivot Table", locale = Locale.UK) {

  val firstName = field(STRING(25)) {
    label = "First Name"
    help = "The client first name"
  }

  val addressClt = field(STRING(50)) {
    label = "Address"
    help = "The client address"
  }

  val ageClt = field(INT(2)) {
    label = "Age"
    help = "The client age"
  }

  init {
    add {
      this[firstName] = "client 1"
      this[addressClt] = "Tunis"
      this[ageClt] = 20
    }
    add {
      this[firstName] = "client 1"
      this[addressClt] = "Bizerte"
      this[ageClt] = 20
    }
    add {
      this[firstName] = "client 2"
      this[addressClt] = "Ben Arous"
      this[ageClt] = 30
    }
  }
}
