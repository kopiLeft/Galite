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

package org.galite.visual.chart

import com.vaadin.flow.component.HasComponents
import org.galite.base.Application
import org.galite.domain.Domain
import org.galite.visual.Window
import org.vaadin.examples.form.menu.actorBar.menubar.Actor

open class Chart(title :String, actors : List<Actor>, application : Application): Window(title,actors,application) {
    var dimension: Dimension<*>? = null
    val measures = mutableListOf<Measure<*>>()

    fun <T: Comparable<T>> dimension(domain: Domain<T>, function: Dimension<T>.() -> Unit):  Dimension<T> {
        val dim = Dimension<T>(domain.size)
        dim.measures.addAll(measures)
        dim.function()
        dimension = dim
        return dim
    }

    fun <T: Comparable<T>> measure(domain: Domain<T>, function: Measure<T>.() -> Unit) : Measure<T> {
        val measure = Measure<T>(domain.size)
        measure.function()
        this.measures.add(measure)
        this.dimension?.measures?.add(measure)
        return measure
    }

    open fun setData() {}
}

fun HasComponents.chart(chartType: ChartType, actors: List<Actor>, application: Application, function: Chart.() -> Unit): Chart {
    val chart = ChartFactory.createChart(chartType, actors, application)
    chart.function()
    chart.setData()
    return chart
}

