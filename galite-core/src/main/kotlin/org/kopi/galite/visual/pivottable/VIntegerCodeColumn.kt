package org.kopi.galite.visual.pivottable

import org.kopi.galite.visual.dsl.pivottable.Dimension
import org.kopi.galite.visual.report.VCellFormat

class VIntegerCodeColumn (ident: String?,
                          position: Dimension.Position?,
                          type: String?,
                          source: String?,
                          name: Array<String>,
                          format: VCellFormat?,
                          private val codes: IntArray)
              : VCodeColumn(ident,
              position,
              type,
              source,
              format,
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