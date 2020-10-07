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

package org.kopi.galite.form

import org.kopi.galite.list.VBooleanColumn
import org.kopi.galite.list.VListColumn
import org.kopi.galite.visual.VlibProperties

class VBooleanField : VBooleanCodeField("boolean",
                                         GENERAL_LOCALIZATION_RESOURCE,
                                         booleanNames,
                                         booleanCodes) {

  /**
   * return the name of this field
   */
  override fun getTypeInformation(): String = VlibProperties.getString("boolean-type-field")

  /**
   * return the name of this field
   */
  override fun getTypeName(): String = VlibProperties.getString("Boolean")

  /**
   * Returns a list column for list.
   */
  override fun getListColumn(): VListColumn = VBooleanColumn(getHeader(), null, getPriority() >= 0)

  companion object {

    /**
     * return the text value
     */
    fun toText(o: Boolean): String = booleanNames[if (o) 0 else 1]

   /*
   * ----------------------------------------------------------------------
   * DATA MEMBERS
   * ----------------------------------------------------------------------
   */

    private const val GENERAL_LOCALIZATION_RESOURCE = "resource/org/kopi/galite/General"
    private val booleanNames = arrayOf("true", "false")
    private val booleanCodes = arrayOf(java.lang.Boolean.TRUE, java.lang.Boolean.FALSE)
  }
}
