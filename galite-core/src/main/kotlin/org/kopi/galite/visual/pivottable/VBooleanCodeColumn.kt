package org.kopi.galite.visual.pivottable

import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.dsl.pivottable.Dimension
import org.kopi.galite.visual.report.VCellFormat

class VBooleanCodeColumn (ident: String?,
                          position: Dimension.Position?,
                          type: String?,
                          source: String?,
                          name: Array<String>,
                          format: VCellFormat?,
                          private val codes: BooleanArray)
            : VCodeColumn(ident,
            position,
            type,
            source,
            format,
            name) {

  init {
    if (codes.size > 2) {
      throw InconsistencyException("Can't define more than two codes for a boolean column")
    }
  }
  override fun getIndex(value: Any?): Int {
    return if ((value as Boolean) == codes[0]) 0 else 1
  }
}