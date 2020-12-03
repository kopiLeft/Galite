package org.kopi.galite.tests.form

import org.junit.Test
import org.kopi.galite.tests.JApplicationTestBase
import kotlin.test.assertEquals

class FormUnitTests: JApplicationTestBase() {

    @Test
    fun sourceFormTest() {
        val formModel = TestForm.model
        assertEquals(TestForm::class.qualifiedName!!.replace(".", "/"), formModel.source)
    }
}
