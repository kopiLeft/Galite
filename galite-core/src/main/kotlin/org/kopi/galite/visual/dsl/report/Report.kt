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

package org.kopi.galite.visual.dsl.report

import java.io.File
import java.io.IOException
import java.util.Locale
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.Op

import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.dsl.common.Action
import org.kopi.galite.visual.dsl.common.LocalizationWriter
import org.kopi.galite.visual.dsl.common.ReportTrigger
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.dsl.common.Window
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.report.Constants
import org.kopi.galite.visual.report.VReport
import org.kopi.galite.visual.util.PrintJob
import org.kopi.galite.visual.visual.ApplicationContext

/**
 * Represents a report that contains fields [fields] and displays a table of [reportRows].

 * @param title The title of this form.
 * @param help The help text.
 * @param locale the window locale.
 */
abstract class Report(title: String, val help: String?, locale: Locale? = null) : Window(title, locale) {

  constructor(title: String, locale: Locale? = null) : this(title, null, locale)

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
  inline fun <reified T : Comparable<T>?> field(domain: Domain<T>,
                                                noinline init: ReportField<T>.() -> Unit): ReportField<T> {
    domain.kClass = T::class
    var source = `access$sourceFile`

    if(domain.javaClass.`package`.name != "org.kopi.galite.visual.domain"
        && !domain.javaClass.simpleName.isNullOrEmpty()) {
      source = domain.javaClass.`package`.name.replace(".", "/") + File.separatorChar + domain.javaClass.simpleName
    }

    val field = ReportField(domain, init, "ANM_${fields.size}", source)

    field.initialize()

    val pos = if(model.model.columns.size == 0) 0 else model.model.columns.size - 1 // TODO!!
    model.model.columns.add(pos, field.buildReportColumn())
    fields.add(field)
    field.addFieldTriggers()

    return field
  }

  fun ReportField<*>.addFieldTriggers() {
    // FIELD TRIGGERS
    val fieldTriggerArray = arrayOfNulls<Trigger>(Constants.TRG_TYPES.size)
    if (computeTrigger != null) {
      fieldTriggerArray[Constants.TRG_COMPUTE] = computeTrigger!!
    }
    if (formatTrigger != null) {
      fieldTriggerArray[Constants.TRG_FORMAT] = formatTrigger!!
    }
    // TODO : Add field triggers here
    model.VKT_Fields_Triggers.add(fieldTriggerArray)
  }

  /**
   * creates and returns a field that accept nulls. It uses [init] method to initialize the field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method.
   * @return a field.
   */
  inline fun <reified T: Comparable<T>?> nullableField(domain: Domain<T>,
                                                       noinline init: ReportField<T>.() -> Unit): ReportField<T?> {
    return field(domain, init) as ReportField<T?>
  }

  /**
   * Adds a row to the report.
   *
   * @param init initializes the row with values.
   */
  fun add(init: ReportRow.() -> Unit) {
    val row = ReportRow(fields)
    row.init()

    val list = row.addReportLine()
    // Last null value is added for the separator column
    model.model.addLine((list + listOf(null)).toTypedArray())

    reportRows.add(row)
  }

  private fun ReportRow.addReportLine(): List<Any?> {
    return fields.map { field ->
      data[field]
    }
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
    // REPORT TRIGGERS
    for (i in VConstants.TRG_TYPES.indices) {
      if (trigger.events shr i and 1 > 0) {
        model.VKT_Report_Triggers[0][i] = trigger
      }
    }

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
  // METHODS FOR SQL
  // ----------------------------------------------------------------------
  /**
   * creates an SQL condition, so that the column have to fit the
   * requirements (value and search operator) of the field.
   */
  protected fun <T> buildSQLCondition(column: ExpressionWithColumnType<T>, field: VField): Op<Boolean> {
    return field.getSearchCondition(column) ?: Op.TRUE
  }

  /**
   * Returns true if there is trigger associated with given event.
   */
  protected fun hasTrigger(event: Int): Boolean = model.hasTrigger(event)

  /**
   * Returns true if there is trigger associated with given event.
   */
  protected fun hasCommandTrigger(event: Int, index: Int): Boolean = model.hasCommandTrigger(event, index)

  fun setMenu() {
    model.setMenu()
  }

  // ----------------------------------------------------------------------
  // HELP
  // ----------------------------------------------------------------------

  fun genHelp(): String? = model.genHelp()

  fun showHelp() {
    model.showHelp()
  }

  fun addDefaultReportCommands() {
    model.addDefaultReportCommands()
  }

  /**
   * Creates a printable object
   * @return        job to print
   */
  fun createPrintJob(): PrintJob = model.createPrintJob()

  /**
   * Prints the report
   */
  fun export(type: Int = VReport.TYP_CSV) {
    model.export(type)
  }

  /**
   * Prints the report
   */
  fun export(file: File, type: Int = VReport.TYP_CSV) {
    model.export(file, type)
  }

  fun setPageTitleParams(param: Any) {
    model.setPageTitleParams(param)
  }

  fun setPageTitleParams(param1: Any, param2: Any) {
    model.setPageTitleParams(param1, param2)
  }

  fun setPageTitleParams(params: Array<Any>) {
    model.setPageTitleParams(params)
  }

  fun setFirstPageHeader(firstPageHeader: String) {
    model.setFirstPageHeader(firstPageHeader)
  }

  fun foldSelection() {
    model.foldSelection()
  }

  fun unfoldSelection() {
    model.unfoldSelection()
  }

  fun foldSelectedColumn() {
    model.foldSelectedColumn()
  }

  fun unfoldSelectedColumn() {
    model.unfoldSelectedColumn()
  }

  /**
   * Sort the displayed tree wrt to a column
   */
  fun sortSelectedColumn() {
    model.sortSelectedColumn()
  }

  /**
   * Sort the displayed tree wrt to a column
   */
  fun editLine() {
    model.editLine()
  }

  fun setColumnData() {
    model.setColumnData()
  }

  fun setColumnInfo() {
    model.setColumnInfo()
  }

  override fun addCommandTrigger() {
    model.VKT_Commands_Triggers.add(arrayOfNulls(Constants.TRG_TYPES.size))
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  override fun genLocalization(destination: String?, locale: Locale?) {
    if (locale != null) {
      val baseName = this::class.simpleName
      requireNotNull(baseName)
      val localizationDestination = destination
        ?: (this.javaClass.classLoader.getResource("")?.path +
                this.javaClass.`package`.name.replace(".", "/"))
      try {
        val writer = ReportLocalizationWriter()
        genLocalization(writer)
        writer.write(localizationDestination, baseName, locale)
      } catch (ioe: IOException) {
        ioe.printStackTrace()
        System.err.println("cannot write : $baseName")
      }
    }
  }

  fun genLocalization(writer: LocalizationWriter) {
    (writer as ReportLocalizationWriter).genReport(title, help, fields, menus, actors)
  }

  // ----------------------------------------------------------------------
  // REPORT MODEL
  // ----------------------------------------------------------------------
  override val model: VReport = object : ReportModel(this@Report) {
    init {
      // TODO: for separator column
      if(VKT_Fields_Triggers.size == 0) {
        VKT_Fields_Triggers.add(arrayOfNulls(Constants.TRG_TYPES.size))
      } else {
        VKT_Fields_Triggers.add(VKT_Fields_Triggers.size - 1, arrayOfNulls(Constants.TRG_TYPES.size))
      }
    }


    override val locale: Locale get() = this@Report.locale ?: ApplicationContext.getDefaultLocale()
  }

  @PublishedApi
  internal val `access$sourceFile`: String
    get() = sourceFile
}
