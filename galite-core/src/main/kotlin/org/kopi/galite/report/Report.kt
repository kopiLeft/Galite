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

import java.io.File
import java.io.IOException
import java.lang.RuntimeException

import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.Window
import org.kopi.galite.domain.Domain
import org.kopi.galite.type.Date
import org.kopi.galite.type.Month
import org.kopi.galite.type.Time
import org.kopi.galite.type.Timestamp
import org.kopi.galite.type.Week

/**
 * Represents a report that contains fields [fields] and displays a table of [reportRows].
 */
abstract class Report: Window() {

  /** Report's fields. */
  val fields = mutableListOf<ReportField<*>>()

  /** Report's data rows. */
  val reportRows = mutableListOf<ReportRow>()

  /** the help text */
  var help: String? = null

  /**
   * creates and returns a field. It uses [init] method to initialize the field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method.
   * @return a field.
   */
  inline fun <reified T : Comparable<T>> field(domain: Domain<T>, init: ReportField<T>.() -> Unit): ReportField<T> {
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
   * Returns the row's data.
   *
   * @param rowNumber the index of the desired row.
   */
  fun getRow(rowNumber: Int): MutableMap<ReportField<*>, Any> = reportRows[rowNumber].data

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

  fun MReport.addReportColumns() {
    columns = fields.map {
      when(it.domain.kClass) {
        Int::class ->
          VIntegerColumn(it.label, it.options, it.align.value, it.groupID, null, it.domain.width ?: 0, null)
        String::class ->
          VStringColumn(it.label, it.options, it.align.value, it.groupID, null, it.domain.width ?: 0, it.domain.height ?: 0, null)
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

  /**
   * Returns the qualified source file name where this object is defined.
   */
  private val sourceFile: String
    get() {
      val basename = this.javaClass.packageName.replace(".", "/") + File.separatorChar
      return basename + this.javaClass.simpleName
    }


  /** Report model*/
  override val model: VReport by lazy {
    genLocalization()

    object : VReport() {
      override fun init() {
        if (reportCommands) {
          addDefaultReportCommands()
        }

        super.model.addReportColumns()
        super.model.addReportLines()

        source = sourceFile
      }

      override fun add() {
        // TODO
      }
    }
  }
}
