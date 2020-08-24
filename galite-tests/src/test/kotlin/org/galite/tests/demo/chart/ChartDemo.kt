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

package org.galite.tests.demo.chart

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.galite.tests.demo.types.Area
import org.galite.tests.demo.types.Continent
import org.galite.tests.demo.types.Population
import org.galite.visual.chart.chart
import org.galite.visual.chart.ChartType
import org.galite.visual.common.Color
import org.vaadin.examples.form.menu.actorBar.menubar.Actor
import org.galite.base.Application
import org.vaadin.examples.form.menu.menubar.GMenuBar
import java.util.*


class TestDemo : Application() {
    var actors = listOf(Actor("Project","Project","Quit","", Icon(VaadinIcon.USER)), Actor("Project","Project","Hilati","", Icon(VaadinIcon.USER)))
    val root = chart(ChartType.BAR, actors, this) {
            val continents = dimension(Continent("")) {
                label = "Continent"
                format { value ->
                    "Continent: $value"
                }
            }

            val area = measure(Area()) {
                label = "Area"
                color = Color.RED
            }

            val population = measure(Population()) {
                label = "Pouation"
                color = Color.BLUE
            }

            continents.add("AF") {
                this[area] = 10000000
                this[population] = 20000000
            }

            continents.add("Eur") {
                this[area] = 40000000
                this[population] = 30000000
            }
        }
    override var menus: GMenuBar
        get() = TODO("Not yet implemented")
        set(value) {}
    override var windows: LinkedList<Component>?
        get() = TODO("Not yet implemented")
        set(value) {}
}
