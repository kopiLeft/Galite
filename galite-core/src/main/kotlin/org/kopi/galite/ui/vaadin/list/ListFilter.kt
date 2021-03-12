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
package org.kopi.galite.ui.vaadin.list

import com.vaadin.flow.component.grid.contextmenu.GridMenuItem
import com.vaadin.flow.data.provider.DataCommunicator
import com.vaadin.flow.data.provider.FilterUtils
import org.kopi.galite.report.VReportRow

class ListFilter(private val propertyId: Any?,
                 filterString: String,
                 ignoreCase: Boolean,
                 private val onlyMatchPrefix: Boolean)   {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

   fun passesFilter(itemId: Any?, item: GridMenuItem<VReportRow>): Boolean {
/*

    val property: ListProperty?
    val propertyValue: Any?
    val value: String

    if (property == null) {
      return false
    }
    propertyValue = property.getValue()
    if (propertyValue == null) {
      return false
    }
    value = if (ignoreCase) property.formatObject(propertyValue).toString().toLowerCase() else property.formatObject(propertyValue).toString()
    if (onlyMatchPrefix) {
      if (!value.startsWith(filterString!!)) {
        return false
      }
    } else {
      if (!value.contains(filterString!!)) {
        return false
      }
    }
*/

    return true
  }

  fun appliesToProperty(propertyId: Any): Boolean {
    return this.propertyId == propertyId
  }

  override fun equals(other: Any?): Boolean {
    if (other == null) {
      return false
    }

    // Only ones of the objects of the same class can be equal
    if (other !is ListFilter) {
      return false
    }
    val o = other

    // Checks the properties one by one
    if (propertyId != o.propertyId && o.propertyId != null && o.propertyId != propertyId) {
      return false
    }
    if (filterString != o.filterString && o.filterString != null && o.filterString != filterString) {
      return false
    }
    if (ignoreCase != o.ignoreCase) {
      return false
    }
    return onlyMatchPrefix == o.onlyMatchPrefix
  }

  override fun hashCode(): Int {
    return (propertyId?.hashCode() ?: 0) xor (filterString?.hashCode() ?: 0)
  }

  private val filterString: String? = if (ignoreCase) filterString.toLowerCase() else filterString
  private val ignoreCase: Boolean = ignoreCase
}
