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
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.l10n.PropertyLocalizer
import org.kopi.galite.visual.util.base.InconsistencyException

class PropertyLocalizerTests : VApplicationTestBase() {

  @Test
  fun propertyLocalizerTests() {
    val root = Element("properties")
    val document = Document(root)
    val property = Element("property")

    property.setAttribute("key", "ident")
    property.setAttribute("value", "value")
    property.setAttribute("help", "help")
    root.addContent(property);

    //case 1
    var propertyLocalizer = PropertyLocalizer(document, "ident")

    assertEquals("value", propertyLocalizer.getValue())


    //case 2
    var exception = assertFailsWith<InconsistencyException> {
      propertyLocalizer = PropertyLocalizer(document, "identError")
    }
    assertEquals("null: property key = identError not found", exception.message)

    //case 3
    document.rootElement.name = "test"
    exception = assertFailsWith<InconsistencyException> {
      propertyLocalizer = PropertyLocalizer(document, "ident")
    }
    assertEquals("bad root element [Element: <test/>]", exception.message)
  }
}
