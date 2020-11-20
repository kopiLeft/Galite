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
package org.kopi.galite.form.dsl

/**
 * This class define a column list information
 *
 * @param columns                 an array of columns
 * @param index                   the indices of this column
 * @param priority                the priority in sorting
 */
class FormFieldColumns(internal val columns: Array<FormFieldColumn?>,
                       var index: FormBlockIndex? = null,
                       var priority: Int = 0) : FieldBlock {

  /**
   * Sets the position in an array of fields
   */
  fun cloneToPos(pos: Int): FormFieldColumns {
    val clone = arrayOfNulls<FormFieldColumn>(columns.size)
    for (i in columns.indices) {
      clone[i] = columns[i]!!.cloneToPos(pos)
    }
    return FormFieldColumns(clone, index, priority)
  }
}
