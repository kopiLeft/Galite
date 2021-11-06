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

import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VField

/**
 * An item sorter implementation for the grid block. Constructs a DefaultItemSorter using the default `Comparator`
 * for comparing `Property`values.
 */
class DGridBlockItemSorter(
        private val model: VBlock
) {

  /**
   * Provides a default comparator used for comparing grid values.
   * The `DefaultComparator` assumes all objects it
   * compares can be cast to Comparable.
   *
   */
  class DefaultComparator(val model: VBlock, val field: VField) : Comparator<DGridBlockContainer.GridBlockItem?>, Serializable {

    override fun compare(o1: DGridBlockContainer.GridBlockItem?, o2: DGridBlockContainer.GridBlockItem?): Int {
      val ascendantSortDirection = o1!!.record > o2!!.record
      val (rec1, rec2) = if (ascendantSortDirection) {
        model.getSortedPosition(o1.record) to model.getSortedPosition(o2.record)
      } else {
        model.getSortedPosition(o2.record) to model.getSortedPosition(o1.record)
      }
      val item1 = DGridBlockContainer.GridBlockItem(o1.record).getValue(field)
      val item2 = DGridBlockContainer.GridBlockItem(o2.record).getValue(field)

      return if (isSortedRecordFilled(rec1) && isSortedRecordFilled(rec2)) {
        // Normal non-null comparison
        if (item1 != null && item2 != null) {
          // Assume the objects can be cast to Comparable, throw
          // ClassCastException otherwise.
          (item1 as Comparable<Any?>).compareTo(item2)
        } else if (item1 == item2) {
          // Objects are equal if both are null
          0
        } else {
          if (item1 == null) {
            -1 // null is less than non-null
          } else {
            1 // non-null is greater than null
          }
        }
      } else if (!isSortedRecordFilled(rec1) && !isSortedRecordFilled(rec2)) {
        rec1.compareTo(rec2) // keep the records order
      } else if (isSortedRecordFilled(rec1)) {
        -1 // empty records are always at the bottom
      } else {
        1 // empty records are always at the bottom
      }
    }

    private fun isSortedRecordFilled(rec: Int): Boolean {
      return (model.isRecordChanged(model.getDataPosition(rec))
              || model.isRecordFetched(model.getDataPosition(rec)))
    }
  }
}
