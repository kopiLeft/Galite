/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.visual.dsl.chart

import java.io.IOException
import java.util.Locale

import org.kopi.galite.visual.chart.CConstants
import org.kopi.galite.visual.chart.VChart
import org.kopi.galite.visual.chart.VChartType
import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.dsl.common.Action
import org.kopi.galite.visual.dsl.common.FormTrigger
import org.kopi.galite.visual.dsl.common.LocalizationWriter
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.dsl.common.Window

/**
 * Represents a chart that contains a [dimension] and a list of [measures].
 *
 * In fact, all you have to do to create a chart is to define the dimensions you need and their measures,
 * then you will have to write a constructor that will load data into these fields.
 *
 * With this Charts, you will also be able to print or export the created chart to different file formats.
 *
 */
abstract class Chart : Window() {
  /** The chart's dimension */
  lateinit var dimension: ChartDimension<*>

  /** The chart's measures */
  val measures = mutableListOf<ChartMeasure<*>>()

  /** the help text */
  open val help: String? = null

  /**
   * Creates a chart dimension, with the specified [domain], used to store values of type [T] and measures values.
   *
   * @param domain the dimension domain.
   * @param init   used to initialize the domain with measures values.
   */
  inline fun <reified T : Comparable<T>?> dimension(domain: Domain<T>,
                                                    init: ChartDimension<T>.() -> Unit): ChartDimension<T> {
    domain.kClass = T::class
    val chartDimension = ChartDimension(domain, `access$sourceFile`)
    chartDimension.init()
    dimension = chartDimension
    return chartDimension
  }

  /**
   * Adds triggers to this chart
   *
   * @param chartTriggerEvents   the trigger events to add
   * @param method               the method to execute when trigger is called
   */
  fun <T> trigger(vararg chartTriggerEvents: ChartTriggerEvent<T>, method: () -> T): Trigger {
    val event = formEventList(chartTriggerEvents)
    val chartAction = Action(null, method)
    val trigger = FormTrigger(event, chartAction)
    triggers.add(trigger)
    return trigger
  }

  private fun formEventList(chartTriggerEvents: Array<out ChartTriggerEvent<*>>): Long {
    var self = 0L

    chartTriggerEvents.forEach { trigger ->
      self = self or (1L shl trigger.event)
    }

    return self
  }

  /**
   * Adds the default chart commands.
   * TODO!
   */
  open fun addDefaultChartCommands() {
    TODO("Add the above commands")
    /*commands.add(Command("Quit", VConstants.MOD_ANY))
    commands.add(Command("Print", VConstants.MOD_ANY))
    commands.add(Command("PrintOptions", VConstants.MOD_ANY))
    commands.add(Command("ExportPNG", VConstants.MOD_ANY))
    commands.add(Command("ExportPDF", VConstants.MOD_ANY))
    commands.add(Command("ExportJPEG", VConstants.MOD_ANY))
    commands.add(Command("ColumnView", VConstants.MOD_ANY))
    commands.add(Command("BarView", VConstants.MOD_ANY))
    commands.add(Command("LineView", VConstants.MOD_ANY))
    commands.add(Command("AreaView", VConstants.MOD_ANY))
    commands.add(Command("PieView", VConstants.MOD_ANY))
    commands.add(Command("Help", VConstants.MOD_ANY))*/
  }

  /**
   * Creates a chart measure, with the specified [domain], used to store values of measure values.
   *
   * @param domain the dimension domain.
   * @param init   used to initialize the measure.
   */
  inline fun <reified T> measure(
          domain: Domain<T>,
          init: ChartMeasure<T>.() -> Unit
  ): ChartMeasure<T> where T : Comparable<T>?, T : Number? {
    domain.kClass = T::class
    val chartMeasure = ChartMeasure(domain, `access$sourceFile`)
    chartMeasure.init()
    this.measures.add(chartMeasure)
    return chartMeasure
  }


  open fun getFields(): List<ChartField<*>> = listOf(dimension) + measures

  ///////////////////////////////////////////////////////////////////////////
  // CHART TRIGGERS EVENTS
  ///////////////////////////////////////////////////////////////////////////
  /**
   * Chart Triggers
   *
   * @param event the event of the trigger
   */
  open class ChartTriggerEvent<T>(val event: Int)

  /**
   * Executed before the chart is displayed.
   */
  val PRECHART = ChartTriggerEvent<Unit>(CConstants.TRG_PRECHART)

  /**
   * Executed at chart initialization.
   */
  val INITCHART = ChartTriggerEvent<Unit>(CConstants.TRG_INIT)

  /**
   * Executed after the chart initialization. This trigger should return a fixed type for the chart
   * [org.kopi.galite.chart.VChartType].
   */
  val CHARTTYPE = ChartTriggerEvent<VChartType>(CConstants.TRG_CHARTTYPE)

  /**
   * Executed after the chart is closed.
   */
  val POSTCHART = ChartTriggerEvent<Unit>(CConstants.TRG_POSTCHART)

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------
  /**
   * !!!FIX : comment move file creation to upper level
   */
  override fun genLocalization(destination: String?, locale: Locale?) {
    if (locale != null) {
      val baseName = this::class.simpleName
      requireNotNull(baseName)
      val localizationDestination = destination
              ?: this.javaClass.classLoader.getResource("")?.path +
              this.javaClass.`package`.name.replace(".", "/")
      try {
        val writer = ChartLocalizationWriter()
        genLocalization(writer)
        writer.write(localizationDestination, baseName, locale!!)
      } catch (ioe: IOException) {
        ioe.printStackTrace()
        System.err.println("cannot write : $baseName")
      }
    }
  }

  /**
   * generates xml localization file for this chart.
   *
   * @param writer the localization writer responsible for generating the xml file.
   */
  fun genLocalization(writer: LocalizationWriter) {
    (writer as ChartLocalizationWriter).genChart(title, help, getFields(), menus, actors)
  }

  var chartType: VChartType
    get() = model.chartType ?: VChartType.DEFAULT
    set(value) {
      model.setType(value)
    }

  // ----------------------------------------------------------------------
  // CHART MODEL
  // ----------------------------------------------------------------------
  override val model: VChart by lazy { ChartModel(this) }

  @PublishedApi
  internal val `access$sourceFile`: String get() = sourceFile
}
