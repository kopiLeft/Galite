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

package org.kopi.galite.visual.pivotTable

import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.UIFactory
import org.kopi.galite.visual.UWindow
import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VWindow
import org.kopi.galite.visual.WindowBuilder
import org.kopi.galite.visual.WindowController

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
  private val activeCommands = ArrayList<VCommand>()
  var help: String? = null

  override fun getType() = org.kopi.galite.visual.Constants.MDL_PIVOT_TABLE

  /**
   * Close window
   */
  @Deprecated("call method in display; model must not be closed")
  fun close() {
    getDisplay()!!.closeWindow()
  }

  override fun destroyModel() {
    super.destroyModel()
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
