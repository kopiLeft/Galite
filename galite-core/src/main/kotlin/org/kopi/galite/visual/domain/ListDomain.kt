/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.visual.domain

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.QueryAlias
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.alias
import org.kopi.galite.visual.dsl.common.FieldList
import org.kopi.galite.visual.dsl.common.ListDescription
import org.kopi.galite.visual.dsl.common.LocalizationWriter
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.list.VList

/**
 * Represents a list domain.
 *
 * The possible values are obtained from the result of a database query.
 *
 * It allows optionally to define a constraint that makes restrictions
 * on the set of allowed values.
 */
abstract class ListDomain<T>(width: Int? = null,
                             height: Int? = null,
                             visibleHeight: Int? = null)
  : Domain<T>(width, height, visibleHeight) {

  /**
   * The statement to select list data. It can get an instance of [ColumnSet] (like a [Table])
   * or a query using [query] method.
   *
   * @sample org.kopi.galite.tests.form.UsersList
   */
  abstract val table: ColumnSet

  /**
   * The field list action. Used to select list data from a form.
   *
   * @sample org.kopi.galite.tests.form.UsersList
   */
  open val access: (() -> DictionaryForm)? = null

  /**
   * Override it if you want to define a constraint that the domain values must verify.
   */
  var check: ((value: T) -> Boolean)? = null

  private var convertUpper = false

  /**
   * The list of elements added to this list domain. Each element describes a column and the title assigned to it.
   */
  val columns = mutableListOf<ListDescription>()

  /**
   * the auto-complete type. [VList.AUTOCOMPLETE_NONE] is the default
   */
  private var autocompleteType = VList.AUTOCOMPLETE_NONE

  /**
   * the auto-complete length
   */
  private var autocompleteLength = 0

  /**
   * Sets a mapping between the values that the domain can take
   * and a corresponding text to be displayed in a field.
   *
   * @receiver the text
   * @param value the value
   */
  infix fun String.keyOf(value: Column<*>): ListDescription {
    val listDescription = ListDescription(this, value, this@ListDomain)

    columns.add(listDescription)

    return listDescription
  }

  /**
   * Sets the width for this list description.
   */
  infix fun ListDescription.hasWidth(width: Int) {
    this.width = width
  }

  /**
   * Mapping of all values that a domain can take
   */
  val list: FieldList<T>
    get() = FieldList(ident,
                      { table },
                      access,
                      columns,
                      autocompleteType,
                      autocompleteLength,
                      access != null)

  /**
   * Transforms values in capital letters.
   */
  fun Domain<String>.convertUpper() {
    convertUpper = true
  }

  /**
   * Checks if the value passed to the field respects the [domain.check] constraint
   *
   * @param value passed value
   * @return  true if the domain is not defined or if the values if verified by the domain's constraint
   * @throws InvalidValueException otherwise
   */
  fun checkValue(value: T): Boolean = when {
    check == null || check!!.invoke(value) -> true
    else -> false
  }

  /**
   * Defines the auto-complete mechanism.
   *
   * @param autocompleteType    the auto-complete type. See [AutoComplete] to get the supported types.
   * @param autocompleteLength  the auto-complete length.
   */
  fun complete(autocompleteType: AutoComplete, autocompleteLength: Int) {
    this.autocompleteType = autocompleteType.value
    this.autocompleteLength = autocompleteLength
  }

  /**
   * Creates a [QueryAlias] that can be quarried as a [ColumnSet] from a [Query]
   *
   * @param query the query
   */
  fun query(query: Query): QueryAlias {
    return query.alias("syn__0__")
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------
  /**
   * Generate localization for this type.
   * When overridden, subclasses MUST call it (because of lists).
   *
   */
  override fun genTypeLocalization(writer: LocalizationWriter) {
    writer.genType(list)
  }
}
