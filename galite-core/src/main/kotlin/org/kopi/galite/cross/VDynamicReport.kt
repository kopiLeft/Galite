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
package org.kopi.galite.cross

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VBooleanCodeField
import org.kopi.galite.form.VBooleanField
import org.kopi.galite.form.VCodeField
import org.kopi.galite.form.VDateField
import org.kopi.galite.form.VField
import org.kopi.galite.form.VFixnumCodeField
import org.kopi.galite.form.VFixnumField
import org.kopi.galite.form.VImageField
import org.kopi.galite.form.VIntegerCodeField
import org.kopi.galite.form.VIntegerField
import org.kopi.galite.form.VMonthField
import org.kopi.galite.form.VStringCodeField
import org.kopi.galite.form.VStringField
import org.kopi.galite.form.VTimeField
import org.kopi.galite.form.VTimestampField
import org.kopi.galite.form.VWeekField
import org.kopi.galite.report.Constants
import org.kopi.galite.report.PConfig
import org.kopi.galite.report.VBooleanCodeColumn
import org.kopi.galite.report.VBooleanColumn
import org.kopi.galite.report.VDateColumn
import org.kopi.galite.report.VDefaultReportActor
import org.kopi.galite.report.VFixnumCodeColumn
import org.kopi.galite.report.VFixnumColumn
import org.kopi.galite.report.VIntegerCodeColumn
import org.kopi.galite.report.VIntegerColumn
import org.kopi.galite.report.VMonthColumn
import org.kopi.galite.report.VNoRowException
import org.kopi.galite.report.VReport
import org.kopi.galite.report.VReportColumn
import org.kopi.galite.report.VReportCommand
import org.kopi.galite.report.VStringCodeColumn
import org.kopi.galite.report.VStringColumn
import org.kopi.galite.report.VTimeColumn
import org.kopi.galite.report.VTimestampColumn
import org.kopi.galite.report.VWeekColumn
import org.kopi.galite.type.Decimal
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.Message
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.VActor
import org.kopi.galite.visual.VExecFailedException
import java.awt.event.KeyEvent
import java.util.*

class VDynamicReport(block: VBlock) : VReport() {
  /**
   * @param     fields  block fields.
   * @return fields that will represent columns in the dynamic report.
   */
  private fun initFields(fields: Array<VField>): Array<VField> {
    val processedFields = arrayListOf<VField>()

    fields.forEach { field ->
      // Images fields cannot be handled in dynamic reports
      if (field !is VImageField && (!field.isInternal() || field.name == block.idField.name)) {
        if (field.getColumnCount() > 0 || block.isMulti() && isFetched) {
          processedFields.add(field)
        }
      }
    }
    if (processedFields.isEmpty()) {
      throw InconsistencyException("Can't generate a report, check that this block contains " +
                                           "unhidden fields with database columns.")
    }
    return processedFields.toTypedArray()
  }

  val isFetched: Boolean
    get() {
      var i = 0
      while (i < block.bufferSize) {
        if (block.isRecordFetched(i)) {
          return true
        }
        i += 1
      }
      return false
    }

