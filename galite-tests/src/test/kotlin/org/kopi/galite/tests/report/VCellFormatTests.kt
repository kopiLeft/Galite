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
package org.kopi.galite.tests.report

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.visual.report.VCellFormat

class VCellFormatTests {
  @Test
  fun testCellFormat() {
    val vcell = VCellFormat()
    val objectCell = TestCell()
    assertEquals("544", vcell.format(544))
    assertEquals("", vcell.format(null))
    assertEquals("TestCell(name=myName, age=88)", vcell.format(objectCell))
  }

  data class TestCell(val name: String = "myName", val age: Int = 88)
}
