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

package org.kopi.galite.visual.pivotTable

import java.io.File
import java.net.MalformedURLException

import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.*
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.dsl.common.Trigger
import org.vaadin.addons.componentfactory.PivotTable.*

/**
 * Represents a pivot table model.
 */
abstract class VPivotTable internal constructor() : VWindow(), VConstants {
  companion object {

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

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  val model: MPivotTable = MPivotTable()
  private var built = false
  private var pageTitle = ""
  var defaultRenderer = Renderer.TABLE  // Default pivot table type
  var aggregator = Pair(Aggregator.COUNT, "") // default Aggregator
  var disabledRerenders = mutableListOf<String>()
  var interactive = Constants.MODE_INTERACTIVE
  val PIVOT_TABLE_Triggers = listOf(arrayOfNulls<Trigger>(Constants.TRG_TYPES.size))
  private val activeCommands = ArrayList<VCommand>()
  var help: String? = null

  override fun getType() = org.kopi.galite.visual.Constants.MDL_PIVOT_TABLE

  /**
   * Close window
   */
  fun close() {
    getDisplay()!!.closeWindow()
  }

  override fun destroyModel() {
    super.destroyModel()
  }

  /**
   * Sets the new type of this pivot table model.
   * @param type The new pivot table type.
   */
  internal fun setDefaultRenderer(renderer: String) {
    defaultRenderer = renderer
  }

  /**
   * Sets aggregation function of this pivot table model.
   * @param aggregate The pivot table aggregation function.
   */
  internal fun setAggregator(aggregate: Pair<String, String>) {
    aggregator = aggregate
  }

  /**
   * Sets aggregation function of this pivot table model.
   * @param aggregate The pivot table aggregation function.
   */
  internal fun setDisabledRerenders(rerenders: MutableList<String>) {
    disabledRerenders = rerenders
  }

  /**
   * Sets the mode of this pivot table model.
   * @param interactive The pivot table mode.
   */
  internal fun setInteractive(mode: Int) {
    interactive = mode
  }

  /**
   * initialise fields
   */
  protected abstract fun init()

  /**
   * build everything after loading
   */
  protected fun build() {
    init()
    // localize the pivot table using the default locale
    localize()
    if (hasTrigger(Constants.TRG_INIT)) {
      callTrigger(Constants.TRG_INIT)
    }
    model.build()
    (getDisplay() as UPivotTable?)?.build()
    built = true

    // all commands are by default enabled
    activeCommands.clear()
    commands.forEach { vCommand ->
      setCommandEnabled(vCommand, true)
    }
  }

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  override fun getLocalizationManger(): LocalizationManager {
    return LocalizationManager(ApplicationContext.getDefaultLocale(), ApplicationContext.getDefaultLocale())
  }

  /**
   * Localizes this pivot table
   *
   */
  open fun localize() {
    localize(manager)
  }

  /**
   * Localizes this pivot table
   *
   * @param     manager         the manger to use for localization
   */
  private fun localize(manager: LocalizationManager) {
    if(ApplicationContext.getDefaultLocale() != locale) {
      val loc = manager.getPivotTableLocalizer(source)

      setPageTitle(loc.getTitle())
      help = loc.getHelp()
      model.columns.forEach { it?.localize(loc) }
    }
  }

  // ----------------------------------------------------------------------
  // DISPLAY INTERFACE
  // ----------------------------------------------------------------------
  open fun initPivotTable() {
    build()
    callTrigger(Constants.TRG_PRE_PIVOT_TABLE)
  }
  // ----------------------------------------------------------------------
  // INTERFACE (COMMANDS)
  // ----------------------------------------------------------------------

  /**
   * Enables/disables the actor.
   */
  fun setCommandEnabled(command: VCommand, enable: Boolean) {
    command.setEnabled(enable)

    if (enable) {
      activeCommands.add(command)
    } else {
      activeCommands.remove(command)
    }
  }

  /**
   * Sets the title
   */
  fun setPageTitle(title: String) {
    pageTitle = title
    setTitle(title)
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  fun setMenu() {
    if (!built) {
      // only when commands are displayed
      return
    }
  }

  // --------------------------------------------------------------------
  // TRIGGER HANDLING
  // --------------------------------------------------------------------

  override fun executeVoidTrigger(trigger: Trigger?) {
    trigger?.action?.method?.invoke()
    super.executeVoidTrigger(trigger)
  }

  fun executeObjectTrigger(trigger: Trigger?): Any? {
    return (trigger?.action?.method as () -> Any?).invoke()
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected fun callTrigger(event: Int): Any? {
    return callTrigger(event, 0, PIVOT_TABLE_Triggers)
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  private fun callTrigger(event: Int, index: Int, triggers: List<Array<Trigger?>>): Any? {
    return when (Constants.TRG_TYPES[event]) {
      Constants.TRG_VOID -> {
        executeVoidTrigger(triggers[index][event])
        null
      }
      Constants.TRG_OBJECT -> executeObjectTrigger(triggers[index][event])
      else -> throw InconsistencyException("BAD TYPE" + Constants.TRG_TYPES[event])
    }
  }

  /**
   * Returns true if there is trigger associated with given event.
   */
  internal fun hasTrigger(event: Int): Boolean = PIVOT_TABLE_Triggers[0][event] != null

  // ----------------------------------------------------------------------
  // HELP
  // ----------------------------------------------------------------------

  fun genHelp(): String? {
    val surl = StringBuffer()
    val fileName: String? = VHelpGenerator().helpOnPivotTable(pageTitle,
      commands,
      model,
      help)

    return if (fileName == null) {
      null
    } else {
      try {
        surl.append(File(fileName).toURI().toURL().toString())
      } catch (mue: MalformedURLException) {
        throw InconsistencyException(mue)
      }
      surl.toString()
    }
  }

  fun showHelp() {
    VHelpViewer().showHelp(genHelp())
  }

  // ----------------------------------------------------------------------
  // Command
  // ----------------------------------------------------------------------

  fun addDefaultPivotTableCommands() {
    initDefaultCommands()
  }

  private fun initDefaultCommands() {
    actors.forEachIndexed { index, vActor ->
      commands.add(VCommand(VConstants.MOD_ANY, this, vActor, index, vActor.ident))
    }
  }
}
