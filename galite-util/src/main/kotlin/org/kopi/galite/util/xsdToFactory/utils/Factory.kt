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

import org.apache.xmlbeans.SchemaType

class Factory(var type: Int,
              var name: String?,
              var packageName: String?,
              var isPrintHeader: Boolean?,
              var content: Array<SchemaType>) {
    /**
     * Get the full name of the created factory
     */
    val fullName: String
      get() = name + typeAsString

  val fileExtension: String
    get() = Constants.KOTLIN_EXT

    /**
     * Translate the type of a factory into a string type to concat with its name
     */
    private val typeAsString: String
      get() {
        when (type) {
          0 -> return "Factory"
          1 -> return "DocumentFactory"
          2 -> return "AttributeFactory"
        }

        return "Factory"
      }
}