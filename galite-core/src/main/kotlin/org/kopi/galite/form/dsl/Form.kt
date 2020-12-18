/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

import java.io.IOException

import org.kopi.galite.common.Action
import org.kopi.galite.common.Actor
import org.kopi.galite.common.Command
import org.kopi.galite.common.FormBooleanTriggerEvent
import org.kopi.galite.common.FormTrigger
import org.kopi.galite.common.FormTriggerEvent
import org.kopi.galite.common.FormVoidTriggerEvent
import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.Menu
import org.kopi.galite.common.Trigger
import org.kopi.galite.common.Window
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VForm
import org.kopi.galite.visual.VActor
import org.kopi.galite.visual.VCommand

/**
 * Represents a form.
 */
abstract class Form : Window() {

  /** Form's actors. */
  val formActors = mutableListOf<Actor>()

  /** Form's blocks. */
  val formBlocks = mutableListOf<FormBlock>()

  /** Form's pages. */
  val pages = mutableListOf<FormPage>()

  /** Form's menus. */
  val menus = mutableListOf<Menu>()

  /**
   * Adds a new actor to this form.
   *
   * An Actor is an item to be linked to a command.
   *
   * @param menu                 the containing menu
   * @param label                the label
   * @param help                 the help
   */
  fun actor(ident: String, menu: Menu, label: String, help: String, init: Actor.() -> Unit): Actor {
    val actor = Actor(ident, menu, label, help)
    actor.init()
    formActors.add(actor)
    return actor
  }

  /**
   * Adds a new block to this form.
   *
   * @param        buffer                 the buffer size of this block
   * @param        visible                the number of visible elements
   * @param        name                   the simple identifier of this block
   * @param        title                  the title of the block
   * @param        formPage              the page containing the block
   */
  fun block(
          buffer: Int,
          visible: Int,
          name: String,
          title: String,
          formPage: FormPage? = null,
          init: FormBlock.() -> Unit
  ): FormBlock = insertBlock(FormBlock(buffer, visible, name, title), formPage, init)

  /**
   * Adds a new block to this form.
   *
   * @param        block                 the block to insert
   * @param        formPage              the page containing the block
   */
  fun <T : FormBlock> insertBlock(block: T, formPage: FormPage? = null, init: (T.() -> Unit)? = null): T {
    if (init != null) {
      block.init()
    }
    if (formPage != null) {
      block.pageNumber = formPage.pageNumber
    }
    block.initialize(this)
    formBlocks.add(block)
    return block
  }

  /**
   * Adds triggers to this form
   *
   * @param formTriggerEvents    the trigger events to add
   * @param method               the method to execute when trigger is called
   */
  private fun <T> trigger(formTriggerEvents: Array<out FormTriggerEvent>, method: () -> T): Trigger {
    val event = formEventList(formTriggerEvents)
    val formAction = Action(null, method)
    val trigger = FormTrigger(event, formAction)
    triggers.add(trigger)
    return trigger
  }

  /**
   * Adds void triggers to this form
   *
   * @param formTriggerEvents  the trigger events to add
   * @param method             the method to execute when trigger is called
   */
  fun trigger(vararg formTriggerEvents: FormVoidTriggerEvent, method: () -> Unit): Trigger {
    return trigger(formTriggerEvents, method)
  }

  /**
   * Adds boolean triggers to this form
   *
   * @param formTriggerEvents the trigger events to add
   * @param method            the method to execute when trigger is called
   */
  fun trigger(vararg formTriggerEvents: FormBooleanTriggerEvent, method: () -> Boolean): Trigger {
    return trigger(formTriggerEvents, method)
  }

  private fun formEventList(formTriggerEvents: Array<out FormTriggerEvent>): Long {
    var self = 0L

    formTriggerEvents.forEach { trigger ->
      self = self or (1L shl trigger.event)
    }

    return self
  }

