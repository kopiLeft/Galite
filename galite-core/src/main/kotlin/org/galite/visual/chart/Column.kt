package org.galite.visual.chart

abstract class Column<T : Comparable<T>>(val size: Int? = null) {
  var label: String = ""
}