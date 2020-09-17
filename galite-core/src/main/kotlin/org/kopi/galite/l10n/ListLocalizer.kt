/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.l10n

import org.jdom2.Document
import org.jdom2.Element
import org.kopi.galite.util.base.InconsistencyException

/**
 * Implements a list localizer.
 *
 * @param             manager         the manager to use for localization
 * @param             document        the document containing the list localization
 * @param             ident           the identifier of the list
 */
class ListLocalizer(manager: LocalizationManager,
                    document: Document,
                    ident: String) : Localizer(manager) {
  /**
   * Returns the title of the specified item.
   *
   * @param             column          the identifier of the column
   */
  fun getColumnTitle(column: String): String = Utils.lookupChild(self, "listdesc", "column", column).getAttributeValue("title")

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private val self: Element

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  init {
    val root: Element = document.rootElement
    val names = listOf("form", "report", "blockinsert", "insert")

    if (root.name !in names) {
      throw InconsistencyException("bad root element $root")
    }
    val type: Element = Utils.lookupChild(root, "type", "ident", ident)

    self = Utils.lookupChild(type, "list")
  }
}
