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

package org.kopi.galite.visual.tablepivot

import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.*

/**
 * Represents a report model.
 */
abstract class VPivotTable internal constructor() : VWindow(), Constants, VConstants {
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
    private var built = false
    private var pageTitle = ""
    private var firstPageHeader = ""
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
        (getDisplay() as UPivotTable?)?.build()
        built = true

        // all commands are by default enabled
        activeCommands.clear()
        commands.forEachIndexed { i, vCommand ->
            setCommandEnabled(vCommand, i, true)
        }
    }

    // ----------------------------------------------------------------------
    // INTERFACE (COMMANDS)
    // ----------------------------------------------------------------------
    /**
     * Enables/disables the actor.
     */
    fun setCommandEnabled(command: VCommand, index: Int, enable: Boolean) {
        @Suppress("NAME_SHADOWING")
        command.setEnabled(true)
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
            val loc = manager.getReportLocalizer(source)

            setPageTitle(loc.getTitle())
            help = loc.getHelp()
        }
    }

    // ----------------------------------------------------------------------
    // DISPLAY INTERFACE
    // ----------------------------------------------------------------------
    open fun initReport() {
        build()
    }
    // ----------------------------------------------------------------------
    // INTERFACE (COMMANDS)
    // ----------------------------------------------------------------------

    /**
     * Sets the title
     */
    fun setPageTitle(title: String) {
        pageTitle = title
        setTitle(title)
    }

    fun setFirstPageHeader(firstPageHeader: String) {
        this.firstPageHeader = firstPageHeader
    }

    // ----------------------------------------------------------------------
    // HELP
    // ----------------------------------------------------------------------

    fun genHelp(): String? {
//    val surl = StringBuffer()
////    val fileName: String? = VHelpGenerator().helpOnReport(pageTitle,
////                                                          commands,
////                                                          model,
////                                                          help)
//
//    return if (fileName == null) {
//      null
//    } else {
//      try {
//        surl.append(File(fileName).toURI().toURL().toString())
//      } catch (mue: MalformedURLException) {
//        throw InconsistencyException(mue)
//      }
//      surl.toString()
//    }
        return ""
    }

    fun showHelp() {
        VHelpViewer().showHelp(genHelp())
    }


    // ----------------------------------------------------------------------
    // Default Actors
    // ----------------------------------------------------------------------
    fun addDefaultReportCommands() {
        initDefaultCommands()
    }

    private fun initDefaultCommands() {
        actors.forEachIndexed { index, vActor ->
            commands.add(VCommand(VConstants.MOD_ANY, this, vActor, index, vActor.ident))
        }
    }
}