  /**
   * create report columns and fill them with data.
   */
  protected fun initColumns() {
    var col = 0

    fields.forEachIndexed { index, field ->
      when (field) {
        is VStringField ->
          columns[col] = VStringColumn(null,
                                       0,
                                       field.align,
                                       getColumnGroups(field),
                                       null,
                                       field.width,
                                       1,
                                       null)
        is VBooleanField ->
          columns[col] = VBooleanColumn(null,
                                        0,
                                        field.align,
                                        getColumnGroups(field),
                                        null,
                                        1,
                                        null)
        is VDateField ->
          columns[col] = VDateColumn(null,
                                     0,
                                     field.align,
                                     getColumnGroups(field),
                                     null,
                                     1,
                                     null)
        is VFixnumField ->
          columns[col] = VFixnumColumn(null,
                                       0,
                                       field.align,
                                       getColumnGroups(field),
                                       null,
                                       field.width,
                                       (field as VFixnumField).getScale(0),
                                       null)
        is VIntegerField ->
          // hidden field ID of the block will represent the last column in the report.
          if (field.name == block.idField.name && field.isInternal()) {
            idColumn = fields.size - 1
            columns[fields.size - 1] = VIntegerColumn(null,
                                                      0,
                                                      field.align,
                                                      getColumnGroups(field),
                                                      null,
                                                      field.width,
                                                      null)
            columns[fields.size - 1]!!.isFolded = true
            // next column will have the position col.
            col -= 1
          } else {
            if (field.name == block.idField.name) {
              idColumn = index
            }
            columns[col] = VIntegerColumn(null,
                                          0,
                                          field.align,
                                          getColumnGroups(field),
                                          null,
                                          field.width,
                                          null)
          }
        is VMonthField ->
          columns[col] = VMonthColumn(null,
                                      0,
                                      field.align,
                                      getColumnGroups(field),
                                      null,
                                      field.width,
                                      null)
        is VTimeField ->
          columns[col] = VTimeColumn(null,
                                     0,
                                     field.align,
                                     getColumnGroups(field),
                                     null,
                                     field.width,
                                     null)
        is VTimestampField ->
          columns[col] = VTimestampColumn(null,
                                          0,
                                          field.align,
                                          getColumnGroups(field),
                                          null,
                                          field.width,
                                          null)
        is VWeekField ->
          columns[col] = VWeekColumn(field.name,
                                     0,
                                     field.align,
                                     getColumnGroups(field),
                                     null,
                                     field.width,
                                     null)
        is VStringCodeField ->
          columns[col] = VStringCodeColumn(null,
                                           null,
                                           null,
                                           0,
                                           field.align,
                                           getColumnGroups(field),
                                           null,
                                           field.width,
                                           null,
                                           (field as VCodeField).labels,
                                           (field as VCodeField).getCodes() as Array<String>)
        is VIntegerCodeField ->
          columns[col] = VIntegerCodeColumn(null,
                                            null,
                                            null,
                                            0,
                                            field.align,
                                            getColumnGroups(field),
                                            null,
                                            field.width,
                                            null,
                                            (field as VCodeField).labels,
                                            getIntArray((field as VCodeField).getCodes() as Array<Int>))
        is VFixnumCodeField ->
          columns[col] = VFixnumCodeColumn(null,
                                           null,
                                           null,
                                           0,
                                           field.align,
                                           getColumnGroups(field),
                                           null,
                                           1,
                                           null,
                                           (field as VCodeField).labels,
                                           (field as VCodeField).getCodes() as Array<Decimal>)
        is VBooleanCodeField ->
          columns[col] = VBooleanCodeColumn(null,
                                            null,
                                            null,
                                            0,
                                            field.align,
                                            getColumnGroups(field),
                                            null,
                                            1,
                                            null,
                                            (field as VCodeField).labels,
                                            getBoolArray((field as VCodeField).getCodes() as Array<Boolean>))
        else -> throw InconsistencyException("Error: unknown field type.")
      }
      // add labels for columns.
      if (field.name != block.idField.name) {
        val columnLabel = if (field.label != null) {
          field.label!!.trim()
        } else {
          field.name
        }
        columns[col]!!.label = columnLabel
      }
      col++
    }
    model.columns = columns
    if (block.isMulti() && isFetched) {
      for (i in 0 until block.bufferSize) {
        if (block.isRecordFilled(i)) {
          block.currentRecord = i
          val list = ArrayList<Any?>()
          for (j in fields.indices) {
            if (fields[j].name != block.idField.name) {
              list.add(fields[j].getObject())
            }
          }
          // add ID field in the end.
          for (j in fields.indices) {
            if (fields[j].name == block.idField.name) {
              list.add(fields[j].getObject())
              break
            }
          }
          model.addLine(list.toTypedArray())
        }
      }
    } else {
      try {
        transaction {
          if (block.isMulti()) {
            block.activeRecord = 0
          }
          val searchCondition = block.getSearchConditions()
          val searchColumns = block.getReportSearchColumns()
          val searchTables = block.getSearchTables()
          if (block.isMulti()) {
            block.activeRecord = -1
            block.activeField = null
          }
          val query  = if(searchCondition == null) {
            searchTables!!.slice(searchColumns!!.toList()).selectAll()
          } else {
            searchTables!!.slice(searchColumns!!.toList()).select(searchCondition)
          }
         query.firstOrNull()?.also {
            if (it[searchColumns[0]] != 0) {
              val result: MutableList<Any> = ArrayList()

              for (i in fields.indices) {
                result.add(it[searchColumns[i]]!!)
              }
              model.addLine(result.toTypedArray())
            }
          }
          query.forEach {
            val result: MutableList<Any> = ArrayList()

            for (i in fields.indices) {
              result.add(it[searchColumns[i]]!!)
            }
            model.addLine(result.toTypedArray())
          }
        }
      } catch (e: Throwable) {
        throw VExecFailedException(e)
      }
    }
  }

