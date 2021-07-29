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
package org.kopi.galite.tests.examples

import java.util.Locale

import org.joda.time.DateTime
import org.kopi.galite.demo.Application
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.DictionaryForm
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.type.Decimal
import org.kopi.galite.type.Image
import org.kopi.galite.type.Month
import org.kopi.galite.type.Time
import org.kopi.galite.type.Week

class SimpleForm : DictionaryForm() {
  override val locale = Locale.UK
  override val title = "Training"
  val action = menu("Action")
  val autoFill = actor(
    ident = "Autofill",
    menu = action,
    label = "Autofill",
    help = "Autofill",
  )

  val block = insertBlock(Traineeship())
}

class Traineeship : FormBlock(1, 1, "Training") {
  val t = table(Training)

  val trainingID = visit(domain = Domain<Int>(25), position = at(1, 1)) {
    label = "training ID"
    help = "training ID"
    columns(t.id)
  }
  val trainingName = visit(domain = Domain<String>(50), position = at(2, 1)) {
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
  val trainingPrice = visit(domain = Domain<Decimal>(10), position = at(3, 1)) {
    label = "training Price"
    help = "training Price"
    columns(t.price) {
      priority = 1
    }
  }
  val active = visit(domain = Domain<Boolean>(2), position = at(4, 1)) {
    label = "active?"
    help = "active"
    columns(t.active) {
      priority = 1
    }
  }
  val date = visit(domain = Domain<DateTime>(30), position = at(5, 1)) {
    label = "Date"
    help = "The Date"
  }
  val week = visit(domain = Domain<Week>(50), position = at(6, 1)) {
    label = "Week"
    help = "The Week"
  }
  val month = visit(domain = Domain<Month>(70), position = at(7, 1)) {
    label = "Month"
    help = "The Month"
  }
  val time= visit(domain = Domain<Time>(20), position = at(8, 1)) {
    label = "Time"
    help = "The Time"
  }
  val photo = visit(domain = Domain<Image>(100, 100), position = at(9, 1)) {
    label = "photo"
    help = "photo"
    columns(t.photo)
  }
  val informations = visit(domain = Domain<String?>(80, 50, 10), position = at(10, 1)) {
    label = "training informations"
    help = "The training informations"
    columns(t.informations) {
      priority = 1
    }
  }
}

fun main() {
  Application.runForm(formName = SimpleForm())
}
