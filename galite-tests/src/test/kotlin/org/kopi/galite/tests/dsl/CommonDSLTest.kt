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

package org.kopi.galite.tests.dsl

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

import org.junit.Test
import org.kopi.galite.tests.examples.Training
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.domain.ListDomain
import org.kopi.galite.visual.Actor
import org.kopi.galite.visual.dsl.common.Command
import org.kopi.galite.visual.dsl.common.FieldList
import org.kopi.galite.visual.dsl.common.ListDescription
import org.kopi.galite.visual.dsl.common.Menu
import org.kopi.galite.visual.dsl.common.Mode
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.form.VConstants.Companion.MOD_ANY
import org.kopi.galite.visual.list.VList
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.ActionHandler

class CommonDSLTests : VApplicationTestBase() {

  @Test
  fun `test Command mode function`() {
    val menu = Menu("Test")
    val actor = Actor(menu, "Test Actor", "Test Actor", "TActor")
    val command = Command(actor, arrayOf(),
                          object : ActionHandler {
                            override fun executeVoidTrigger(VKT_Type: Int) {}
                            override fun executeVoidTrigger(trigger: Trigger?) {}
                            override fun performAsyncAction(action: Action) {}
                            override fun performAction(action: Action, block: Boolean) {}
                          }) {}

    assertEquals(MOD_ANY, command.mode) // default mode is MOD_ANY

    command.setModes(Mode.INSERT)
    assertEquals(2, command.mode) // mode = 0 | 1 << 1

    command.setModes(Mode.INSERT, Mode.UPDATE)
    assertEquals(6, command.mode) // mode = 0 | 1 << 1 | 1 << 2

    command.setModes(Mode.INSERT, Mode.UPDATE, Mode.QUERY)
    assertEquals(7, command.mode) // mode = 0 | 1 << 1 | 1 << 2 || 1 << 0
  }

  @Test
  fun `test FieldList`() {
    val fieldList = FieldList<String>("String", { Training }, null, mutableListOf(), 0, 0, false)

    assertFalse(fieldList.hasAction())
    assertFalse(fieldList.hasShortcut())

    val vFieldList = fieldList.buildListModel("Source", "ident")

    assertIs<VList>(vFieldList)
    assertEquals(null, vFieldList.action)
    assertEquals(0, vFieldList.autocompleteLength)
    assertEquals(0, vFieldList.autocompleteType)
    assertEquals(Training, vFieldList.table())
  }

  @Test
  fun `test ListDescription`() {
    val trainingListDomain = TrainingList()
    val listDescription = ListDescription("Training Title", Training.trainingName, trainingListDomain)
    val vListColumn = listDescription.buildModel()

    assertEquals(Training.trainingName, vListColumn.column)
    assertEquals("Training Title", vListColumn.title)
  }
}

class TrainingList : ListDomain<Int>(20) {
  override val table = Training
}
