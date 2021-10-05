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
import org.kopi.galite.visual.l10n.ChartLocalizer
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.util.base.InconsistencyException

class ChartLocalizerTests : TestBase() {

  @Test
  fun chartLocalizer() {
    val root = Element("chart")
    val document = Document(root)
    val field = Element("field");

    root.setAttribute("name", "ident");
    root.setAttribute("title", "title");
    root.setAttribute("help", "help");
    field.setAttribute("ident", "ident");
    field.setAttribute("label", "label");
    field.setAttribute("help", "help");
    root.addContent(field);

    //case 1
    val locale = Locale("fr", "FR")
    val localizationManager = LocalizationManager(locale, locale)
    var chartLocalizer = ChartLocalizer(localizationManager, document)

    assertEquals("title", chartLocalizer.getTitle())
    assertEquals("help", chartLocalizer.getHelp())
    assertEquals("label", chartLocalizer.getFieldLocalizer("ident").getLabel())
    assertEquals("help", chartLocalizer.getFieldLocalizer("ident").getHelp())

    //case 2
    document.rootElement.name = "test"
    var exception = assertFailsWith<InconsistencyException> {
      chartLocalizer = ChartLocalizer(localizationManager, document)
    }
    assertEquals("bad root element [Element: <test/>]", exception.message)
  }

}
