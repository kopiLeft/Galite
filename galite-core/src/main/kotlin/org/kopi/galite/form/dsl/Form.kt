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
import org.kopi.galite.common.FormTrigger
import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.Trigger
import org.kopi.galite.common.Window
import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VForm
import org.kopi.galite.visual.ApplicationContext

/**
 * Represents a form.
 */
abstract class Form : Window() {

  /** Form's blocks. */
  val formBlocks = mutableListOf<FormBlock>()

  /** Form's pages. */
  val pages = mutableListOf<FormPage>()

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
  fun <T> trigger(vararg formTriggerEvents: FormTriggerEvent<T>, method: () -> T): Trigger {
    val event = formEventList(formTriggerEvents)
    val formAction = Action(null, method)
    val trigger = FormTrigger(event, formAction)
    triggers.add(trigger)
    return trigger
  }

  private fun formEventList(formTriggerEvents: Array<out FormTriggerEvent<*>>): Long {
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

  ///////////////////////////////////////////////////////////////////////////
  // FORM TRIGGERS EVENTS
  ///////////////////////////////////////////////////////////////////////////
  /**
   * Form Triggers
   *
   * @param event the event of the trigger
   */
  open class FormTriggerEvent<T>(val event: Int)

  /**
   * executed when initializing the form and before the PREFORM Trigger, also executed at ResetForm command
   */
  val INIT = FormTriggerEvent<Unit>(VConstants.TRG_INIT) // void trigger

  /**
   * executed before the form is displayed and after the INIT Trigger, not executed at ResetForm command
   */
  val PREFORM = FormTriggerEvent<Unit>(VConstants.TRG_PREFORM)       // void trigger

  /**
   * executed when closing the form
   */
  val POSTFORM = FormTriggerEvent<Unit>(VConstants.TRG_POSTFORM)     // void trigger

  /**
   * executed upon ResetForm command
   */
  val RESET = FormTriggerEvent<Boolean>(VConstants.TRG_RESET)        // Boolean trigger

  /**
   * a special trigger that returns a boolean value of whether the form have been changed or not,
   * you can use it to bypass the system control for changes this way :
   *
   * trigger(CHANGED) {
   *   false
   * }
   */
  val CHANGED  = FormTriggerEvent<Boolean>(VConstants.TRG_CHANGED)    // Boolean trigger

  /**
   * executed when quitting the form
   * actually not available
   */
  val QUITFORM = FormTriggerEvent<Boolean>(VConstants.TRG_QUITFORM)  // Boolean trigger

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
      val localizationDestination = destination
              ?: this.javaClass.classLoader.getResource("")?.path +
              this.javaClass.packageName.replace(".", "/")
      try {
        val writer = FormLocalizationWriter()
        genLocalization(writer)
        writer.write(localizationDestination, baseName, locale!!)
      } catch (ioe: IOException) {
        ioe.printStackTrace()
        System.err.println("cannot write : $baseName")
      }
    }
  }

  fun genLocalization(writer: LocalizationWriter) {
    (writer as FormLocalizationWriter)
            .genForm(title, formBlocks.map { it.ownDomains }.flatten(), menus, actors, pages, formBlocks)
  }

  /** Form model */
  override val model: VForm by lazy {
    object : VForm() {
      override fun init() {
        initialize()
      }
    }
  }

  fun VForm.initialize() {
    source = sourceFile
    locale = this@Form.locale ?: ApplicationContext.getDefaultLocale()
    setTitle(title)
    pages = this@Form.pages.map {
      it.title
    }.toTypedArray()
    pagesIdents = this@Form.pages.map {
      it.ident
    }.toTypedArray()
    this.addActors(this@Form.actors.map { actor ->
      actor.buildModel(sourceFile)
    }.toTypedArray())
    this.commands = this@Form.commands.map { command ->
      command.buildModel(this, actors)
    }.toTypedArray()

    this.handleTriggers(triggers)

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
  }

  /**
   * Handling form triggers
   */
  private fun VForm.handleTriggers(triggers: MutableList<Trigger>) {
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
