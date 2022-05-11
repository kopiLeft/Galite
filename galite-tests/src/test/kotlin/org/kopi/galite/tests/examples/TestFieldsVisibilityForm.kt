/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.Icon
import org.kopi.galite.visual.Mode
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Block

class TestFieldsVisibilityForm : DictionaryForm(title = "Fields Visibility Form", locale = Locale.UK) {
  val action = menu("Action")
  val query = actor(
          menu = action,
          label = "Query Mode",
          help = "Change the mode to query"
  ) {
    icon = Icon.ALL
  }
  val insert = actor(
          menu = action,
          label = "Insert Mode",
          help = "Change the mode to insert"
  ) {
    icon = Icon.INSERT
  }

  val update = actor(
          menu = action,
          label = "Update Mode",
          help = "Change the mode to update"
  ) {
    icon = Icon.EDIT
  }

  val blockWithAllFieldVisibilityTypes = insertBlock(BlockWithChangingFieldVisibilityTypes()) {
    command(item = query) {
      setMode(Mode.QUERY)
    }
    command(item = update) {
      setMode(Mode.UPDATE)
    }
    command(item = insert) {
      setMode(Mode.INSERT)
    }
  }
}

class BlockWithChangingFieldVisibilityTypes : Block("Block With All Field Visibility Types", 1, 1) {
  val hiddenField = hidden(domain = INT(25)) {
    label = "hidden field"
    help = "hidden field"
  }

  val visitFieldToSkippedField = visit(domain = INT(25), position = at(1, 2)) {
    label = "visit field to skipped"
    help = "visit field"
    onInsertSkipped()
    onUpdateSkipped()
  }

  val visitFieldToHiddenField = visit(domain = INT(25), position = at(1, 3)) {
    label = "visit field to hidden"
    help = "visit field"
    onInsertHidden()
    onUpdateHidden()
  }

  val visitFieldToMustFillField = visit(domain = INT(25), position = at(1, 4)) {
    label = "visit field to mustfill"
    help = "visit field"
    onInsertMustFill()
    onUpdateMustFill()
  }

  val mustFillToSkippedField = mustFill(domain = INT(25), position = at(2, 2)) {
    label = "mustFill field to skipped"
    help = "mustFill field"
    onInsertSkipped()
    onUpdateSkipped()
  }

  val mustFillToVisitField = mustFill(domain = INT(25), position = at(2, 3)) {
    label = "mustFill field to visit"
    help = "mustFill field"
    onInsertVisit()
    onUpdateVisit()
  }

  val mustFillToHiddenField = mustFill(domain = INT(25), position = at(2, 4)) {
    label = "mustFill field to hidden"
    help = "mustFill field"
    onInsertHidden()
    onUpdateHidden()
  }

  val skippedToHiddenField = skipped(domain = INT(25), position = at(3, 2)) {
    label = "skipped field to hidden"
    help = "skipped field"
    onInsertHidden()
    onUpdateHidden()
  }

  val skippedToVisitField = skipped(domain = INT(25), position = at(3, 3)) {
    label = "skipped field to visit"
    help = "skipped field"
    onInsertVisit()
    onUpdateVisit()
  }

  val skippedToMustFillField = skipped(domain = INT(25), position = at(3, 4)) {
    label = "skipped field to mustfill"
    help = "skipped field"
    onInsertMustFill()
    onUpdateMustFill()
  }
}

fun main() {
  runForm(formName = TestFieldsVisibilityForm())
}
