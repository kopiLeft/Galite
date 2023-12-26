package org.kopi.galite.visual.pivottable

import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.dsl.pivottable.Dimension

class VBooleanCodeColumn (ident: String?,
                          position: Dimension.Position?,
                          type: String?,
                          source: String?,
                          name: Array<String>,
                          private val codes: BooleanArray)
            : VCodeColumn(ident,
            position,
            type,
            source,
            name) {

  init {
    if (codes.size > 2) {
      throw InconsistencyException("Can't define more than two codes for a boolean column")
    }
  }

  override fun compareTo(object1: Any, object2: Any): Int {
    return if (object1 == object2) 0 else if (true == object1) 1 else -1
  }

  override fun getIndex(value: Any?): Int {
    return if ((value as Boolean) == codes[0]) 0 else 1
  }
}