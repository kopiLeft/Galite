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

import org.kopi.galite.common.Actor
import org.kopi.galite.common.Command
import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.Trigger
import org.kopi.galite.common.Window
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VForm
import org.kopi.galite.visual.VCommand

/**
 * A block on a form
 * A block contains fields and reference to database
 *
 * @param        buffer                the buffer size of this block
 * @param        visible               the number of visible elements
 * @param        ident                 the simple identifier of this block
 * @param        shortcut              the shortcut of this block
 * @param        title                 the title of the block
 * @param        border                the border of the block
 * @param        align                 the type of alignment in form
 * @param        help                  the help
 * @param        options               the options
 * @param        tables                the tables accessed on the database
 * @param        indices               the indices for database
 * @param        access                the access mode
 * @param        commands              the commands associated with this block
 * @param        triggers              the triggers executed by this form
 * @param        fields                the objects that populate the block
 */
class FormBlock(var buffer: Int, var visible: Int, ident: String, val title: String) : FormElement(ident), VConstants {
  var border: Int = 0
  var align: FormBlockAlign? = null
  val help: String? = null
  var options: Int = 0
  var blockTables: MutableList<FormBlockTable> = mutableListOf()
  var indices: MutableList<FormBlockIndex> = mutableListOf()
  var access: IntArray = IntArray(3) { VConstants.ACS_MUSTFILL }
  lateinit var commands: Array<Command?>
  lateinit var triggers: Array<Trigger>
  lateinit var dropListMap: HashMap<*, *>
  private var maxRowPos = 0
  private var maxColumnPos = 0
  private var displayedFields = 0

  /** Blocks's fields. */
  val blockFields = mutableListOf<FormField<*>>()

  /** Blocks's commands. */
  val blockCommands = mutableListOf<Command>()

  /**
   * Adds the [table] to this block
   */
  fun <T : Table> table(table: T): T {
    val formBlockTable = FormBlockTable(table.tableName, table.tableName, table)
    blockTables.add(formBlockTable)
    return table
  }

  /**
   * creates and returns a field. It uses [init] method to initialize the field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method.
   * @return a field.
   */
  inline fun <reified T : Comparable<T>> mustFill(domain: Domain<T>, position: FormPosition, init: FormField<T>.() -> Unit): FormField<T> {
    domain.kClass = T::class
    val field = FormField(domain, blockFields.size, position)
    field.access = IntArray(3) { VConstants.ACS_MUSTFILL }
    field.init()
    blockFields.add(field)
    return field
  }

  /**
   * creates and returns a field. It uses [init] method to initialize the field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method.
   * @return a field.
   */
  inline fun <reified T : Comparable<T>> visit(domain: Domain<T>, position: FormPosition, init: FormField<T>.() -> Unit): FormField<T> {
    domain.kClass = T::class
    val field = FormField(domain, blockFields.size, position)
    field.access = IntArray(3) { VConstants.ACS_VISIT }
    field.init()
    blockFields.add(field)
    return field
  }

  /**
   * creates and returns a field. It uses [init] method to initialize the field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method.
   * @return a field.
   */
  inline fun <reified T : Comparable<T>> skipped(domain: Domain<T>, position: FormPosition, init: FormField<T>.() -> Unit): FormField<T> {
    domain.kClass = T::class
    val field = FormField(domain, blockFields.size, position)
    field.access = IntArray(3) { VConstants.ACS_SKIPPED }
    field.init()
    blockFields.add(field)
    return field
  }

  /**
   * creates and returns a field. It uses [init] method to initialize the field.
   *
   * @param domain  the domain of the field.
   * @param init    initialization method.
   * @return a field.
   */
  inline fun <reified T : Comparable<T>> hidden(domain: Domain<T>, init: FormField<T>.() -> Unit): FormField<T> {
    domain.kClass = T::class
    val field = FormField(domain, blockFields.size)
    field.access = IntArray(3) { VConstants.ACS_HIDDEN }
    field.init()
    blockFields.add(field)
    return field
  }

  /**
   * Sets the field in position given by x and y location
   *
   * @param line		the line
   * @param column		the column
   */
  fun at(line: Int, column: Int): FormPosition = FormCoordinatePosition(line, 0, column, 0)

