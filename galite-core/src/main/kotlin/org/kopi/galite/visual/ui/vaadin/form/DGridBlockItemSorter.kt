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

import java.io.Serializable
import java.util.*

import org.kopi.galite.visual.form.VBlock

import com.vaadin.flow.data.binder.PropertyDefinition
import com.vaadin.flow.function.ValueProvider

/**
 * An item sorter implementation for the grid block. Constructs a DefaultItemSorter using the default `Comparator`
 * for comparing `Property`values.
 */
class DGridBlockItemSorter(
        private val model: VBlock
) : ValueProvider<DGridBlockContainer.GridBlockItem?, Any> {

  private var sortPropertyIds: Array<Any>? = null
  private var sortDirections: BooleanArray? = null
  private var container: PropertyDefinition<DGridBlockContainer.GridBlockItem?, Any>? = null
  private val propertyValueComparator: Comparator<Any?> = DefaultPropertyValueComparator()

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  override fun apply(source: DGridBlockContainer.GridBlockItem?): Any {
    TODO("Not yet implemented")
  }

  /*
   * (non-Javadoc)
   *
   * @see com.vaadin.data.util.ItemSorter#compare(java.lang.Object,
   * java.lang.Object)
   */
  fun compare(o1: Any, o2: Any): Int {
    val rec1 = model.getSortedPosition(o1 as Int)
    val rec2 = model.getSortedPosition(o2 as Int)
    return if (isSortedRecordFilled(rec1) && isSortedRecordFilled(rec2)) {
      if (sortPropertyIds!!.isEmpty()) {
        rec1.compareTo(rec2) // keep the records order
      } else {
        compareProperties(rec1, rec2)
      }
    } else if (!isSortedRecordFilled(rec1) && !isSortedRecordFilled(rec2)) {
      rec1.compareTo(rec2) // keep the records order
    } else if (isSortedRecordFilled(rec1)) {
      -1 // empty records are always at the bottom
    } else {
      1 // empty records are always at the bottom
    }
  }

  protected fun isSortedRecordFilled(rec: Int): Boolean {
    return (model.isRecordChanged(model.getDataPosition(rec))
            || model.isRecordFetched(model.getDataPosition(rec)))
  }

  /**
   * Compare the properties values.
   * @param o1 The first item.
   * @param o2 The second item.
   * @return The comparison result.
   */
  protected fun compareProperties(o1: Any?, o2: Any?): Int {
    TODO()
  }

  /**
   * Provides a default comparator used for comparing [Property] values.
   * The `DefaultPropertyValueComparator` assumes all objects it
   * compares can be cast to Comparable.
   *
   */
  class DefaultPropertyValueComparator : Comparator<Any?>, Serializable {
    override fun compare(o1: Any?, o2: Any?): Int {
      var result = 0

      // Normal non-null comparison
      result = if (o1 != null && o2 != null) {
        // Assume the objects can be cast to Comparable, throw
        // ClassCastException otherwise.
        (o1 as Comparable<Any?>).compareTo(o2)
      } else if (o1 == o2) {
        // Objects are equal if both are null
        0
      } else {
        if (o1 == null) {
          -1 // null is less than non-null
        } else {
          1 // non-null is greater than null
        }
      }
      return result
    }
  }
}
