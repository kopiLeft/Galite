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
import org.kopi.galite.report.Point
import kotlin.test.assertEquals

class PointTests {
  val origin = Point()
  val customPoint = Point(4, 5)
  val customPoint2 = Point(4, 5)

  @Test
  fun testOriginPoint() {
    assertEquals(0, origin.x)
    assertEquals(0, origin.y)
    assertEquals(false, origin.equals(customPoint))
  }

  @Test
  fun testCustomPoint() {
    assertEquals(4, customPoint.x)
    assertEquals(5, customPoint.y)
    assertEquals(false, customPoint.equals(origin))
  }
}
