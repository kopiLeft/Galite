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
import org.kopi.galite.visual.dsl.common.Menu
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.dsl.common.Window
import org.kopi.galite.visual.dsl.common.LocalizableElement
import org.kopi.galite.visual.form.Commands
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.fullcalendar.VFullCalendarBlock
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.DefaultActor
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.WindowController
import org.kopi.galite.visual.dsl.common.Actor
import org.kopi.galite.visual.dsl.common.Command
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand

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
      block.block.pageNumber = formPage.pageNumber
    }
    block.initialize(this)
    blocks.add(block)

    val vBlock = block.getBlockModel(model)
    vBlock.setInfo(block.block.pageNumber, model)
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
   * @exception        org.kopi.galite.visual.VException        an exception may be raised by field.leave
   */
  fun gotoBlock(target: Block) {
    model.gotoBlock(target.block)
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
  open fun getFormElement(ident: String?): LocalizableElement? {
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
                                    ?: (this.javaClass.classLoader.getResource("")?.path +
                                        this.javaClass.`package`.name.replace(".", "/"))
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

  override val model: VForm = object : VForm(sourceFile) {
    init {
      setTitle(title)
    }

    override val locale get() = this@Form.locale ?: ApplicationContext.getDefaultLocale() // TODO!!

    override fun formClassName(): String = this@Form.javaClass.name
  }

  // ----------------------------------------------------------------------
  // Default form settings
  // ----------------------------------------------------------------------

  /**
   * Add form menus
   */
  fun insertMenus() {
    file; edit; action
  }

  /**
   * Add form actors
   */
  fun insertActors() {
    quit
    _break
    validate
    print
    printLabel
    preview
    mail
    autofill
    editItem
    editItemS
    searchOperator
    changeBlock
    copyDocument
    insertLine
    deleteLine
    all
    nothing
    menuQuery
    serialQuery
    insertMode
    save
    delete
    dynamicReport
    help
    showHideFilter
    report
  }

  fun insertCommands() {
    autofill
    editItem
    editItemS

    quitForm; resetForm; helpForm
  }

  fun insertDefaultActors() {
    autofill
    editItem
    editItemS
  }

  // --------------------MENUS-----------------

  val file by lazy { menu(FileMenu()) }

  val edit by lazy { menu(EditMenu()) }

  val action by lazy { menu(ActionMenu()) }

  // --------------------ACTORS----------------

  val quit by lazy { actor(Quit()) }

  val _break by lazy { actor(Break()) }

  val validate by lazy { actor(Validate())}

  val print by lazy { actor(Print())}

  val printLabel by lazy { actor(PrintLabel())}

  val preview by lazy { actor(Preview()) }

  val mail by lazy { actor(Mail()) }

  val autofill by lazy { actor(Autofill()) }

  val newItem by lazy { actor(NewItem()) }

  val editItem by lazy { actor(EditItem()) }

  val editItemS by lazy { actor(EditItem_S()) }

  val searchOperator by lazy { actor(SearchOperator()) }

  val changeBlock by lazy { actor(ChangeBlock()) }

  val copyDocument by lazy { actor(CopyDocument()) }

  val insertLine by lazy { actor(InsertLine()) }

  val deleteLine by lazy { actor(DeleteLine()) }

  val all by lazy { actor(All()) }

  val nothing by lazy { actor(Nothing()) }

  val menuQuery by lazy { actor(MenuQuery()) }

  val serialQuery by lazy { actor(SerialQuery()) }

  val insertMode by lazy { actor(InsertMode()) }

  val save by lazy { actor(Save()) }

  val delete by lazy { actor(Delete()) }

  val report by lazy { actor(CreateReport()) }

  val dynamicReport by lazy { actor(CreateDynamicReport()) }

  val showHideFilter by lazy { actor(ShowHideFilter()) }

  val help by lazy { actor(Help()) }

  // -------------------------------------------------------------------
  // FORM-LEVEL COMMANDS
  // -------------------------------------------------------------------

  val resetForm  by lazy {
    command(item = _break) {
      resetForm()
    }
  }

  val quitForm by lazy {
    command(item = quit) {
      quitForm()
    }
  }

  val helpForm by lazy {
    command(item = help) {
      showHelp()
    }
  }

  // -------------------------------------------------------------------
  // BLOCK-LEVEL COMMANDS
  // -------------------------------------------------------------------

  val Block.breakCmd: Command
    get() = command(item = _break) {
      resetBlock()
    }

  val Block.recursiveQueryCmd: Command
    get() = command(item = menuQuery) {
      Commands.recursiveQuery(block)
    }

  val Block.menuQueryCmd: Command
    get() = command(item = menuQuery) {
      Commands.menuQuery(block)
    }

  val Block.queryMoveCmd: Command
    get() = command(item = menuQuery) {
      Commands.queryMove(block)
    }

  val Block.serialQueryCmd: Command
    get() = command(item = serialQuery) {
      Commands.serialQuery(block)
    }

  val Block.insertModeCmd: Command
    get() = command(item = insertMode) {
      insertMode()
    }

  val Block.saveCmd: Command
    get() = command(item = save) {
      saveBlock()
    }
  val Block.deleteCmd: Command
    get() = command(item = delete) {
      deleteBlock()
    }

  val Block.insertLineCmd: Command
    get() = command(item = insertLine) {
      insertLine()
    }

  val Block.showHideFilterCmd: Command
    get() = command(item = showHideFilter) {
      showHideFilter()
    }

  // ------------------------------------------------------------------
  // BLOCK-LEVEL ACTORS
  // ------------------------------------------------------------------

  inner class Quit : Actor(menu = FileMenu(), label = "Quit", help = "Close this form.") {
    init {
      key = Key.ESCAPE
      icon = Icon.QUIT
    }
  }

  class Break : Actor(menu = FileMenu(), label = "Break", help = "Reset current changes.") {
    init {
      key = Key.F3
      icon = Icon.BREAK
    }
  }

  class Validate : Actor(menu = FileMenu(), label = "Validate", help = "Validate form informations.") {
    init {
      key = Key.F8
      icon = Icon.VALIDATE
    }
  }

  class Print : Actor(menu = FileMenu(), label = "Print", help = "Print report.") {
    init {
      key = Key.F6
      icon = Icon.PRINT
    }
  }

  class PrintLabel : Actor(menu = FileMenu(), label = "Label", help = "Print labels.") {
    init {
      key = Key.F6
      icon = Icon.PRINT
    }
  }

  class Preview : Actor(menu = FileMenu(), label = "Preview", help = "Show report preview.") {
    init {
      key = Key.SHIFT_F6
      icon = Icon.PREVIEW
    }
  }

  class Mail : Actor(menu = FileMenu(), label = "e-mail", help = "Send document via e-mail.") {
    init {
      key = Key.F9
      icon = Icon.MAIL
    }
  }

 inner class Autofill : DefaultActor(
    menu = EditMenu(), label = "Standard", help = "List possible values.", command = PredefinedCommand.AUTOFILL) {
    init {
      key = Key.F2
    }
  }

  class NewItem : DefaultActor(
    menu = EditMenu(), label = "New", help = "Add new element.", command = PredefinedCommand.NEW_ITEM
  ) {
    init {
      key = Key.SHIFT_F4
    }
  }

  class EditItem : DefaultActor(
    menu = EditMenu(), label = "Edit", help = "Edit selected element.", command = PredefinedCommand.EDIT_ITEM
  ) {
    init {
      key = Key.SHIFT_F2
    }
  }

  class EditItem_S : DefaultActor(
    menu = EditMenu(), label = "Edit", help = "Edit selected element.", command = PredefinedCommand.EDIT_ITEM_SHORTCUT
  ) {
    init {
      key = Key.F2
    }
  }

  class SearchOperator : Actor(menu = EditMenu(), label = "Condition", help = "Change search operator.") {
    init {
      key = Key.F5
      icon = Icon.SEARCH_OP
    }
  }

  class ChangeBlock : Actor(menu = EditMenu(), label = "Block", help = "Moves cursor to another block.") {
    init {
      key = Key.F8
      icon = Icon.BLOCK
    }
  }

  class CopyDocument :
    Actor(menu = EditMenu(), label = "Copy", help = "Provide a copy of the currently called document.") {
    init {
      key = Key.F4
      icon = Icon.COPY
    }
  }

  class InsertLine : Actor(menu = EditMenu(), label = "Line +", help = "Insert a new line in block.") {
    init {
      key = Key.F4
      icon = Icon.INSERT_LINE
    }
  }

  class DeleteLine : Actor(menu = EditMenu(), label = "Line -", help = "Delete selected line.") {
    init {
      key = Key.F5
      icon = Icon.DELETE_LINE
    }
  }

  class All : Actor(menu = EditMenu(), label = "All", help = "Select all.") {
    init {
      key = Key.F4
      icon = Icon.ALL
    }
  }

  class Nothing : Actor(menu = EditMenu(), label = "Nothing", help = "Select nothing.") {
    init {
      key = Key.F5
      icon = Icon.NOTHING
    }
  }

  class MenuQuery : Actor(menu = ActionMenu(), label = "List", help = "Query: display results in a list.") {
    init {
      key = Key.F8
      icon = Icon.MENU_QUERY
    }
  }

  class SerialQuery : Actor(menu = ActionMenu(), label = "Query", help = "Load data considering the filled fields.") {
    init {
      key = Key.F6
      icon = Icon.SERIAL_QUERY
    }
  }

  class InsertMode : Actor(menu = ActionMenu(), label = "New", help = "Create a new record.") {
    init {
      key = Key.F4
      icon = Icon.INSERT
    }
  }

  class Save : Actor(menu = ActionMenu(), label = "Save", help = "Save changes to database.") {
    init {
      key = Key.F7
      icon = Icon.SAVE
    }
  }

  class Delete : Actor(menu = ActionMenu(), label = "Delete", help = "Delete selected record.") {
    init {
      key = Key.F5
      icon = Icon.DELETE
    }
  }

  class CreateReport : Actor(menu = ActionMenu(), label = "Report", help = "Create report.") {
    init {
      key = Key.F8
      icon = Icon.REPORT
    }
  }

  class CreateDynamicReport : Actor(menu = ActionMenu(), label = "Dyn. Report", help = "Create dynamic report.") {
    init {
      key = Key.F11
      icon = Icon.PREVIEW
    }
  }

  class ShowHideFilter : Actor(menu = ActionMenu(), label = "Show/Hide filter", help = "Show or hide block filters.") {
    init {
      key = Key.SHIFT_F12
      icon = Icon.SEARCH_OP
    }
  }

  class Help : Actor(menu = HelpMenu(), label = "Help", help = "Show help for a selected field.") {
    init {
      key = Key.F1
      icon = Icon.HELP
    }
  }

  // menu classes

  class FileMenu : Menu("File")

  class EditMenu : Menu("Edit")

  class ActionMenu : Menu("Action")

  class HelpMenu : Menu("Help")
}
