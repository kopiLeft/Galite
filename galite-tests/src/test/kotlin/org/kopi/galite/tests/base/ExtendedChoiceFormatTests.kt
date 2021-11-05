package org.kopi.galite.tests.base

import org.junit.Test
import org.kopi.galite.visual.base.ExtendedChoiceFormat
import kotlin.test.assertEquals

class ExtendedChoiceFormatTests {

    val extendedChoiceFormat = ExtendedChoiceFormat(limits= doubleArrayOf(0.0,0.5,1.0), arrayOf<String>("zero", "half", "one"), true)

    @Test
    fun testFormat(){
        assertEquals("one", extendedChoiceFormat.format(1.0))
        assertEquals("one", extendedChoiceFormat.format(1))
        assertEquals("half", extendedChoiceFormat.format(0.5))
        assertEquals("zero", extendedChoiceFormat.format(0))
        assertEquals("zero", extendedChoiceFormat.format(0.0))
    }
    @Test
    fun testFormatObject(){
        // internally .format will call .formatObject if the value isn't a Number
        assertEquals("one", extendedChoiceFormat.format("1.0"))
        assertEquals("one", extendedChoiceFormat.format("0.0"))
        assertEquals("one", extendedChoiceFormat.format(object {val name="test"}))
    }
}