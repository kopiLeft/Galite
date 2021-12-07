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

import java.util.Locale

import org.kopi.galite.visual.chart.CConstants
import org.kopi.galite.visual.chart.VChart
import org.kopi.galite.visual.db.DBContextHandler
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.visual.ApplicationContext

class ChartModel(val chart: Chart, context: DBContextHandler? = null): VChart(context) {

  init {
    init()
    // localize the chart using the default locale
    localize(ApplicationContext.getDefaultLocale())
  }

  override val locale: Locale get() = chart.locale ?: ApplicationContext.getDefaultLocale()

  /**
   * Handling triggers
   */
  fun handleTriggers(triggers: MutableList<Trigger>) {
    // CHART TRIGGERS
    val chartTriggerArray = arrayOfNulls<Trigger>(CConstants.TRG_TYPES.size)
    triggers.forEach { trigger ->

      for (i in VConstants.TRG_TYPES.indices) {
        if (trigger.events shr i and 1 > 0) {
          chartTriggerArray[i] = trigger
        }
      }
      VKT_Triggers[0] = chartTriggerArray
    }

    // DIMENSION TRIGGERS
    chart.dimension.also {
      val fieldTriggerArray = arrayOfNulls<Trigger>(CConstants.TRG_TYPES.size)

      if(it.formatTrigger != null) {
        fieldTriggerArray[CConstants.TRG_FORMAT] = it.formatTrigger!!
      }
      // TODO : Add field triggers here
      VKT_Triggers.add(fieldTriggerArray)
    }

    // MEASURE TRIGGERS
    chart.measures.forEach {
      val fieldTriggerArray = arrayOfNulls<Trigger>(CConstants.TRG_TYPES.size)

      if(it.colorTrigger != null) {
        fieldTriggerArray[CConstants.TRG_COLOR] = it.colorTrigger!!
      }
      // TODO : Add field triggers here
      VKT_Triggers.add(fieldTriggerArray)
    }

    // COMMANDS TRIGGERS
    commands?.forEach {
      val fieldTriggerArray = arrayOfNulls<Trigger>(CConstants.TRG_TYPES.size)
      // TODO : Add commands triggers here
      VKT_Triggers.add(fieldTriggerArray)
    }
  }

  override fun init() {
    setTitle(chart.title)
    help = chart.help
    addActors(chart.actors.map { actor ->
      actor.buildModel(chart.sourceFile)
    }.toTypedArray())
    commands = chart.commands.map { command ->
      command.buildModel(this, actors)
    }.toTypedArray()

    source = chart.sourceFile

    dimensions = listOf(chart.dimension).map { it.model }.toTypedArray()
    measures = chart.measures.map { it.model }.toTypedArray()

    addChartLines()

    handleTriggers(chart.triggers)
  }

  override fun add() {
    // TODO
  }

  fun VChart.addChartLines() {
    chart.dimension.values.forEach {
      addRow(arrayOf(it.value), it.measureList.values.toTypedArray())
    }
  }
}
