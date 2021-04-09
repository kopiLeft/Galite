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
package org.kopi.galite.demo.allFieldTypes

import java.util.Locale

import org.kopi.galite.demo.Application
import org.kopi.galite.domain.CodeDomain
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.dsl.DictionaryForm
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.type.Date
import org.kopi.galite.type.Decimal
import org.kopi.galite.type.Image
import org.kopi.galite.type.Month
import org.kopi.galite.type.Time
import org.kopi.galite.type.Timestamp
import org.kopi.galite.type.Week

object AllFieldTypesForm : DictionaryForm() {
  override val locale = Locale.UK
  override val title = "All field types test"
  val page = page("AllFields")
  val action = menu("Action")
  val autoFill = actor(
          ident = "Autofill",
          menu = action,
          label = "Autofill",
          help = "Autofill",
  )

  val block = insertBlock(BlockAllFields, page)
}

object BlockAllFields : FormBlock(1, 1, "AllFields") {

  val stringField = mustFill(domain = Domain<String>(50), position = at(1, 1)) {
    label = "String Field"
    help = "The String Field"
  }
  val intField = mustFill(domain = Domain<Int>(25), position = at(2, 1)) {
    label = "Int Field"
    help = "The Int Field"
  }
  val booleanField = visit(domain = Domain<Boolean>(50), position = at(3, 1)) {
    label = "Boolean Field"
    help = "The Boolean Field"
  }
  val decimalField = visit(domain = Domain<Decimal>(50), position = at(4, 1)) {
    label = "Decimal Field"
    help = "The Decimal Field"
  }
  val dateField = visit(domain = Domain<Date>(30), position = at(5, 1)) {
    label = "Date Field"
    help = "The Date Field"
  }
  val weekField = visit(domain = Domain<Week>(500), position = at(6, 1)) {
    label = "Week Field"
    help = "The Week Field"
  }
  val monthField = visit(domain = Domain<Month>(700), position = at(7, 1)) {
    label = "Month Field"
    help = "The Month Field"
  }
  val timeField = visit(domain = Domain<Time>(20), position = at(8, 1)) {
    label = "Time Field"
    help = "The Time Field"
  }
  val timestampField = visit(domain = Domain<Timestamp>(20), position = at(9, 1)) {
    label = "Timestamp Field"
    help = "Timestamp Time Field"
  }
  val imageField = visit(domain = Domain<Image>(100, 100), position = at(10, 1)) {
    label = "Image Field"
    help = "Image Time Field"
  }
  val codeDomain = visit(domain = Color, position = at(11, 1)) {
    label = "CodeDomain"
    help = "The CodeDomain Field"
  }
}
object Color : CodeDomain<String>() {
  init {
    "red" keyOf "color 1"
    "green" keyOf "color 2"
    "blue" keyOf "color 3"
    "black" keyOf "color 4"
    "white" keyOf "color 5"
  }
}
fun main() {
  Application.runForm(formName = AllFieldTypesForm)
}
