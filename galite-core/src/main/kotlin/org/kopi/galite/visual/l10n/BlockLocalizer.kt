/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.l10n

import org.jdom2.Document
import org.jdom2.Element
import org.kopi.galite.visual.util.base.InconsistencyException

/**
 * Implements a block localizer.
 *
 * @param             manager         the manager to use for localization
 * @param             document        the document containing the block localization
 * @param             ident           the identifier of the block
 */
class BlockLocalizer(manager: LocalizationManager,
                     document: Document,
                     ident: String) : Localizer(manager) {
  /**
   * Returns the value of the title attribute.
   */
  fun getTitle(): String = self.getAttributeValue("title")

  /**
   * Returns the value of the help attribute.
   */
  fun getHelp(): String? = self.getAttributeValue("help")

  /**
   * Returns the message for the specified index.
   *
   * @param             ident           the identifier of the index
   */
  fun getIndexMessage(ident: String): String {
    val e: Element = Utils.lookupChild(self, "index", "ident", ident)
    return e.getAttributeValue("message")
  }

  /**
   * Constructs a field localizer for the given field.
   *
   * @param             ident           the identifier of the field
   */
  fun getFieldLocalizer(ident: String): FieldLocalizer {
    return FieldLocalizer(manager, Utils.lookupChild(self, "field", "ident", ident))
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private val self: Element

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  init {
    val root: Element = document.rootElement
    val names = listOf("form", "blockinsert")

    if (root.name !in names) {
      throw InconsistencyException("bad root element $root")
    }
    self = Utils.lookupChild(root, "block", "name", ident)
  }
}
