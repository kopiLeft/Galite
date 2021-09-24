/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.form.dsl

import java.awt.Point

import org.jetbrains.exposed.sql.Table
import org.kopi.galite.chart.Chart
import org.kopi.galite.common.Action
import org.kopi.galite.common.Actor
import org.kopi.galite.common.Command
import org.kopi.galite.common.FormTrigger
import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.Trigger
import org.kopi.galite.common.Window
import org.kopi.galite.domain.CodeDomain
import org.kopi.galite.domain.Domain
import org.kopi.galite.domain.ListDomain
import org.kopi.galite.form.Commands
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VForm
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.WindowController

/**
 * A block is a set of data which are stocked in the database and shown on a [Form].
 * A block is created in order to either view the content of a database, to insert
 * new data in the database or to update existing data in the database.
 *
 * @param        buffer                the buffer size of this block
 * @param        visible               the number of visible elements
 * @param        ident                 the simple identifier of this block
 * @param        shortcut              the shortcut of this block
 * @param        title                 the title of the block
 * @param        border                the border of the block
 * @param        align                 the type of alignment in form
 * @param        help                  the help
 * @param        blockOptions          the block options
 * @param        tables                the tables accessed on the database
 * @param        indices               the indices for database
 * @param        access                the access mode
 * @param        commands              the commands associated with this block
 * @param        triggers              the triggers executed by this form
 * @param        fields                the objects that populate the block
 */
