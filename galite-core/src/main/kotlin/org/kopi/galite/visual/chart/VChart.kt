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

package org.kopi.galite.visual.chart

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.text.MessageFormat
import java.util.Locale

import kotlin.collections.ArrayList

import org.kopi.galite.visual.base.Utils
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.l10n.ChartLocalizer
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.print.Printable
import org.kopi.galite.visual.util.PPaperType
import org.kopi.galite.visual.util.PrintJob
import org.kopi.galite.visual.util.base.InconsistencyException
import org.kopi.galite.visual.ApplicationConfiguration
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.Constants
import org.kopi.galite.visual.FileHandler
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.UIFactory
import org.kopi.galite.visual.UWindow
import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VExecFailedException
import org.kopi.galite.visual.VHelpViewer
import org.kopi.galite.visual.VWindow
import org.kopi.galite.visual.VlibProperties
import org.kopi.galite.visual.WindowBuilder
import org.kopi.galite.visual.WindowController

import com.lowagie.text.Rectangle

/**
 * Creates a new chart model.
 * The `VChart` is a window containing a chart inside.
 * The chart can have any type. The standard implementation will
 * provide **five** chart types :
 *
 *  * Bar chart;
 *  * Area chart;
 *  * Line chart;
 *  * Column chart;
 *  * Pie chart;
 *
 * Other chart implementations can be provided by extending this class.
 *
 * @throws VException Visual errors.
 */
abstract class VChart : VWindow(), CConstants, Printable {

