package org.kopi.galite.tests.chart

import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.visual.chart.*
import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.chart.Chart
import org.kopi.galite.visual.dsl.chart.ChartDimension
import org.kopi.galite.visual.dsl.chart.DimensionData
import org.kopi.galite.visual.type.Time
import org.kopi.galite.visual.type.Timestamp
import org.kopi.galite.visual.type.Week
import java.util.*
import kotlin.test.assertEquals

class VDimensionTests {
    @Test
    fun personalizedVDimension(){
        class TestDimension(ident: String): VDimension(ident, null){
            override fun toString(value: Any?): String {
                return value.toString()
            }
        }
        val testDimension:TestDimension = TestDimension("TestIdent")

        assertEquals("null", testDimension.format(null))
        assertEquals("5", testDimension.format(5))
        assertEquals("1.618", testDimension.format(1.618))
        assertEquals("String", testDimension.format("String"))
    }
    @Test
    fun vWeekDimensionTest(){
        val vWeekDimension: VWeekDimension = VWeekDimension("vweek", null)

        assertEquals(CConstants.EMPTY_TEXT, vWeekDimension.format(null))
        assertEquals("18.1", vWeekDimension.format(Week(70)))
    }

    @Test
    fun vTimestampDimensionTest(){
        val vTimestampDimension: VTimestampDimension = VTimestampDimension("vtsd", null)

        assertEquals(CConstants.EMPTY_TEXT, vTimestampDimension.format(null))
        assertEquals("2021-01-01 00:00:00.000000", vTimestampDimension.format(Timestamp("2021-01-01 00:00:00")))
    }
    @Test
    fun vTimeDimensionTest(){
        val vTimeDimension: VTimeDimension = VTimeDimension("vtd", null)

        assertEquals(CConstants.EMPTY_TEXT, vTimeDimension.format(null))
        assertEquals("23:30",vTimeDimension.format(Time(23,30,0)))
    }
    @Test
    fun vStringDimensionTest(){
        val vStringDimension: VStringDimension = VStringDimension("vsd", null)

        assertEquals(CConstants.EMPTY_TEXT, vStringDimension.format(null))
        assertEquals("String", vStringDimension.format("String"))
        assertEquals("5", vStringDimension.format(5))
    }

}