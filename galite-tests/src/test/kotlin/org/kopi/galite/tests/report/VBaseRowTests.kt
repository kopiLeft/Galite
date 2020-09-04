package org.kopi.galite.tests.report

import org.junit.Test
import org.kopi.galite.report.VBaseRow
import org.kopi.galite.report.VReportRow
import kotlin.test.assertEquals

class VBaseRowTests : TestBase {

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
    assertArraysEquals(vBaseRowIntTest , cloneData)
  }
}