  companion object {
    const val TYP_PDF = 1
    const val TYP_PNG = 2
    const val TYP_JPEG = 3

    // --------------------------------------------------------------------
    // STATIC INITIALIZATION
    // --------------------------------------------------------------------
    init {
      WindowController.windowController.registerWindowBuilder(Constants.MDL_CHART, object : WindowBuilder {

        override fun createWindow(model: VWindow): UWindow = UIFactory.uiFactory.createView(model) as UChart
      })
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  private var cmdBarView: VCommand? = null
  private var cmdColumnView: VCommand? = null
  private var cmdLineView: VCommand? = null
  private var cmdAreaView: VCommand? = null
  private var cmdPieView: VCommand? = null
  private var built = false
  private var pageTitle = ""
  var help: String? = null
  var chartType: VChartType? = null  // chart type
    private set

  val VKT_Chart_Triggers = listOf(arrayOfNulls<Trigger>(CConstants.TRG_TYPES.size))
  val VKT_Dimension_Triggers = mutableListOf<Array<Trigger?>>()
  val VKT_Measure_Triggers = mutableListOf<Array<Trigger?>>()
  val VKT_Commands_Triggers = mutableListOf<Array<Trigger?>>()
  private val activeCommands: ArrayList<VCommand> = ArrayList()
  var printOptions: VPrintOptions = VPrintOptions()

  /**
   * The chart dimensions. The actual version supports only one dimension
   */
  internal var dimensions = listOf<VDimension>() // TODO: should be protected

  /**
   * The chart measures.
   */
  internal val measures = mutableListOf<VMeasure>() // TODO: should be protected
  private val rows: ArrayList<VRow> = ArrayList(500)

  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  /**
   * Localizes this report
   *
   * @param     locale  the locale to use
   */
  fun localize(locale: Locale?) {
    var manager: LocalizationManager?

    manager = LocalizationManager(locale, ApplicationContext.getDefaultLocale())
    // localizes the actors in VWindow
    localize(manager)
    manager = null
  }

  /**
   * Localizes this report
   *
   * @param     manager         the manger to use for localization
   */
  protected fun localize(manager: LocalizationManager) {
    if(ApplicationContext.getDefaultLocale() != locale) {
      val loc: ChartLocalizer = manager.getChartLocalizer(source)

      setPageTitle(loc.getTitle())
      help = loc.getHelp()
      // dimensions
      dimensions.forEach {
        it.localize(loc)
      }
      // measures
      measures.forEach {
        it.localize(loc)
      }
    }
  }

  // --------------------------------------------------------------------
  // IMPLEMENTATIONS
  // --------------------------------------------------------------------

  override fun createPrintJob(): PrintJob {
    return try {
      val printJob: PrintJob
      val file = Utils.getTempFile("galite", "pdf")
      val paper = PPaperType.getPaperTypeFromCode(printOptions.paperType)

      val page = if (printOptions.paperLayout == "Landscape") {
        Rectangle(paper.height.toFloat(), paper.width.toFloat())
      } else {
        Rectangle(paper.width.toFloat(), paper.height.toFloat())
      }
      export(file, TYP_PDF)
      printJob = PrintJob(file, true, page)
      printJob.dataType = PrintJob.DAT_PDF
      printJob.title = getTitle()
      printJob.numberOfPages = 1
      printJob.documentType = getDocumentType()
      printJob
    } catch (e: IOException) {
      throw VExecFailedException(e)
    }
  }

  /**
   * Returns the document type.
   * @return The document type.
   */
  open fun getDocumentType(): Int = Printable.DOC_UNKNOWN

  override fun getType(): Int = Constants.MDL_CHART

  /**
   * Sets the chart menu. This will enable and disable
   * commands according to the chart generation context.
   */
  fun setMenu() {
    if (!built) {
      // only when commands are displayed
      return
    }
    if (cmdBarView != null) {
      setCommandEnabled(cmdBarView!!, chartType !== VChartType.BAR)
    }
    if (cmdColumnView != null) {
      setCommandEnabled(cmdColumnView!!, chartType !== VChartType.COLUMN)
    }
    if (cmdLineView != null) {
      setCommandEnabled(cmdLineView!!, chartType !== VChartType.LINE)
    }
    if (cmdAreaView != null) {
      setCommandEnabled(cmdAreaView!!, chartType !== VChartType.AREA)
    }
    if (cmdPieView != null) {
      setCommandEnabled(cmdPieView!!, chartType !== VChartType.PIE)
    }
  }

  /**
   * Initialization of the chart model.
   * @throws VException Visual errors.
   */
  fun initChart() {
    build()
    callTrigger(CConstants.TRG_PRECHART)
  }

  /**
   * build everything after loading
   */
  protected fun build() {
    if (rows.isEmpty()) {
      throw VNoChartRowException(MessageCode.getMessage("VIS-00015"))
    }
    (getDisplay() as UChart).build()
    setType(VChartType.DEFAULT, false)
    built = true
    if (hasTrigger(CConstants.TRG_INIT)) {
      callTrigger(CConstants.TRG_INIT)
    }
    if (hasFixedType()) {
      setType(callTrigger(CConstants.TRG_CHARTTYPE) as VChartType)
    }
    // all commands are by default enabled
    activeCommands.clear()
    commands?.forEachIndexed { i, it ->
      when (it.getIdent()) {
        "BarView" -> {
          cmdBarView = it
        }
        "ColumnView" -> {
          cmdColumnView = it
        }
        "LineView" -> {
          cmdLineView = it
        }
        "AreaView" -> {
          cmdAreaView = it
        }
        "PieView" -> {
          cmdPieView = it
        }
        else -> {
          setCommandEnabled(it, i, true)
        }
      }
    }
  }

  /**
   * Returns `true` is the chart has a fixed type.
   * @return `true` is the chart has a fixed type.
   */
  fun hasFixedType(): Boolean = hasTrigger(CConstants.TRG_CHARTTYPE)

  /**
   * Returns the chart fixed type.
   * @return The chart fixed type.
   */
  open fun getFixedType(): VChartType? = callTrigger(CConstants.TRG_CHARTTYPE) as VChartType?

  /**
   * Refreshes the chart display.
   */
  fun refresh() {
    (getDisplay() as UChart).refresh()
  }

  /**
   * Closes the chart window.
   */
  fun close() {
    getDisplay()!!.closeWindow()
  }

  /**
   * Shows the chart help window.
   */
  fun showHelp() {
    VHelpViewer().showHelp(genHelp())
  }

  override fun destroyModel() {
    try {
      callTrigger(CConstants.TRG_POSTCHART)
    } catch (v: VException) {
      // ignore
      v.printStackTrace()
    }
    super.destroyModel()
  }

  /**
   * Sets the new type of this chart model.
   * @param type The new chart type.
   */
  fun setType(type: VChartType) {
    setType(type, true)
  }

  /**
   * Prints the report
   */
  fun export(type: Int = TYP_PNG) {
    val ext = when (type) {
      TYP_PNG -> ".png"
      TYP_PDF -> ".pdf"
      TYP_JPEG -> ".jpeg"
      else -> throw InconsistencyException("Export type unknown")
    }
    val file = FileHandler.fileHandler!!.chooseFile(getDisplay()!!,
                                                    ApplicationConfiguration.getConfiguration()!!.getDefaultDirectory(),
                                                    "chart$ext")
    if (file != null) {
      try {
        export(file, type)
      } catch (e: IOException) {
        throw VExecFailedException(e)
      }
    }
  }

  /**
   * Exports the chart to the given format.
   * @param file The destination file.
   * @param type The export type.
   * @throws IOException I/O errors.
   */
  fun export(file: File, type: Int) {
    val destination = FileOutputStream(file)
    var exported = false

    setWaitInfo(VlibProperties.getString("export-message"))
    try {
      exported = when (type) {
        TYP_PDF -> {
          (getDisplay() as UChart).getType()!!.exportToPDF(destination, printOptions)
          true
        }
        TYP_PNG -> {
          (getDisplay() as UChart).getType()!!.exportToPNG(destination, printOptions.imageWidth, printOptions.imageHeight)
          true
        }
        TYP_JPEG -> {
          (getDisplay() as UChart).getType()!!.exportToJPEG(destination, printOptions.imageWidth, printOptions.imageHeight)
          true
        }
        else -> throw InconsistencyException("Export type unknown")
      }
    } finally {
      destination.close()
      unsetWaitInfo()
      if (exported) {
        fireFileProduced(file)
      }
    }
  }

  /**
   * Sets the new type of this chart model.
   * @param type The new chart type.
   * @param refresh should we refresh the view side ?
   */
  internal fun setType(type: VChartType, refresh: Boolean) {
    if (hasFixedType() && type != getFixedType()) {
      return
    }
    chartType = type
    type.createDataSeries(this)
    (getDisplay() as UChart).setType(ChartTypeFactory.getChartTypeFactory().createTypeView(getTitle(), type))
    if (refresh) {
      refresh()
      (getDisplay() as UChart).typeChanged()
    }
  }

  /**
   * Appends a row to the chart rows.
   * @param dimensions The dimension value.
   * @param measures The measures values.
   */
  internal fun addRow(dimensions: Array<Any?>, measures: Array<Any?>) {
    rows.add(VRow(dimensions, measures))
  }

  /**
   * Returns the column count.
   * @return The column count.
   */
  protected open fun getColumnCount(): Int = dimensions.size + measures.size

  // ----------------------------------------------------------------------
  // COMMANDS
  // ----------------------------------------------------------------------

  /**
   * Enables/disables the actor.
   */
  fun setCommandEnabled(command: VCommand, index: Int, enable: Boolean) {
    var enable = enable

    if (enable) {
      // we need to check if VKT_Commands_Triggers is not empty
      // ex : org.kopi.galite.visual.cross.VDynamicReport
      if (VKT_Commands_Triggers.isNotEmpty() && hasCommandTrigger(CConstants.TRG_CMDACCESS, index)) {

        val active = try {
          (callCommandTrigger(CConstants.TRG_CMDACCESS, index) as Boolean)
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

  fun executeBooleanTrigger(trigger: Trigger?): Boolean {
    throw InconsistencyException("SHOULD BE REDEFINED")
  }

  fun executeIntegerTrigger(trigger: Trigger?): Int {
    throw InconsistencyException("SHOULD BE REDEFINED")
  }

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
  protected fun callTrigger(event: Int): Any? {
    return callTrigger(event, 0, VKT_Chart_Triggers)
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected fun callDimensionTrigger(event: Int, index: Int): Any? {
    return callTrigger(event, index, VKT_Dimension_Triggers)
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected fun callMeasureTrigger(event: Int, index: Int): Any? {
    return callTrigger(event, index, VKT_Measure_Triggers)
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected fun callCommandTrigger(event: Int, index: Int): Any? {
    return callTrigger(event, index, VKT_Commands_Triggers)
  }

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  private fun callTrigger(event: Int, index: Int, triggers: List<Array<Trigger?>>): Any? {
    return when (CConstants.TRG_TYPES[event]) {
      CConstants.TRG_VOID -> {
        executeVoidTrigger(triggers[index][event])
        null
      }
      CConstants.TRG_OBJECT -> executeObjectTrigger(triggers[index][event])
      else -> throw InconsistencyException("BAD TYPE" + CConstants.TRG_TYPES[event])
    }
  }

  /**
   * Returns true if there is trigger associated with given event.
   */
  internal fun hasTrigger(event: Int): Boolean = VKT_Chart_Triggers[0][event] != null

  /**
   * Returns true if there is trigger associated with given event.
   */
  internal fun hasCommandTrigger(event: Int, index: Int): Boolean = VKT_Commands_Triggers[index][event] != null

  /**
   * Returns the dimension column.
   * @return The dimension column.
   */
  fun getMeasure(column: Int): VMeasure = measures[column]

  /**
   * Returns the dimension column.
   * @return The dimension column.
   */
  fun getDimension(column: Int): VDimension = dimensions[column]

  /**
   * Sets the title
   */
  fun setPageTitle(title: String) {
    pageTitle = title
    setTitle(title)
  }

  /**
   * Sets the page title parameter.
   * @param param The page title parameter.
   */
  fun setPageTitleParams(param: Any) {
    setPageTitleParams(arrayOf(param))
  }

  /**
   * Sets the page title parameters.
   * @param param1 The first parameter.
   * @param param2 The second parameter.
   */
  fun setPageTitleParams(param1: Any, param2: Any) {
    setPageTitleParams(arrayOf(param1, param2))
  }

  /**
   * Sets the page title parameters.
   * @param params The parameters to be set.
   */
  fun setPageTitleParams(params: Array<Any>) {
    setPageTitle(MessageFormat.format(pageTitle, *params))
  }

  /**
   * Returns the chart rows.
   * @return The chart rows.
   */
  internal fun getRows(): Array<VRow> = rows.toTypedArray()

  /**
   * Returns the chart columns.
   * @return the chart columns.
   */
  internal open fun getColumns(): Array<VColumn> {
    val columns: Array<VColumn?> = arrayOfNulls(getColumnCount())

    dimensions.forEachIndexed { i, it ->
      columns[i] = it
    }
    measures.forEachIndexed { i, it ->
      columns[i + dimensions.size] = it
    }

    return columns.requireNoNulls()
  }

  // ----------------------------------------------------------------------
  // HELP
  // ----------------------------------------------------------------------
  fun genHelp(): String? {
    val surl = StringBuffer()
    val fileName = VHelpGenerator().helpOnChart(getTitle(),
                                                commands,
                                                getColumns(),
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

  // --------------------------------------------------------------------
  // ABSTRACT METHODS
  // --------------------------------------------------------------------
  /**
   * The chart columns initialization. Will be implemented by subclasses
   * @throws VException Visual errors.
   */
  protected abstract fun init()
}
