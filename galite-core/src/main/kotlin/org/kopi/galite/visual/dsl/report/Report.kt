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

package org.kopi.galite.visual.dsl.report

import java.io.IOException
import java.util.Locale

import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.dsl.common.Action
import org.kopi.galite.visual.dsl.common.LocalizationWriter
import org.kopi.galite.visual.dsl.common.ReportTrigger
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.dsl.common.Window
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.report.Constants
import org.kopi.galite.visual.report.MReport
import org.kopi.galite.visual.report.VCalculateColumn
import org.kopi.galite.visual.report.VCellFormat
import org.kopi.galite.visual.report.VDateColumn
import org.kopi.galite.visual.report.VDecimalColumn
import org.kopi.galite.visual.report.VIntegerColumn
import org.kopi.galite.visual.report.VMonthColumn
import org.kopi.galite.visual.report.VReport
import org.kopi.galite.visual.report.VSeparatorColumn
import org.kopi.galite.visual.visual.ApplicationContext

/**
 * Represents a report that contains fields [fields] and displays a table of [reportRows].
 */
abstract class Report : Window() {

  /** Report's fields. */
  val fields = mutableListOf<ReportField<*>>()

  /** Report's data rows. */
  val reportRows = mutableListOf<ReportRow>()

  /** the help text */
  open val help: String? = null

  /**
   * creates and returns a field. It uses [init] method to initialize the field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method.
   * @return a field.
   */
  inline fun <reified T : Comparable<T>?> field(domain: Domain<T>,
                                                noinline init: ReportField<T>.() -> Unit): ReportField<T> {
    domain.kClass = T::class
    val field = ReportField(domain, "ANM_${fields.size}", init, `access$sourceFile`)
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
  fun <T> trigger(vararg reportTriggerEvents: ReportTriggerEvent<T>, method: () -> T): Trigger {
    val event = reportEventList(reportTriggerEvents)
    val reportAction = Action(null, method)
    val trigger = ReportTrigger(event, reportAction)
    triggers.add(trigger)
    return trigger
  }

  private fun reportEventList(reportTriggerEvents: Array<out ReportTriggerEvent<*>>): Long {
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

  ///////////////////////////////////////////////////////////////////////////
  // REPORT TRIGGERS
  ///////////////////////////////////////////////////////////////////////////
  /**
   * Block Triggers
   *
   * @param event the event of the trigger
   */
  open class ReportTriggerEvent<T>(val event: Int)

  /**
   * Executed before the report is displayed.
   */
  val PREREPORT = ReportTriggerEvent<Unit>(Constants.TRG_PREREPORT)

  /**
   * Executed after the report is closed.
   */
  val POSTREPORT = ReportTriggerEvent<Unit>(Constants.TRG_POSTREPORT)

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  override fun genLocalization(destination: String?, locale: Locale?) {
    if (locale != null) {
      val baseName = this::class.simpleName
      requireNotNull(baseName)
      val localizationDestination = destination
              ?: this.javaClass.classLoader.getResource("")?.path + this.javaClass.`package`.name.replace(".", "/")
      try {
        val writer = ReportLocalizationWriter()
        genLocalization(writer)
        writer.write(localizationDestination, baseName, locale!!)
      } catch (ioe: IOException) {
        ioe.printStackTrace()
        System.err.println("cannot write : $baseName")
      }
    }
  }

  fun genLocalization(writer: LocalizationWriter) {
    (writer as ReportLocalizationWriter).genReport(title, help, fields, menus, actors)
  }

  // TODO add Decimal types
  fun MReport.addReportColumns() {
    val userFields = fields.map {
      if (it.group != null) {
        it.groupID = fields.indexOf(it.group)
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
    reportRows.forEach {
      val list = fields.map { field ->
        it.data[field]
      }

      // Last null value is added for the separator column
      addLine((list + listOf(null)).toTypedArray())
    }
  }

  fun initFields() {
    fields.forEach {
      it.initialize()
    }
  }

  // ----------------------------------------------------------------------
  // REPORT MODEL
  // ----------------------------------------------------------------------
  override val model: VReport by lazy {
    initFields()
    ReportModel()
  }

  inner class ReportModel: VReport() {
    override val locale: Locale get() = this@Report.locale ?: ApplicationContext.getDefaultLocale()

    /**
     * Handling triggers
     */
    fun handleTriggers(triggers: MutableList<Trigger>) {
      // REPORT TRIGGERS
      super.VKT_Triggers = mutableListOf(IntArray(Constants.TRG_TYPES.size))
      triggers.forEach { trigger ->
        val blockTriggerArray = IntArray(Constants.TRG_TYPES.size)
        for (i in VConstants.TRG_TYPES.indices) {
          if (trigger.events shr i and 1 > 0) {
            blockTriggerArray[i] = i
            super.triggers[i] = trigger
          }
        }
        super.VKT_Triggers!![0] = blockTriggerArray
      }

      // FIELD TRIGGERS
      fields.forEach {
        val fieldTriggerArray = IntArray(Constants.TRG_TYPES.size)
        if (it.computeTrigger != null) {
          fieldTriggerArray[Constants.TRG_COMPUTE] = it.computeTrigger!!.events.toInt()
        }
        if (it.formatTrigger != null) {
          fieldTriggerArray[Constants.TRG_FORMAT] = it.formatTrigger!!.events.toInt()
        }
        // TODO : Add field triggers here
        super.VKT_Triggers!!.add(fieldTriggerArray)
      }

      // TODO: for separator column
      super.VKT_Triggers!!.add(IntArray(Constants.TRG_TYPES.size))

      // COMMANDS TRIGGERS
      commands?.forEach {
        val fieldTriggerArray = IntArray(Constants.TRG_TYPES.size)
        // TODO : Add commands triggers here
        super.VKT_Triggers!!.add(fieldTriggerArray)
      }
    }

    override fun init() {
      setTitle(title)
      super.setPageTitle(title)
      help = this@Report.help
      this.addActors(this@Report.actors.map { actor ->
        actor.buildModel(sourceFile)
      }.toTypedArray())
      this.commands = this@Report.commands.map { command ->
        command.buildModel(this, actors)
      }.toTypedArray()

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

  @PublishedApi
  internal val `access$sourceFile`: String
    get() = sourceFile
}
