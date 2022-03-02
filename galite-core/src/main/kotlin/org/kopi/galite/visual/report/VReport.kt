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

package org.kopi.galite.visual.report

import java.awt.event.KeyEvent
import java.io.File
import java.net.MalformedURLException
import java.text.MessageFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import kotlin.jvm.Throws

import org.apache.poi.ss.formula.functions.T
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.kopi.galite.visual.cross.VDynamicReport
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.print.Printable
import org.kopi.galite.visual.print.Printable.Companion.DOC_UNKNOWN
import org.kopi.galite.visual.util.PrintJob
import org.kopi.galite.visual.util.base.InconsistencyException
import org.kopi.galite.visual.visual.ApplicationConfiguration
import org.kopi.galite.visual.visual.ApplicationContext
import org.kopi.galite.visual.visual.FileHandler
import org.kopi.galite.visual.visual.Message
import org.kopi.galite.visual.visual.UIFactory
import org.kopi.galite.visual.visual.UWindow
import org.kopi.galite.visual.visual.VCommand
import org.kopi.galite.visual.visual.VException
import org.kopi.galite.visual.visual.VHelpViewer
import org.kopi.galite.visual.visual.VRuntimeException
import org.kopi.galite.visual.visual.VWindow
import org.kopi.galite.visual.visual.VlibProperties
import org.kopi.galite.visual.visual.WindowBuilder
import org.kopi.galite.visual.visual.WindowController

/**
 * Represents a report model.
 *
 * @param ctxt Database context handler
 */
