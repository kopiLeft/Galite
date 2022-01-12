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

import java.util.Locale

import org.kopi.galite.tests.desktop.runForm
import org.kopi.galite.visual.domain.Convert
import org.kopi.galite.visual.domain.DECIMAL
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.FieldOption
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.maxValue
import org.kopi.galite.visual.dsl.form.minValue

class TestFieldsForm : DictionaryForm(title = "Fields Form", locale = Locale.UK) {

  val action = menu("Action")
  val autoFill = actor(
    menu = action,
    label = "Autofill",
    help = "Autofill",
    command = PredefinedCommand.AUTOFILL
  )

  val serialQuery = actor(
    menu = action,
    label = "serialQuery",
    help = "serial query",
  ) {
    key = Key.F6
    icon = Icon.SERIAL_QUERY
  }

  val blockWithDifferentTypes = insertBlock(BlockWithDifferentTypes())
  val blockWithAllFieldVisibilityTypes = insertBlock(BlockWithAllFieldVisibilityTypes())
  val blockWithSaveCommand = insertBlock(BlockWithSaveCommand()) {
    command(item = serialQuery) {
      serialQuery()
    }
  }
}

class BlockWithAllFieldVisibilityTypes : Block("Block With All Field Visibility Types", 1, 1) {
  val hiddenField = hidden(domain = INT(25)) {
    label = "hidden field"
    help = "hidden field"
  }

  val visitField = visit(domain = INT(3), position = at(1, 1)) {
    label = "visit field"
    help = "visit field"
    minValue = 10
    maxValue = 50
  }

  val mustFillField = mustFill(domain = STRING(50), position = at(2, 1)) {
    label = "mustFill field"
    help = "mustFill field"
  }

  val skippedField = skipped(domain = STRING(50), position = at(3, 1)) {
    label = "skipped field"
    help = "skipped field"
  }
}

class BlockWithDifferentTypes : Block("Block With Different Types", 1, 1) {
  val upperStringField = visit(domain = STRING(50, Convert.UPPER), position = at(1, 1)) {
    label = "upper string field"
    help = "upper string field"
  }
  val nameStringField = visit(domain = STRING(50, Convert.NAME), position = at(1, 2)) {
    label = "name string field"
    help = "name string field"
  }
  val lowerStringField = visit(domain = STRING(50, Convert.LOWER), position = at(1, 3)) {
    label = "lower string field"
    help = "lower string field"
  }
  val intField = visit(domain = INT(3), position = at(2, 1)) {
    label = "visit field"
    help = "visit field"
    minValue = 10
    maxValue = 50
  }
  val decimalField = visit(domain = DECIMAL(5, 2), position = at(3, 1)) {
    label = "decimal field"
    help = "decimal field"
  }
}

class BlockWithSaveCommand : Block("Block With Save Command", 1, 1) {
  val t = table(Trainer)
  val trainerID = hidden(domain = INT(25)) {
    label = "trainer ID"
    help = "trainer ID"
    columns(t.id) {
      priority = 1
    }
  }
  val trainerFirstName = visit(domain = STRING(25), position = at(1, 1)) {
    label = "trainer First Name"
    help = "trainer First Name"
    columns(t.trainerFirstName)
    options(FieldOption.QUERY_LOWER)
  }
  val trainerLastName = visit(domain = STRING(25), position = at(1, 2)) {
    label = "trainer Last Name"
    help = "trainer Last Name"
    columns(t.trainerLastName)
    options(FieldOption.QUERY_UPPER)
  }
  val uc = hidden(domain = INT(20)) { columns(t.uc) }
  val ts = hidden(domain = INT(20)) { columns(t.ts) }
}

fun main() {
  runForm(formName = TestFieldsForm())
}
