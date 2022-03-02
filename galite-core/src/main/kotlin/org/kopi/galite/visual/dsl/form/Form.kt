/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.visual.dsl.form

import java.io.IOException
import java.util.Locale

import org.kopi.galite.visual.dsl.chart.Chart
import org.kopi.galite.visual.dsl.common.Action
import org.kopi.galite.visual.dsl.common.FormTrigger
import org.kopi.galite.visual.dsl.common.LocalizationWriter
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.dsl.common.Window
import org.kopi.galite.visual.dsl.common.WindowElement
import org.kopi.galite.visual.form.Commands
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.fullcalendar.VFullCalendarBlock
import org.kopi.galite.visual.visual.ApplicationContext
import org.kopi.galite.visual.visual.VException
import org.kopi.galite.visual.visual.WindowController

/**
 * Represents a form.
 *
 * @param title The title of this form.
 * @param locale the window locale.
 */
abstract class Form(title: String, locale: Locale? = null) : Window(title, locale) {

  /** Form's blocks. */
  val blocks = mutableListOf<Block>()

  /** Form's pages. */
  val pages = mutableListOf<FormPage>()

  /**
   * Adds a new block to this form.
   *
   * @param        title                  the title of the block
   * @param        buffer                 the buffer size of this block
   * @param        visible                the number of visible elements
   * @param        formPage              the page containing the block
   */
  fun block(
          title: String,
          buffer: Int,
          visible: Int,
          init: Block.() -> Unit
  ): Block = insertBlock(Block(title, buffer, visible), init)

  /**
   * Adds a new block to this page which belongs to this form.
   *
   * @param        buffer                 the buffer size of this block
   * @param        visible                the number of visible elements
   * @param        title                  the title of the block
   */
  fun FormPage.block(
    title: String,
    buffer: Int,
    visible: Int,
    init: Block.() -> Unit
  ): Block = insertBlock(Block(title, buffer, visible), this, init)

  /**
   * Adds a new block to this form.
   *
   * @param        block                 the block to insert
   * @receiver                           the page containing the block
   */
  fun <T : Block> FormPage.insertBlock(block: T, init: (T.() -> Unit)? = null): T =
    this.form.insertBlock(block, this, init)

  /**
   * Adds a new block to this form.
   *
   * @param        block                 the block to insert
   * @param        formPage              the page containing the block
   */
  private fun <T : Block> insertBlock(block: T, formPage: FormPage? = null, init: (T.() -> Unit)? = null): T {
    if (init != null) {
      block.init()
    }
    if (formPage != null) {
      block.pageNumber = formPage.pageNumber
    }
    block.initialize(this)
    blocks.add(block)

    val vBlock = block.getBlockModel(model)
    vBlock.setInfo(block.pageNumber, model)
    if(vBlock !is VFullCalendarBlock) {
      vBlock.initIntern()
    }
    block.fields.forEach { formField ->
      formField.initialValues.forEach {
        formField.vField.setObject(it.key, it.value)
      }
    }

    model.addBlock(vBlock)
    return block
  }

  /**
   * Adds a new block to this form.
   *
   * @param        block                 the block to insert
   */
  fun <T : Block> insertBlock(block: T, init: (T.() -> Unit)? = null): T = insertBlock(block, null, init)

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
    for (i in VConstants.TRG_TYPES.indices) {
      if (trigger.events shr i and 1 > 0) {
        model.VKT_Triggers[0][i] = trigger
      }
    }

    return trigger
  }

  override fun addCommandTrigger() {
    model.VKT_Triggers.add(arrayOfNulls(VConstants.TRG_TYPES.size))
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
    val page = FormPage(pages.size, "Id\$${pages.size}", title, this)

    pages.add(page)
    model.pages.add(page.title)

    return page
  }

  /**
   * Adds a new page to this form. You can use this method to create Pages in your form, this is optional
   * and will create a Tab for each page you create under the form's toolbar.
   *
   * @param        title                the title of the page
   * @return       the form page. You can use it as a parameter to a block it to define that the block
   * will be inserted in this page. You can put as much blocks you want in each page
   */
  fun page(title: String, init: FormPage.() -> Unit): FormPage {
    val page = page(title)

    page.init()
    return page
  }

  /**
   * Aborts current processing, resets form.
   * @exception        VException        an exception may occur in form.reset()
   */
  fun resetForm() {
    Commands.resetForm(model)
  }

  /**
   * Shows to the user want to show a help about this form.
   */
  fun showHelp() {
    model.showHelp()
  }

  /**
   * Aborts current processing, closes form.
   * @exception        VException        an exception may occur in form.close()
   */
  fun quitForm() {
    Commands.quitForm(model)
  }

  /**
   * GOTO BLOCK
   * @exception        org.kopi.galite.visual.visual.VException        an exception may be raised by field.leave
   */
  fun gotoBlock(target: VBlock) {
    model.gotoBlock(target)
  }

  fun showChart(chart: Chart) {
    WindowController.windowController.doNotModal(chart)
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
  open fun getFormElement(ident: String?): WindowElement? {
    blocks.forEach { formBlock ->
      if (formBlock.ident == ident || formBlock.shortcut == ident) {
        return formBlock
      }
    }
    return null
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  override fun genLocalization(destination: String?, locale: Locale?) {
    if (locale != null) {
      val baseName = this::class.simpleName
      requireNotNull(baseName)
      val localizationDestination = destination
              ?: this.javaClass.classLoader.getResource("")?.path +
              this.javaClass.`package`.name.replace(".", "/")
      try {
        val writer = FormLocalizationWriter()
        genLocalization(writer)
        writer.write(localizationDestination, baseName, locale)
      } catch (ioe: IOException) {
        ioe.printStackTrace()
        System.err.println("cannot write : $baseName")
      }
    }
  }

  fun genLocalization(writer: LocalizationWriter) {
    (writer as FormLocalizationWriter)
            .genForm(title, blocks.map { it.ownDomains }.flatten(), menus, actors, pages, blocks)
  }

  // ----------------------------------------------------------------------
  // FORM MODEL
  // ----------------------------------------------------------------------

  override val model: VForm = object : VForm() {
    init {
      source = sourceFile // TODO: move to VWindow
      setTitle(title)
    }

    override val locale get() = this@Form.locale ?: ApplicationContext.getDefaultLocale() // TODO!!

    override fun formClassName(): String = this@Form.javaClass.name
  }
}
