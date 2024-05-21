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

import java.time.Year

interface Constants {
  companion object {
    const val KOTLIN_EXT = "kt"
    const val XSD_EXT = "xsd"
    const val WSDL_EXT = "wsdl"
    const val CONF_EXT = "xsdconfig"
    const val INDENTATION: Int = 2

    /**
     * Catégories de fabrique
     */
    const val TYPES = 0
    const val DOCUMENTS = 1
    const val ATTRIBUTES = 2

    val HEADER = "// ----------------------------------------------------------------------\n" +
                 "// Copyright (c) 2013-${Year.now()} kopiLeft Services SARL, Tunisie\n" +
                 "// Copyright (c) 2018-${Year.now()} ProGmag SAS, France\n" +
                 "// ----------------------------------------------------------------------\n" +
                 "// All rights reserved - tous droits réservés.\n" +
                 "// ----------------------------------------------------------------------\n"
  }
}
