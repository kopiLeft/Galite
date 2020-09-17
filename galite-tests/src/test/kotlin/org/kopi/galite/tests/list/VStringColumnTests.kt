/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.tests.list

import org.junit.Test
import org.kopi.galite.list.VImageColumn
import org.kopi.galite.list.VListColumn
import org.kopi.galite.list.VStringColumn
import org.mozilla.javascript.ScriptRuntime.getObjectIndex
import kotlin.test.assertEquals

class VStringColumnTests {

  @Test
  fun vStringColumnTests() {
    val vStringColumn = VStringColumn("title", "column", 1, 2, true)

    assertEquals("", vStringColumn.formatObject(null))
    assertEquals("St...ring", vStringColumn.formatObject("String"))
    assertEquals("java.lang.String", vStringColumn.getDataType().typeName)
  }
}