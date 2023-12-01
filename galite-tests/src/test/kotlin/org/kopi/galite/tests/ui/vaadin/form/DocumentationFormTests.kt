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
package org.kopi.galite.tests.ui.vaadin.form

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.expectConfirmNotification
import org.kopi.galite.testing.expectInformationNotification
import org.kopi.galite.testing.findModel
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.examples.DocumentationForm
import org.kopi.galite.tests.examples.TestTriggers
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase

import com.github.mvysny.kaributesting.v10._find
import com.vaadin.flow.component.tabs.Tab

class DocumentationFormTests : GaliteVUITestBase() {
  val form = DocumentationForm()

  @Before
  fun `login to the App`() {
    login()

    // Open the form
    form.open()
  }

  @Test
  fun `test INIT form trigger`() {
    // check that INIT form trigger change the value of the field initTriggerForm
    assertEquals("INIT Trigger", form.formTriggers.initTriggerForm.findModel().getString())
  }

  @Test
  fun `test PREFORM form trigger`() {
    // check that PREFORM form trigger change the value of the field initTriggerForm
    assertEquals("PREFORM Trigger", form.formTriggers.preFormTriggerForm.findModel().getString())
  }

  @Test
  fun `test POSTFORM form trigger`() {

    //click on quit command to quit form and call POSTFORM trigger
    form.quit.triggerCommand()
    expectConfirmNotification(true)

    // check that POSTFORM form trigger insert value in tha database
    transaction {
      val value = TestTriggers.selectAll().last()[TestTriggers.INS]

      assertEquals("POSTFORM Trigger", value)
    }
  }

  @Test
  fun `test RESETFORM form trigger`() {
    //click on resetForm command to reset form and call RESET FORM trigger
    form._break.triggerCommand()
    expectConfirmNotification(true)
    // check that RESETFORM prevent the resent of the form and don't clear the fields
    assertEquals("INIT Trigger", form.formTriggers.initTriggerForm.findModel().getString())
    assertEquals("PREFORM Trigger", form.formTriggers.preFormTriggerForm.findModel().getString())
  }

    @Test
  fun `test Tabs in form`() {
      val tabs = _find<Tab>()

      // assert pages title
      assertEquals(form.p1.title, tabs[0].label)
      assertEquals(form.p2.title, tabs[1].label)
      assertEquals(form.p3.title, tabs[2].label)

      // assert if pages are enable
      assertTrue(tabs[0].isEnabled)
      assertTrue(tabs[1].isEnabled)
      assertFalse(tabs[2].isEnabled)

      // assert that just the firs tab is selected
      assertTrue(tabs[0].isSelected)
      assertFalse(tabs[1].isSelected)
      assertFalse(tabs[2].isSelected)
  }

  @Test
  fun `test form command`() {
    form.cut.triggerCommand()

    // assert that form command work and open an information notification as action
    expectInformationNotification("form command")
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      transaction {
        initModules()
      }
    }
  }
}
