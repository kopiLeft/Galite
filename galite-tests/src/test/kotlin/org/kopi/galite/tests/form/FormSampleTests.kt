package org.kopi.galite.tests.form

import org.junit.Test
import org.kopi.galite.tests.JApplicationTestBase
import kotlin.test.assertEquals

class FormSampleTests: JApplicationTestBase() {

    @Test
    fun sourceFormTest() {
        val formModel = FormSample.model
        assertEquals(FormSample::class.qualifiedName!!.replace(".", "/"), formModel.source)
    }
}
