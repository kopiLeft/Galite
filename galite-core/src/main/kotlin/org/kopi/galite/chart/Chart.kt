/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

package org.kopi.galite.chart

import org.kopi.galite.common.Action
import org.kopi.galite.common.ChartTriggerEvent
import org.kopi.galite.common.ChartTypeTriggerEvent
import org.kopi.galite.common.ChartVoidTriggerEvent
import org.kopi.galite.common.FormTrigger
import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.Trigger
import org.kopi.galite.common.Window
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.VConstants
import org.kopi.galite.report.Constants
import org.kopi.galite.visual.VWindow
import java.io.IOException

/**
 * Represents a chart that contains a [dimension] and a list of [measures].
 *
 * @param name the name of the chart. It represents the title
 */
abstract class Chart() : Window() {
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
  inline fun <reified T : Comparable<T>?> dimension(domain: Domain<T>, init: ChartDimension<T>.() -> Unit): ChartDimension<T> {
    domain.kClass = T::class
    val chartDimension = ChartDimension(domain)
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
  private fun <T> trigger(chartTriggerEvents: Array<out ChartTriggerEvent>, method: () -> T): Trigger {
    val event = formEventList(chartTriggerEvents)
    val chartAction = Action(null, method)
    val trigger = FormTrigger(event, chartAction)
    triggers.add(trigger)
    return trigger
  }

  private fun formEventList(chartTriggerEvents: Array<out ChartTriggerEvent>): Long {
    var self = 0L

    chartTriggerEvents.forEach { trigger ->
      self = self or (1L shl trigger.event)
    }

    return self
  }

  /**
   * Adds void triggers to this chart
   *
   * @param chartTriggerEvents the trigger events to add
   * @param method             the method to execute when trigger is called
   */
  fun trigger(vararg chartTriggerEvents: ChartVoidTriggerEvent, method: () -> Unit): Trigger {
    return trigger(chartTriggerEvents, method)
  }

  /**
   * Adds void triggers to this chart
   *
   * @param chartTriggerEvents the trigger events to add
   * @param method             the method to execute when trigger is called
   */
  fun trigger(vararg chartTriggerEvents: ChartTypeTriggerEvent, method: () -> VChartType): Trigger {
    return trigger(chartTriggerEvents, method)
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
    val chartMeasure = ChartMeasure(domain)
    chartMeasure.init()
    this.measures.add(chartMeasure)
    return chartMeasure
  }


  open fun getFields(): List<ChartField<*>> = listOf(dimension) + measures

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------
  /**
   * !!!FIX : comment move file creation to upper level (VKPhylum?)
   */
  open fun genLocalization(destination: String? = null) {
    if (locale != null) {
      val baseName = this::class.simpleName
      requireNotNull(baseName)
      val destination = destination
              ?: this.javaClass.classLoader.getResource("")?.path +
              this.javaClass.packageName.replace(".", "/")
      try {
        val writer = ChartLocalizationWriter()
        genLocalization(writer)
        writer.write(destination, baseName, locale!!)
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
    (writer as ChartLocalizationWriter).genChart(title,
                                                 help,
                                                 getFields())
  }

  fun VChart.addChartLines() {
    dimension.values.forEach {
      addRow(arrayOf(it.value), it.measureList.values.toTypedArray())
    }
  }

  var chartType: VChartType
    get() = (model as VChart).chartType ?: VChartType.DEFAULT
    set(value) {
      (model as VChart).setType(value)
    }

  override val model: VWindow by lazy {
    genLocalization()

    object : VChart() {
      /**
       * Handling triggers
       */
      fun handleTriggers(triggers: MutableList<Trigger>) {
        // CHART TRIGGERS
        triggers.forEach { trigger ->
          val chartTriggerArray = IntArray(Constants.TRG_TYPES.size)
          for (i in VConstants.TRG_TYPES.indices) {
            if (trigger.events shr i and 1 > 0) {
              chartTriggerArray[i] = i
              super.triggers[i] = trigger
            }
          }
          super.VKT_Triggers[0] = chartTriggerArray
        }

        // DIMENSION TRIGGERS
        dimensions.forEach {
          val fieldTriggerArray = IntArray(VConstants.TRG_TYPES.size)
          // TODO : Add field triggers here
          super.VKT_Triggers.add(fieldTriggerArray)
        }

        // MEASURE TRIGGERS
        measures.forEach {
          val fieldTriggerArray = IntArray(VConstants.TRG_TYPES.size)
          // TODO : Add field triggers here
          super.VKT_Triggers.add(fieldTriggerArray)
        }

        // COMMANDS TRIGGERS
        commands?.forEach {
          val fieldTriggerArray = IntArray(VConstants.TRG_TYPES.size)
          // TODO : Add commands triggers here
          super.VKT_Triggers.add(fieldTriggerArray)
        }
      }

      override fun init() {
        source = sourceFile

        super.dimensions = listOf(this@Chart.dimension).map { it.model }.toTypedArray()
        super.measures = this@Chart.measures.map { it.model }.toTypedArray()

        addChartLines()

        handleTriggers(this@Chart.triggers)
      }

      override fun add() {
        // TODO
      }
    }
  }
}
