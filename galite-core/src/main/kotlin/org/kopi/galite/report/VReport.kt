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

import org.kopi.galite.base.DBContextHandler
import org.kopi.galite.chart.VHelpGenerator
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VField
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.l10n.ReportLocalizer
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

import java.io.File
import java.net.MalformedURLException
import java.text.MessageFormat
import java.util.*

abstract class VReport protected constructor(ctxt: DBContextHandler? = null) : VWindow(), Constants, VConstants, Printable {
  companion object {
    const val TYP_CSV = 1
    const val TYP_PDF = 2
    const val TYP_XLS = 3
    const val TYP_XLSX = 4

    init {
      WindowController.windowController?.registerWindowBuilder(org.kopi.galite.visual.Constants.MDL_REPORT, object : WindowBuilder {
        override fun createWindow(model: VWindow): UWindow {
          return UIFactory.uiFactory.createView(model) as UReport
        }
      })
    }
  }

  override fun getType() = org.kopi.galite.visual.Constants.MDL_REPORT

  /**
   * Redisplay the report after change in formating
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
    getDisplay().closeWindow()
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
    activeCommands.setSize(0)
    if (commands != null) {
      for (i in commands!!.indices) {
        val command: VCommand = commands!![i]
        when {
          command.getIdent() == "Fold" -> cmdFold = command
          command.getIdent() == "Unfold" -> cmdUnfold = command
          command.getIdent() == "Sort" -> cmdSort = command
          command.getIdent() == "FoldColumn" -> cmdFoldColumn = command
          command.getIdent() == "UnfoldColumn" -> cmdUnfoldColumn = command
          command.getIdent() == "OpenLine" -> cmdOpenLine = command
          command.getIdent() == "ColumnInfo" -> cmdColumnInfo = command
          command.getIdent() == "EditColumnData" -> cmdEditColumn = command
          else -> {
            setCommandEnabled(commands!![i], model.getModelColumnCount() + i + 1, true)
          }
        }

      }
    }
  }

  fun columnMoved(pos: IntArray?) {
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
  fun localize(locale: Locale) {
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
    val loc: ReportLocalizer = manager.getReportLocalizer(source)
    setPageTitle(loc.getTitle())
    help = loc.getHelp()

    model.columns.forEach { it.localize(loc) }
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  // ----------------------------------------------------------------------
  // DISPLAY INTERFACE
  // ----------------------------------------------------------------------
  fun initReport() {
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
      // ex : org.kopi.vkopi.lib.cross.VDynamicReport
      if (VKT_Triggers != null && hasTrigger(Constants.TRG_CMDACCESS, index)) {
        var active: Boolean
        try {
          active = (callTrigger(Constants.TRG_CMDACCESS, index) as Boolean)
        } catch (e: VException) {
          // trigger call error ==> command is considered as active
          active = true
        }
        enable = active
      }
      command.isEnabled = enable
      activeCommands.addElement(command)
    } else {
      activeCommands.removeElement(command)
      command.isEnabled = false
    }
  }

  /**
   * Enables/disables the actor.
   */
  fun setCommandEnabled(command: VCommand, enable: Boolean) {
    command.isEnabled = enable
    if (enable) {
      activeCommands.addElement(command)
    } else {
      activeCommands.removeElement(command)
    }
  }

  override fun createPrintJob(): PrintJob {
    val exporter: PExport2PDF
    val printJob: PrintJob
    exporter = PExport2PDF((getDisplay() as UReport).table,
            model,
            printOptions,
            pageTitle,
            firstPageHeader,
            Message.getMessage("toner_save_mode") == "true")
    printJob = exporter.export()
    printJob.setDocumentType(documentType)
    return printJob
  }
  /**
   * Prints the report
   */
  /**
   * Prints the report
   */
  fun export(type: Int = TYP_CSV) {
    val ext: String = when (type) {
      TYP_CSV -> ".csv"
      TYP_PDF -> ".pdf"
      TYP_XLS -> ".xls"
      TYP_XLSX -> ".xlsx"
      else -> throw InconsistencyException("Export type unkown")
    }
    val file: File = FileHandler.fileHandler.chooseFile(getDisplay(),
            ApplicationConfiguration.getConfiguration().getDefaultDirectory(),
            "report$ext")
    file?.let { export(it, type) }
  }

