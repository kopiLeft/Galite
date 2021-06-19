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
package org.kopi.galite.tests.report

import org.junit.Test
import org.kopi.galite.report.VBaseRow
import org.kopi.galite.tests.common.TestBase
import kotlin.test.assertEquals

class VBaseRowTests : TestBase() {

  val intData: Array<Any?> = arrayOf(1, 2, 3, 4)

  @Test
  fun vBaseRowIntTest() {
    /**Base row containing integer data*/
    val vBaseRowIntTest: VBaseRow = VBaseRow(intData)

    assertEquals(0, vBaseRowIntTest.getLevel())
    assertEquals(1, vBaseRowIntTest.getValueAt(0))

    vBaseRowIntTest.setValueAt(0, 5)
    assertEquals(5, vBaseRowIntTest.getValueAt(0))

    val cloneData = vBaseRowIntTest.cloneArray()
    assertArraysEquals(vBaseRowIntTest.data, cloneData)
  }
}
