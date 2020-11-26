/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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

import java.awt.event.KeyEvent
import java.io.File
import java.net.MalformedURLException
import java.text.MessageFormat
import java.util.Locale

import org.kopi.galite.cross.VDynamicReport
import org.kopi.galite.db.DBContextHandler
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VField
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.print.Printable
import org.kopi.galite.print.Printable.Companion.DOC_UNKNOWN
import org.kopi.galite.type.Date
import org.kopi.galite.util.PrintJob
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.ApplicationConfiguration
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.FileHandler
import org.kopi.galite.visual.Message
import org.kopi.galite.visual.UIFactory
import org.kopi.galite.visual.UWindow
import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VHelpViewer
import org.kopi.galite.visual.VRuntimeException
import org.kopi.galite.visual.VWindow
import org.kopi.galite.visual.VlibProperties
import org.kopi.galite.visual.WindowBuilder
import org.kopi.galite.visual.WindowController
import kotlin.jvm.Throws

/**
 * Represents a report model.
 *
 * @param ctxt Database context handler
 */
abstract class VReport internal constructor(ctxt: DBContextHandler? = null) : VWindow(), Constants, VConstants, Printable {
  companion object {
    const val TYP_CSV = 1
    const val TYP_PDF = 2
    const val TYP_XLS = 3
    const val TYP_XLSX = 4

    init {
      WindowController.windowController.registerWindowBuilder(org.kopi.galite.visual.Constants.MDL_REPORT, object : WindowBuilder {
        override fun createWindow(model: VWindow): UWindow {
          return UIFactory.uiFactory.createView(model) as UReport
        }
      })
    }
  }

  override fun getType() = org.kopi.galite.visual.Constants.MDL_REPORT

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
   * @exception        org.kopi.galite.visual.VException        may be raised by triggers
   */
  protected abstract fun init()

  /**
   * build everything after loading
   */
  protected fun build() {
    model.build()
    model.createTree()
    (getDisplay() as UReport).build()
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
          setCommandEnabled(vCommand, model.getModelColumnCount() + i + 1, true)
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
  /**
   * Localizes this report
   *
   * @param     locale  the locale to use
   */
  open fun localize(locale: Locale?) {
    var manager: LocalizationManager?
    manager = LocalizationManager(locale, ApplicationContext.getDefaultLocale())

    // localizes the actors in VWindow
    super.localizeActors(manager)
    localize(manager)
    manager = null
  }

  /**
   * Localizes this report
   *
   * @param     manager         the manger to use for localization
   */
  private fun localize(manager: LocalizationManager) {
    val loc = manager.getReportLocalizer(source)

    setPageTitle(loc.getTitle())
    help = loc.getHelp()
    model.columns.forEach { it?.localize(loc) }
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
    var enable = enable

    if (enable) {
      // we need to check if VKT_Triggers is initialized
      // ex : org.kopi.galite.cross.VDynamicReport
      if (VKT_Triggers != null && hasTrigger(Constants.TRG_CMDACCESS, index)) {

        val active: Boolean = try {
          callTrigger(Constants.TRG_CMDACCESS, index) as Boolean
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
    val exporter: PExport2PDF = PExport2PDF((getDisplay() as UReport).getTable(),
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
    fireFileProduced(file, pageTitle + "_" + Date.now().format("yyyyMMdd") + extension)
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
      executeVoidTrigger(cmdOpenLine!!.trigger)
    }
  }

  @Throws(VException::class)
  fun setColumnData() {
    if (cmdEditColumn != null) {
      executeVoidTrigger(cmdEditColumn!!.trigger)
    }
  }

  @Throws(VException::class)
  fun setColumnInfo() {
    if (cmdColumnInfo != null) {
      executeVoidTrigger(cmdColumnInfo!!.trigger)
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
  protected fun buildSQLCondition(column: String, field: VField): String {
    val condition: String? = field.getSearchCondition()

    return if (condition == null) {
      " TRUE = TRUE "
    } else {
      "$column $condition"
    }
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------
  override fun executeVoidTrigger(VKT_Type: Int) {}

  open fun executeObjectTrigger(VKT_Type: Int): Any = throw InconsistencyException("SHOULD BE REDEFINED")

  fun executeBooleanTrigger(VKT_Type: Int): Boolean = throw InconsistencyException("SHOULD BE REDEFINED")

  fun executeIntegerTrigger(VKT_Type: Int): Int = throw InconsistencyException("SHOULD BE REDEFINED")

  fun getDocumentType(): Int = DOC_UNKNOWN

  /**
   * overridden by forms to implement triggers
   * default triggers
   */
  protected fun execTrigger(block: Any, id: Int): Any? {
    executeVoidTrigger(id)
    return null
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected fun callTrigger(event: Int, index: Int = 0): Any? {
    return when (Constants.TRG_TYPES[event]) {
      Constants.TRG_VOID -> {
        executeVoidTrigger(VKT_Triggers!![index][event])
        null
      }
      Constants.TRG_OBJECT -> executeObjectTrigger(VKT_Triggers!![index][event])
      Constants.TRG_BOOLEAN -> executeBooleanTrigger(VKT_Triggers!![index][event])
      else -> throw InconsistencyException("BAD TYPE" + Constants.TRG_TYPES[event])
    }
  }

  /**
   * Returns true iff there is trigger associated with given event.
   */
  protected fun hasTrigger(event: Int, index: Int = 0): Boolean = VKT_Triggers!![index][event] != 0

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
      setCommandEnabled(cmdEditColumn!!, column != -1 && model.getAccessibleColumn(column)!!.addedAtRuntime)
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
                                                          commands!!.requireNoNulls(),
                                                          model,
                                                          help)

    return if (fileName == null) {
      null
    } else {
      try {
        surl.append(File(fileName).toURL().toString())
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
  //  initDefaultActors()
  //  initDefaultCommands()
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
    commands = arrayOfNulls(actors.size)
    actors.forEachIndexed { index, vActor ->
      commands!![index] = VCommand(VConstants.MOD_ANY, this, vActor, index,null, vActor!!.actorIdent)
    }
  }

  open var help: String? = null

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
  protected var VKT_Triggers: Array<IntArray>? = null
  var commands: Array<VCommand?>? = null
  private val activeCommands = ArrayList<VCommand>()
  var printOptions: PConfig = PConfig() // The print options
  var media: String? = null             // The media for this document

  init {
    if (ctxt != null) {
      dBContext = ctxt.dBContext
    }
    init()

    // localize the report using the default locale
    localize(ApplicationContext.getDefaultLocale())
  }
}
