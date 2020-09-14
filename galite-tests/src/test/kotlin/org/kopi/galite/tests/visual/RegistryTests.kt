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

package org.kopi.galite.tests.visual

import org.joda.time.Weeks
import org.junit.Test
import org.kopi.galite.visual.Registry
import kotlin.test.assertEquals

class RegistryTests {
  val registry1 = Registry("firstDomaineName", null)
  val registry2 = Registry("secondDomaineName", null)
  val testRegistry = Registry("mainDomain", arrayOf(registry1, registry2))

  @Test
  fun registryTest() {
    testRegistry.buildDependencies()
    assertEquals("org.kopi.galite.visual.Messages", testRegistry.dependencies["mainDomain"])
    assertEquals("org.kopi.galite.resource.Messages", testRegistry.dependencies["VIS"])
    assertEquals(true, testRegistry.dependencies.containsKey("firstDomaineName"))
    assertEquals(true, testRegistry.dependencies.containsKey("secondDomaineName"))
    val a: Weeks
  }
}