  // methods overridden from VReport
  override fun localize(locale: Locale?) {
    // report columns inherit their localization from the Block.
    // actors are localized with VlibProperties.
  }

  override fun add() {}
  override fun init() {}

  override fun initReport() {
    build()
  }

  override fun destroyModel() {
    //
  }

  // ----------------------------------------------------------------------
  // Default Actors
  // ----------------------------------------------------------------------
  private fun initDefaultActors() {
    actorsDef = arrayOfNulls(11)
    createActor("File", "Quit", QUIT_ICON, KeyEvent.VK_ESCAPE, 0, Constants.CMD_QUIT)
    createActor("File", "Print", PRINT_ICON, KeyEvent.VK_F6, 0, Constants.CMD_PRINT)
    createActor("File", "ExportCSV", EXPORT_ICON, KeyEvent.VK_F8, 0, Constants.CMD_EXPORT_CSV)
    createActor("File", "ExportXLSX", EXPORT_ICON, KeyEvent.VK_F9, KeyEvent.SHIFT_MASK, Constants.CMD_EXPORT_XLSX)
    createActor("File", "ExportPDF", EXPORT_ICON, KeyEvent.VK_F9, 0, Constants.CMD_EXPORT_PDF)
    createActor("Action", "Fold", FOLD_ICON, KeyEvent.VK_F2, 0, Constants.CMD_FOLD)
    createActor("Action", "Unfold", UNFOLD_ICON, KeyEvent.VK_F3, 0, Constants.CMD_UNFOLD)
    createActor("Action", "FoldColumn", FOLD_COLUMN_ICON, KeyEvent.VK_UNDEFINED, 0, Constants.CMD_FOLD_COLUMN)
    createActor("Action", "UnfoldColumn", UNFOLD_COLUMN_ICON, KeyEvent.VK_UNDEFINED, 0, Constants.CMD_UNFOLD_COLUMN)
    createActor("Action", "Sort", SERIALQUERY_ICON, KeyEvent.VK_F4, 0, Constants.CMD_SORT)
    createActor("Help", "Help", HELP_ICON, KeyEvent.VK_F1, 0, Constants.CMD_HELP)
    // !!! wael 20070418: these actors can be added in the future.
    //    createActor("File", "Preview", null, KeyEvent.SHIFT_MASK + KeyEvent.VK_F6, 0, Constants.CMD_PREVIEW);
    //    createActor("File", "PrintOptions", "border", KeyEvent.VK_F7, KeyEvent.SHIFT_MASK, Constants.CMD_PRINT_OPTIONS);
    //    createActor("Action", "OpenLine", "edit", KeyEvent.VK_UNDEFINED, 0, CMD_OPEN_LINE);
    //    createActor("Settings", "RemoveConfiguration", null, KeyEvent.VK_UNDEFINED, 0, Constants.CMD_REMOVE_CONFIGURATION);
    //    createActor("Settings", "LoadConfiguration", "save", KeyEvent.VK_UNDEFINED, 0, Constants.CMD_LOAD_CONFIGURATION);
    //    createActor("Action", "ColumnInfo", "options", KeyEvent.VK_UNDEFINED , 0, Constants.CMD_COLUMN_INFO);
    addActors(actorsDef.requireNoNulls())
  }

