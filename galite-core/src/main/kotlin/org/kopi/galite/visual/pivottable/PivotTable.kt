/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.pivottable

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.api.*
import org.kopi.galite.visual.*
import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.dsl.report.ReportField
import org.kopi.galite.visual.dsl.report.ReportRow
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.report.Constants
import org.kopi.galite.visual.report.VReportColumn
import java.io.File
import java.util.*
import kotlin.reflect.full.starProjectedType

open class PivotTable(title: String?, var help: String?, override val locale: Locale?) : VWindow(), Constants, VConstants {

  constructor(title: String, locale: Locale? = null) : this(title, null, locale)

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private var pageTitle = ""

  /** Report's fields. */
  val fields = mutableListOf<ReportField<*>>()

  /** A report data row */
  val rows = mutableListOf<ReportRow>()

  val columns = mutableListOf<VReportColumn>()

  lateinit var grouping: Grouping

  var funct = Function.NONE

  internal lateinit var dataframe: AnyFrame
  private var aggregateFields = arrayOf<String>()
  var aggregateField: ReportField<*>? = null
    private set
  val model: MPivotTable = MPivotTable(this)

  init {
    setTitle(title)
  }

  fun <T: Number> aggregate(function: Function, field: ReportField<T>) {
    funct = function
    aggregateFields = arrayOf(field.label!!)
    aggregateField = field
  }

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

    val field = ReportField(domain, init, "ANM_${fields.size}", domain.source.ifEmpty { source })

    field.initialize()

    val pos = if(columns.size == 0) 0 else columns.size - 1 // TODO!!
    columns.add(pos, field.buildReportColumn())
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
  @Suppress("UNCHECKED_CAST")
  inline fun <reified T: Comparable<T>?> nullableField(domain: Domain<T>,
                                                       noinline init: ReportField<T>.() -> Unit): ReportField<T?> {
    return field(domain, init) as ReportField<T?>
  }

  override fun getType() = org.kopi.galite.visual.Constants.MDL_PIVOT_TABLE

  /**
   * Redisplay the pivot table after change in formatting
   */
  @Deprecated("call method in display; model must not be refreshed")
  fun redisplay() {
    (getDisplay() as UPivotTable).redisplay()
  }

  /**
   * Close window
   */
  @Deprecated("call method in display; model must not be closed")
  fun close() {
    getDisplay()!!.closeWindow()
  }

  override fun destroyModel() {
    /*try { TODO
      callTrigger(org.kopi.galite.visual.pivottable.Constants.TRG_POSTREPORT)
    } catch (v: VException) {
      // ignore
    }*/
    super.destroyModel()
  }

  fun columnMoved(pos: IntArray) {
    (getDisplay() as UPivotTable).columnMoved(pos)
  }

  /**
   * Sets the title
   */
  fun setPageTitle(title: String) {
    pageTitle = title
    setTitle(title)
  }

  fun getValueAt(row: Int, col: Int): String = model.getValueAt(row, col)

  // ----------------------------------------------------------------------
  // DISPLAY INTERFACE
  // ----------------------------------------------------------------------
  open fun initPivotTable() {
    build()
    //callTrigger(Constants.TRG_PREPIVOT) TODO
  }

  /**
   * Localizes this pivot table.
   *
   * @param     manager         the manger to use for localization.
   */
  private fun localize(manager: LocalizationManager) {
    if (ApplicationContext.getDefaultLocale() != locale) {
      val loc = manager.getReportLocalizer(source)

      setTitle(loc.getTitle())
      help = loc.getHelp()
      columns.forEach { it.localize(loc) }
    }
  }

  fun build() {
    localize(manager)
    buildDataFrame()
    model.build()
    (getDisplay() as UPivotTable?)?.build()
  }

  private fun buildDataFrame() {
    val df = dataFrameOf(fields)

    dataframe = if (grouping.columns.isEmpty() && grouping.rows.isEmpty()) {
      df.aggregate()
    } else if (grouping.rows.isEmpty()) {
      df.pivot().aggregate()
    } else if (grouping.columns.isEmpty()) {
      df.groupBy().aggregate()
    } else {
      df.pivot().groupBy().aggregate().sortBy()
    }
  }

  private fun <T> getAllValuesOf(field: ReportField<T>): List<T> = rows.map { it[field] }

  private fun getAllFormattedValuesOf(field: ReportField<*>): List<String> = rows.map { field.model.format(it[field]) }

  private fun dataFrameOf(header: Iterable<ReportField<*>>): AnyFrame =
    header.map { field ->
      val (values, type) = if (field == aggregateField) {
        getAllValuesOf(field) to field.domain.kClass!!.starProjectedType
      } else {
        getAllFormattedValuesOf(field) to String::class.starProjectedType
      }

      DataColumn.create(
        field.model.label,
        values,
        type
      )
    }.toDataFrame()

  private fun DataFrame<Any?>.pivot(): Pivot<Any?> {
    return if (grouping.columns.size == 1) {
      this.pivot(grouping.columns[0].label!!)
    } else {
      this.pivot {
        grouping.columns
          .subList(2, grouping.columns.size)
          .fold(grouping.columns[0].label!! then grouping.columns[1].label!!) { a, b ->
            a then b.label!!
          }
      }
    }
  }

