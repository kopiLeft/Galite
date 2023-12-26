package org.kopi.galite.visual.pivottable

import org.kopi.galite.visual.dsl.pivottable.Dimension
import java.math.BigDecimal

/**
 * Represents a pivot table column description
 * @param    ident        The identifier of the field
 * @param    position     The position of the dimension field
 */
class VDecimalColumn(ident: String?,
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
  override fun compareTo(object1: Any, object2: Any): Int =
    (object1 as BigDecimal).compareTo(object2 as BigDecimal)
}
