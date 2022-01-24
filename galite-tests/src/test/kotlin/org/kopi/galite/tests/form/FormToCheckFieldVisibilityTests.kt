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
package org.kopi.galite.tests.form

import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.kopi.galite.tests.ui.swing.JApplicationTestBase

class FormToCheckFieldVisibilityTests: JApplicationTestBase() {
  @Test
  fun changeBlockAccessTest() {
    FormToCheckFieldVisibility.model

    assertArrayEquals(intArrayOf(1, 0, 1), FormToCheckFieldVisibility.testBlock.name.access)
    assertArrayEquals(intArrayOf(4, 4, 2), FormToCheckFieldVisibility.testBlock.age.access)
    assertArrayEquals(intArrayOf(0, 1, 4), FormToCheckFieldVisibility.testBlock.gender.access)
    assertArrayEquals(intArrayOf(2, 2, 0), FormToCheckFieldVisibility.testBlock.country.access)
  }
}
