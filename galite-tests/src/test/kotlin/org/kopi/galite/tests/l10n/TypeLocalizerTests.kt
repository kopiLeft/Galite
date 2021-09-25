/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.l10n.TypeLocalizer
import org.kopi.galite.visual.util.base.InconsistencyException

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TypeLocalizerTests {

  @Test
  fun listLocalizerTests() {
    val root = Element("form")
    val document = Document(root)
    val type = Element("type")
    val code = Element("code")
    val codedesc = Element("codedesc")

    type.setAttribute("ident", "ident")
    code.setAttribute("ident", "ident")
    code.setAttribute("ident2", "ident2")
    codedesc.setAttribute("ident", "ident")
    codedesc.setAttribute("label", "label")
    root.addContent(type)
    type.addContent(code)
    code.addContent(codedesc)

    //case 1
    val locale = Locale("fr", "FR")
    val localizationManager = LocalizationManager(locale, locale)
    var typeLocalizer = TypeLocalizer(localizationManager, document, "ident")

    assertEquals("label", typeLocalizer.getCodeLabel("ident"))

    //case 2
    var exception = assertFailsWith<InconsistencyException> {
      type.removeContent(code);
      typeLocalizer = TypeLocalizer(localizationManager, document, "ident")
    }
    assertEquals("null: code not found", exception.message)

    //case 3
    exception = assertFailsWith<InconsistencyException> {
      type.addContent(code)
      val code2 = Element("code")

      type.addContent(code2);
      typeLocalizer = TypeLocalizer(localizationManager, document, "ident")
    }
    assertEquals("null: code not unique", exception.message)
  }
}
