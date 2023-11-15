/*
 * Copyright (c) 2013-2023 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2023 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual.dsl.pivottable

import java.io.IOException
import java.util.Locale

import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.dsl.common.*
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.pivottable.Constants
import org.kopi.galite.visual.pivottable.VPivotTable

abstract class PivotTable(title: String, val help: String?, locale: Locale? = null) : Window(title, locale) {

  constructor(title: String, locale: Locale? = null) : this(title, null, locale)

  /** Pivot table's fields. */
  val dimensions = mutableListOf<Dimension<*>>()
  val measures = mutableListOf<Measure<*>>()

  /** Pivot table's data rows. */
  val rows = mutableListOf<PivotTableRow>()


  inline fun <reified T : Comparable<T>?> dimension(domain: Domain<T>,
                                                    position: Dimension.Position,
                                                    noinline init: Dimension<T>.() -> Unit): Dimension<T> {
    domain.kClass = T::class
    val dimension = Dimension(domain, init, "DIMENSION_${dimensions.size}", position, domain.source.ifEmpty { `access$sourceFile` })
    dimension.init()
    model.model.columns.add(dimension.buildPivotTableColumn())
    dimensions.add(dimension)

    return dimension
  }

  inline fun <reified T : Comparable<T>?> measure(domain: Domain<T>,
                                                  noinline init: Measure<T>.() -> Unit): Measure<T> {
    domain.kClass = T::class
    val measure = Measure(domain, init, "MEASURE_${measures.size}", domain.source.ifEmpty { `access$sourceFile` })
    measure.init()
    model.model.columns.add(measure.buildPivotTableColumn())
    measures.add(measure)

    return measure
  }

  /**
   * Adds a row to the Pivot table.
   *
   * @param init initializes the row with values.
   */
  fun add(init: PivotTableRow.() -> Unit) {
    val row = PivotTableRow((dimensions + measures).toMutableList())
    row.init()

    val list = row.addLine()
    model.model.addLine(list.toTypedArray())

    rows.add(row)
  }

  private fun PivotTableRow.addLine(): List<Any?> {
    return (dimensions + measures).toMutableList().map { field ->
      data[field]
    }
  }

  /**
   * Adds triggers to this pivot table
   *
   * @param pivotTriggerEvents   the trigger events to add
   * @param method               the method to execute when trigger is called
   */
  fun <T> trigger(vararg pivotTriggerEvents: PivotTableTriggerEvent<T>, method: () -> T): Trigger {
    val event = formEventList(pivotTriggerEvents)
    val pivotTableAction = Action(null, method)
    val trigger = FormTrigger(event, pivotTableAction)

    triggers.add(FormTrigger(event, Action(null, method)))

    // PIVOT TABLE TRIGGERS
    triggers.forEach { trigger ->

      for (i in VConstants.TRG_TYPES.indices) {
        if (trigger.events shr i and 1 > 0) {
          model.PIVOT_TABLE_Triggers[0][i] = trigger
        }
      }
    }

    return trigger
  }

  private fun formEventList(PivotTableTriggerEvent: Array<out PivotTableTriggerEvent<*>>): Long {
    var self = 0L

    PivotTableTriggerEvent.forEach { trigger ->
      self = self or (1L shl trigger.event)
    }

    return self
  }

  /**
   * Returns the row's data.
   *
   * @param rowNumber the index of the desired row.
   */
  fun getRow(rowNumber: Int): MutableMap<PivotTableField<*>, Any?> = rows[rowNumber].data

  /**
   * Returns rows of data for a specific [field].
   *
   * @param field the field.
   */
  fun getRowsForField(field: PivotTableField<*>) = rows.map { it.data[field] }

  fun setMenu() {
    model.setMenu()
  }

  ///////////////////////////////////////////////////////////////////////////
  // PIVOT TABLE TRIGGERS EVENTS
  ///////////////////////////////////////////////////////////////////////////

  /**
   * Pivot table Triggers
   *
   * @param event the event of the trigger
   */
  open class PivotTableTriggerEvent<T>(val event: Int)

  /**
   * Executed at pivot table initialization.
   */

  val INIT = PivotTableTriggerEvent<Unit>(Constants.TRG_INIT)

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
        val writer = PivotTableLocalizationWriter()
        genLocalization(writer)
        writer.write(localizationDestination, baseName, locale)
      } catch (ioe: IOException) {
        ioe.printStackTrace()
        System.err.println("cannot write : $baseName")
      }
    }
  }

  fun genLocalization(writer: LocalizationWriter) {
    (writer as PivotTableLocalizationWriter).genPivotTable(title, (dimensions + measures).toMutableList(), menus, actors)
  }

  var defaultRenderer: String
    get() = model.defaultRenderer
    set(value) {
      model.setDefaultRenderer(value)
    }

  var aggregator: Pair<String, String>
    get() = model.aggregator
    set(value) {
      model.setAggregator(value)
    }

  var disabledRerenders: MutableList<String>
    get() = model.disabledRerenders
  set(value) {
    model.setDisabledRerenders(value)
  }

  var interactive: Int
    get() = model.interactive
    set(value) {
      model.setInteractive(value)
    }

  // ----------------------------------------------------------------------
  // Pivot table MODEL
  // ----------------------------------------------------------------------
  override val model: VPivotTable = object : VPivotTable() {

    override fun init() {
      dimensions.forEach {
        it.initField()
      }
      measures.forEach {
        it.initField()
      }
    }

    override val locale: Locale get() = this@PivotTable.locale ?: ApplicationContext.getDefaultLocale()
  }

  init {
    model.setTitle(title)
    model.setPageTitle(title)
    model.help = help
    model.source = sourceFile
  }

  @PublishedApi
  internal val `access$sourceFile`: String
    get() = sourceFile
}
