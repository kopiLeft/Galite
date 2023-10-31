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

import java.util.Locale

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.pivotTable.PivotTable

class DocumentationPivotTable : PivotTable(title = "Test Pivot Table", locale = Locale.UK) {


  // test to upper Case format + align left
  val name = field(STRING(25)) {
    label = "Name"
  }

  // test to lower Case format + align right
  val lastName = field(STRING(25)) {
    label = "last Name "
  }

  // test normal format + align center
  val middleName = field(STRING(25)) {
    label = "middleName"
  }

  val age = field(INT(25)) {
    label = "age"
  }

  val testTable = TestTable.selectAll()

  init {
    transaction {
      testTable.forEach { result ->
        add {
          this[name] = result[TestTable.name]
          this[lastName] = result[TestTable.lastName]!!
          this[middleName] = result[TestTable.lastName]!!
          this[age] = result[TestTable.age]!!
        }
      }
    }
  }
}
