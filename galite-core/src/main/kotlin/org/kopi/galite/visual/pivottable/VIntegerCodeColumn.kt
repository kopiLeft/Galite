package org.kopi.galite.visual.pivottable

import org.kopi.galite.visual.dsl.pivottable.Dimension

class VIntegerCodeColumn (ident: String?,
                          position: Dimension.Position?,
                          type: String?,
                          source: String?,
                          name: Array<String>,
                          private val codes: IntArray)
              : VCodeColumn(ident,
              position,
              type,
              source,
              name) {

  private var fastIndex = -1 // if array = {fastIndex, fastIndex + 1, ...}

  init {
    fastIndex = codes[0]
    for (i in 1 until codes.size) {
      if (codes[i] != fastIndex + i) {
        fastIndex = -1
        break
      }
    }
  }

  /**
   * Compares two objects.
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

  /**
   * Get the index of the value.
   */
  override fun getIndex(value: Any?): Int {
    if (fastIndex != -1) {
      return (value as Int) - fastIndex
    }
    for (i in codes.indices) {
      if ((value as Int) == codes[i]) {
        return i
      }
    }
    return -1
  }
}