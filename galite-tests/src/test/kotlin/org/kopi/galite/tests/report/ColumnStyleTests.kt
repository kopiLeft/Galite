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
package org.kopi.galite.tests.report

import java.awt.Color

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.report.ColumnStyle

class ColumnStyleTests : VApplicationTestBase() {

  var columnStyle = ColumnStyle()

  @Test
  fun getForegroundTest() {
    columnStyle.foregroundCode = 2
    assertEquals(Color.red, columnStyle.getForeground())
  }

  @Test
  fun getBackgroundTest() {
    columnStyle.backgroundCode = 1
    assertEquals(Color.black, columnStyle.getBackground())
  }

  @Test
  fun getFontNameTest() {
    columnStyle.fontName = 3
    assertEquals("Courier", columnStyle.getFont().name)
  }

  @Test
  fun getFontTest() {
    columnStyle.fontStyle = 1
    assertEquals(1, columnStyle.getFont().style)
  }
}
