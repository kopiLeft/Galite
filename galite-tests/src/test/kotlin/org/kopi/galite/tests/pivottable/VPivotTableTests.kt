/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2023 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.tests.pivottable

import java.awt.event.KeyEvent

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.pivottable.Dimension
import org.kopi.galite.visual.pivottable.VPivotTable
import org.kopi.galite.visual.VActor
import org.kopi.galite.visual.domain.INT

/**
 *
 * This class contains tests for the pivot table model [VPivotTable]
 *
 * @see VPivotTable
 */
class VPivotTableTests: VApplicationTestBase() {

  /**
   * Checks that f12 actor is the first pivot table actor.
   */
  @Test
  fun pivotTableVActorTest() {
    withPivotTable {
      val f12 = VActor("File",
                       "org/kopi/galite/visual/Window",
                       "GotoShortcuts",
                       "org/kopi/galite/visual/Window",
                       null,
                       KeyEvent.VK_F12,
                       0)
              .also {
                it.menuName = "File"
                it.menuItem = "Shortcuts"
              }

      // Actor checks
      assertEquals(f12, model.actors[0])
      assertEquals(f12, model.actors[0])
      assertEquals(-8, model.actors[0].number)
    }
  }

  /**
   * Tests the pivot table data model
   */
  @Test
  fun vmodelTest() {
    withPivotTable {
      val name = dimension(STRING(20), Dimension.Position.ROW) {
        label = "name"
        help = "The user name"
      }

      val age = measure(INT(4)) {
        label = "Age"
        help = "The age of the user"
      }

      add {
        this[name] = "Riadh"
        this[age] = 27
      }

      // Dimension checks
      assertEquals("DIMENSION_0", name.ident)
      assertEquals("name", name.label)
      assertEquals("The user name", name.help)
      assertEquals(Dimension.Position.ROW, name.position)

      // Measure checks
      assertEquals("MEASURE_0", age.ident)
      assertEquals("Age", age.label)
      assertEquals("The age of the user", age.help)
    }
  }
}
