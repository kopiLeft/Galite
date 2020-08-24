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

package org.galite.tests.demo.types

import org.galite.domain.Domain

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select

object Cities: Table() {
    val id = long("id")
    val name = varchar("name", 50)
    val pop = integer("pop")
}


class Example: Domain<String>() {
    override val code= code (
        "Foo" to "F",
        "Bar" to "org.galite.chart.tests.demo.B"
    )
}

class City(param: String, idThteshold: Long): Domain<String>(20) {
    override val convertUpper = true

    override val list = list {
        query = Cities.select {
            Cities.id greater idThteshold
        }

        column<String>("org.galite.chart.tests.demo.City Name", Cities.name, 5)
        column<Int>("population", Cities.pop, 25)
    }

    // CHECK Doesn't exist in Kopi but we can add it
    override val check = { value: String ->
        value != param
    }
}

class Continent(val exception: String) : Domain<String>(20) {
    override val check = { value: String ->
        value != exception
    }

    override val code = code (
        "Asia" to "AS",
        "Africa" to "AF",
        "North America" to "NA",
        "South America" to "SA",
        "Antarctica" to "AN",
        "Europe" to "EU",
        "Australia" to "AST"
    )
}

class Population : Domain<Int>(20) {
    override val check = { value: Int ->
        value > 100
    }
}

class Area : Domain<Int>(20) {
    override val check = { value: Int ->
        value > 100
    }
}
