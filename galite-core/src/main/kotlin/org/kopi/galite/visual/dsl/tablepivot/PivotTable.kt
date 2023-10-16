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

package org.kopi.galite.visual.dsl.tablepivot

import java.io.IOException
import java.util.Locale
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.Op

import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.dsl.common.LocalizationWriter
import org.kopi.galite.visual.dsl.common.Window
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.report.Constants
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.tablepivot.VPivotTable

/**
 * Represents a report that contains fields [fields] and displays a table of [reportRows].

 * @param title The title of this form.
 * @param help The help text.
 * @param locale the window locale.
 */
abstract class PivotTable(title: String, val help: String?, locale: Locale? = null) : Window(title, locale) {

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

    val field = ReportField(domain, init, "ANM_${fields.size}", domain.source.ifEmpty { `access$sourceFile` })

    field.initialize()
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
   * creates and returns fields. It uses [init] method to initialize the fields.
   *
   * @param fieldsNumber the number of fields to create.
   * @param domain  the domain of the field.
   * @param init    initialization method.
   * @return a field.
   */
  inline fun <reified T : Comparable<T>?> field(fieldsNumber: Int,
                                                domain: Domain<T>,
                                                noinline init: ReportField<T>.() -> Unit): List<ReportField<T?>> {
    return (0 until fieldsNumber).map {
      nullableField(domain, init).also { field ->
        field.model.label = "${field.label}_${it + 1}"
      }
    }
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

    reportRows.add(row)
  }

  private fun ReportRow.addReportLine(): List<Any?> {
    return fields.map { field ->
      data[field]
    }
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
   * Sets the title
   */
  fun setPageTitle(title: String) {
    model.setPageTitle(title)
  }

  fun setFirstPageHeader(firstPageHeader: String) {
    model.setFirstPageHeader(firstPageHeader)
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
  override val model: VPivotTable = object : VPivotTable() {

    override fun init() {
      fields.forEach {
        it.initField()

        if (it.group != null) {
          it.groupID = fields.indexOf(it.group)
          it.model.groups = it.groupID
        }
      }
    }

    override val locale: Locale get() = this@PivotTable.locale ?: ApplicationContext.getDefaultLocale()
  }

  init {
    model.setTitle(title)
    model.setPageTitle(title)
    model.help = help
    model.source = sourceFile

    if (reportCommands) {
      addDefaultReportCommands()
    }

  }

  @PublishedApi
  internal val `access$sourceFile`: String
    get() = sourceFile
}
