package org.galite.exceptions

import org.galite.visual.chart.Measure
import java.lang.IllegalArgumentException

class MissingMeasureException(measure: Measure<*>, dimension: Comparable<*>) : IllegalArgumentException() {
    override val message = "Missing measure ${measure.label} for the dimension $dimension"
}