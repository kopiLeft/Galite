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

package org.galite.tests.chart

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import org.galite.base.Application
import org.galite.domain.Domain
import org.galite.exceptions.MissingMeasureException
import org.galite.visual.chart.Chart
import org.galite.visual.chart.Formatter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.vaadin.examples.form.menu.actorBar.menubar.Actor
import org.vaadin.examples.form.menu.menubar.GMenuBar
import java.util.*

class ChartTests : Application() {
    var actors = listOf(Actor("Project","Project","Quit","", Icon(VaadinIcon.USER)), Actor("Project","Project","Hilati","", Icon(VaadinIcon.USER)))

    /**
     * Tests creating dimension and one measure and fill them with data
     */
    @Test
    fun dimensionOneMeasureTest() {
        with(Chart("",actors, this)) {
            val dimension = dimension(StringTestType(10)) {
            }

            val measure1 = measure(IntTestType(10)) {
            }

            dimension.add("abc") {
                this[measure1] = 20
            }

            assertEquals(1, dimension.values.size)
            assertEquals(1, dimension.measures.size)
            assertEquals("abc", dimension.values.first().value)
            assertEquals(20, dimension.values.first().measures[measure1])
        }
    }

    /**
     * Tests creating dimension and multiple measures and fill them with data
     * Defines measures before the dimension
     */
    @Test
    fun dimensionMultipleMeasureTest() {
        with(Chart("",actors, this)) {
            val measure1 = measure(IntTestType(10)) {
            }

            val measure2 = measure(IntTestType(10)) {
            }

            val dimension = dimension(StringTestType(10)) {
            }

            dimension.add("abc") {
                this[measure1] = 20
                this[measure2] = 90
            }

            dimension.add("def") {
                this[measure1] = 30
                this[measure2] = 100
            }

            dimension.add("ghi") {
                this[measure1] = 40
                this[measure2] = 110
            }

            assertEquals(3, dimension.values.size)
            assertEquals(2, dimension.measures.size)
            assertEquals("abc", dimension.values[0].value)
            assertEquals("def", dimension.values[1].value)
            assertEquals("ghi", dimension.values[2].value)
            assertEquals(20, dimension.values[0].measures[measure1])
            assertEquals(90, dimension.values[0].measures[measure2])
            assertEquals(30, dimension.values[1].measures[measure1])
            assertEquals(100, dimension.values[1].measures[measure2])
            assertEquals(40, dimension.values[2].measures[measure1])
            assertEquals(110, dimension.values[2].measures[measure2])
        }
    }

    /**
     * Tests chart data formatter
     */
    @Test
    fun chartDataEncodeFormatterTest1() {
        with(Chart("",actors, this)) {
            val measure1 = measure(IntTestType(10)) {
                label = "measure1"
            }

            val measure2 = measure(IntTestType(10)) {
                label = "measure2"
            }

            val dimension = dimension(StringTestType(10)) {
                label = "dimension"
            }

            dimension.add("Tunis") {
                this[measure1] = 12
                this[measure2] = 20
            }

            dimension.add("Bizerte") {
                this[measure1] = 45
                this[measure2] = 33
            }

            val formattedData = Formatter.encode(dimension)

            assertEquals("[[\"dimension\",\"measure1\",\"measure2\"],[\"Tunis\",12,20],[\"Bizerte\",45,33]]",
            formattedData)
        }
    }

    /**
     * Tests chart data formatter that fails when there is a measure not set.
     */
    @Test
    fun chartDataEncodeFormatterTest2() {
        with(Chart("",actors, this)) {
            val measure1 = measure(IntTestType(10)) {
                label = "measure1"
            }

            val measure2 = measure(IntTestType(10)) {
                label = "measure2"
            }

            val dimension = dimension(StringTestType(10)) {
                label = "dimension"
            }

            dimension.add("Tunis") {
                this[measure1] = 12
                this[measure2] = 20
            }

            dimension.add("Bizerte") {
                this[measure1] = 45
            }

        }
    }

    override var menus: GMenuBar
        get() = TODO("Not yet implemented")
        set(value) {}
    override var windows: LinkedList<Component>?
        get() = TODO("Not yet implemented")
        set(value) {}
}

class StringTestType(val param: Int): Domain<String>(25)
class IntTestType(val param: Int): Domain<Int>(25)