  private fun DataFrame<Any?>.groupBy(): GroupBy<Any?, Any?> {
    return if (grouping.rows.size == 1) {
      this.groupBy(grouping.rows[0].label!!)
    } else {
      this.groupBy {
        grouping.rows
          .subList(2, grouping.rows.size)
          .fold(grouping.rows[0].label!! and grouping.rows[1].label!!) { a, b ->
            a and b.label!!
          }
      }
    }
  }

  fun Pivot<*>.groupBy(): PivotGroupBy<Any?> {
    return if (grouping.rows.size == 1) {
      this.groupBy(grouping.rows[0].label!!)
    } else {
      this.groupBy {
        grouping.rows
          .subList(2, grouping.rows.size)
          .fold(grouping.rows[0].label!! and grouping.rows[1].label!!) { a, b ->
            a and b.label!!
          }
      }
    }
  }

  private fun DataFrame<*>.sortBy(): DataFrame<Any?> {
    return if (grouping.rows.size == 1) {
      this.sortBy(grouping.rows[0].label!!)
    } else {
      this.sortBy {
        grouping.rows
          .subList(2, grouping.rows.size)
          .fold(grouping.rows[0].label!! and grouping.rows[1].label!!) { a, b ->
            a and b.label!!
          }
      }
    }
  }

  /**
   * Adds a row to the pivot table.
   *
   * @param init initializes the row with values.
   */
  fun add(init: ReportRow.() -> Unit) {
    val row = ReportRow(fields)
    row.init()

    // Last null value is added for the separator column
    rows.add(row)
  }

  private fun Aggregatable<Any?>.aggregate(): DataFrame<Any?> {
    return when (funct) {
      Function.MAX -> _max()
      Function.MEAN -> _mean()
      Function.SUM -> _sum()
      Function.MIN -> _min()
      else -> TODO()
    }
  }

  private fun Aggregatable<*>._max(): DataFrame<Any?> {
    return when (this) {
      is Pivot<*> -> {
        this.max(*aggregateFields).toDataFrame()
      }
      is DataFrame<*> -> {
        this.max().toDataFrame()
      }
      is GroupBy<*, *> -> {
        this.max(*aggregateFields)
      }
      is PivotGroupBy<*> -> {
        this.max(*aggregateFields)
      }
      else -> {
        throw UnsupportedOperationException()
      }
    }
  }

  private fun Aggregatable<*>._mean(): DataFrame<Any?> {
    return when (this) {
      is Pivot<*> -> {
        this.mean().toDataFrame()
      }
      is DataFrame<*> -> {
        this.mean().toDataFrame()
      }
      is GroupBy<*, *> -> {
        this.mean(*aggregateFields)
      }
      is PivotGroupBy<*> -> {
        this.mean(*aggregateFields)
      }
      else -> {
        throw UnsupportedOperationException()
      }
    }
  }

  /**
   * Returns the number of columns managed by the data source object.
   *
   * @return    the number or columns to display
   */
  fun getColumnCount(): Int = model.getColumnCount()

  /**
   * Returns the number of records managed by the data source object.
   *
   * @return    the number or rows in the model
   */
  fun getRowCount(): Int = model.getRowCount()

  private fun Aggregatable<*>._sum(): DataFrame<Any?> {
    return when (this) {
      is Pivot<*> -> {
        this.sum(*aggregateFields).toDataFrame()
      }
      is DataFrame<*> -> {
        this.sum().toDataFrame()
      }
      is GroupBy<*, *> -> {
        this.sum(*aggregateFields)
      }
      is PivotGroupBy<*> -> {
        this.sum(*aggregateFields)
      }
      else -> {
        throw UnsupportedOperationException()
      }
    }
  }

  private fun Aggregatable<*>._min(): DataFrame<Any?> {
    return when (this) {
      is Pivot<*> -> {
        this.min().toDataFrame()
      }
      is DataFrame<*> -> {
        this.min().toDataFrame()
      }
      is GroupBy<*, *> -> {
        this.min(*aggregateFields)
      }
      is PivotGroupBy<*> -> {
        this.min(*aggregateFields)
      }
      else -> {
        throw UnsupportedOperationException()
      }
    }
  }

  companion object {
    const val TYP_CSV = 1
    const val TYP_PDF = 2
    const val TYP_XLS = 3
    const val TYP_XLSX = 4

    init {
      WindowController.windowController.registerWindowBuilder(
        org.kopi.galite.visual.Constants.MDL_PIVOT_TABLE,
        object : WindowBuilder {
          override fun createWindow(model: VWindow): UWindow {
            return UIFactory.uiFactory.createView(
              model) as UPivotTable
          }
        }
      )
    }
  }

  @PublishedApi
  internal val `access$sourceFile`: String get() =
    this.javaClass.`package`.name.replace(".", "/") +
            File.separatorChar +
            this.javaClass.simpleName
}

enum class Function {
  NONE,
  SUM,
  MEAN,
  MIN,
  MAX
}

class Grouping(val columns: List<ReportField<*>>, val rows: List<ReportField<*>>)
