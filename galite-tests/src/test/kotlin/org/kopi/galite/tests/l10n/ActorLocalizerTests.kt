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
import org.kopi.galite.tests.common.TestBase
import org.kopi.galite.visual.l10n.ActorLocalizer
import org.kopi.galite.util.base.InconsistencyException

class ActorLocalizerTests : TestBase() {

  @Test
  fun actorLocalizerTests() {
    val root = Element("form")
    val document = Document(root)
    val actor = Element("actor")

    actor.setAttribute("ident", "ident")
    actor.setAttribute("label", "label")
    actor.setAttribute("help", "help")
    root.addContent(actor);

    //case 1
    var actorLocalizer = ActorLocalizer(document, "ident")

    assertEquals("label", actorLocalizer.getLabel())
    assertEquals("help", actorLocalizer.getHelp())

    //case 2
    var exception = assertFailsWith<InconsistencyException> {
      actorLocalizer = ActorLocalizer(document, "identError")
    }
    assertEquals("null: actor ident = identError not found", exception.message)

    //case 3
    document.rootElement.name = "test"
    exception = assertFailsWith<InconsistencyException> {
      actorLocalizer = ActorLocalizer(document, "ident")
    }
    assertEquals("bad root element [Element: <test/>]", exception.message)
  }
}
