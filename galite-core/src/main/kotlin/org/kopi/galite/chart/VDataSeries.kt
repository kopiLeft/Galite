package org.kopi.galite.chart

import java.io.Serializable

/**
 * A chart data series includes a dimension and its measures.
 */
class VDataSeries(
        /**
         * @return the measures
         */
        //---------------------------------------------------------------------
        // DATA MEMBERS
        //---------------------------------------------------------------------
        /*package*/
        val dimension: VDimensionData) : Serializable {
  //---------------------------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------------------------

  /**
   * @return the measures
   */
  fun getMeasures() = measures.toTypedArray()

  /*package*/
  val measures: List<VMeasureData> = listOf()
}
