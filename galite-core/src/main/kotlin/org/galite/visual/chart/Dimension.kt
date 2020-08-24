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

class Dimension<T: Comparable<T>>(size: Int? = null) : Column<T>(size) {
    val values = mutableListOf<DimensionValue<T>>()
    val measures = mutableListOf<Measure<*>>()

    fun add(value: T, measuresSetting: Dimension<T>.() -> Unit) {
        val dimensionValue = DimensionValue(value, mutableMapOf())
        values.add(dimensionValue)
        this.measuresSetting()
    }

    operator fun <V: Comparable<V>> set(measure: Measure<V>, value: V) {
        if(measure in measures) {
            values.last().measures.putIfAbsent(measure, value)
        }
    }

    fun format(function: (T) -> String) {
        TODO("Not yet implemented")
    }
    var format: String? = null

    fun equals(other: Dimension<*>): Boolean {
        if (this === other) return true
        if (this.label != other.label) return false
        TODO("Not yet implemented")


        return true
    }

}

class DimensionValue<T: Comparable<T>>(val value: T, val measures: MutableMap<Measure<*>, Any>) {

}