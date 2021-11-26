/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
import org.kopi.galite.visual.form.Commands
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.visual.ApplicationContext
import org.kopi.galite.visual.visual.VException
import org.kopi.galite.visual.visual.WindowController

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
   * @param        buffer                 the buffer size of this block
   * @param        visible                the number of visible elements
   * @param        title                  the title of the block
   */
  fun FormPage.block(
    buffer: Int,
    visible: Int,
    title: String,
    init: FormBlock.() -> Unit
  ): FormBlock = insertBlock(FormBlock(buffer, visible, title, title), this, init)

  /**
   * Adds a new block to this form.
   *
   * @param        block                 the block to insert
   * @receiver                           the page containing the block
   */
  fun <T : FormBlock> FormPage.insertBlock(block: T, init: (T.() -> Unit)? = null): T =
    this.form.insertBlock(block, this, init)

  /**
   * Adds a new block to this form.
   *
   * @param        block                 the block to insert
   * @param        formPage              the page containing the block
   */
  private fun <T : FormBlock> insertBlock(block: T, formPage: FormPage? = null, init: (T.() -> Unit)? = null): T {
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
   * Adds a new block to this form.
   *
   * @param        block                 the block to insert
   */
  fun <T : FormBlock> insertBlock(block: T, init: (T.() -> Unit)? = null): T = insertBlock(block, null, init)

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
    val page = FormPage(pages.size, "Id\$${pages.size}", title, this)
    pages.add(page)
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
    val page = FormPage(pages.size, "Id\$${pages.size}", title, this)
    page.init()
    pages.add(page)
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
    model.showHelp(model)
  }

  /**
   * Aborts current processing, closes form.
   * @exception        VException        an exception may occur in form.close()
   */
  fun quitForm() {
    Commands.quitForm(model)
  }

  fun showChart(chart: Chart) {
    WindowController.windowController.doNotModal(chart)
  }

  open fun setTextOnFieldLeave(): Boolean = false

  open fun forceCheckList(): Boolean = true

  // ----------------------------------------------------------------------
  // Navigation
  // ----------------------------------------------------------------------
  /**
   * GOTO PAGE [page]
   * @exception        org.kopi.galite.visual.visual.VException
   */
  open fun gotoPage(page: Int) {
    model.gotoPage(page)
  }

  /**
   * GOTO BLOCK
   * @exception        org.kopi.galite.visual.visual.VException
   */
  fun gotoBlock(target: VBlock) {
    model.gotoBlock(target)
  }

  /**
   * Go to the next block
   * @exception        org.kopi.galite.visual.visual.VException
   */
  fun gotoNextBlock() {
    model.gotoNextBlock()
  }

  fun enterBlock() {
    model.enterBlock()
  }

  /**
   * Returns true if the form contents have been changed by the user.
   *
   * NOTE: Trigger [CHANGED] returns true if form is considered changed.
   */
  fun isChanged(): Boolean = model.isChanged()


  /**
   * Resets form to initial state
   *
   * NOTE: Trigger [RESET] returns true if reset handled by trigger
   * @exception        org.kopi.galite.visual.visual.VException
   */
  fun reset() {
    model.reset()
  }

  /**
   * create a list of items and return id of selected one or -1
   *
   * @param        showUniqueItem        open a list if there is only one item also
   * @exception        org.kopi.galite.visual.visual.VException
   */
  fun singleMenuQuery(parent: Window, showUniqueItem: Boolean): Int = model.singleMenuQuery(parent.model, showUniqueItem)

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected open fun callTrigger(event: FormTriggerEvent<*>, index: Int): Any? = model.callTrigger(event.event, index)

  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected open fun callTrigger(event: FormTriggerEvent<*>): Any? = model.callTrigger(event.event)

  /**
   * @return If there is trigger associated with event
   */
  protected open fun hasTrigger(event: FormTriggerEvent<*>): Boolean = model.hasTrigger(event.event, 0)

  /**
   * @return If there is trigger associated with event
   */
  protected open fun hasTrigger(event: FormTriggerEvent<*>, index: Int): Boolean = model.hasTrigger(event.event, index)

  // ----------------------------------------------------------------------
  // UTILS
  // ----------------------------------------------------------------------

  /**
   * setBlockRecords
   * inform user about nb records fetched and current one
   */
  open fun setFieldSearchOperator(op: Int) {
    model.setFieldSearchOperator(op)
  }

  /**
   * Returns the number of blocks.
   */
  open fun getBlockCount(): Int = formBlocks.size

  /**
   * Returns the block with given index.
   * @param        index                the index of the specified block
   */
  open fun getBlock(index: Int): FormBlock? = getFormElement(model.getBlock(index).name)

  /**
   * return a Block from its name
   * @param        name                name of the block
   * @return    the first block with this name, or null if the block is not found.
   */
  open fun getBlock(name: String): FormBlock? = getFormElement(model.getBlock(name)?.name)

  /**
   * Returns the current block
   */
  open fun getActiveBlock(): FormBlock? = getFormElement(model.getActiveBlock()?.name)

  /**
   * Sets the current block
   */
  open fun setActiveBlock(block: FormBlock) {
    model.setActiveBlock(block.vBlock)
  }

  /**
   * Launch file preview
   */
  fun documentPreview(file: String) {
    model.documentPreview(file)
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
  open fun getFormElement(ident: String?): FormBlock? {
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
            .genForm(title, formBlocks.map { it.ownDomains }.flatten(), menus, actors, pages, formBlocks)
  }

  // ----------------------------------------------------------------------
  // FORM MODEL
  // ----------------------------------------------------------------------
  override val model: VForm by lazy { FormModel() }

  inner class FormModel: VForm() {
    override val locale get() = this@Form.locale ?: ApplicationContext.getDefaultLocale()

    override fun setTextOnFieldLeave(): Boolean = this@Form.setTextOnFieldLeave()

    override fun forceCheckList(): Boolean = this@Form.forceCheckList()

    override fun init() {
      initialize()
    }
  }

  protected fun VForm.initialize() {
    source = sourceFile
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
        vBlock.setInfo(formBlock.pageNumber, this)
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
    val formTriggerArray = arrayOfNulls<Trigger>(VConstants.TRG_TYPES.size)
    triggers.forEach { trigger ->
      for (i in VConstants.TRG_TYPES.indices) {
        if (trigger.events shr i and 1 > 0) {
          formTriggerArray[i] = trigger
        }
      }
      VKT_Triggers[0] = formTriggerArray
    }

    // COMMANDS TRIGGERS
    this@Form.commands.forEach {
      val fieldTriggerArray = arrayOfNulls<Trigger>(VConstants.TRG_TYPES.size)
      // TODO : Add commands triggers here
      VKT_Triggers.add(fieldTriggerArray)
    }
  }
}