  /**
   * Prints the report
   */
  fun export(file: File?, type: Int = TYP_CSV) {
    setWaitInfo(VlibProperties.getString("export-message"))
    val extension: String
    val exporter: PExport
    when (type) {
      TYP_CSV -> {
        extension = ".csv"
        exporter = PExport2CSV((getDisplay() as UReport).table,
                model,
                printOptions,
                pageTitle)
      }
      TYP_PDF -> {
        extension = ".pdf"
        exporter = PExport2PDF((getDisplay() as UReport).table,
                model,
                printOptions,
                pageTitle,
                firstPageHeader,
                Message.getMessage("toner_save_mode") == "true")
      }
      TYP_XLS -> {
        extension = ".xls"
        exporter = PExport2XLS((getDisplay() as UReport).table,
                model,
                printOptions,
                pageTitle)
      }
      TYP_XLSX -> {
        extension = ".xlsx"
        exporter = PExport2XLSX((getDisplay() as UReport).table,
                model,
                printOptions,
                pageTitle)
      }
      else -> throw InconsistencyException("Export type unkown")
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
    val column = selectedColumn
    if (column != -1) {
      model.foldingColumn(column)
    } else {
      val (x, y) = selectedCell
      if (y !== -1 && x !== -1) {
        model.foldingRow(y, x)
      }
    }
    setMenu()
  }

  fun unfoldSelection() {
    val column = selectedColumn
    if (column != -1) {
      model.unfoldingColumn(column)
    } else {
      val (x, y) = selectedCell
      if (y !== -1 && x !== -1) {
        model.unfoldingRow(y, x)
      }
    }
    setMenu()
  }

  fun foldSelectedColumn() {
    val column = selectedColumn
    if (column != -1) {
      model.setColumnFolded(column, true)
    }
    (getDisplay() as UReport).resetWidth()
    setMenu()
  }

  fun unfoldSelectedColumn() {
    val column = selectedColumn
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
    model.sortColumn(selectedColumn)
  }

  /**
   * Sort the displayed tree wrt to a column
   */
  fun editLine() {
    if (cmdOpenLine != null) {
      executeVoidTrigger(cmdOpenLine!!.trigger)
    }
  }

  fun setColumnData() {
    if (cmdEditColumn != null) {
      executeVoidTrigger(cmdEditColumn!!.trigger)
    }
  }

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
  val valueOfFieldId: Int
    get() {
      var idCol = -1
      var id = -1
      var i = 0
      while (i < model.getModelColumnCount() && idCol == -1) {
        if (model.getModelColumn(i).getIdent() == "ID") {
          idCol = i
        }
        i++
      }
      if (idCol != -1 && selectedCell.y !== -1) {
        id = (model.getRow(selectedCell.y).getValueAt(idCol) as Int).toInt()
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
  fun getValueOfField(key: Any?): Any? {
    var col = -1
    var i = 0
    while (i < model.getModelColumnCount() && col == -1) {
      if (model.getModelColumn(i).getIdent() == key) {
        col = i
      }
      i++
    }
    return if (col != -1 && selectedCell.y !== -1) {
      model.getRow(selectedCell.y).getValueAt(col)
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
    val condition: String?
    condition = field.getSearchCondition()
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

  fun executeObjectTrigger(VKT_Type: Int): Any {
    throw InconsistencyException("SHOULD BE REDEFINED")
  }

  fun executeBooleanTrigger(VKT_Type: Int): Boolean {
    throw InconsistencyException("SHOULD BE REDEFINED")
  }

  fun executeIntegerTrigger(VKT_Type: Int): Int {
    throw InconsistencyException("SHOULD BE REDEFINED")
  }

  val documentType: Int
    get() = DOC_UNKNOWN

  /**
   * overridden by forms to implement triggers
   * default triggers
   */
  protected fun execTrigger(block: Any?, id: Int): Any? {
    executeVoidTrigger(id)
    return null
  }
  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
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
  /**
   * Returns true iff there is trigger associated with given event.
   */
  protected fun hasTrigger(event: Int, index: Int = 0): Boolean {
    return VKT_Triggers!![index][event] != 0
  }

  fun setMenu() {
    if (!built) {
      // only when commands are displayed
      return
    }
    val column = selectedColumn
    val (x, y) = selectedCell
    val foldEnabled = column != -1 && !model.isColumnFold(column) ||
            x !== -1 && y !== -1 && !model.isRowFold(y, x)
    val unfoldEnabled = column != -1 || x !== -1 && y !== -1
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
      setCommandEnabled(cmdEditColumn!!, column != -1 && model.getAccessibleColumn(column).isAddedAtRuntime())
    }
  }
  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  /**
   * Returns the selected column or -1 if no column is selected.
   */
  private val selectedColumn: Int
    private get() = (getDisplay() as UReport).selectedColumn

  /**
   * Returns the selected cell or !!! ??? if no cell is selected.
   */
  private val selectedCell: Point
    private get() = (getDisplay() as UReport).selectedCell

  fun genHelp(): String? {
    val fileName: String
    val surl = StringBuffer()
    fileName = VHelpGenerator().helpOnReport(pageTitle,
            commands,
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

  /**
   * Set the source for this document
   */
  protected lateinit var source: String
    set
  protected var model: MReport
  private var built = false
  private var pageTitle = ""
  private var firstPageHeader = ""

  // ----------------------------------------------------------------------
  // HELP
  // ----------------------------------------------------------------------
  var help: String? = null
  protected var VKT_Triggers: Array<IntArray>? = null
  protected var commands: Array<VCommand>? = null
  private val activeCommands: Vector<VCommand>
  /**
   * sets the print options
   */
  /**
   * sets the print options
   */
  // print configuration object
  var printOptions: PConfig
  /**
   * Get the media for this document
   */
  /**
   * Set the media for this document
   */
  var media: String? = null
  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------
  /**
   * Constructor
   */
  /**
   * Constructor
   */
  init {
    model = MReport()
    printOptions = PConfig()
    activeCommands = Vector<VCommand>()
    if (ctxt != null) {
      setDBContext(ctxt.dBContext)
    }
    init()

    // localize the report using the default locale
    localize(ApplicationContext.getDefaultLocale())
  }
}
