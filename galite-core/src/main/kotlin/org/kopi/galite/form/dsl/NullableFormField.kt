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

import org.jetbrains.exposed.sql.Column
import org.kopi.galite.domain.Domain
import org.kopi.galite.type.Type
import java.math.BigDecimal

/**
 * This class represents a nullable form field(visit, skipped and hidden).
 *
 */
class NullableFormField<T: Any?>(block: FormBlock,
                           domain: Domain<T>,
                           fieldIndex: Int,
                           initialAccess: Int,
                           position: FormPosition? = null)
            : FormField<T>(block,
                           domain,
                           fieldIndex,
                           initialAccess,
                           position) {

  /**
   * Assigns [columns] to this field.
   *
   * @param joinColumns columns to use to make join between block tables
   * @param init        initialises the form field column properties (index, priority...)
   */
  fun <V: T> columns(vararg joinColumns: Column<V>, init: (FormFieldColumns<T>.() -> Unit)? = null) {
    initColumn(*joinColumns, init = init)
  }

  /**
   * Assigns [columns] to this field.
   *
   * @param joinColumns columns to use to make join between block tables
   * @param init        initialises the form field column properties (index, priority...)
   */
  fun <K: BigDecimal?> FormField<T>.columns(vararg joinColumns: Column<K>, init: (FormFieldColumns<T>.() -> Unit)? = null) {
    initColumn(*joinColumns, init = init)
  }

  /**
   * Assigns [columns] to this field.
   *
   * @param joinColumns columns to use to make join between block tables
   * @param init        initialises the form field column properties (index, priority...)
   */
  @JvmName("otherTypesColumns")
  fun <V: Type<T, K>, K> FormField<T>.columns(vararg joinColumns: Column<K>, init: (FormFieldColumns<T>.() -> Unit)? = null) {
    initColumn(*joinColumns, init = init)
  }
}