  // ----------------------------------------------------------------------
  // Default Actors
  // ----------------------------------------------------------------------
  private fun createActor(menuIdent: String,
                          actorIdent: String,
                          iconIdent: String,
                          key: Int,
                          modifier: Int,
                          trigger: Int) {
    actorsDef[number] = VDefaultReportActor(menuIdent, actorIdent, iconIdent, key, modifier)
    actorsDef[number]!!.number = trigger
    number++
  }

  // ----------------------------------------------------------------------
  // Default Commands
  // ----------------------------------------------------------------------
  private fun initDefaultCommands() {
    commands = arrayOfNulls(actorsDef.size)
    for (i in 0..10) {
      commands!![i] = VReportCommand(this, actorsDef[i]!!)
    }
  }

  /**
   * return the report column group for the given table.
   */
  private fun getColumnGroups(table: Int): Int {
    val fields = block.fields
    for (i in fields.indices) {
      if (fields[i].isInternal() && fields[i].getColumnCount() > 1) {
        val col: Int = fields[i].fetchColumn(table)
        if (col != -1 && fields[i].getColumn(col)!!.name == block.idField.name) {
          if (fields[i].fetchColumn(0) != -1) {
            // group with the Id of the block.
            return idColumn
          }
        }
      }
    }
    return -1
  }

  /**
   * return the report column group for the given field.
   */
  private fun getColumnGroups(field: VField): Int {
    return if (field.getColumnCount() == 0 || field.getColumn(0)!!.getTable() == 0) {
      -1
    } else {
      getColumnGroups(field.getColumn(0)!!.getTable())
    }
  }

  // ----------------------------------------------------------------------
  //  useful Methods.
  // ----------------------------------------------------------------------
  private fun getBoolArray(codes: Array<Boolean>): BooleanArray {
    val result = BooleanArray(codes.size)

    for (i in codes.indices) {
      result[i] = codes[i]
    }
    return result
  }

  private fun getIntArray(codes: Array<Int>): IntArray {
    val result = IntArray(codes.size)

    for (i in codes.indices) {
      result[i] = codes[i]
    }
    return result
  }

  // ----------------------------------------------------------------------
  // Data Members
  // ----------------------------------------------------------------------
  private val columns: Array<VReportColumn?>
  private val fields: Array<VField>
  private val block: VBlock
  private lateinit var actorsDef: Array<VActor?>
  private var number = 0
  private var idColumn = 0

  companion object {
    /**
     * Implements interface for COMMAND CreateDynamicReport
     */
    fun createDynamicReport(block: VBlock) {
      try {
        val report: VReport
        block.form.setWaitInfo(Message.getMessage("report_generation"))
        report = VDynamicReport(block)
        report.doNotModal()
      } catch (e: VNoRowException) {
        block.form.error(MessageCode.getMessage("VIS-00057"))
      } finally {
        block.form.unsetWaitInfo()
      }
      block.setRecordChanged(0, false)
    }

    const val EXPORT_ICON = "export"
    const val FOLD_ICON = "fold"
    const val UNFOLD_ICON = "unfold"
    const val FOLD_COLUMN_ICON = "foldColumn"
    const val UNFOLD_COLUMN_ICON = "unfoldColumn"
    const val SERIALQUERY_ICON = "serialquery"
    const val HELP_ICON = "help"
    const val QUIT_ICON = "quit"
    const val PRINT_ICON = "print"
  }

  init {
    printOptions = PConfig()
    dBContext = block.dBContext
    this.block = block
    fields = initFields(block.fields)
    columns = arrayOfNulls(fields.size)
    idColumn = -1
    setPageTitle(block.title)
    initDefaultActors()
    initDefaultCommands()
    initColumns()
  }
}
