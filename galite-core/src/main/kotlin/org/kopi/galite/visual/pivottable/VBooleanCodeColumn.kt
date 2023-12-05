package org.kopi.galite.visual.pivottable

import org.kopi.galite.visual.dsl.pivottable.Dimension

class VBooleanCodeColumn (ident: String?,
                          position: Dimension.Position?,
                          type: String?,
                          source: String?,
                          name: Array<String> )
            : VCodeColumn(ident,
            position,
            type,
            source,
            name) {

  override fun getIndex(value: Any): Int {
    TODO("Not yet implemented")
  }
}