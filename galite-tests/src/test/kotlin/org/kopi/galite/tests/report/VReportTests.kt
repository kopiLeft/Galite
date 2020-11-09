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
package org.kopi.galite.tests.report

import org.junit.Test
import org.kopi.galite.domain.Domain
import org.kopi.galite.report.Constants
import org.kopi.galite.report.PConfig
import org.kopi.galite.report.Report

import org.kopi.galite.report.VReport
import org.kopi.galite.tests.ApplicationTestBase
import org.kopi.galite.visual.VActor
import java.awt.event.KeyEvent
import java.util.*
import kotlin.test.assertEquals

/**
 *
 * This class contains tests for the report model [VReport]
 *
 * @see VReport
 */
class VReportTests: ApplicationTestBase() {

  /**
   * Checks that f12 actor is the first report actor.
   */
  @Test
  fun reportVAactorTest() {
    val report = SimpleReport.reportModel
    val f12 = VActor("File",
                     "org/kopi/galite/resource/Window",
                     "GotoShortcuts",
                     "org/kopi/galite/resource/Window",
                     null,
                     KeyEvent.VK_F12,
                     0)
            .also {
              it.menuName = "Fichier"
              it.menuItem = "Favoris"
            }

    // Actor checks
    assertEquals(f12, report.actors[0])
    assertEquals(f12, report.actors[0])
    assertEquals(-8, report.actors[0].number)
  }

  /**
   * Tests the report source, title, small icon and print config
   */
  @Test
  fun reportSourceTitleTest() {
    val report = SimpleReport.reportModel
    assertEquals("org/kopi/galite/tests/report/SimpleReport", report.source)
    assertEquals("SimpleReport", report.getTitle())
    assertEquals(null, report.smallIcon)
    assertEquals(PConfig(), report.printOptions)
  }

  /**
   * Tests the report data model
   */
  @Test
  fun vReportModelTest() {
    val model = SimpleReport.reportModel.model
    val id = model.columns[0]!!
    val name = model.columns[1]!!

    // Columns checks
    assertEquals("name", name.ident)
    assertEquals("name", name.label)
    assertEquals("The user name", name.help)
    assertEquals(Constants.CLO_VISIBLE, name.options)
    assertEquals(Constants.ALG_DEFAULT, name.align)
    assertEquals(-1, name.groups)
    assertEquals(null, name.function)
    assertEquals(true, name.visible)
    assertEquals(false, name.folded)
   // assertEquals(0, name.width) TODO
   // assertEquals(0, name.height) TODO
    assertEquals(true, id.isHidden())
   // assertEquals(0, name.width) TODO
   // assertEquals(0, name.height) TODO
  }

  /**
   * Simple Report with two fields.
   */
  object SimpleReport : Report() {
    override val locale = Locale.FRANCE

    override val title = "SimpleReport"

    val id = field(Domain<Int>(20)) {
      label = "id"
      help = "The user id"
      hidden = true
    }

    val name = field(Domain<String>(20)) {
      label = "name"
      help = "The user name"
    }

    val age = field(Domain<Int>(3)) {
      label = "age"
      help = "The user age"
    }

    init {
      add {
        this[id] = 1
        this[name] = "Hichem"
        this[age] = 26
      }
      add {
        this[id] = 2
        this[name] = "Sarra"
        this[age] = 23
      }
      add {
        this[id] = 3
        this[name] = "Houssem"
        this[age] = 25
      }
      add {
        this[id] = 4
        this[name] = "Zied"
        this[age] = 24
      }
    }
  }
}