abstract class VReport internal constructor() : VWindow(), Constants, VConstants, Printable {
  companion object {
    const val TYP_CSV = 1
    const val TYP_PDF = 2
    const val TYP_XLS = 3
    const val TYP_XLSX = 4

    init {
      WindowController.windowController.registerWindowBuilder(
        org.kopi.galite.visual.visual.Constants.MDL_REPORT,
        object : WindowBuilder {
          override fun createWindow(model: VWindow): UWindow {
            return UIFactory.uiFactory.createView(
              model) as UReport
          }
        }
      )
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private var cmdFold: VCommand? = null
  private var cmdUnfold: VCommand? = null
  private var cmdSort: VCommand? = null
  private var cmdOpenLine: VCommand? = null
  private var cmdFoldColumn: VCommand? = null
  private var cmdUnfoldColumn: VCommand? = null
  private var cmdColumnInfo: VCommand? = null
  private var cmdEditColumn: VCommand? = null
  override lateinit var source: String // The source for this document
  val model: MReport = MReport()
  private var built = false
  private var pageTitle = ""
  private var firstPageHeader = ""
  var VKT_Report_Triggers = mutableListOf<Array<Trigger?>>(arrayOfNulls(Constants.TRG_TYPES.size))
  var VKT_Fields_Triggers = mutableListOf<Array<Trigger?>>()
  var VKT_Commands_Triggers = mutableListOf<Array<Trigger?>>()
  private val activeCommands = ArrayList<VCommand>()
  var printOptions: PConfig = PConfig() // The print options
  var media: String? = null             // The media for this document
  var help: String? = null

  override fun getType() = org.kopi.galite.visual.visual.Constants.MDL_REPORT

  /**
   * Redisplay the report after change in formatting
   */
  @Deprecated("call method in display; model must not be refreshed")
  fun redisplay() {
    (getDisplay() as UReport).redisplay()
  }

  /**
   * Close window
   */
  @Deprecated("call method in display; model must not be closed")
  fun close() {
    getDisplay()!!.closeWindow()
  }

  override fun destroyModel() {
    try {
      callTrigger(Constants.TRG_POSTREPORT)
    } catch (v: VException) {
      // ignore
    }
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
    model.build()
    model.createTree()
    (getDisplay() as UReport?)?.build()
    built = true

    // all commands are by default enabled
    activeCommands.clear()
    commands?.forEachIndexed { i, vCommand ->
      when {
        vCommand!!.getIdent() == "Fold" -> cmdFold = vCommand
        vCommand.getIdent() == "Unfold" -> cmdUnfold = vCommand
        vCommand.getIdent() == "Sort" -> cmdSort = vCommand
        vCommand.getIdent() == "FoldColumn" -> cmdFoldColumn = vCommand
        vCommand.getIdent() == "UnfoldColumn" -> cmdUnfoldColumn = vCommand
        vCommand.getIdent() == "OpenLine" -> cmdOpenLine = vCommand
        vCommand.getIdent() == "ColumnInfo" -> cmdColumnInfo = vCommand
        vCommand.getIdent() == "EditColumnData" -> cmdEditColumn = vCommand
        else -> {
          setCommandEnabled(vCommand, i, true)
        }
      }
    }
  }

  fun columnMoved(pos: IntArray) {
    (getDisplay() as UReport).columnMoved(pos)
  }
  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  override fun getLocalizationManger(): LocalizationManager {
    return LocalizationManager(ApplicationContext.getDefaultLocale(), ApplicationContext.getDefaultLocale())
  }

  /**
   * Localizes this report
   *
   */
  open fun localize() {
    localize(manager)
  }

  /**
   * Localizes this report
   *
   * @param     manager         the manger to use for localization
   */
  private fun localize(manager: LocalizationManager) {
    if(ApplicationContext.getDefaultLocale() != locale) {
      val loc = manager.getReportLocalizer(source)

      setPageTitle(loc.getTitle())
      help = loc.getHelp()
      model.columns.forEach { it?.localize(loc) }
    }
  }

  // ----------------------------------------------------------------------
  // DISPLAY INTERFACE
  // ----------------------------------------------------------------------
  open fun initReport() {
    build()
    callTrigger(Constants.TRG_PREREPORT)
  }
  // ----------------------------------------------------------------------
  // INTERFACE (COMMANDS)
  // ----------------------------------------------------------------------
  /**
   * Enables/disables the actor.
   */
  fun setCommandEnabled(command: VCommand, index: Int, enable: Boolean) {
    @Suppress("NAME_SHADOWING")
    var enable = enable

    if (enable) {
      // we need to check if VKT_Triggers is not empty
      // ex : org.kopi.galite.visual.cross.VDynamicReport
      if (VKT_Commands_Triggers.isNotEmpty() && hasCommandTrigger(Constants.TRG_CMDACCESS, index)) {

        val active: Boolean = try {
          callCommandTrigger(Constants.TRG_CMDACCESS, index) as Boolean
        } catch (e: VException) {
          // trigger call error ==> command is considered as active
          true
        }
        enable = active
      }
      command.setEnabled(enable)
      activeCommands.add(command)
    } else {
      activeCommands.remove(command)
      command.setEnabled(false)
    }
  }

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

  override fun createPrintJob(): PrintJob {
    val exporter = PExport2PDF((getDisplay() as UReport).getTable(),
                               model,
                               printOptions,
                               pageTitle,
                               firstPageHeader,
                               Message.getMessage("toner_save_mode") == "true")
    val printJob: PrintJob = exporter.export()

    printJob.documentType = getDocumentType()
    return printJob
  }

  /**
   * Prints the report
   */
  fun export(type: Int = TYP_CSV) {
    val ext: String = when (type) {
      TYP_CSV -> ".csv"
      TYP_PDF -> ".pdf"
      TYP_XLS -> ".xls"
      TYP_XLSX -> ".xlsx"
      else -> throw InconsistencyException("Export type unknown")
    }
    val file: File? = FileHandler.fileHandler?.chooseFile(getDisplay()!!,
                                                          ApplicationConfiguration.getConfiguration()!!.getDefaultDirectory(),
                                                          "report$ext")
    file?.let { export(it, type) }
  }

  /**
   * Prints the report
   */
  fun export(file: File, type: Int = TYP_CSV) {
    setWaitInfo(VlibProperties.getString("export-message"))
    val extension: String
    val exporter: PExport

    when (type) {
      TYP_CSV -> {
        extension = ".csv"
        exporter = PExport2CSV((getDisplay() as UReport).getTable(),
                               model,
                               printOptions,
                               pageTitle)
      }
      TYP_PDF -> {
        extension = ".pdf"
        exporter = PExport2PDF((getDisplay() as UReport).getTable(),
                               model,
                               printOptions,
                               pageTitle,
                               firstPageHeader,
                               Message.getMessage("toner_save_mode") == "true")
      }
      TYP_XLS -> {
        extension = ".xls"
        exporter = PExport2XLS((getDisplay() as UReport).getTable(),
                               model,
                               printOptions,
                               pageTitle)
      }
      TYP_XLSX -> {
        extension = ".xlsx"
        exporter = PExport2XLSX((getDisplay() as UReport).getTable(),
                                model,
                                printOptions,
                                pageTitle)
      }
      else -> throw InconsistencyException("Export type unknown")
    }
    exporter.export(file)
    unsetWaitInfo()
    fireFileProduced(file, pageTitle + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + extension)
  }

  /**
   * Sets the title
   */
  fun setPageTitle(title: String) {
    pageTitle = title
    setTitle(title)
  }

  fun setPageTitleParams(param: Any) {
    setPageTitleParams(arrayOf(param))
  }

  fun setPageTitleParams(param1: Any, param2: Any) {
    setPageTitleParams(arrayOf(param1, param2))
  }

  fun setPageTitleParams(params: Array<Any>) {
    setPageTitle(MessageFormat.format(pageTitle, *params))
  }

  fun setFirstPageHeader(firstPageHeader: String) {
    this.firstPageHeader = firstPageHeader
  }

  fun getColumn(i: Int): VReportColumn {
    return model.getModelColumn(i)
  }

  fun foldSelection() {
    val column = getSelectedColumn()

    if (column != -1) {
      model.foldingColumn(column)
    } else {
      val (x, y) = getSelectedCell()
      if (y != -1 && x != -1) {
        model.foldingRow(y, x)
      }
    }
    setMenu()
  }

  fun unfoldSelection() {
    val column = getSelectedColumn()

    if (column != -1) {
      model.unfoldingColumn(column)
    } else {
      val (x, y) = getSelectedCell()
      if (y != -1 && x != -1) {
        model.unfoldingRow(y, x)
      }
    }
    setMenu()
  }

  fun foldSelectedColumn() {
    val column = getSelectedColumn()

    if (column != -1) {
      model.setColumnFolded(column, true)
    }
    (getDisplay() as UReport).resetWidth()
    setMenu()
  }

  fun unfoldSelectedColumn() {
    val column = getSelectedColumn()

    if (column != -1) {
      model.setColumnFolded(column, false)
    }
    (getDisplay() as UReport).resetWidth()
    setMenu()
  }

  /**
   * Sort the displayed tree wrt to a column
   */
  fun sortSelectedColumn() {
    model.sortColumn(getSelectedColumn())
  }

  /**
   * Sort the displayed tree wrt to a column
   */
  @Throws(VException::class)
  fun editLine() {
    if (cmdOpenLine != null) {
      cmdOpenLine!!.action?.invoke()
    }
  }

  @Throws(VException::class)
  fun setColumnData() {
    if (cmdEditColumn != null) {
      cmdEditColumn!!.action?.invoke()
    }
  }

  @Throws(VException::class)
  fun setColumnInfo() {
    if (cmdColumnInfo != null) {
      cmdColumnInfo!!.action?.invoke()
    }
  }

  // ----------------------------------------------------------------------
  // INTERFACE (COMMANDS)
  // ----------------------------------------------------------------------
  /**
   * Adds a line.
   */
  abstract fun add()

  /**
   * Returns the ID
   */
  fun getValueOfFieldId(): Int {
    var idCol = -1
    var id = -1
    var i = 0

    while (i < model.getModelColumnCount() && idCol == -1) {
      if (model.getModelColumn(i).ident == "ID") {
        idCol = i
      }
      i++
    }
    if (idCol != -1 && getSelectedCell().y != -1) {
      id = (model.getRow(getSelectedCell().y)?.getValueAt(idCol) as Int)
    }
    return if (id == -1) {
      throw VRuntimeException()
    } else {
      id
    }
  }

  /**
   * Return the value of a field in the selected row
   * by passing its name(key)
   */
  fun getValueOfField(key: Any): Any? {
    var col = -1
    var i = 0

    while (i < model.getModelColumnCount() && col == -1) {
      if (model.getModelColumn(i).ident == key) {
        col = i
      }
      i++
    }
    return if (col != -1 && getSelectedCell().y != -1) {
      model.getRow(getSelectedCell().y)?.getValueAt(col)
    } else null
  }
  // ----------------------------------------------------------------------
  // METHODS FOR SQL
  // ----------------------------------------------------------------------
  /**
   * creates an SQL condition, so that the column have to fit the
   * requirements (value and search operator) of the field.
   */
  protected fun buildSQLCondition(column: ExpressionWithColumnType<T>, field: VField): String {
    val condition = field.getSearchCondition(column)

    return if (condition == null) {
      " TRUE = TRUE "
    } else {
      "$column $condition"
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------
  override fun executeVoidTrigger(trigger: Trigger?) {
    trigger?.action?.method?.invoke()
  }

  open fun executeObjectTrigger(trigger: Trigger?): Any = throw InconsistencyException("SHOULD BE REDEFINED")

  fun executeBooleanTrigger(trigger: Trigger?): Boolean = throw InconsistencyException("SHOULD BE REDEFINED")

  fun executeIntegerTrigger(trigger: Trigger?): Int = throw InconsistencyException("SHOULD BE REDEFINED")

  fun getDocumentType(): Int = DOC_UNKNOWN

  /**
   * overridden by forms to implement triggers
   * default triggers
   */
  protected fun execTrigger(block: Any, trigger: Trigger?): Any? {
    executeVoidTrigger(trigger)
    return null
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  internal fun callCommandTrigger(event: Int, index: Int): Any? {
    return callTrigger(event, index, VKT_Commands_Triggers)
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  internal fun callTrigger(event: Int): Any? {
    return callTrigger(event, 0, VKT_Report_Triggers)
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  internal fun callFieldTrigger(event: Int, index: Int): Any? {
    return callTrigger(event, index, VKT_Fields_Triggers)
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
      Constants.TRG_BOOLEAN -> executeBooleanTrigger(triggers[index][event])
      else -> throw InconsistencyException("BAD TYPE" + Constants.TRG_TYPES[event])
    }
  }

  /**
   * Returns true if there is trigger associated with given event.
   */
  protected fun hasTrigger(event: Int): Boolean = VKT_Report_Triggers[0][event] != null

  /**
   * Returns true if there is trigger associated with given event.
   */
  protected fun hasCommandTrigger(event: Int, index: Int): Boolean = VKT_Commands_Triggers[index][event] != null

  fun setMenu() {
    if (!built) {
      // only when commands are displayed
      return
    }
    val column = getSelectedColumn()
    val (x, y) = getSelectedCell()
    val foldEnabled = column != -1 && !model.isColumnFold(column) || x != -1 && y != -1 && !model.isRowFold(y, x)
    val unfoldEnabled = column != -1 || x != -1 && y != -1

    if (cmdFold != null) {
      setCommandEnabled(cmdFold!!, foldEnabled)
    }
    if (cmdUnfold != null) {
      setCommandEnabled(cmdUnfold!!, unfoldEnabled)
    }
    if (cmdSort != null) {
      setCommandEnabled(cmdSort!!, column != -1)
    }
    if (cmdOpenLine != null) {
      setCommandEnabled(cmdOpenLine!!, model.isRowLine(y))
    }
    if (cmdFoldColumn != null) {
      setCommandEnabled(cmdFoldColumn!!, column != -1)
    }
    if (cmdUnfoldColumn != null) {
      setCommandEnabled(cmdUnfoldColumn!!, column != -1)
    }
    if (cmdColumnInfo != null) {
      setCommandEnabled(cmdColumnInfo!!, column != -1)
    }
    if (cmdEditColumn != null) {
      setCommandEnabled(cmdEditColumn!!, column != -1 && model.getAccessibleColumn(column)!!.isAddedAtRuntime)
    }
  }
  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  /**
   * Returns the selected column or -1 if no column is selected.
   */
  private fun getSelectedColumn(): Int = (getDisplay() as UReport).getSelectedColumn()

  /**
   * Returns the selected cell or !!! ??? if no cell is selected.
   */
  private fun getSelectedCell(): Point = (getDisplay() as UReport).getSelectedCell()

  // ----------------------------------------------------------------------
  // HELP
  // ----------------------------------------------------------------------

  fun genHelp(): String? {
    val surl = StringBuffer()
    val fileName: String? = VHelpGenerator().helpOnReport(pageTitle,
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

  fun addDefaultReportCommands() {
    initDefaultActors()
    initDefaultCommands()
  }

  private fun initDefaultActors() {
    addActors(arrayOf(
            VDefaultReportActor("File", "Quit", VDynamicReport.QUIT_ICON, KeyEvent.VK_ESCAPE, 0),
            VDefaultReportActor("File", "Print", VDynamicReport.PRINT_ICON, KeyEvent.VK_F6, 0),
            VDefaultReportActor("File", "ExportCSV", VDynamicReport.EXPORT_ICON, KeyEvent.VK_F8, 0),
            VDefaultReportActor("File", "ExportXLSX", VDynamicReport.EXPORT_ICON, KeyEvent.VK_F9, KeyEvent.SHIFT_MASK),
            VDefaultReportActor("File", "ExportPDF", VDynamicReport.EXPORT_ICON, KeyEvent.VK_F9, 0),
            VDefaultReportActor("Action", "Fold", VDynamicReport.FOLD_ICON, KeyEvent.VK_F2, 0),
            VDefaultReportActor("Action", "Unfold", VDynamicReport.UNFOLD_ICON, KeyEvent.VK_F3, 0),
            VDefaultReportActor("Action", "FoldColumn", VDynamicReport.FOLD_COLUMN_ICON, KeyEvent.VK_UNDEFINED, 0),
            VDefaultReportActor("Action", "UnfoldColumn", VDynamicReport.UNFOLD_COLUMN_ICON, KeyEvent.VK_UNDEFINED, 0),
            VDefaultReportActor("Action", "Sort", VDynamicReport.SERIALQUERY_ICON, KeyEvent.VK_F4, 0),
            VDefaultReportActor("Help", "Help", VDynamicReport.HELP_ICON, KeyEvent.VK_F1, 0),
    ))
  }

  private fun initDefaultCommands() {
    actors.forEachIndexed { index, vActor ->
      commands.add(VCommand(VConstants.MOD_ANY, this, vActor, index, vActor.actorIdent))
    }
  }
}
