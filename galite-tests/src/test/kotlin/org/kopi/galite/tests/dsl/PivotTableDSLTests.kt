/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2023 kopiRight Managed Solutions GmbH, Wien AT
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
import org.kopi.galite.tests.ui.vaadin.triggers.PivotTableTriggersTest.INIT
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.pivottable.Dimension
import org.kopi.galite.visual.dsl.pivottable.PivotTable

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

    assertEquals(pivotTable.firstName.label, pivotTableModel.model.columns[0]!!.label)
    assertEquals(pivotTable.firstName.help, pivotTableModel.model.columns[0]!!.help)
    assertEquals(Dimension.Position.ROW, pivotTableModel.model.columns[0]!!.position)
    assertEquals(listOf("client 1", "client 1", "client 2"), pivotTable.getRowsForField(pivotTable.firstName))

    assertEquals(pivotTable.addressClt.label, pivotTableModel.model.columns[1]!!.label)
    assertEquals(pivotTable.addressClt.help, pivotTableModel.model.columns[1]!!.help)
    assertEquals(Dimension.Position.COLUMN, pivotTableModel.model.columns[1]!!.position)
    assertEquals(listOf("Tunis", "Bizerte", "Ben Arous"), pivotTable.getRowsForField(pivotTable.addressClt))

    assertEquals(pivotTable.ageClt.label, pivotTableModel.model.columns[2]!!.label)
    assertEquals(pivotTable.ageClt.help, pivotTableModel.model.columns[2]!!.help)
    assertEquals(Dimension.Position.NONE, pivotTableModel.model.columns[2]!!.position)
    assertEquals(listOf(20, 20, 30), pivotTable.getRowsForField(pivotTable.ageClt))
  }

  @Test
  fun `test pivot table rows`() {
    val pivotTable = PivotTableWithData()

    pivotTable.add {
      this[pivotTable.firstName] = "client Test"
      this[pivotTable.addressClt] = "Test"
      this[pivotTable.ageClt] = 10
    }
    pivotTable.model.initPivotTable()
    val pivotTableModel = pivotTable.model.model

    assertEquals(4, pivotTableModel.getRowCount())
    assertEquals("Tunis", pivotTableModel.getRow(0)?.getValueAt(1))
    assertEquals("client Test", pivotTableModel.getRow(3)?.getValueAt(0))
    assertEquals("Test", pivotTableModel.getRow(3)?.getValueAt(1))
    assertEquals(10, pivotTableModel.getRow(3)?.getValueAt(2))
  }

   @Test
   fun `test pivot table has triggers`() {
     val pivotTable = PivotTableWithData()
     val pivotTableModel = pivotTable.model

     assertEquals(true, pivotTableModel._hasTrigger(INIT.event))
   }
}

class BasicPivotTable : PivotTable(
  title = "Clients Pivot table",
  help = "This is a pivot table that contains information about clients",
  locale = Locale.UK
)

class PivotTableWithData : PivotTable(title = "Clients Pivot table", locale = Locale.UK) {

  val init = trigger(INIT) {}

  val firstName = dimension(STRING(25), Dimension.Position.ROW) {
    label = "First Name"
    help = "The client first name"
  }

  val addressClt = dimension(STRING(50), Dimension.Position.COLUMN) {
    label = "Address"
    help = "The client address"
  }

  val ageClt = measure(INT(2)) {
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
