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

package org.kopi.galite.visual.dsl.pivotTable

import java.io.IOException
import java.util.Locale

import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.dsl.common.*
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.pivotTable.Constants
import org.kopi.galite.visual.pivotTable.VPivotTable

abstract class PivotTable(title: String, val help: String?, locale: Locale? = null) : Window(title, locale) {

  constructor(title: String, locale: Locale? = null) : this(title, null, locale)

  /** Pivot table's fields. */
  val dimensionListe = mutableListOf<Dimension<*>>()
  val measureListe = mutableListOf<Measure<*>>()

  /** Pivot table's data rows. */
  val pivotTableRows = mutableListOf<PivotTableRow>()


  inline fun <reified T : Comparable<T>?> dimension(domain: Domain<T>,
                                                    position: Position,
                                                    noinline init: Dimension<T>.() -> Unit): Dimension<T> {
    domain.kClass = T::class

    val dimension = Dimension(domain, init, "ANM_${dimensionListe.size}",position, domain.source.ifEmpty { `access$sourceFile` })

    dimension.init()

    model.model.columns.add(dimension.buildPivotTableColumn())
    dimensionListe.add(dimension)

    return dimension
  }

  inline fun <reified T : Comparable<T>?> measure(domain: Domain<T>,
                                                noinline init: Measure<T>.() -> Unit): Measure<T> {
    domain.kClass = T::class

    val dimension = Measure(domain, init, "ANM_${measureListe.size}", domain.source.ifEmpty { `access$sourceFile` })

    dimension.init()

    model.model.columns.add(dimension.buildPivotTableColumn())
    measureListe.add(dimension)

    return dimension
  }

  /**
   * Adds a row to the Pivot table.
   *
   * @param init initializes the row with values.
   */
  fun add(init: PivotTableRow.() -> Unit) {
    val row = PivotTableRow((dimensionListe + measureListe).toMutableList())
    row.init()

    val list = row.addLine()
    // Last null value is added for the separator column
    model.model.addLine((list + listOf(null)).toTypedArray())

    pivotTableRows.add(row)
  }

  private fun PivotTableRow.addLine(): List<Any?> {
    return (dimensionListe + measureListe).toMutableList().map { field ->
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

    triggers.add(trigger)

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
  fun getRow(rowNumber: Int): MutableMap<PivotTableField<*>, Any?> = pivotTableRows[rowNumber].data

  /**
   * Returns rows of data for a specific [field].
   *
   * @param field the field.
   */
  fun getRowsForField(field: PivotTableField<*>) = pivotTableRows.map { it.data[field] }
  /**
   * Adds default Pivot table commands
   */
  open val pivotTableCommands = false

  fun setMenu() {
    model.setMenu()
  }

  // ----------------------------------------------------------------------
  // Command
  // ----------------------------------------------------------------------

  fun addDefaultPivotTableCommands() {
    model.addDefaultPivotTableCommands()
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

  val INITPIVOTTABLE = PivotTableTriggerEvent<Unit>(Constants.TRG_INIT)

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
    (writer as PivotTableLocalizationWriter).genPivotTable(title, (dimensionListe + measureListe).toMutableList(), menus, actors)
  }

  var pivotTableType: String
    get() = model.pivottableType
    set(value) {
      model.setType(value)
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
      dimensionListe.forEach {
        it.initField()
      }
      measureListe.forEach {
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

    if (pivotTableCommands) {
      addDefaultPivotTableCommands()
    }
  }

  @PublishedApi
  internal val `access$sourceFile`: String
    get() = sourceFile
}
