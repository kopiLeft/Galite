package org.galite.visual.chart

import org.galite.domain.Domain

abstract class Column<T: Comparable<T>>(val size: Int? = null)  {
    var label: String = ""
}