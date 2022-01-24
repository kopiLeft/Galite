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

package org.kopi.galite.tests.l10n

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

import org.jdom2.Document
import org.jdom2.Element
import org.junit.Test
import org.kopi.galite.visual.l10n.ModuleLocalizer
import org.kopi.galite.visual.util.base.InconsistencyException

class ModuleLocalizerTests {

  @Test
  fun moduleLocalizerTests() {
    val root = Element("modules")
    val document = Document(root)
    val module = Element("module")

    module.setAttribute("ident", "ident")
    module.setAttribute("label", "label")
    module.setAttribute("help", "help")
    root.addContent(module);

    //case 1
    var moduleLocalizer = ModuleLocalizer(document, "ident")

    assertEquals("label", moduleLocalizer.getLabel())
    assertEquals("help", moduleLocalizer.getHelp())

    //case 2
    var exception = assertFailsWith<InconsistencyException> {
      moduleLocalizer = ModuleLocalizer(document, "identError")
    }
    assertEquals("null: module ident = identError not found", exception.message)

    //case 3
    document.rootElement.name = "test"
    exception = assertFailsWith<InconsistencyException> {
      moduleLocalizer = ModuleLocalizer(document, "ident")
    }
    assertEquals("bad root element [Element: <test/>]", exception.message)
  }
}
