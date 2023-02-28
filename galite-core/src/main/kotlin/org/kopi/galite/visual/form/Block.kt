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
package org.kopi.galite.visual.form

import java.awt.Point

import org.jetbrains.exposed.sql.Table
import org.kopi.galite.visual.Actor
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.visual.domain.ListDomain
import org.kopi.galite.visual.dsl.common.Action
import org.kopi.galite.visual.dsl.common.FormTrigger
import org.kopi.galite.visual.dsl.common.LocalizationWriter
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.dsl.common.Window
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.Color
import org.kopi.galite.visual.Command
import org.kopi.galite.visual.Mode
import org.kopi.galite.visual.VColor
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.dsl.form.Access
import org.kopi.galite.visual.dsl.form.BlockOption
import org.kopi.galite.visual.dsl.form.DictionaryForm
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.FormBlockIndex
import org.kopi.galite.visual.dsl.form.FormCoordinatePosition
import org.kopi.galite.visual.dsl.form.FormDescriptionPosition
import org.kopi.galite.visual.dsl.form.FormField
import org.kopi.galite.visual.dsl.form.FormLocalizationWriter
import org.kopi.galite.visual.dsl.form.FormMultiFieldPosition
import org.kopi.galite.visual.dsl.form.FormPage
import org.kopi.galite.visual.dsl.form.FormPosition

/**
 * A block is a set of data which are stocked in the database and shown on a [Form].
 * A block is created in order to either view the content of a database, to insert
 * new data in the database or to update existing data in the database.
 *
 * @param        title                 the title of the block.
 * @param        buffer                the buffer size of this block.
 * @param        visible               the number of visible elements.
 */
