/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.visual.ApplicationContext

/**
 * Represents a chart that contains a [dimension] and a list of [measures].
 *
 * In fact, all you have to do to create a chart is to define the dimensions you need and their measures,
 * then you will have to write a constructor that will load data into these fields.
 *
 * With this Charts, you will also be able to print or export the created chart to different file formats.
 *
 *
 * @param title The title of this form.
 * @param help The help text.
 * @param locale The window locale.
 */
abstract class Chart(title: String, val help: String?, locale: Locale? = null) : Window(title, locale) {

  constructor(title: String, locale: Locale? = null) : this(title, null, locale)

  /** The chart's dimension */
  lateinit var dimension: ChartDimension<*>

  /** The chart's measures */
  val measures = mutableListOf<ChartMeasure<*>>()

  /**
   * Creates a chart dimension, with the specified [domain], used to store values of type [T] and measures values.
   *
   * @param domain the dimension domain.
   * @param init   used to initialize the domain with measures values.
   */
  inline fun <reified T : Comparable<T>?> dimension(domain: Domain<T>,
                                                    init: ChartDimension<T>.() -> Unit): ChartDimension<T> {
    domain.kClass = T::class
    val chartDimension = ChartDimension(domain, this, `access$sourceFile`)
    chartDimension.init()
    dimension = chartDimension

    chartDimension.addDimensionTriggers()

    setDimension()
    return chartDimension
  }

  fun ChartDimension<*>.addDimensionTriggers() {
    // DIMENSION TRIGGERS
    val fieldTriggerArray = arrayOfNulls<Trigger>(CConstants.TRG_TYPES.size)

    if(formatTrigger != null) {
      fieldTriggerArray[CConstants.TRG_FORMAT] = formatTrigger!!
    }
    // TODO : Add field triggers here
    this@Chart.model.VKT_Dimension_Triggers.add(fieldTriggerArray)
  }

  fun setDimension() {
    model.dimensions = listOf(dimension.model) // TODO
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

    // CHART TRIGGERS
    triggers.forEach { trigger ->

      for (i in VConstants.TRG_TYPES.indices) {
        if (trigger.events shr i and 1 > 0) {
          model.VKT_Chart_Triggers[0][i] = trigger
        }
      }
    }

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
    addMeasure(chartMeasure)

    chartMeasure.addMeasureTriggers()

    return chartMeasure
  }

  fun ChartMeasure<*>.addMeasureTriggers() {
    // MEASURE TRIGGERS
    val fieldTriggerArray = arrayOfNulls<Trigger>(CConstants.TRG_TYPES.size)

    if(colorTrigger != null) {
      fieldTriggerArray[CConstants.TRG_COLOR] = colorTrigger
    }
    // TODO : Add field triggers here
    this@Chart.model.VKT_Measure_Triggers.add(fieldTriggerArray)
  }

  fun addMeasure(chartMeasure: ChartMeasure<*>) {
    this.measures.add(chartMeasure)
    model.measures.add(chartMeasure.model)
  }

  open fun getFields(): List<ChartField<*>> = listOf(dimension) + measures


  override fun addCommandTrigger() {
    model.VKT_Commands_Triggers.add(arrayOfNulls(CConstants.TRG_TYPES.size))
  }

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
  override val model: VChart = object: VChart() {

    init {
      init()
      // localize the chart using the default locale
      localize(ApplicationContext.getDefaultLocale())
    }

    override val locale: Locale get() = this@Chart.locale ?: ApplicationContext.getDefaultLocale()

    override fun init() {
      setTitle(title)
      help = this@Chart.help
      source = sourceFile
    }
  }

  @PublishedApi
  internal val `access$sourceFile`: String get() = sourceFile
}
