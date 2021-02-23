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

import org.kopi.galite.form.VBlock
import java.io.Serializable
import java.util.*

/**
 * An item sorter implementation for the grid block. Constructs a DefaultItemSorter using the default `Comparator`
 * for comparing `Property`values.
 */
class DGridBlockItemSorter(
// --------------------------------------------------
        // DATA MEMBERS
        // --------------------------------------------------
        private val model: VBlock
) : ItemSorter {
  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
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
    val item1: Item = container.getItem(o1)
    val item2: Item = container.getItem(o2)

    /*
     * Items can be null if the container is filtered. Null is considered
     * "less" than not-null.
     */if (item1 == null) {
      return if (item2 == null) {
        0
      } else {
        1
      }
    } else if (item2 == null) {
      return -1
    }
    for (i in sortPropertyIds.indices) {
      val result = compareProperty(sortPropertyIds[i], sortDirections[i], item1, item2)

      // If order can be decided
      if (result != 0) {
        return result
      }
    }
    return 0
  }

  /**
   * Compares the property indicated by `propertyId` in the items
   * indicated by `item1` and `item2` for order. Returns
   * a negative integer, zero, or a positive integer as the property value in
   * the first item is less than, equal to, or greater than the property value
   * in the second item. If the `sortDirection` is false the
   * returned value is negated.
   *
   *
   * The comparator set for this `DefaultItemSorter` is used for
   * comparing the two property values.
   *
   * @param propertyId
   * The property id for the property that is used for comparison.
   * @param sortDirection
   * The direction of the sort. A false value negates the result.
   * @param item1
   * The first item to compare.
   * @param item2
   * The second item to compare.
   * @return a negative, zero, or positive integer if the property value in
   * the first item is less than, equal to, or greater than the
   * property value in the second item. Negated if
   * `sortDirection` is false.
   */
  protected fun compareProperty(
          propertyId: Any?,
          sortDirection: Boolean,
          item1: Item,
          item2: Item
  ): Int {
    // Get the properties to compare
    val property1: Property<*> = item1.getItemProperty(propertyId)
    val property2: Property<*> = item2.getItemProperty(propertyId)
    // Get the values to compare
    val value1: Any? = if (property1 == null) null else property1.getValue()
    val value2: Any? = if (property2 == null) null else property2.getValue()
    // Result of the comparison
    var result = 0
    result = if (sortDirection) {
      propertyValueComparator.compare(value1, value2)
    } else {
      propertyValueComparator.compare(value2, value1)
    }
    return result
  }

  /*
   * (non-Javadoc)
   *
   * @see com.vaadin.data.util.ItemSorter#setSortProperties(com.vaadin.data.
   * Container .Sortable, java.lang.Object[], boolean[])
   */
  fun setSortProperties(
          container: Sortable,
          propertyId: Array<Any>,
          ascending: BooleanArray
  ) {
    this.container = container
    // Removes any non-sortable property ids
    val ids: MutableList<Any> = ArrayList()
    val orders: MutableList<Boolean> = ArrayList()
    val sortable: Collection<*> = container.getSortableContainerPropertyIds()
    for (i in propertyId.indices) {
      if (sortable.contains(propertyId[i])) {
        ids.add(propertyId[i])
        orders.add(java.lang.Boolean.valueOf(if (i < ascending.size) ascending[i] else true))
      }
    }
    sortPropertyIds = ids.toTypedArray()
    sortDirections = BooleanArray(orders.size)
    for (i in sortDirections.indices) {
      sortDirections[i] = orders[i]
    }
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
      } else if (o1 === o2) {
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

  private var sortPropertyIds: Array<Any>? = null
  private var sortDirections: BooleanArray? = null
  private var container: Container? = null
  private val propertyValueComparator: Comparator<Any?> = DefaultPropertyValueComparator()
}