  /**
   * Sets the field in position given by x and y location
   *
   * @param lineRange		the line range (lineRange.first: the line,
   *                            lineRange.last: the last line into this field may be placed)
   * @param column		the column
   */
  fun at(lineRange: IntRange, column: Int): FormPosition =
          FormCoordinatePosition(lineRange.first, lineRange.last, column, 0)

  /**
   * Sets the field in position given by x and y location
   *
   * @param line		the line
   * @param columnRange		the line range (columnRange.first: the column,
   *                            columnRange.last: the last column into this field may be placed)
   */
  fun at(line: Int, columnRange: IntRange): FormPosition =
          FormCoordinatePosition(line, 0, columnRange.first, columnRange.last)

  /**
   * Sets the field in position given by x and y location
   *
   * @param lineRange		the line range (lineRange.first: the line,
   *                            lineRange.last: the last line into this field may be placed)
   * @param columnRange		the line range (columnRange.first: the column,
   *                            columnRange.last: the last column into this field may be placed)
   */
  fun at(lineRange: IntRange, columnRange: IntRange): FormPosition =
          FormCoordinatePosition(lineRange.first, lineRange.last, columnRange.first, columnRange.last)

  /**
   * Sets the field in position given by x location
   *
   * @param lineRange		the line range (lineRange.first: the line,
   *                            lineRange.last: the last line into this field may be placed)
   */
  fun at(lineRange: IntRange): FormPosition = FormMultiFieldPosition(lineRange.first, lineRange.last)

  /**
   * Sets the field in position given by x location
   *
   * @param line		the line
   */
  fun at(line: Int): FormPosition = FormMultiFieldPosition(line, 0)

  /**
   * Sets the field in position that follows a specific [field]
   *
   * @param field		the field
   */
  fun <T : Comparable<T>> follow(field: FormField<T>): FormPosition = FormDescriptionPosition(field)

  /**
   * creates and returns a form block index. It uses [init] method to initialize the index.
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
   * @param item    the actor linked to the command.
   * @param init    initialization method.
   * @return a field.
   */
  fun command(item: Actor, init: Command.() -> Unit): Command {
    val command = Command(item)
    command.init()
    blockCommands.add(command)
    return command
  }

  /**
   * Make a tuning pass in order to create informations about exported
   * elements such as block fields positions
   *
   * @param window        the actual context of analyse
   */
  override fun initialize(window: Window) {
    val bottomRight = Point(0, 0)

    blockFields.forEach { field ->
      field.initialize(this)
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

  fun hasOption(option: Int): Boolean = options and option == option

  /**
   * Returns true if the size of the buffer == 1, false otherwise
   */
  fun isSingle(): Boolean = buffer == 1

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------
  /**
   * !!!FIX:taoufik
   */
  override fun genLocalization(writer: LocalizationWriter) {
    (writer as FormLocalizationWriter).genBlock(ident,
                                                title,
                                                help,
                                                indices.toTypedArray(),
                                                blockFields.toTypedArray())
  }

  /** Returns block model */
  fun getBlockModel(vForm: VForm, source: String? = null): VBlock {
    return object : VBlock(vForm) {
      override fun setInfo() {
        blockFields.forEach {
          it.setInfo()
        }
      }

      init {
        //TODO ----------begin-------------
        super.VKT_Triggers = arrayOf(IntArray(200), IntArray(200), IntArray(200), IntArray(200), IntArray(200), IntArray(200))
        super.access = intArrayOf(
                4, 4, 4
        )

        /** Used actors in form*/
        val usedActors  = form.actors.map { vActor ->
          vActor?.actorIdent to vActor
        }.toMap()

        super.commands = blockCommands?.map {
          VCommand(it.mode,
                   this,
                   usedActors[it.item.ident],
                   -1,
                   it.name!!,
                   it.action
                  )
        }.toTypedArray()

        //TODO ------------end-----------

        super.source = source ?: sourceFile
        super.bufferSize = buffer
        super.maxRowPos = this@FormBlock.maxRowPos
        super.maxColumnPos = this@FormBlock.maxColumnPos
        super.name = ident
        super.tables = blockTables.map {
          it.table
        }.toTypedArray()
        fields = blockFields.map {
          it.getFieldModel()
        }.toTypedArray()
        super.indices = this@FormBlock.indices.map {
          it.ident
        }.toTypedArray()
      }
    }
  }
}
