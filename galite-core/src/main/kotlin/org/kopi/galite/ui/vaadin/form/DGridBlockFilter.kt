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
package org.kopi.galite.ui.vaadin.form

import org.kopi.galite.form.VField

import com.vaadin.flow.function.SerializablePredicate

class DGridBlockFilter(
        private val field: VField,
        filterString: String,
        private val ignoreCase: Boolean,
        private val onlyMatchPrefix: Boolean
) : SerializablePredicate<DGridBlockContainer.GridBlockItem> {

  private val filterString = if (ignoreCase) filterString.toLowerCase() else filterString

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------

  fun appliesToProperty(propertyId: Any): Boolean {
    return this.field == propertyId // TODO
  }

  override fun equals(obj: Any?): Boolean {
    if (obj == null) {
      return false
    }

    // Only ones of the objects of the same class can be equal
    if (obj !is DGridBlockFilter) {
      return false
    }
    val o = obj

    // Checks the properties one by one
    if (field != o.field && o.field != null && o.field != field) {
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

  override fun hashCode(): Int = (field?.hashCode() ?: 0) xor (filterString?.hashCode() ?: 0)

  override fun test(item: DGridBlockContainer.GridBlockItem): Boolean {
    val value = if (ignoreCase) item.getValue(field).toString().toLowerCase()
                  else item.getValue(field).toString()

    if (onlyMatchPrefix) {
      if (!value.startsWith(filterString)) {
        return false
      }
    } else {
      if (!value.contains(filterString)) {
        return false
      }
    }
    return true
  }
}
