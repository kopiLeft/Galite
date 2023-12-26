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

  override fun getIndex(value: Any?): Int {
    codes.forEachIndexed { index, code ->
      if (value == code) {
        return index
      }
    }
    throw InconsistencyException(">>>>$value")
  }
}