open class FormBlock(var buffer: Int,
                     var visible: Int,
                     val title: String,
                     ident: String? = null)
  : FormElement(ident), VConstants {
  var border: Int = 0
  var align: FormBlockAlign? = null
  val help: String? = null
  private var blockOptions: Int = 0
  private var blockTables: MutableList<FormBlockTable> = mutableListOf()
  private var indices: MutableList<FormBlockIndex> = mutableListOf()
  internal var access: IntArray = IntArray(3) { VConstants.ACS_MUSTFILL }
  private lateinit var commands: Array<Command?>
  private val triggers = mutableListOf<Trigger>()
  var dropListMap = HashMap<String, String>()
  var dropList : MutableList<String>? = null
  private var maxRowPos = 0
  private var maxColumnPos = 0
  private var displayedFields = 0
  lateinit var form: Form

  /** Block's fields. */
  val blockFields = mutableListOf<FormField<*>>()

  /** Block's commands. */
  private val blockCommands = mutableListOf<Command>()

  /** Domains of fields added to this block. */
  val ownDomains = mutableListOf<Domain<*>>()

  // ----------------------------------------------------------------------
  // BLOCK TRIGGERS
  // ----------------------------------------------------------------------

  /**
   * Adds triggers to this form block. The block triggers are the same as form triggers on the block level.
   * There are actually a set of block triggers you can use to execute actions once they are fired.
   * objects extending [BlockTriggerEvent] are the supported triggers.
   *
   * @param blockTriggers the triggers to add
   * @param method        the method to execute when trigger is called
   */
  fun <T> trigger(vararg blockTriggers: BlockTriggerEvent<T>, method: () -> T): Trigger =
          initTrigger(blockTriggers, method)

  /**
   * Adds protected triggers to this block.
   *
   * @param blockTriggers the triggers to add
   * @param method        the method to execute when trigger is called
   */
  fun trigger(vararg blockTriggers: BlockProtectedTriggerEvent, method: () -> Unit): Trigger =
          initTrigger(blockTriggers, method)

  fun <T> initTrigger(blockTriggers: Array<out BlockTriggerEvent<*>>, method: () -> T) : Trigger {
    val event = blockEventList(blockTriggers)
    val blockAction = Action(null, method)
    val trigger = FormTrigger(event, blockAction)
    triggers.add(trigger)
    return trigger
  }

  private fun blockEventList(blockTriggers: Array<out BlockTriggerEvent<*>>): Long {
    var self = 0L

    blockTriggers.forEach { trigger ->
      self = self or (1L shl trigger.event)
    }

    return self
  }

  /**
   * Adds the [table] to this block. It refers to certain tables in the database whereby the first table
   * is the one on which the user will work. The remaining tables are the so-called "look-up tables",
   * i.e tables that are associated with the first one.
   *
   * @param table     the database table
   */
  fun <T : Table> table(table: T): T {
    val formBlockTable = FormBlockTable(table.tableName, table.tableName, table)
    blockTables.add(formBlockTable)
    return table
  }

  /**
   * Creates and returns a MUSTFILL field.
   *
   * MUSTFILL fields are accessible fields that the user must fill with a value.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method to initialize the field.
   * @return a MUSTFILL field.
   */
  inline fun <reified T> mustFill(domain: Domain<T>,
                                  position: FormPosition,
                                  init: MustFillFormField<T>.() -> Unit): FormField<T> {
    initDomain(domain)
    val field = MustFillFormField(this, domain, blockFields.size, VConstants.ACS_MUSTFILL, position)
    field.init()
    field.initialize(this)
    blockFields.add(field)
    return field
  }

  /**
   * Creates and returns a VISIT field.
   *
   * VISIT fields are accessible, can be modified but not necessary.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method to initialize the field.
   * @return a VISIT field.
   */
  inline fun <reified T> visit(domain: Domain<T>,
                               position: FormPosition,
                               init: NullableFormField<T>.() -> Unit): FormField<T?> {
    return initField(domain, init, VConstants.ACS_VISIT, position)
  }

  /**
   * Creates and returns a SKIPPED field.
   *
   * SKIPPED fields are read only fields, you can read the value but you can't modify it.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method to initialize the field.
   * @return a SKIPPED field.
   */
  inline fun <reified T> skipped(domain: Domain<T>,
                                 position: FormPosition,
                                 init: NullableFormField<T>.() -> Unit): FormField<T?> {
    return initField(domain, init, VConstants.ACS_SKIPPED, position)
  }

  /**
   * Creates and returns a HIDDEN field.
   *
   * HIDDEN field are invisible in the form, they are used to store hidden operations and database joins.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method to initialize the field.
   * @return a HIDDEN field.
   */
  inline fun <reified T> hidden(domain: Domain<T>, init: NullableFormField<T>.() -> Unit): FormField<T?> {
    return initField(domain, init, VConstants.ACS_HIDDEN)
  }

  /**
   * Initializes a field.
   */
  inline fun <reified T> initField(domain: Domain<T>,
                                   init: NullableFormField<T>.() -> Unit,
                                   access: Int,
                                   position: FormPosition? = null): FormField<T?> {
    initDomain(domain)
    val field = NullableFormField(this, domain, blockFields.size, access, position)
    field.init()
    field.initialize(this)
    if (dropList == null) {
      blockFields.add(field)
    } else {
      if (domain.kClass != String::class
              && TODO("add Image type")) {
        error("The field is droppable but its type is not supported as a drop target.")
      } else {
        val flavor = addDropList(dropList!!, field)
        if (flavor == null) {
          blockFields.add(field)
        } else {
          error("The extension is already defined as a drop target for this field. ")
        }
      }
    }
    return field as FormField<T?>
  }

  inline fun <reified T> initDomain(domain: Domain<T>) {
    domain.kClass = T::class
    if (domain is CodeDomain) {
      ownDomains.add(domain)
    } else if (domain is ListDomain) {
      ownDomains.add(domain)
    }
  }

  /**
   * Defines the position of the field in the current block.
   *
   * @param line                the line
   * @param column                the column
   */
  fun at(line: Int, column: Int): FormPosition = FormCoordinatePosition(line, 0, column, 0)

  /**
   * Defines the position of the field in the current block.
   *
   * @param lineRange                the line range (lineRange.first: the line,
   *                            lineRange.last: the last line into this field may be placed)
   * @param column                the column
   */
  fun at(lineRange: IntRange, column: Int): FormPosition =
          FormCoordinatePosition(lineRange.first, lineRange.last, column, 0)

  /**
   * Defines the position of the field in the current block.
   *
   * @param line                the line
   * @param columnRange                the line range (columnRange.first: the column,
   *                            columnRange.last: the last column into this field may be placed)
   */
  fun at(line: Int, columnRange: IntRange): FormPosition =
          FormCoordinatePosition(line, 0, columnRange.first, columnRange.last)

  /**
   * Defines the position of the field in the current block.
   *
   * @param lineRange                the line range (lineRange.first: the line,
   *                            lineRange.last: the last line into this field may be placed)
   * @param columnRange                the line range (columnRange.first: the column,
   *                            columnRange.last: the last column into this field may be placed)
   */
  fun at(lineRange: IntRange, columnRange: IntRange): FormPosition =
          FormCoordinatePosition(lineRange.first, lineRange.last, columnRange.first, columnRange.last)

  /**
   * Defines the position of the field in the current block.
   *
   * @param lineRange                the line range (lineRange.first: the line,
   *                            lineRange.last: the last line into this field may be placed)
   */
  fun at(lineRange: IntRange): FormPosition = FormMultiFieldPosition(lineRange.first, lineRange.last)

  /**
   * Defines the position of the field in the current block.
   *
   * @param line                the line
   */
  fun at(line: Int): FormPosition = FormMultiFieldPosition(line, 0)

  /**
   * Defines the position of the field in the current block.
   *
   * Puts the field in the position that follows a specific [field].
   *
   * @param field                the field
   */
  fun follow(field: FormField<*>): FormPosition = FormDescriptionPosition(field)

  /**
   * creates and returns a form block index. It is used to define a value in the database
   * which is to remain unique so that it can not appear anymore in another field of the same column.
   *
   * @param message                the error message in the default locale
   */
  fun index(message: String): FormBlockIndex {
    val formBlockIndex = FormBlockIndex("Id\$${indices.size}", message, indices.size)
    indices.add(formBlockIndex)
    return formBlockIndex
  }

  /**
   * Adds a new command to this block.
   *
   * PS: Block commands are commands accessible only from the block where they are called.
   *
   * @param item    the actor linked to the command.
   * @param init    initialization method.
   */
  fun command(item: Actor, init: Command.() -> Unit): Command {
    val command = Command(item)
    command.init()
    blockCommands.add(command)
    return command
  }

  /**
   * Call and add a command to this block.
   *
   * PS: Block commands are commands accessible only from the block where they are called.
   *
   * @param window    the form linked to the command.
   * @param command   the command.
   */
  fun command(window: Window, command: Command)  {
    window.actors.add(command.item)
    command(item = command.item) {
      action = command.action
    }
  }

  /**
   * Adds the block options. you can use one or more option from the options available for block.
   *
   * Use [BlockOption] to see the list of these block options.
   *
   * @param options the block options
   */
  fun options(vararg options: BlockOption) {
    options.forEach { blockOption ->
      blockOptions = blockOptions or blockOption.value
    }
  }

  /**
   * This method changes the blocks' visibility.
   *
   * Use [Access] to see the list of the access.
   * Use [Modes] to see the list of the modes.
   *
   * @param access the access to set in the block
   * @param modes the list of modes where the access will be changed
   */
  fun blockVisibility(access: Access, vararg modes: Modes) {
    if (modes.contains(Modes.QUERY)) {
      this.access[VConstants.MOD_QUERY] = access.value
    }
    if (modes.contains(Modes.INSERT)) {
      this.access[VConstants.MOD_INSERT] = access.value
    }
    if (modes.contains(Modes.UPDATE)) {
      this.access[VConstants.MOD_UPDATE] = access.value
    }
  }

  /**
   * Alignment statements are useful to align a block(source block) referring to another one(target block)
   *
   * @param targetBlock the referred block name
   * @param positions   sets of two form field
   *    the one in the left is the source block form field
   *    the other one is for the target block form field
   */
  fun align(targetBlock: FormBlock, vararg positions: Pair<FormField<*>, FormField<*>>) {
    val targets = ArrayList<Int>()

    blockFields.forEach { field ->
      if(positions.toMap().contains(field)) {
        val targetField = positions.toMap()[field]

        targets.add(targetBlock.blockFields.indexOf(targetField))
      }
    }

    align = FormBlockAlign(targetBlock,
                           targets)
  }

  /**
   * Make a tuning pass in order to create informations about exported
   * elements such as block fields positions
   *
   * @param window        the actual context of analyse
   */
  override fun initialize(window: Window) {
    this.form = window as Form
    val bottomRight = Point(0, 0)

    blockFields.forEach { field ->
      if (field.position != null) {
        field.position!!.createRBPoint(bottomRight, field)
      }
      // ACCESS
      for (i in 0..2) {
        field.access[i] = field.access[i].coerceAtMost(access[i])
      }
    }

    maxRowPos = bottomRight.y
    maxColumnPos = bottomRight.x
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  fun positionField(field: FormField<*>): FormPosition {
    return FormCoordinatePosition(++displayedFields)
  }

  fun positionField(pos: FormPosition?) {
    pos!!.setChartPosition(++displayedFields)
  }

  fun hasOption(option: Int): Boolean = blockOptions and option == option

  /**
   * Returns true if the size of the buffer == 1, false otherwise
   */
  fun isSingle(): Boolean = buffer == 1

  /**
   * Returns the form block table
   */
  fun getTable(table: Table): FormBlockTable {
    return blockTables.find { it.table == table }
            ?: throw Exception("The table ${table.tableName} is not defined in this block")
  }

  /**
   * Returns the table number
   *
   * TODO : Do we really need this?
   */
  fun getTableNum(table: FormBlockTable): Int {
    val indexOfTable = blockTables.indexOf(table)
    return if (indexOfTable >= -1) indexOfTable else throw InconsistencyException()
  }

  /**
   * Saves current block (insert or update)
   */
  fun saveBlock() {
    vBlock.validate()
    Commands.saveBlock(vBlock)
  }

  /**
   * Resets current block
   */
  fun resetBlock() {
    Commands.resetBlock(vBlock)
  }

  /**
   * Queries block, fetches first record.
   * @exception        VException        an exception may occur during DB access
   */
  fun serialQuery() {
    Commands.serialQuery(vBlock)
  }

  /**
   * Sets the block into insert mode.
   * @exception        VException        an exception may occur during DB access
   */
  fun insertMode() {
    Commands.insertMode(vBlock)
  }

  /**
   * Inserts an empty line in multi-block.
   * @exception        VException        an exception may occur during DB access
   */
  fun insertLine() {
    Commands.insertLine(vBlock)
  }

  /**
   * Navigate between accessible blocks
   * @exception        VException        an exception may occur during DB access
   */
  fun changeBlock() {
    Commands.changeBlock(vBlock)
  }

  /**
   * Sets the search operator for the current field
   * @exception        VException        an exception may occur during DB access
   */
  fun searchOperator() {
    Commands.setSearchOperator(vBlock)
  }

  fun showHideFilter() {
    Commands.showHideFilter(vBlock)
  }

  /**
   * * Loads block from database
   */
  fun load() {
    vBlock.load()
  }

  /**
   * * Loads block from database
   */
  fun deleteBlock() {
    Commands.deleteBlock(vBlock)
  }

  /**
   * Menu query block, fetches selected record.
   */
  fun DictionaryForm.recursiveQuery() {
    Commands.recursiveQuery(vBlock)
  }

  /**
   * Menu query block, fetches selected record, then moves to next block
   */
  fun queryMove() {
      Commands.queryMove(vBlock)
  }

  fun addDropList(dropList: MutableList<String>, field: FormField<*>): String? {
    for (i in dropList.indices) {
      val extension = dropList[i].toLowerCase()
      if (dropListMap[extension] != null) {
        return extension
      }
      dropListMap[extension] = field.getIdent()
    }
    return null
  }

  // ----------------------------------------------------------------------
  // BLOCK TRIGGERS EVENTS
  // ----------------------------------------------------------------------
  /**
   * Block Triggers
   *
   * @param event the event of the trigger
   */
  open class BlockTriggerEvent<T>(val event: Int)

  /**
   * Block protected Triggers
   *
   * @param event the event of the trigger
   */
  class BlockProtectedTriggerEvent(event: Int) : BlockTriggerEvent<Unit>(event)

  /**
   * executed before querying the database
   */
  val PREQRY = BlockProtectedTriggerEvent(VConstants.TRG_PREQRY)    // protected trigger

  /**
   * executed after querying the database
   */
  val POSTQRY =  BlockProtectedTriggerEvent(VConstants.TRG_POSTQRY)  // protected trigger

  /**
   * executed before a row is deleted
   */
  val PREDEL = BlockProtectedTriggerEvent(VConstants.TRG_PREDEL)    // protected trigger

  /**
   * executed after a row is deleted
   */
  val POSTDEL = BlockProtectedTriggerEvent(VConstants.TRG_POSTDEL)  // protected trigger

  /**
   * executed before a row is inserted
   */
  val PREINS = BlockProtectedTriggerEvent(VConstants.TRG_PREINS)    // protected trigger

  /**
   * executed after a row is inserted
   */
  val POSTINS = BlockProtectedTriggerEvent(VConstants.TRG_POSTINS)  // protected trigger

  /**
   * executed before a row is updated
   */
  val PREUPD = BlockProtectedTriggerEvent(VConstants.TRG_PREUPD)    // protected trigger

  /**
   * executed after a row is updated
   */
  val POSTUPD = BlockProtectedTriggerEvent(VConstants.TRG_POSTUPD)  // protected trigger

  /**
   * executed before saving a row
   */
  val PRESAVE = BlockProtectedTriggerEvent(VConstants.TRG_PRESAVE)  // protected trigger

  /**
   * executed upon record entry
   */
  val PREREC = BlockTriggerEvent<Unit>(VConstants.TRG_PREREC)    // void trigger

  /**
   * executed upon record exit
   */
  val POSTREC = BlockTriggerEvent<Unit>(VConstants.TRG_POSTREC)  // void trigger

  /**
   * executed upon block entry
   */
  val PREBLK = BlockTriggerEvent<Unit>(VConstants.TRG_PREBLK)    // void trigger

  /**
   * executed upon block exit
   */
  val POSTBLK = BlockTriggerEvent<Unit>(VConstants.TRG_POSTBLK)  // void trigger

  /**
   * executed upon block validation
   */
  val VALBLK = BlockTriggerEvent<Unit>(VConstants.TRG_VALBLK)    // void trigger

  /**
   * executed upon record validation
   */
  val VALREC = BlockTriggerEvent<Unit>(VConstants.TRG_VALREC)    // void trigger

  /**
   * is executed when the block is in the InsertMode. This trigger becomes active when
   * the user presses the key F4. It will then enable the system to load standard values
   * which will be proposed to the user if he wishes to enter new data.
   */
  val DEFAULT = BlockTriggerEvent<Unit>(VConstants.TRG_DEFAULT)  // void trigger

  /**
   * executed upon block initialization
   */
  val INIT = BlockTriggerEvent<Unit>(VConstants.TRG_INIT)        // void trigger

  /**
   * executed upon Reset command (ResetForm)
   */
  val RESET = BlockTriggerEvent<Boolean>(VConstants.TRG_RESET)      // Boolean trigger

  /**
   * a special trigger that returns a boolean value of whether the block have been changed or not,
   * you can use it to bypass the system control for changes by returning false in the trigger's method:
   *
   * trigger(CHANGED) {
   *   false
   * }
   *
   */
  val CHANGED = BlockTriggerEvent<Boolean>(VConstants.TRG_CHANGED)  // Boolean trigger

  /**
   * defines whether a block can or not be accessed, it must always return a boolean value.
   *
   * trigger(ACCESS) {
   *   Block.mode == MOD_QUERY  // Tests if the block is in query mode,
   *                               //this block is only accessible on query mode
   * }
   *
   */
  val ACCESS = BlockTriggerEvent<Boolean>(VConstants.TRG_ACCESS)    // Void trigger

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  override fun genLocalization(writer: LocalizationWriter) {
    (writer as FormLocalizationWriter).genBlock(ident, title, help, indices, blockFields)
  }

  fun showChart(chart: Chart) {
    WindowController.windowController.doNotModal(chart)
  }

  /** The block model */
  lateinit var vBlock: VBlock

  /** Returns block model */
  fun getBlockModel(vForm: VForm, source: String? = null): VBlock {

    fun getFieldsCommands(): List<Command> {
      return blockFields.mapNotNull {
        it.commands
      }.flatten()
    }

    return object : VBlock(vForm) {
      /**
       * Handling triggers
       */
      fun handleTriggers(triggers: MutableList<Trigger>) {
        // BLOCK TRIGGERS
        val blockTriggerArray = IntArray(VConstants.TRG_TYPES.size)
        triggers.forEach { trigger ->
          for (i in VConstants.TRG_TYPES.indices) {
            if (trigger.events shr i and 1 > 0) {
              blockTriggerArray[i] = i
              super.triggers[i] = trigger
            }
          }
          super.VKT_Triggers[0] = blockTriggerArray
        }

        // FIELD TRIGGERS
        blockFields.forEach { field ->
          val fieldTriggerArray = IntArray(VConstants.TRG_TYPES.size)

          field.triggers.forEach { trigger ->
            for (i in VConstants.TRG_TYPES.indices) {
              if (trigger.events shr i and 1 > 0) {
                fieldTriggerArray[i] = i
                super.triggers[i] = trigger
              }
            }
          }
          super.VKT_Triggers.add(fieldTriggerArray)
        }

        // COMMANDS TRIGGERS
        blockCommands.forEach {
          val fieldTriggerArray = IntArray(VConstants.TRG_TYPES.size)
          // TODO : Add commands triggers here
          super.VKT_Triggers.add(fieldTriggerArray)
        }

        // FIELDS COMMANDS TRIGGERS
        val fieldsCommands = getFieldsCommands()
        fieldsCommands.forEach {
          val fieldTriggerArray = IntArray(VConstants.TRG_TYPES.size)
          // TODO : Add field commands triggers here
          super.VKT_Triggers.add(fieldTriggerArray)
        }
      }

      override fun setInfo(form: VForm) {
        blockFields.forEach {
          it.setInfo(super.source, form)
        }
      }

      init {
        //TODO ----------begin-------------

        handleTriggers(this@FormBlock.triggers)

        //TODO ------------end-----------

        super.source = source ?: sourceFile
        super.title = this@FormBlock.title
        super.help = this@FormBlock.help
        super.bufferSize = buffer
        super.displaySize = visible
        super.pageNumber = this@FormBlock.pageNumber
        super.border = this@FormBlock.border
        super.maxRowPos = this@FormBlock.maxRowPos
        super.maxColumnPos = this@FormBlock.maxColumnPos
        super.displayedFields = this@FormBlock.displayedFields
        super.commands = blockCommands.map { command ->
          command.buildModel(this, form.actors)
        }.toTypedArray()
        super.name = ident
        super.options = blockOptions
        super.access = this@FormBlock.access
        super.tables = blockTables.map {
          it.table
        }.toTypedArray()
        fields = blockFields.map { formField ->
          formField.vField
        }.toTypedArray()
        super.indices = this@FormBlock.indices.map {
          it.message
        }.toTypedArray()
        super.indicesIdents = this@FormBlock.indices.map {
          it.ident
        }.toTypedArray()
        alignment = align?.getBlockAlignModel()
      }
    }.also {
      vBlock = it
    }
  }
}
