/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.tests.examples

import org.kopi.galite.visual.domain.BOOL
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.Fixed
import org.kopi.galite.visual.domain.IMAGE
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.form.FormBlock

class Traineeship : FormBlock(1, 10, "Training") {
  val t = table(Training)

  val trainingID = visit(domain = INT(25), position = at(1, 1)) {
    label = "training ID"
    help = "training ID"
    columns(t.id) {
      priority = 1
    }
  }
  val trainingName = visit(domain = STRING(50), position = at(2, 1)) {
    label = "training Name"
    help = "training Name"
    columns(t.trainingName) {
      priority = 1
    }
  }
  val trainingType = visit(domain = Type, position = follow(trainingName)) {
    label = "training Type"
    help = "training Type"
    columns(t.type) {
      priority = 1
    }
  }
  val trainingPrice = visit(domain = DECIMAL(10, 5), position = at(3, 1)) {
    label = "training Price"
    help = "training Price"
    columns(t.price) {
      priority = 1
    }
  }
  val active = visit(domain = BOOL, position = at(4, 1)) {
    label = "active?"
    help = "active"
    columns(t.active) {
      priority = 1
    }
  }
  val photo = visit(domain = IMAGE(100, 100), position = at(9, 1)) {
    label = "photo"
    help = "photo"
    columns(t.photo)
  }
  val informations = visit(domain = STRING(80, 50, 10, Fixed.ON), position = at(10, 1)) {
    label = "training informations"
    help = "The training informations"
    columns(t.informations) {
      priority = 1
    }
  }
}

object Type : CodeDomain<Int>() {
  init {
    "Galite" keyOf 1
    "Kotlin" keyOf 2
    "Java" keyOf 3
    "Python" keyOf 4
    "Html" keyOf 5
  }
}
