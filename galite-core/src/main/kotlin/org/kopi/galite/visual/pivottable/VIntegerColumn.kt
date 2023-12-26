package org.kopi.galite.visual.pivottable

import org.kopi.galite.visual.dsl.pivottable.Dimension

class VIntegerColumn(ident: String?,
                     position: Dimension.Position?)
      : VPivotTableColumn(ident,
                          position) {

  /**
   * Compare two objects.
   *
   * @param    object1    the first operand of the comparison
   * @param    object2    the second operand of the comparison
   * @return              -1 if the first operand is smaller than the second
   *           1 if the second operand if smaller than the first
   *           0 if the two operands are equal
   */
  override fun compareTo(object1: Any, object2: Any): Int {
    val v1 = (object1 as Int)
    val v2 = (object2 as Int)

    return if (v1 < v2) -1 else if (v1 > v2) 1 else 0
  }
}