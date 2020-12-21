package org.kopi.galite.tests.form

import kotlin.test.assertEquals

import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.kopi.galite.tests.JApplicationTestBase

class FormSampleTests: JApplicationTestBase() {
  @Test
  fun sourceFormTest() {
    val formModel = FormSample.model

    assertEquals(FormSample::class.qualifiedName!!.replace(".", "/"), formModel.source)
  }

  @Test
  fun changeBlockAccessTest() {
    FormSample.model

    assertEquals(1, FormSample.tb4ToTestChangeBlockAccess.vBlock.getAccess())

    assertArrayEquals(intArrayOf(0, 0, 0), FormSample.tb4ToTestChangeBlockAccess.id.access)
    assertArrayEquals(intArrayOf(1, 1, 4), FormSample.tb4ToTestChangeBlockAccess.name.access)
    assertArrayEquals(intArrayOf(1, 1, 4), FormSample.tb4ToTestChangeBlockAccess.password.access)
    assertArrayEquals(intArrayOf(1, 1, 2), FormSample.tb4ToTestChangeBlockAccess.age.access)
  }
}
