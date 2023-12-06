package org.kopi.galite.visual.pivottable

import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.dsl.pivottable.Dimension
import org.kopi.galite.visual.report.VCellFormat
import java.math.BigDecimal

class VDecimalCodeColumn (ident: String?,
                          position: Dimension.Position?,
                          type: String?,
                          source: String?,
                          name: Array<String>,
                          format: VCellFormat?,
                          private val codes: Array<BigDecimal?>)
          : VCodeColumn(ident,
                        position,
                        type,
                        source,
                        format,
                        name) {

  /**
   * Get the index of the value.
   */
  override fun getIndex(value: Any?): Int {
    for (i in codes.indices) {
      if (value == codes[i]) {
        return i
      }
    }
    throw InconsistencyException(">>>>$value")
    }
  }