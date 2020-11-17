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

import org.jetbrains.exposed.sql.Table
import org.kopi.galite.common.Actor
import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.Command
import org.kopi.galite.common.Trigger
import org.kopi.galite.domain.Domain
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VForm

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
class FormBlock(var buffer: Int, var visible: Int, ident: String) : FormElement(ident), VConstants {
  var title: String = ident
  var border: Int = 0
  var align: FormBlockAlign? = null
  val help: String? = null
  var options: Int = 0
  var blockTables: MutableList<FormBlockTable> = mutableListOf()
  var indices: Array<FormBlockIndex> = arrayOf()
  lateinit var access: IntArray
  lateinit var commands: Array<Command?>
  lateinit var triggers: Array<Trigger>
  lateinit var dropListMap: HashMap<*, *>

  /** Blocks's fields. */
  val blockFields = mutableListOf<FormField<*>>()

  /** Blocks's commands. */
  val blockCommands = mutableListOf<Command>()

  /**
   * Adds the [table] to this block
   */
  fun <T: Table> table(table: T): T {
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
  inline fun <reified T : Comparable<T>> mustFill(domain: Domain<T>, init: FormField<T>.() -> Unit): FormField<T> {
    domain.kClass = T::class
    val field = FormField(domain)
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
  inline fun <reified T : Comparable<T>> visit(domain: Domain<T>, init: FormField<T>.() -> Unit): FormField<T> {
    domain.kClass = T::class
    val field = FormField(domain)
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
  inline fun <reified T : Comparable<T>> skipped(domain: Domain<T>, init: FormField<T>.() -> Unit): FormField<T> {
    domain.kClass = T::class
    val field = FormField(domain)
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
    val field = FormField(domain)
    field.access = IntArray(3) { VConstants.ACS_HIDDEN }
    field.init()
    blockFields.add(field)
    return field
  }

  /**
   * Adds a new command to this block.
   *
   * @param item    the actor linked to the command.
   * @param init    initialization method.
   * @return a field.
   */
  fun command(item: Actor, init: Command.() -> Unit): Command {
    val command = Command()
    command.init()
    blockCommands.add(command)
    return command
  }

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
                                                indices,
                                                blockFields.toTypedArray())
  }


  /** Returns block model */
  fun getBlockModel(vForm: VForm): VBlock {
    return object : VBlock(vForm) {
      override fun setInfo() {
        blockFields.forEach {
          it.setInfo()
        }
      }

      init {
        super.tables = blockTables.map {
          it.table
        }.toTypedArray()
        fields = blockFields.map {
          it.getFieldModel()
        }.toTypedArray()
      }
    }
  }
}
