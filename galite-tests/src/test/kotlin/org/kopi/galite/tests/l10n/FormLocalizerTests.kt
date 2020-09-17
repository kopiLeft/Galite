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

package org.kopi.galite.tests.l10n

import org.jdom2.Document
import org.jdom2.Element
import org.junit.Test
import org.kopi.galite.l10n.FormLocalizer
import org.kopi.galite.util.base.InconsistencyException

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FormLocalizerTests {

  @Test
  fun formLocalizer() {
    val root = Element("form")
    val document = Document(root)
    val page = Element("page")

    root.setAttribute("title", "title")
    page.setAttribute("ident", "Id$1")
    page.setAttribute("title", "titleOfPage-1")
    root.addContent(page)

    //case 1
    var formLocalizer = FormLocalizer(document)

    assertEquals("title", formLocalizer.getTitle())
    assertEquals("titleOfPage-1", formLocalizer.getPage(1))

    //case 2
    var exception = assertFailsWith<InconsistencyException> {
      document.rootElement.name = "test"
      formLocalizer = FormLocalizer(document)
    }
    assertEquals("bad root element [Element: <test/>]", exception.message)

    //case 3
    exception = assertFailsWith<InconsistencyException> {
      println(formLocalizer.getPage(2))
    }
    assertEquals("page 2 not found", exception.message)
  }
}
