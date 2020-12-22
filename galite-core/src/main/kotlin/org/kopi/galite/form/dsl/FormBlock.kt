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
package org.kopi.galite.form.dsl

import java.awt.Point

import org.jetbrains.exposed.sql.Table
import org.kopi.galite.chart.Chart
import org.kopi.galite.common.Action
import org.kopi.galite.common.Actor
import org.kopi.galite.common.BlockBooleanTriggerEvent
import org.kopi.galite.common.BlockProtectedTriggerEvent
import org.kopi.galite.common.BlockTriggerEvent
import org.kopi.galite.common.BlockVoidTriggerEvent
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
import org.kopi.galite.form.VCodeField
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VForm
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.VCommand
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
  lateinit var dropListMap: HashMap<*, *>
  private var maxRowPos = 0
  private var maxColumnPos = 0
  private var displayedFields = 0
  lateinit var form: Form

  /** Blocks's fields. */
  val blockFields = mutableListOf<FormField<*>>()

  /** Blocks's commands. */
  private val blockCommands = mutableListOf<Command>()

  /** Domains of fields added to this block. */
  val ownDomains = mutableListOf<Domain<*>>()

  // ----------------------------------------------------------------------
  // BLOCK TRIGGERS
  // ----------------------------------------------------------------------

  /**
   * Adds triggers to this form block. The block triggers are the same as form triggers on the block level.
   * There are actually a set of block triggers you can use to execute actions once they are fired.
   * see [BlockTrigger] to get the list of supported triggers.
   *
   * @param blockTriggers the triggers to add
   * @param method        the method to execute when trigger is called
   */
  private fun <T> trigger(blockTriggers: Array<out BlockTriggerEvent>, method: () -> T): Trigger {
    val event = blockEventList(blockTriggers)
    val blockAction = Action(null, method)
    val trigger = FormTrigger(event, blockAction)
    triggers.add(trigger)
    return trigger
  }

  private fun blockEventList(blockTriggers: Array<out BlockTriggerEvent>): Long {
    var self = 0L

    blockTriggers.forEach { trigger ->
      self = self or (1L shl trigger.event)
    }

    return self
  }

  /**
   * Adds protected triggers to this block.
   *
   * @param blockTriggers the triggers to add
   * @param method        the method to execute when trigger is called
   */
  fun trigger(vararg blockTriggers: BlockProtectedTriggerEvent, method: () -> Unit): Trigger {
    return trigger(blockTriggers, method)
  }

  /**
   * Adds void trigger to this block.
   *
   * @param blockTriggerEvents the triggers to add
   * @param method             the method to execute when trigger is called
   */
  fun trigger(vararg blockTriggerEvents: BlockVoidTriggerEvent, method: () -> Unit): Trigger {
    return trigger(blockTriggerEvents, method)
  }

  /**
   * Adds boolean triggers to this block.
   *
   * @param blockTriggers the triggers to add
   * @param method        the method to execute when trigger is called
   */
  fun trigger(vararg blockTriggers: BlockBooleanTriggerEvent, method: () -> Boolean): Trigger {
    return trigger(blockTriggers, method)
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
  inline fun <reified T : Comparable<T>?> mustFill(domain: Domain<T>,
                                                   position: FormPosition,
                                                   init: FormField<T>.() -> Unit): FormField<T> {
    return initField(domain, init, VConstants.ACS_MUSTFILL, position)
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
  inline fun <reified T : Comparable<T>?> visit(domain: Domain<T>,
                                                position: FormPosition,
                                                init: FormField<T>.() -> Unit): FormField<T> {
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
  inline fun <reified T : Comparable<T>?> skipped(domain: Domain<T>,
                                                  position: FormPosition,
                                                  init: FormField<T>.() -> Unit): FormField<T> {
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
  inline fun <reified T : Comparable<T>?> hidden(domain: Domain<T>, init: FormField<T>.() -> Unit): FormField<T> {
    return initField(domain, init, VConstants.ACS_HIDDEN)
  }

  /**
   * Initializes a field.
   */
  inline fun <reified T : Comparable<T>?> initField(domain: Domain<T>,
                                                    init: FormField<T>.() -> Unit,
                                                    access: Int,
                                                    position: FormPosition? = null): FormField<T> {
    domain.kClass = T::class
    if(domain.type is CodeDomain<T>) {
      ownDomains.add(domain)
    } else if(domain.type is ListDomain<T>) {
      TODO()
    }
    val field = FormField(this, domain, blockFields.size, access, position)
    field.init()
    field.initialize(this)
    field.setInfo()
    blockFields.add(field)
    return field
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
  fun <T : Comparable<T>?> follow(field: FormField<T>): FormPosition = FormDescriptionPosition(field)

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
   * Changing the visibility of the block.
   *
   * Use [Access] to see the list of the access.
   * Use [Modes] to see the list of the modes.
   *
   * @param access the access to set in the block
   * @param modes the list of modes where the access will be changed
   */
  fun changeBlockAccess(access: Access, vararg modes: Modes) {
    if (modes.contains(Modes.MOD_QUERY)) {
      this.access[VConstants.MOD_QUERY] = access.value
    }
    if (modes.contains(Modes.MOD_INSERT)) {
      this.access[VConstants.MOD_INSERT] = access.value
    }
    if (modes.contains(Modes.MOD_UPDATE)) {
      this.access[VConstants.MOD_UPDATE] = access.value
    }
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
    }

    maxRowPos = bottomRight.y
    maxColumnPos = bottomRight.x
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  fun positionField(field: FormField<*>): FormPosition? {
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
    Commands.saveBlock(vBlock)
  }

  /**
   * Menu query block, fetches selected record.
   */
  fun DictionaryForm.recursiveQuery() {
    Commands.recursiveQuery(vBlock)
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  override fun genLocalization(writer: LocalizationWriter) {
    (writer as FormLocalizationWriter).genBlock(ident,
                                                title,
                                                help,
                                                indices.toTypedArray(),
                                                blockFields.toTypedArray())
  }

  fun showChart(chart: Chart) {
    WindowController.windowController.doNotModal(chart);
  }

  /** The block model */
  lateinit var vBlock: VBlock

  /** Returns block model */
  fun getBlockModel(vForm: VForm, source: String? = null): VBlock {

    fun getFieldsCommands(): List<Command> {
      return blockFields.map {
        it.commands
      }.filterNotNull().flatten()
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
        blockFields.forEach {
          val fieldTriggerArray = IntArray(VConstants.TRG_TYPES.size)
          // TODO : Add field triggers here
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

      override fun setInfo() {
      }

      init {
        //TODO ----------begin-------------
        /** Used actors in form*/
        val usedActors = form.actors.map { vActor ->
          vActor?.actorIdent to vActor
        }.toMap()

        super.commands = blockCommands.map {
          VCommand(it.mode,
                   this,
                   usedActors[it.item.ident],
                   -1,
                   it.name!!,
                   it.action
          )
        }.toTypedArray()

        handleTriggers(this@FormBlock.triggers)

        //TODO ------------end-----------

        super.source = source ?: sourceFile
        super.bufferSize = buffer
        super.displaySize = visible
        super.pageNumber = this@FormBlock.pageNumber
        super.maxRowPos = this@FormBlock.maxRowPos
        super.maxColumnPos = this@FormBlock.maxColumnPos
        super.displayedFields = this@FormBlock.displayedFields
        super.name = ident
        super.options = blockOptions
        super.access = this@FormBlock.access
        super.tables = blockTables.map {
          it.table
        }.toTypedArray()
        fields = blockFields.map {
          it.vField.also {
            if(it is VCodeField) {
              it.source = super.source
            }
          }
        }.toTypedArray()
        super.indices = this@FormBlock.indices.map {
          it.ident
        }.toTypedArray()
      }
    }.also {
      vBlock = it
    }
  }
}
