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

package org.kopi.galite.visual.dsl.report

import java.util.Locale

import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.report.Constants
import org.kopi.galite.visual.report.MReport
import org.kopi.galite.visual.report.VCalculateColumn
import org.kopi.galite.visual.report.VCellFormat
import org.kopi.galite.visual.report.VReport
import org.kopi.galite.visual.report.VSeparatorColumn
import org.kopi.galite.visual.visual.ApplicationContext

class ReportModel(val report: Report): VReport() {

  init {
    init()

    // localize the report using the default locale
    localize(ApplicationContext.getDefaultLocale())
  }

  override val locale: Locale get() = report.locale ?: ApplicationContext.getDefaultLocale()

  override fun init() {
    setTitle(report.title)
    setPageTitle(report.title)
    help = report.help
    addActors(
      report.actors.map { actor ->
        actor.buildModel(report.sourceFile)
      }.toTypedArray()
    )
    commands = report.commands.map { command ->
      command.buildModel(this, actors)
    }.toTypedArray()

    source = report.sourceFile

    if (report.reportCommands) {
      addDefaultReportCommands()
    }

    model.addReportColumns()
    model.addReportLines()

    handleTriggers(report.triggers)
  }

  override fun add() {
    // TODO
  }

  fun MReport.addReportColumns() {
    val userFields = report.fields.map {
      if (it.group != null) {
        it.groupID = report.fields.indexOf(it.group)
      }

      val function: VCalculateColumn? = if (it.computeTrigger != null) {
        it.computeTrigger!!.action.method() as VCalculateColumn
      } else {
        null
      }

      val format: VCellFormat? = if (it.formatTrigger != null) {
        it.formatTrigger!!.action.method() as VCellFormat
      } else {
        null
      }

      it.domain.buildReportFieldModel(it, function, format).also { column ->
        column.label = it.label ?: ""
        column.help = it.help
      }
    }
    columns = (userFields + VSeparatorColumn()).toTypedArray()
  }

  private fun MReport.addReportLines() {
    report.reportRows.forEach {
      val list = report.fields.map { field ->
        it.data[field]
      }

      // Last null value is added for the separator column
      addLine((list + listOf(null)).toTypedArray())
    }
  }

  /**
   * Handling triggers
   */
  fun handleTriggers(triggers: MutableList<Trigger>) {
    // REPORT TRIGGERS
    VKT_Triggers = mutableListOf(arrayOfNulls(Constants.TRG_TYPES.size))
    val reportTriggerArray = arrayOfNulls<Trigger>(Constants.TRG_TYPES.size)

    triggers.forEach { trigger ->
      for (i in VConstants.TRG_TYPES.indices) {
        if (trigger.events shr i and 1 > 0) {
          reportTriggerArray[i] = trigger
        }
      }
      VKT_Triggers!![0] = reportTriggerArray
    }

    // FIELD TRIGGERS
    report.fields.forEach {
      val fieldTriggerArray = arrayOfNulls<Trigger>(Constants.TRG_TYPES.size)
      if (it.computeTrigger != null) {
        fieldTriggerArray[Constants.TRG_COMPUTE] = it.computeTrigger!!
      }
      if (it.formatTrigger != null) {
        fieldTriggerArray[Constants.TRG_FORMAT] = it.formatTrigger!!
      }
      // TODO : Add field triggers here
      VKT_Triggers!!.add(fieldTriggerArray)
    }

    // TODO: for separator column
    VKT_Triggers!!.add(arrayOfNulls(Constants.TRG_TYPES.size))

    // COMMANDS TRIGGERS
    commands?.forEach {
      val fieldTriggerArray = arrayOfNulls<Trigger>(Constants.TRG_TYPES.size)
      // TODO : Add commands triggers here
      VKT_Triggers!!.add(fieldTriggerArray)
    }
  }
}