  /**
   * Adds a new page to this form. You can use this method to create Pages in your form, this is optional
   * and will create a Tab for each page you create under the form's toolbar.
   *
   * @param        title                the title of the page
   * @return       the form page. You can use it as a parameter to a block it to define that the block
   * will be inserted in this page. You can put as much blocks you want in each page
   */
  fun page(title: String): FormPage {
    val page = FormPage(pages.size, "Id\$${pages.size}", title)
    pages.add(page)
    return page
  }

  /**
   * Adds a new menu to this form. Defining a menu means adding an entry to the menu bar in the top of the form
   *
   * @param label                the menu label in default locale
   * @return                     the menu. It is used later to adding actors to this menu by specifying
   * the menu name in the actor definition.
   */
  fun menu(label: String): Menu {
    val menu = Menu(label)
    menus.add(menu)
    return menu
  }

  /**
   * Adds a new command to this form.
   *
   * @param item    the actor linked to the command.
   * @param init    initialization method.
   */
  fun command(item: Actor, init: Command.() -> Unit): Command {
    val command = Command(item)
    command.init()
    commands.add(command)
    return command
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Get block
   */
  open fun getFormElement(ident: String?): FormElement? {
    formBlocks.forEach { formBlock ->
      if (formBlock.ident == ident || formBlock.shortcut == ident) {
        return formBlock
      }
    }
    return null
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  fun genLocalization(destination: String? = null) {
    if (locale != null) {
      val baseName = this::class.simpleName
      requireNotNull(baseName)
      val destination = destination
              ?: this.javaClass.classLoader.getResource("")?.path +
              this.javaClass.packageName.replace(".", "/")
      try {
        val writer = FormLocalizationWriter()
        genLocalization(writer)
        writer.write(destination, baseName, locale!!)
      } catch (ioe: IOException) {
        ioe.printStackTrace()
        System.err.println("cannot write : $baseName")
      }
    }
  }

  fun genLocalization(writer: LocalizationWriter) {
    (writer as FormLocalizationWriter).genForm(title,
                                               menus.toTypedArray(),
                                               formActors.toTypedArray(),
                                               pages.toTypedArray(),
                                               formBlocks.toTypedArray()
    )
  }

  /** Form model */
  override val model: VForm by lazy {
    genLocalization()

    object : VForm() {
      override fun init() {
        initialize()
      }

      init {
      }
    }
  }

  fun VForm.initialize() {
    source = sourceFile
    pages = this@Form.pages.map {
      it.ident
    }.toTypedArray()
    this.actors = formActors.map {
      VActor(it.menu.label, sourceFile, it.ident, sourceFile, it.icon, it.keyCode, it.keyModifier)
    }.toTypedArray()
    blocks = formBlocks.map { formBlock ->
      formBlock.getBlockModel(this, source).also { vBlock ->
        vBlock.setInfo(formBlock.pageNumber)
        vBlock.initIntern()
        formBlock.blockFields.forEach { formField ->
          formField.initialValues.forEach {
            formField.vField.setObject(it.key, it.value) // FIXME temporary workaround
          }
        }
      }
    }.toTypedArray()

    //TODO ----------begin-------------
    this.commands = this@Form.commands.map { command ->
      VCommand(command.mode,
               this,
               actors.find { it?.actorIdent ==  command.item.ident },
               -1,
               command.name!!,
               command.action
      )
    }.toTypedArray()
    this.handleTriggers(triggers)
  }

  /**
   * Handling form triggers
   */
  fun VForm.handleTriggers(triggers: MutableList<Trigger>) {
    // FORM TRIGGERS
    val formTriggerArray = IntArray(VConstants.TRG_TYPES.size)
    triggers.forEach { trigger ->
      for (i in VConstants.TRG_TYPES.indices) {
        if (trigger.events shr i and 1 > 0) {
          formTriggerArray[i] = i
          formTriggers[i] = trigger
        }
      }
      VKT_Triggers[0] = formTriggerArray
    }

    // COMMANDS TRIGGERS
    this@Form.commands.forEach {
      val fieldTriggerArray = IntArray(VConstants.TRG_TYPES.size)
      // TODO : Add commands triggers here
      VKT_Triggers.add(fieldTriggerArray)
    }
  }
}