open class Block(title: String,
                 var buffer: Int,
                 var visible: Int)
  : VBlock(title, buffer, visible), VConstants {


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

    // BLOCK TRIGGERS
    for (i in VConstants.TRG_TYPES.indices) {
      if (trigger.events shr i and 1 > 0) {
        VKT_Block_Triggers[0][i] = trigger
      }
    }

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
    tables.add(table)

    return table
  }

  /**
   * Creates and returns a MUSTFILL field.
   *
   * MUSTFILL fields are accessible fields that the user must fill with a value.
   *
   * @param domain  the domain of the field.
   * @param initializer    initialization method to initialize the field.
   * @return a MUSTFILL field.
   */
  inline fun <reified T> mustFill(domain: Domain<T>,
                                  position: FormPosition,
                                  initializer: FormField<T>.() -> Unit): FormField<T> {
    initDomain(domain)
    val field = FormField(this, domain, fields.size, VConstants.ACS_MUSTFILL, position, "FLD_${fields.size}")

    return init(field, initializer)
  }

  /**
   * Creates and returns a VISIT field.
   *
   * VISIT fields are accessible, can be modified but not necessary.
   *
   * @param domain  the domain of the field.
   * @param initializer    initialization method to initialize the field.
   * @return a VISIT field.
   */
  inline fun <reified T> visit(domain: Domain<T>,
                               position: FormPosition,
                               initializer: FormField<T>.() -> Unit): FormField<T> {
    return initField(domain, initializer, VConstants.ACS_VISIT, position)
  }

  /**
   * Creates and returns a SKIPPED field.
   *
   * SKIPPED fields are read only fields, you can read the value but you can't modify it.
   *
   * @param domain  the domain of the field.
   * @param initializer    initialization method to initialize the field.
   * @return a SKIPPED field.
   */
  inline fun <reified T> skipped(domain: Domain<T>,
                                 position: FormPosition,
                                 initializer: FormField<T>.() -> Unit): FormField<T> {
    return initField(domain, initializer, VConstants.ACS_SKIPPED, position)
  }

  /**
   * Creates and returns a HIDDEN field.
   *
   * HIDDEN field are invisible in the form, they are used to store hidden operations and database joins.
   *
   * @param domain         the domain of the field.
   * @param initializer    initialization method to initialize the field.
   * @return a HIDDEN field.
   */
  inline fun <reified T> hidden(domain: Domain<T>, initializer: FormField<T>.() -> Unit): FormField<T> {
    return initField(domain, initializer, VConstants.ACS_HIDDEN)
  }

  /**
   * Initializes a field.
   */
  inline fun <reified T> initField(domain: Domain<T>,
                                   initializer: FormField<T>.() -> Unit,
                                   access: Int,
                                   position: FormPosition? = null): FormField<T> {
    initDomain(domain)
    val field = FormField(this, domain, fields.size, access, position, "FLD_${fields.size}")

    return init(field, initializer)
  }

  /**
   * Initializes a field.
   */
  inline fun <reified T, U: FormField<T>> init(field: U,
                                               initializer: U.() -> Unit): U {
    field.initializer()
    field.initialize(this)
    field.addFieldTrigger()
    blockFields.add(field.vField)
    fields.add(field)
    return field
  }

  fun FormField<*>.addFieldTrigger() {
    // FIELD TRIGGERS
    val fieldTriggerArray = arrayOfNulls<Trigger>(VConstants.TRG_TYPES.size)

    triggers.forEach { trigger ->
      for (i in VConstants.TRG_TYPES.indices) {
        if (trigger.events shr i and 1 > 0) {
          fieldTriggerArray[i] = trigger
        }
      }
    }
    this@Block.VKT_Field_Triggers.add(fieldTriggerArray)
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
    indicesIdents.add(formBlockIndex.ident)

    return formBlockIndex
  }

  /**
   * Adds a new command to this block.
   *
   * PS: Block commands are commands accessible only from the block where they are called.
   *
   * @param item    the actor linked to the command.
   * @param modes   the modes in which the command should be executed.
   * @param action  the action function.
   */
  fun command(item: Actor, vararg modes: Mode, action: () -> Unit): Command {
    val command = Command(item, modes, this, action = action)

    commands.add(command)

    // COMMANDS TRIGGERS
    // TODO : Add commands triggers here
    VKT_Command_Triggers.add(arrayOfNulls(VConstants.TRG_TYPES.size))

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
    window.actors.add(command.item!!)
    command(item = command.item, action = command.action)
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
      this.options = this.options or blockOption.value
    }
  }

  /**
   * This method changes the blocks' visibility.
   *
   * Use [Access] to see the list of the access.
   * Use [Mode] to see the list of the modes.
   *
   * @param access the access to set in the block
   * @param modes the list of modes where the access will be changed
   */
  fun blockVisibility(access: Access, vararg modes: Mode) {
    if (modes.contains(Mode.QUERY)) {
      this.access[VConstants.MOD_QUERY] = access.value
    }
    if (modes.contains(Mode.INSERT)) {
      this.access[VConstants.MOD_INSERT] = access.value
    }
    if (modes.contains(Mode.UPDATE)) {
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
  fun align(targetBlock: Block, vararg positions: Pair<FormField<*>, FormField<*>>) {
    val targets = mutableListOf<Int>()

    fields.forEach { field ->
      if(positions.toMap().contains(field)) {
        val targetField = positions.toMap()[field]

        targets.add(targetBlock.fields.indexOf(targetField))
      }
    }

    alignment = BlockAlignment(targetBlock, targets.toIntArray())
  }

  /**
   * Make a tuning pass in order to create informations about exported
   * elements such as block fields positions
   *
   * @param window        the actual context of analyse
   */
  fun initialize(window: Window) {
    this.form = window.model as VForm
    val bottomRight = Point(0, 0)

    fields.forEach { field ->
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

  fun hasOption(option: Int): Boolean = options and option == option

  /**
   * Returns true if the size of the buffer == 1, false otherwise
   */
  fun isSingle(): Boolean = buffer == 1

  /**
   * Returns the form block table
   */
  fun getTable(table: Table): Table {
    return if (tables.contains(table)) {
      table
    } else {
      throw Exception("The table ${table.tableName} is not defined in this block")
    }
  }

  /**
   * Returns the table number
   *
   */
  fun getTableNum(table: Table): Int {
    val indexOfTable = tables.indexOf(table)
    return if (indexOfTable >= -1) indexOfTable else throw InconsistencyException("Table ${table.tableName} not found.")
  }

  /**
   * @param page the page number of this block
   */
  fun setInfo(page: FormPage, form: Form) {
    setInfo(page.pageNumber, form.model)
  }

  fun resolve(field: VField?): FormField<*>? = fields.singleOrNull { it.vField == field }

  /**
   * Sets the color properties of the given record.
   * @param r The record number.
   * @param foreground The foreground color.
   * @param background The background color.
   */
  fun setColor(r: Int, foreground: Color?, background: Color?) {
    val foreground = if(foreground == null) {
      null
    } else {
      VColor(foreground.red, foreground.green, foreground.blue)
    }
    val background = if(background == null) {
      null
    } else {
      VColor(background.red, background.green, background.blue)
    }

    setColor(r, foreground, background)
  }

  /**
   * Saves current block (insert or update)
   */
  fun saveBlock() {
    Commands.saveBlock(this)
  }

  /**
   * Resets current block
   */
  fun resetBlock() {
    Commands.resetBlock(this)
  }

  /**
   * Queries block, fetches first record.
   * @exception        VException        an exception may occur during DB access
   */
  fun serialQuery() {
    Commands.serialQuery(this)
  }

  /**
   * Inserts an empty line in multi-block.
   * @exception        VException        an exception may occur during DB access
   */
  fun insertLine() {
    Commands.insertLine(this)
  }

  /**
   * Navigate between accessible blocks
   * @exception        VException        an exception may occur during DB access
   */
  fun changeBlock() {
    Commands.changeBlock(this)
  }

  /**
   * Sets the search operator for the current field
   * @exception        VException        an exception may occur during DB access
   */
  fun searchOperator() {
    Commands.setSearchOperator(this)
  }

  /**
   * * Loads block from database
   */
  open fun deleteBlock() {
    Commands.deleteBlock(this)
  }

  /**
   * Menu query block, fetches selected record.
   */
  fun DictionaryForm.recursiveQuery() {
    Commands.recursiveQuery(this@Block)
  }

  /**
   * Menu query block, fetches selected record, then moves to next block
   */
  fun queryMove() {
    Commands.queryMove(this)
  }

  fun setMode(mode: Mode) {
    setMode(mode.value)
  }

  /**
   * Adds a field drop list. A check is performed to test if the dropped extension
   * id associated to another field. In this case, the conflicted drop extension is
   * returned. otherwise null is returned.
   */
  fun addDropList(dropList: Array<out String>, field: FormField<*>): String? {
    for (i in dropList.indices) {
      val extension = dropList[i].lowercase()
      if (dropListMap[extension] != null) {
        return extension
      }
      dropListMap[extension] = field
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

  fun genLocalization(writer: LocalizationWriter) {
    (writer as FormLocalizationWriter).genBlock(ident, title, help, indices, fields)
  }

  // ----------------------------------------------------------------------
  // BLOCK MODEL
  // ----------------------------------------------------------------------

  override fun setInfo(form: VForm) {
    this@Block.fields.forEach {
      it.setInfo(super.source)
    }
  }

  var isModelInitialized = false

  /** Returns block model */
  open fun getBlockModel(vForm: VForm): VBlock {
    form = vForm
    source = if (this::class.isInner && vForm.source != null) vForm.source!! else sourceFile
    name = ident
    isModelInitialized = true
    return this
  }
}
