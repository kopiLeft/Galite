package org.galite.visual.chart

import org.galite.exceptions.MissingMeasureException

object Formatter {
  fun encode(dimension: Dimension<*>) = buildString {
    append("[[")
    append(dimension.label.quoteIfNecessary())
    dimension.measures.forEach {
      append(",")
      append(it.label.quoteIfNecessary())
    }
    append("]")
    dimension.values.forEach { dimensionValue ->
      append(",")
      append("[")
      append(dimensionValue.value.quoteIfNecessary())
      dimension.measures.forEach { measure ->
        append(",")
        append(dimensionValue.measures.getOrElse(measure,
            { throw MissingMeasureException(measure, dimensionValue.value) }
        ))
      }
      append("]")
    }
    append("]")
  }

  fun decode(string: String): Dimension<*> {
    TODO("Not yet implemented")
  }

  /**
   * Adds quote to receiver if necessary.
   */
  fun <T> T.quoteIfNecessary(): String {
    val stringRepresentation = this.toString()

    return if (!stringRepresentation.startsWith("\"") && !stringRepresentation.endsWith("\""))
      "\"" + stringRepresentation + "\""
    else
      stringRepresentation
  }
}