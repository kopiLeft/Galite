/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.ui.report

import java.util.Optional

import com.vaadin.flow.data.binder.PropertyDefinition
import com.vaadin.flow.data.binder.PropertySet
import com.vaadin.flow.data.binder.Setter
import com.vaadin.flow.function.ValueProvider

import org.kopi.galite.report.MReport

/**
 * The `VTable` is a Vaadin [Grid] data container adapted
 * to dynamic reports needs.
 *
 * @param model The table model.
 */
class VTable(val model: MReport): PropertyDefinition<VTable, String> {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  fun getContainerPropertyIds(): Collection<*>? {
    TODO()
  }



  fun getType(propertyId: Any?): Class<*>? {
    TODO()
  }


  fun size(): Int {
    TODO()
  }

  protected fun getAllItemIds(): List<Int>? {
    TODO()
  }


  /**
   * Returns the column name of a given column index.
   * @param column The column index.
   * @return The column name.
   */
  fun getColumnName(column: Int): String {
    val label: String = model.accessibleColumns[column]!!.label
    return if (label == null || label.isEmpty()) {
      ""
    } else label
  }

  /**
   * Returns the column align.
   * @param column The column index.
   * @return The column align.
   */
  fun getColumnAlign(column: Int): Int {
    TODO()
  }

  /**
   * Returns the column count.
   * @return the column count.
   */
  fun getColumnCount(): Int {
    return model.getColumnCount()
  }

  /**
   * Builds a [List] if [Integer] IDs.
   * @param length The ID list length.
   * @return The IDs list.
   */
  private fun buildIds(length: Int): List<Int>? {
    val ids: MutableList<Int> = ArrayList(length)
    for (i in 0 until length) {
      ids.add(i)
    }
    return ids
  }

  override fun getType(): Class<String> {
    return String::class.java
  }

  override fun getGetter(): ValueProvider<VTable, String> = TableValueProvider()

  override fun getSetter(): Optional<Setter<VTable, String>>? = null

  override fun getPropertyHolderType(): Class<*> {
    return String::class.java
  }

  override fun getName(): String = ""

  override fun getCaption(): String =  ""

  override fun getPropertySet(): PropertySet<VTable> {
    TODO()
  }

  override fun getParent(): PropertyDefinition<VTable, *>? = null

  class TableValueProvider : ValueProvider<VTable, String> {

    override fun apply(source: VTable): String {
      TODO()
    }
  }
}
