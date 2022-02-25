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

import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.dsl.common.Action
import org.kopi.galite.visual.dsl.common.LocalizationWriter
import org.kopi.galite.visual.dsl.common.ReportTrigger
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.dsl.common.Window
import org.kopi.galite.visual.report.Constants
import org.kopi.galite.visual.report.VReport
import org.kopi.galite.visual.util.PrintJob

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
    val field = ReportField(domain, init, "ANM_${fields.size}", `access$sourceFile`)
    fields.add(field)
    return field
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

  var pageTitle: String? = null

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

  // ----------------------------------------------------------------------
  // REPORT MODEL
  // ----------------------------------------------------------------------
  override val model: VReport by lazy {
    initFields()
    ReportModel(this).also { r ->
      pageTitle?.let { r.setPageTitle(it) }
      isModelInitialized = true
    }
  }

  fun initFields() {
    fields.forEach {
      it.initialize()
    }
  }

  @PublishedApi
  internal val `access$sourceFile`: String
    get() = sourceFile
}
