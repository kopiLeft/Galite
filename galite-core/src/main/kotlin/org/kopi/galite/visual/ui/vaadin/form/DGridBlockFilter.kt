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
package org.kopi.galite.visual.ui.vaadin.form

import org.kopi.galite.visual.form.VField

import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.function.SerializablePredicate

class DGridBlockFilter(
        private val filterFields: List<FilterField>,
        private val ignoreCase: Boolean,
        private val onlyMatchPrefix: Boolean
) : SerializablePredicate<DGridBlockContainer.GridBlockItem> {

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------

  override fun test(item: DGridBlockContainer.GridBlockItem): Boolean {
    for (field in filterFields) {
      val value = if (ignoreCase) formatObject(item, field.model).toString().toLowerCase() else formatObject(item, field.model).toString()
      val filterString = if (ignoreCase) field.value.toLowerCase() else field.value

      if (onlyMatchPrefix) {
        if (!value.startsWith(filterString)) {
          return false
        }
      } else {
        if (!value.contains(filterString)) {
          return false
        }
      }
    }

    return true
  }

  /**
   * Formats a grid item's value.
   *
   * @param item The item.
   * @param field the field model
   * @return The formatted object.
   */
  private fun formatObject(item: DGridBlockContainer.GridBlockItem, field: VField): String? =
    formatObject(item.getValue(field), field)

  /**
   * Formats an object.
   *
   * @param o The object to be formatted.
   * @param field the field model
   * @return The formatted object.
   */
  private fun formatObject(o: Any?, field: VField): String? {
    return field.toText(o)
  }

  override fun equals(obj: Any?): Boolean {
    if (obj == null) {
      return false
    }

    for (i in filterFields.indices) {
      val field = filterFields[i]
      val filterString = if (ignoreCase) field.value.toLowerCase() else field.value

      // Only ones of the objects of the same class can be equal
      if (obj !is DGridBlockFilter) {
        return false
      }
      val o = obj
      val otherField = o.filterFields[i]
      val otherFilterString = if (ignoreCase) otherField.value.toLowerCase() else otherField.value

      // Checks the properties one by one
      if (otherFilterString != filterString) {
        return false
      }
      if (ignoreCase != o.ignoreCase) {
        return false
      }
      if (onlyMatchPrefix != o.onlyMatchPrefix) {
        return false
      }
    }

    return true
  }

  override fun hashCode(): Int {
    var result = filterFields.hashCode()
    result = 31 * result + ignoreCase.hashCode()
    result = 31 * result + onlyMatchPrefix.hashCode()
    return result
  }
}

class FilterField(val model: VField, private val textField: TextField) {
  val value: String get() = textField.value
}
