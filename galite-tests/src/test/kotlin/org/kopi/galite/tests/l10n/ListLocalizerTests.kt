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

import java.util.Locale

import org.jdom2.Document
import org.jdom2.Element
import org.junit.Test
import org.kopi.galite.l10n.ListLocalizer
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.util.base.InconsistencyException

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ListLocalizerTests {

  @Test
  fun listLocalizerTests() {
    val root = Element("form")
    val document = Document(root)
    val type = Element("type")
    val list = Element("list")
    val listdesc = Element("listdesc")

    type.setAttribute("ident", "ident")
    list.setAttribute("ident", "ident")
    list.setAttribute("ident2", "ident2")
    listdesc.setAttribute("column", "column")
    listdesc.setAttribute("title", "title")
    root.addContent(type)
    type.addContent(list)
    list.addContent(listdesc)

    //case 1
    val locale = Locale("fr", "FR")
    val localizationManager = LocalizationManager(locale, locale)
    var listLocalizer = ListLocalizer(localizationManager, document, "ident")

    assertEquals("title", listLocalizer.getColumnTitle("column"))

    //case 2
    var exception = assertFailsWith<InconsistencyException> {
      type.removeContent(list);
      listLocalizer = ListLocalizer(localizationManager, document, "ident")
    }
    assertEquals("null: list not found", exception.message)

    //case 3
    exception = assertFailsWith<InconsistencyException> {
      type.addContent(list)
      val list2 = Element("list")

      type.addContent(list2);
      listLocalizer = ListLocalizer(localizationManager, document, "ident")
    }
    assertEquals("null: list not unique", exception.message)
  }
}
