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

import org.jdom2.Element
import org.kopi.galite.util.base.InconsistencyException

/**
 * Defines methods used for localization.
 */
object Utils {
  // ----------------------------------------------------------------------
  // UTILITIES
  // ----------------------------------------------------------------------
  /**
   * Returns the child with specified type and attribute = value.
   */
  fun lookupChild(parent: Element,
                  type: String,
                  attribute: String,
                  value: String): Element {
    val children = parent.getChildren(type)

    children.forEach { element ->
      if (element.getAttributeValue(attribute) != null && element.getAttributeValue(attribute) == value) {
        return element
      }
    }
    throw InconsistencyException((if (parent.document == null) "<filename not set>" else parent.document.baseURI)
            + ": " + type + " " + attribute + " = " + value + " not found")
  }

  /**
   * Returns the child with specified type.
   */
  fun lookupChild(parent: Element, type: String): Element {
    val childs: List<Element> = parent.getChildren(type)

    when {
      childs.isEmpty() -> throw InconsistencyException(parent.document.baseURI + ": "
              + type + " not found")
      childs.size > 1 -> throw InconsistencyException(parent.document.baseURI + ": "
              + type + " not unique")
      else -> return childs[0]
    }
  }
}
