/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.kopi.galite.ui.vaadin.chart

import java.awt.Color
import java.io.OutputStream
import java.io.Serializable
import java.util.Random

import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

import org.kopi.galite.chart.UChartType
import org.kopi.galite.chart.VDataSeries
import org.kopi.galite.chart.VPrintOptions
import org.kopi.galite.chart.VDimensionData

import com.github.appreciated.apexcharts.config.chart.Type
import com.github.appreciated.apexcharts.config.plotoptions.builder.BarBuilder
import com.github.appreciated.apexcharts.helper.Series
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.github.appreciated.apexcharts.ApexCharts
import com.github.appreciated.apexcharts.config.builder.ChartBuilder
import com.github.appreciated.apexcharts.config.builder.PlotOptionsBuilder
import com.github.appreciated.apexcharts.config.builder.XAxisBuilder

/**
 * Creates a new abstract chart type from a chart title and a data series array.
 * @param title The chart title.
 * @param dataSeries The data series.
 */
abstract class DAbstractChartType protected constructor(private val type: Type,
                                                        private val title: String?,
                                                        private val dataSeries: Array<VDataSeries>
                                                        ) : HorizontalLayout(), UChartType {

  init {
    // FIXME: temporary styling
    minWidth = "500px"
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun build() {
    val apex = ApexCharts()
    val dimensions = mutableListOf<String>()
    val names = mutableListOf<String>()
    val values = mutableListOf<Pair<String, Double?>>()

    dataSeries.forEachIndexed { i, serie ->
      val dimension: VDimensionData = serie.dimension
      val measures = serie.getMeasures()

      dimensions.add(dimension.value.toString())

      measures.forEach {
        values.add(it.name to it.value?.toDouble())
      }

      for (measure in measures) {
        if (!names.contains(measure.name)) {
          names.add(measure.name)
        }
      }
    }

    val finalValues = mutableListOf<List<Double?>>()

    for (name in names) {
      finalValues.add(values.filter { it.first == name }.map { it.second })
    }

    val series = mutableListOf<Series<Double>>()

    finalValues.forEachIndexed { index, value ->
      series.add(Series(names[index], *value.toTypedArray()))
    }

    when (type) {
      Type.pie -> {
        finalValues.forEach {
          val chart = ApexCharts()

          chart.setChart(ChartBuilder.get().withType(type).build())
          chart.setSeries(*it.toTypedArray())
          chart.setLabels(*dimensions.toTypedArray())
          add(chart)
        }
      }

      Type.bar, Type.line, Type.area -> {
        apex.setChart(ChartBuilder.get().withType(type).build())
        apex.setSeries(*series.toTypedArray())
        apex.setXaxis(XAxisBuilder.get().withCategories(*dimensions.toTypedArray()).build())
        apex.setLabels(*dimensions.toTypedArray())
      }

      Type.rangeBar -> {
        apex.setChart(ChartBuilder.get().withType(Type.bar).build())
        apex.setSeries(*series.toTypedArray())
        apex.setXaxis(XAxisBuilder.get().withCategories(*dimensions.toTypedArray()).build())
        apex.setPlotOptions(PlotOptionsBuilder.get().withBar(BarBuilder.get().withHorizontal(true).build()).build())

      }
    }
    if (type != Type.pie) {
      super.add(apex)
    }

  }

  override fun refresh() {
    TODO()
  }

  override fun exportToPDF(destination: OutputStream, options: VPrintOptions) {
    TODO()
    // DONE IN CLIENT SIDE
  }

  override fun exportToPNG(destination: OutputStream, width: Int, height: Int) {
    TODO()
    // DONE IN CLIENT SIDE
  }

  override fun exportToJPEG(destination: OutputStream, width: Int, height: Int) {
    TODO()
    // DONE IN CLIENT SIDE
  }

  /**
   * Returns `true` if the color list should be created.
   * @return `true` if the color list should be created.
   */
  protected open fun setColorsList(): Boolean {
    return false
  }

  /**
   * Creates the charts series map.
   * @param dataSeries The data series model
   * @return The charts series map.
   */
  protected fun createDefaultChartsSerie(dataSeries: Array<VDataSeries>): Map<String, DefaultChartsSeries> {
    val chartsSeries: MutableMap<String, DefaultChartsSeries>
    chartsSeries = HashMap()
    for (data in dataSeries) {
      fillChartsSeriesMap(chartsSeries, data)
    }
    return chartsSeries
  }

  /**
   * Fills the charts series map with data from a given series model.
   * @param chartsSeries The charts series map.
   * @param data The data series model.
   */
  protected fun fillChartsSeriesMap(chartsSeries: MutableMap<String, DefaultChartsSeries>, data: VDataSeries) {
    for (measure in data.getMeasures()) {
      if (!chartsSeries.containsKey(measure.name)) {
        chartsSeries[measure.name] = DefaultChartsSeries(measure.name)
      }
      chartsSeries[measure.name]!!.values.add(measure.value)
    }
  }

  /**
   * Returns the chart series to be appended to chart data.
   * @param dataSeries The data series model.
   * @return the chart series to be appended to chart data.
   */
  protected fun createChartSeries(dataSeries: Array<VDataSeries>) {
    TODO()
  }

  /**
   * Creates a new charts series from a given name and values.
   * @param name The chart series name.
   * @param labels The list of X axis labels
   * @param values The chart series values.
   * @return The charts series object.
   */
  protected fun createChartSeries(labels: List<Comparable<*>?>, name: String?, values: List<Any?>) {
    TODO()
  }

  /**
   * Creates the color list.
   * @return The color list.
   */
  protected fun createColorArray(): Array<Color> {
    val colors: MutableSet<Color> = HashSet(BASIC_COLORS.toList())

    // add some random colors
    for (i in 0..19) {
      colors.add(randomAdditionalColor())
    }
    return colors.toTypedArray()
  }

  /**
   * Random an additional color.
   * @return The selected color.
   */
  protected fun randomAdditionalColor(): Color {
    val rand = Random()
    val index = rand.nextInt(ADDITIONAL_COLORS.size - 1 + 1)
    return ADDITIONAL_COLORS[index]
  }

  //---------------------------------------------------
  // ABSTRACT METHODS
  //---------------------------------------------------
  /**
   * Creates the chart data to be used for the chart type.
   * @param name The chart data name.
   * @return The chart data to be used.
   */
  protected abstract fun createChartData(name: String?)
  /** : ChartData TODO */

  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  /**
   * A default charts series used for all chart types except the
   * Pie chart which will be drawn differently.
   *
   * @param name The series name.
   */
  class DefaultChartsSeries(name: Comparable<*>) : Serializable {

    /**
     * The series name.
     */
    val name = name.toString()

    /**
     * The series values.
     */
    val values: MutableList<Any?>

    /**
     * Creates a new Charts series instance.
     */
    init {
      Color(255, 0, 0)
      values = ArrayList()
    }
  }

  companion object {
    // colors
    private val BASIC_COLORS: Array<Color> = arrayOf(
            Color(255, 0, 0),
            Color(0, 255, 0),
            Color(0, 0, 255),
            Color(255, 255, 0),
            Color(0, 255, 255),
            Color(255, 0, 255),
            Color(128, 0, 0),
            Color(128, 128, 0),
            Color(0, 128, 0),
            Color(128, 0, 128),
            Color(0, 128, 128),
            Color(0, 0, 128)
    )
    private val ADDITIONAL_COLORS: Array<Color> = arrayOf<Color>(
            Color(128, 0, 0),
            Color(139, 0, 0),
            Color(165, 42, 42),
            Color(178, 34, 34),
            Color(220, 20, 60),
            Color(255, 0, 0),
            Color(255, 99, 71),
            Color(255, 127, 80),
            Color(205, 92, 92),
            Color(240, 128, 128),
            Color(233, 150, 122),
            Color(250, 128, 114),
            Color(255, 160, 122),
            Color(255, 69, 0),
            Color(255, 140, 0),
            Color(255, 165, 0),
            Color(255, 215, 0),
            Color(184, 134, 11),
            Color(218, 165, 32),
            Color(238, 232, 170),
            Color(189, 183, 107),
            Color(240, 230, 140),
            Color(128, 128, 0),
            Color(255, 255, 0),
            Color(154, 205, 50),
            Color(85, 107, 47),
            Color(107, 142, 35),
            Color(124, 252, 0),
            Color(127, 255, 0),
            Color(173, 255, 47),
            Color(0, 100, 0),
            Color(0, 128, 0),
            Color(34, 139, 34),
            Color(0, 255, 0),
            Color(50, 205, 50),
            Color(144, 238, 144),
            Color(143, 188, 143),
            Color(0, 250, 154),
            Color(0, 255, 127),
            Color(46, 139, 87),
            Color(102, 205, 170),
            Color(60, 179, 113),
            Color(32, 178, 170),
            Color(47, 79, 79),
            Color(0, 128, 128),
            Color(0, 139, 139),
            Color(0, 255, 255),
            Color(0, 255, 255),
            Color(224, 255, 255),
            Color(0, 206, 209),
            Color(64, 224, 208),
            Color(72, 209, 204),
            Color(175, 238, 238),
            Color(127, 255, 212),
            Color(176, 224, 230),
            Color(95, 158, 160),
            Color(70, 130, 180),
            Color(100, 149, 237),
            Color(0, 191, 255),
            Color(30, 144, 255),
            Color(173, 216, 230),
            Color(135, 206, 235),
            Color(135, 206, 250),
            Color(25, 25, 112),
            Color(0, 0, 128),
            Color(0, 0, 139),
            Color(0, 0, 205),
            Color(0, 0, 255),
            Color(65, 105, 225),
            Color(138, 43, 226),
            Color(75, 0, 130),
            Color(72, 61, 139),
            Color(106, 90, 205),
            Color(123, 104, 238),
            Color(147, 112, 219),
            Color(139, 0, 139),
            Color(148, 0, 211),
            Color(153, 50, 204),
            Color(186, 85, 211),
            Color(128, 0, 128),
            Color(216, 191, 216),
            Color(221, 160, 221),
            Color(238, 130, 238),
            Color(255, 0, 255),
            Color(218, 112, 214),
            Color(199, 21, 133),
            Color(219, 112, 147),
            Color(255, 20, 147),
            Color(255, 105, 180),
            Color(255, 182, 193),
            Color(255, 192, 203),
            Color(250, 235, 215),
            Color(245, 245, 220),
            Color(255, 228, 196),
            Color(255, 235, 205),
            Color(245, 222, 179),
            Color(255, 248, 220),
            Color(255, 250, 205),
            Color(250, 250, 210),
            Color(255, 255, 224),
            Color(139, 69, 19),
            Color(160, 82, 45),
            Color(210, 105, 30),
            Color(205, 133, 63),
            Color(244, 164, 96),
            Color(222, 184, 135),
            Color(210, 180, 140),
            Color(188, 143, 143),
            Color(255, 228, 181),
            Color(255, 222, 173),
            Color(255, 218, 185),
            Color(255, 228, 225),
            Color(255, 240, 245),
            Color(250, 240, 230),
            Color(253, 245, 230),
            Color(255, 239, 213),
            Color(255, 245, 238),
            Color(245, 255, 250),
            Color(112, 128, 144),
            Color(119, 136, 153),
            Color(176, 196, 222),
            Color(230, 230, 250),
            Color(255, 250, 240),
            Color(240, 248, 255),
            Color(248, 248, 255),
            Color(240, 255, 240),
            Color(255, 255, 240),
            Color(240, 255, 255),
            Color(255, 250, 250),
            Color(105, 105, 105),
            Color(128, 128, 128),
            Color(169, 169, 169),
            Color(192, 192, 192),
            Color(211, 211, 211),
            Color(220, 220, 220),
            Color(245, 245, 245)
    )
  }
}
