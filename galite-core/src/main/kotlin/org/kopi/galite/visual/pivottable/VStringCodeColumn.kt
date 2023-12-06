package org.kopi.galite.visual.pivottable

import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.dsl.pivottable.Dimension
import org.kopi.galite.visual.report.VCellFormat

class VStringCodeColumn (ident: String?,
                         position: Dimension.Position?,
                         type: String?,
                         source: String?,
                         name: Array<String>,
                         format: VCellFormat?,
                         private val codes: Array<String?>)
          : VCodeColumn(ident,
                        position,
                        type,
                        source,
                        format,
                        name) {

  override fun getIndex(value: Any?): Int {
    codes.forEachIndexed { index, code ->
      if (value == code) {
        return index
      }
    }
    throw InconsistencyException(">>>>$value")
  }
}