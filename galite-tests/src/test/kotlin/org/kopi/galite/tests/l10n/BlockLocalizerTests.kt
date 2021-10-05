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

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

import org.jdom2.Document
import org.jdom2.Element
import org.junit.Test
import org.kopi.galite.tests.common.TestBase
import org.kopi.galite.visual.l10n.BlockLocalizer
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.util.base.InconsistencyException

class BlockLocalizerTests : TestBase() {

  @Test
  fun blockLocalizer() {
    val root = Element("form")
    val document = Document(root)
    val block = Element("block")
    val field = Element("field")

    block.setAttribute("name", "ident")
    block.setAttribute("title", "title")
    block.setAttribute("help", "help")
    root.addContent(block);

    field.setAttribute("ident", "ident")
    field.setAttribute("label", "label")
    field.setAttribute("help", "help")
    block.addContent(field);

    //case 1
    val locale = Locale("fr", "FR")
    val localizationManager = LocalizationManager(locale, locale)
    var blockLocalizer = BlockLocalizer(localizationManager, document, "ident")

    assertEquals("title", blockLocalizer.getTitle())
    assertEquals("help", blockLocalizer.getHelp())
    assertEquals("label", blockLocalizer.getFieldLocalizer("ident").getLabel())
    assertEquals("help", blockLocalizer.getFieldLocalizer("ident").getHelp())

    //case 2
    var exception = assertFailsWith<InconsistencyException> {
      blockLocalizer = BlockLocalizer(localizationManager, document, "identError")
    }
    assertEquals("null: block name = identError not found", exception.message)

    //case 3
    document.rootElement.name = "test"
    exception = assertFailsWith<InconsistencyException> {
      blockLocalizer = BlockLocalizer(localizationManager, document, "ident")
    }
    assertEquals("bad root element [Element: <test/>]", exception.message)
  }
}
