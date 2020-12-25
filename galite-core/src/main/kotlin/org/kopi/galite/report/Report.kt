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

package org.kopi.galite.report

import java.io.IOException
import java.lang.RuntimeException

import org.kopi.galite.common.Action
import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.ReportTrigger
import org.kopi.galite.common.ReportTriggerEvent
import org.kopi.galite.common.Trigger
import org.kopi.galite.common.Window
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.VConstants
import org.kopi.galite.type.Date
import org.kopi.galite.type.Month
import org.kopi.galite.type.Time
import org.kopi.galite.type.Timestamp
import org.kopi.galite.type.Week

/**
 * Represents a report that contains fields [fields] and displays a table of [reportRows].
 */
abstract class Report : Window() {

  /** Report's fields. */
  val fields = mutableListOf<ReportField<*>>()

  /** Report's data rows. */
  val reportRows = mutableListOf<ReportRow>()

  /**
   * creates and returns a field. It uses [init] method to initialize the field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method.
   * @return a field.
   */
  inline fun <reified T : Comparable<T>?> field(domain: Domain<T>, init: ReportField<T>.() -> Unit): ReportField<T> {
    domain.kClass = T::class
    val field = ReportField(domain)
    field.init()
    fields.add(field)
    return field
  }

  /**
   * Adds a row to the report.
   *
   * @param init initializes the row with values.
   */
  fun add(init: ReportRow.() -> Unit) {
    val row = ReportRow(fields)
    row.init()
    reportRows.add(row)
  }

  /**
   * Adds report trigger to this block.
   *
   * @param reportTriggerEvents the trigger events to add
   * @param method              the method to execute when trigger is called
   */
  fun trigger(vararg reportTriggerEvents: ReportTriggerEvent, method: () -> Unit): Trigger {
    val event = reportEventList(reportTriggerEvents)
    val reportAction = Action(null, method)
    val trigger = ReportTrigger(event, reportAction)
    triggers.add(trigger)
    return trigger
  }

  private fun reportEventList(reportTriggerEvents: Array<out ReportTriggerEvent>): Long {
    var self = 0L

    reportTriggerEvents.forEach { trigger ->
      self = self or (1L shl trigger.event)
    }

    return self
  }

  /**
   * Returns the row's data.
   *
   * @param rowNumber the index of the desired row.
   */
  fun getRow(rowNumber: Int): MutableMap<ReportField<*>, Any?> = reportRows[rowNumber].data

  /**
   * Returns rows of data for a specific [field].
   *
   * @param field the field.
   */
  fun getRowsForField(field: ReportField<*>) = reportRows.map { it.data[field] }

  /**
   * Adds default report commands
   */
  open val reportCommands = false

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  fun genLocalization(destination: String? = null) {
    if (locale != null) {
      val baseName = this::class.simpleName
      requireNotNull(baseName)
      val destination = destination
              ?: this.javaClass.classLoader.getResource("")?.path + this.javaClass.packageName.replace(".", "/")
      try {
        val writer = ReportLocalizationWriter()
        genLocalization(writer)
        writer.write(destination, baseName, locale!!)
      } catch (ioe: IOException) {
        ioe.printStackTrace()
        System.err.println("cannot write : $baseName")
      }
    }
  }

  fun genLocalization(writer: LocalizationWriter) {
    (writer as ReportLocalizationWriter).genReport(title,
                                                   help,
                                                   fields)
  }

  // TODO add Decimal types
  fun MReport.addReportColumns() {
    columns = fields.map {
      when (it.domain.kClass) {
        Int::class ->
          VIntegerColumn(it.label, it.options, it.align.value, it.groupID, null, it.domain.width ?: 0, null)
        String::class ->
          VStringColumn(it.label, it.options, it.align.value, it.groupID, null, it.domain.width ?: 0,
                        it.domain.height ?: 0, null)
        Boolean::class ->
          VBooleanColumn(it.label, it.options, it.align.value, it.groupID, null, it.domain.width ?: 0, null)
        Date::class, java.util.Date::class ->
          VDateColumn(it.label, it.options, it.align.value, it.groupID, null, it.domain.width ?: 0, null)
        Month::class ->
          VMonthColumn(it.label, it.options, it.align.value, it.groupID, null, it.domain.width ?: 0, null)
        Week::class ->
          VWeekColumn(it.label, it.options, it.align.value, it.groupID, null, it.domain.width ?: 0, null)
        Time::class ->
          VTimeColumn(it.label, it.options, it.align.value, it.groupID, null, it.domain.width ?: 0, null)
        Timestamp::class ->
          VTimestampColumn(it.label, it.options, it.align.value, it.groupID, null, it.domain.width ?: 0, null)
        else -> throw RuntimeException("Type ${it.domain.kClass!!.qualifiedName} is not supported")
      }
    }.toTypedArray()
  }

  private fun MReport.addReportLines() {
    reportRows.forEach {
      addLine(it.data.values.toTypedArray())
    }
  }


  /** Report model*/
  override val model: VReport
    get() {
      genLocalization()

      return object : VReport() {
        /**
         * Handling triggers
         */
        fun handleTriggers(triggers: MutableList<Trigger>) {
          // BLOCK TRIGGERS
          triggers.forEach { trigger ->
            val blockTriggerArray = IntArray(Constants.TRG_TYPES.size)
            for (i in VConstants.TRG_TYPES.indices) {
              if (trigger.events shr i and 1 > 0) {
                blockTriggerArray[i] = i
                super.triggers[i] = trigger
              }
            }
            super.VKT_Triggers[0] = blockTriggerArray
          }

          // FIELD TRIGGERS
          fields.forEach {
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

          if (reportCommands) {
            addDefaultReportCommands()
          }

          super.model.addReportColumns()
          super.model.addReportLines()

          handleTriggers(this@Report.triggers)
        }

        override fun add() {
          // TODO
        }
      }
    }
}
