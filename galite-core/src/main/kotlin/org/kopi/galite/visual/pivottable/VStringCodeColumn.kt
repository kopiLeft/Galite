package org.kopi.galite.visual.pivottable

import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.dsl.pivottable.Dimension

class VStringCodeColumn (ident: String?,
                         position: Dimension.Position?,
                         type: String?,
                         source: String?,
                         name: Array<String>,
                         private val codes: Array<String?>)
          : VCodeColumn(ident,
                        position,
                        type,
                        source,
                        name) {

  /**
   * Compares two objects.
   *
   * @param        object1        the first operand of the comparison
   * @param        object2        the second operand of the comparison
   * @return        -1 if the first operand is smaller than the second
   * 1 if the second operand if smaller than the first
   * 0 if the two operands are equal
   */
  override fun compareTo(object1: Any, object2: Any): Int {
    return (object1 as String).compareTo((object2 as String))
  }

  override fun getIndex(value: Any?): Int {
    codes.forEachIndexed { index, code ->
      if (value == code) {
        return index
      }
    }
    throw InconsistencyException(">>>>$value")
  }
}