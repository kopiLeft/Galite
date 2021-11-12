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

package org.kopi.galite.tests.dsl

import kotlin.test.assertEquals
import kotlin.test.assertIs

import org.junit.Test
import org.kopi.galite.tests.examples.Training
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.domain.AutoComplete
import org.kopi.galite.visual.domain.ListDomain
import org.kopi.galite.visual.dsl.common.Actor
import org.kopi.galite.visual.dsl.common.Command
import org.kopi.galite.visual.dsl.common.FieldList
import org.kopi.galite.visual.dsl.common.ListDescription
import org.kopi.galite.visual.dsl.common.Menu
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.form.VConstants.Companion.MOD_ANY
import org.kopi.galite.visual.list.VList


class CommonDSLTests : VApplicationTestBase() {

  @Test
  fun `test Command mode function`() {
    val menu = Menu("Test")
    val actor = Actor("TActor", menu, "Test Actor", "Test Actor", 0)
    val command = Command(actor)

    assertEquals(command.mode, MOD_ANY) // default mode is MOD_ANY

    command.mode(Mode.INSERT)
    assertEquals(command.mode, 2) // mode = 0 | 1 << 1

    command.mode(Mode.INSERT, Mode.UPDATE)
    assertEquals(command.mode, 6) // mode = 0 | 1 << 1 | 1 << 2

    command.mode(Mode.INSERT, Mode.UPDATE, Mode.QUERY)
    assertEquals(command.mode, 7) // mode = 0 | 1 << 1 | 1 << 2 || 1 << 0
  }

  @Test
  fun `test FieldList`() {
    val fieldList = FieldList<String>("String", Training, null, mutableListOf(), 0, 0, false)

    assertEquals(fieldList.hasAction(), false)
    assertEquals(fieldList.hasShortcut(), false)

    val vFieldList = fieldList.buildListModel("Source")

    assertIs<VList>(vFieldList)
    assertEquals(vFieldList.action, null)
    assertEquals(vFieldList.autocompleteLength, 0)
    assertEquals(vFieldList.autocompleteType, 0)
    assertEquals(vFieldList.table, Training)
  }

  @Test
  fun `test ListDescription`() {
    var trainingListDomain = TrainingList()
    val listDescription = ListDescription("Training Title", Training.trainingName, trainingListDomain)
    val vListColumn = listDescription.buildModel()

    assertEquals(vListColumn.column, Training.trainingName)
    assertEquals(vListColumn.title, "Training Title")
  }
}

class TrainingList : ListDomain<Int>(20) {

  override val table = Training

  val autoComplete = complete(AutoComplete.LEFT, 1)

  init {
    "ID" keyOf Training.id
    "Name" keyOf Training.trainingName
    "type" keyOf Training.type
    "UNIT_PRICE" keyOf Training.price
    "ACTIVE" keyOf Training.active
    "PHOTO" keyOf Training.photo
    "INFORMATION" keyOf Training.informations
    "UC" keyOf Training.uc
    "TS" keyOf Training.ts
  }
}
