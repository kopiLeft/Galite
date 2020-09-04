package org.kopi.galite.tests.report

import org.junit.Test
import org.kopi.galite.report.VBaseRow
import org.kopi.galite.report.VReportRow
import kotlin.test.assertEquals

class VBaseRowTests {

  val intData: Array<Any?> = arrayOf(1, 2, 3, 4)
  val stringData: Array<Any?> = arrayOf("first", "second", "third")

  /**Base row containing integer data*/
  val vBaseRowIntTest: VBaseRow = VBaseRow(intData)

  /**Base row containing string data*/
  val vBaseRowStringTest = VBaseRow(stringData)

  @Test
  fun vBaseRowIntTest() {
    assertEquals(0, vBaseRowIntTest.getLevel())
    assertEquals(1, vBaseRowIntTest.getValueAt(0))

    vBaseRowIntTest.setValueAt(0, 5)
    assertEquals(5, vBaseRowIntTest.getValueAt(0))

    val cloneData = vBaseRowIntTest.cloneArray()
    for (i in 0 until vBaseRowIntTest.data.size) {
      assertEquals(vBaseRowIntTest.data[i], cloneData[i])
    }
  }

  @Test
  fun vBaseRowStringTest() {
    assertEquals(0, vBaseRowStringTest.getLevel())
    assertEquals("first", vBaseRowStringTest.getValueAt(0))

    vBaseRowStringTest.setValueAt(0, "zero")
    assertEquals("zero", vBaseRowStringTest.getValueAt(0))

    val cloneData = vBaseRowStringTest.cloneArray()
    for (i in 0 until vBaseRowStringTest.data.size) {
      assertEquals(vBaseRowStringTest.data[i], cloneData[i])
    }
  }
}
