/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2024 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.util.xsdToFactory.utils

import java.util.*

import org.apache.xmlbeans.XmlError

class XmlErrorWatcher (private val underlying: MutableCollection<XmlError>?) :
  AbstractCollection<XmlError>() {
  override fun add(element: XmlError): Boolean {
    if (firstError == null && element.severity == XmlError.SEVERITY_ERROR) {
      firstError = element
    }
    if (underlying == null) {
      return false
    }
    return underlying.add(element)
  }

  override fun iterator(): MutableIterator<XmlError> {
    if (underlying == null) {
      return ArrayList<XmlError>().iterator()
    }

    return underlying.iterator()
  }

  override val size: Int get() = underlying?.size ?: 0

  private var firstError: XmlError? = null
}