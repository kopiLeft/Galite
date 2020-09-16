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
 * Implements a form localizer.
 *
 * @param             document        the document containing the form localization
 */
class FormLocalizer(document: Document) {
  /**
   * Returns the value of the title attribute.
   */
  fun getTitle(): String = root.getAttributeValue("title")

  /**
   * Returns the value of the page child.
   *
   * @param             position                the position of the page
   */
  fun getPage(position: Int): String {
    val pages: List<Element> = root.getChildren("page")
    pages.forEach {
      if (it.getAttributeValue("ident") == "Id$$position") {
        return it.getAttributeValue("title")
      }
    }
    throw InconsistencyException("page $position not found")
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private val root: Element = document.rootElement

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  init {
    if (root.name != "form") {
      throw InconsistencyException("bad root element $root")
    }
  }
}
