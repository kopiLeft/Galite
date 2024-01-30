/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.chart

import org.kopi.galite.visual.chart.UChartType
import org.kopi.galite.visual.chart.VDataSeries

import com.github.appreciated.apexcharts.ApexCharts
import com.github.appreciated.apexcharts.config.builder.*
import com.github.appreciated.apexcharts.config.chart.Type
import com.github.appreciated.apexcharts.config.chart.builder.ToolbarBuilder
import com.github.appreciated.apexcharts.config.legend.Position
import com.github.appreciated.apexcharts.config.plotoptions.builder.BarBuilder
import com.github.appreciated.apexcharts.config.responsive.builder.OptionsBuilder
import com.github.appreciated.apexcharts.config.subtitle.Align
import com.github.appreciated.apexcharts.config.subtitle.builder.StyleBuilder
import com.github.appreciated.apexcharts.config.yaxis.builder.AxisBorderBuilder
import com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder
import com.github.appreciated.apexcharts.helper.Series
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

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
    minWidth = "800px"
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  override fun build() {
    val apex = ApexCharts()
    val labels = mutableListOf<String>()
    val names = mutableListOf<String>()
    val values = mutableListOf<Pair<String, Double?>>()

    dataSeries.forEach { series ->
      val dimension = series.dimension
      val measures = series.getMeasures()

      labels.add(dimension.value.toString())

      measures.forEach {
        values.add(it.name to it.value?.toDouble())
      }

      for (measure in measures) {
        if (!names.contains(measure.name)) {
          names.add(measure.name)
        }
      }
    }

    val finalValues = names.map { name ->
      values.filter { it.first == name }.map { it.second }
    }

    val series = finalValues.mapIndexed { index, value ->
      Series(names[index], *value.toTypedArray())
    }

    when (type) {
      Type.PIE -> {
        finalValues.forEach {
          apex.setTitle(TitleSubtitleBuilder.get().withText(title?.uppercase()).withAlign(Align.CENTER).withStyle(StyleBuilder.get().withFontSize("30px").withColor("black").build()).build())
          apex.setChart(ChartBuilder.get().withType(type).withFontFamily("Helvetica, Arial, sans-serif").withToolbar(ToolbarBuilder.get().withShow(true).build()).build())
          apex.setSeries(*it.toTypedArray())
          apex.setLabels(*labels.toTypedArray())
          apex.setLegend(LegendBuilder.get().withPosition(Position.RIGHT).build())
          apex.setResponsive(ResponsiveBuilder.get().withBreakpoint(480.0).withOptions(OptionsBuilder.get().withLegend(LegendBuilder.get().withPosition(Position.BOTTOM).build()).build()).build())
        }
      }

      Type.BAR, Type.LINE, Type.AREA -> {
        apex.setTitle(TitleSubtitleBuilder.get().withText(title?.uppercase()).withAlign(Align.CENTER).withStyle(StyleBuilder.get().withFontSize("30px").withColor("black").build()).build())
        apex.setChart(ChartBuilder.get().withType(type).build())
        apex.setSeries(*series.toTypedArray())
        apex.setDataLabels(DataLabelsBuilder.get().withEnabled(true).build())
        apex.setXaxis(XAxisBuilder.get().withCategories(*labels.toTypedArray()).build())
        if (series.size > 1) {
          apex.setYaxis(
                  arrayOf(
                          YAxisBuilder.get()
                                  .withTitle(TitleBuilder.get().withText(series.toTypedArray()[0].name).build())
                                  .withAxisBorder(AxisBorderBuilder.get().withShow(false).build())
                                  .build(),

                          YAxisBuilder.get()
                                  .withTitle(TitleBuilder.get().withText(series.toTypedArray()[1].name).build())
                                  .withAxisBorder(AxisBorderBuilder.get().withShow(true).build())
                                  .withOpposite(true)
                                  .build()
                  )
          )
        }
        apex.setLabels(*labels.toTypedArray())
      }

      Type.RANGEBAR -> {
        apex.setTitle(TitleSubtitleBuilder.get().withText(title?.uppercase()).withAlign(Align.CENTER).withStyle(StyleBuilder.get().withFontSize("30px").withColor("black").build()).build())
        apex.setChart(ChartBuilder.get().withType(Type.BAR).build())
        apex.setLabels(*labels.toTypedArray())
        apex.setSeries(*series.toTypedArray())
        apex.setXaxis(XAxisBuilder.get().withCategories(*labels.toTypedArray()).build())
        apex.setPlotOptions(PlotOptionsBuilder.get().withBar(BarBuilder.get().withHorizontal(true).build()).build())
      }
      else -> {
        throw Exception("Unsupported chart type.")
      }
    }

    add(apex)
  }

  override fun isEnabled(): Boolean { return super.isEnabled() }

  override fun setEnabled(enabled: Boolean) { super.setEnabled(enabled) }
}
