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

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

import org.jdom2.Document
import org.jdom2.Element
import org.junit.Test
import org.kopi.galite.visual.l10n.MessageLocalizer
import org.kopi.galite.visual.util.base.InconsistencyException

class MessageLocalizerTests {

  @Test
  fun messageLocalizerTests() {
    val root = Element("messages")
    val document = Document(root)
    val message = Element("message")

    message.setAttribute("ident", "ident")
    message.setAttribute("text", "text")
    root.addContent(message);

    //case 1
    var messageLocalizer = MessageLocalizer(document, "ident")

    assertEquals("text", messageLocalizer.getText())

    //case 2
    var exception = assertFailsWith<InconsistencyException> {
      messageLocalizer = MessageLocalizer(document, "identError")
    }
    assertEquals("null: message ident = identError not found", exception.message)

    //case 3
    document.rootElement.name = "test"
    exception = assertFailsWith<InconsistencyException> {
      messageLocalizer = MessageLocalizer(document, "ident")
    }
    assertEquals("bad root element [Element: <test/>]", exception.message)
  }
}
