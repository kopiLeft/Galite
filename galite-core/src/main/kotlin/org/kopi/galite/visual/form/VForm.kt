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

import java.io.File
import java.lang.Error
import java.net.MalformedURLException
import java.sql.SQLException

import javax.swing.event.EventListenerList

import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.util.PrintJob
import org.kopi.galite.visual.util.base.InconsistencyException
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.Constants
import org.kopi.galite.visual.DefaultActor
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.UIFactory
import org.kopi.galite.visual.UWindow
import org.kopi.galite.visual.VActor
import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VExecFailedException
import org.kopi.galite.visual.VHelpViewer
import org.kopi.galite.visual.VWindow
import org.kopi.galite.visual.WindowBuilder
import org.kopi.galite.visual.WindowController

abstract class VForm protected constructor(source: String? = null) : VWindow(source), VConstants {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  // static (from DSL) data
  val blocks = mutableListOf<VBlock>()
  val pages = mutableListOf<String>()
  val VKT_Triggers = mutableListOf(arrayOfNulls<Trigger>(VConstants.TRG_TYPES.size))

  // dynamic data
  private val blockMoveAllowed = true
  private var activeBlock: VBlock? = null

  private val formListener = EventListenerList()

  // ----------------------------------------------------------------------
  // SHARED DATA MEMBERS
  // ----------------------------------------------------------------------
  private var autofillActor: VActor? = null
  private var editItemActor: VActor? = null
  private var editItemActor_S: VActor? = null
  private var newItemActor: VActor? = null

  // ---------------------------------------------------------------------
  // PREDEFINED COMMANDS
  // ---------------------------------------------------------------------
  val cmdAutofill:    VCommand = VFieldCommand(this, CMD_AUTOFILL)
  val cmdEditItem_S:  VCommand = VFieldCommand(this, CMD_EDITITEM_S)
  val cmdEditItem:    VCommand = VFieldCommand(this, CMD_EDITITEM)
  val cmdNewItem:     VCommand = VFieldCommand(this, CMD_NEWITEM)

  companion object {
    const val CMD_NEWITEM = -2
    const val CMD_EDITITEM = -3
    const val CMD_EDITITEM_S = -4
    const val CMD_AUTOFILL = -5

    init {
      WindowController.windowController.registerWindowBuilder(Constants.MDL_FORM, object : WindowBuilder {
        override fun createWindow(model: VWindow): UWindow {
          return UIFactory.uiFactory.createView(model) as UForm
        }
      })
    }
  }

  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------

  init {
    localize()
  }

  /**
   * loads the form
   */
  protected fun initIntern() {
    if (!ApplicationContext.isGeneratingHelp()) {
      initialise()
      callTrigger(VConstants.TRG_PREFORM)
    }
  }

  override fun getType(): Int = Constants.MDL_FORM

  @Deprecated("")
  open fun getCaller(): VForm? = null

  /**
   * Returns true if it is allowed to quit this model
   * (the form for this model)
   */
  override fun allowQuit(): Boolean {
    var allowed: Boolean
    try {
      allowed = if (hasTrigger(VConstants.TRG_QUITFORM)) {
        callTrigger(VConstants.TRG_QUITFORM) as Boolean
      } else {
        super.allowQuit()
      }
    } catch (e: VException) {
      allowed = false
      error(e.message)
    }
    return allowed
  }

  /**
   * close model if allowed
   */
  override fun willClose(code: Int) {
    Commands.quitForm(this, CDE_ESCAPED)
  }

  /**
   * close the form
   */
  override fun destroyModel() {
    try {
      if (activeBlock != null) {
        // !! lackner 2003.07.31
        // why a close before leave???
        // must be before close otherwise NullPointerException
        activeBlock!!.close()
        activeBlock!!.leave(false)
      }
      callTrigger(VConstants.TRG_POSTFORM)
    } catch (e: VException) {
      throw InconsistencyException(e)
    }
    // do not set to null because the values (of
    // fields in the block) are still used
    //    blocks = null;
    super.destroyModel()
  }

  /**
   * implemented for compatibility with old gui
   */
  @Deprecated("")
  fun executeAfterStart() {
    //do nothing
    // overridden in Buchen.vf in fibu
  }

  /**
   * implemented for compatibility with old gui
   * used in tib/Artikel.vf
   */
  @Deprecated("")
  fun checkUI() {
    // checkUI does now nothing
    // not useful to call
  }

  override fun enableCommands() {
    super.enableCommands()
    // Form-level commands are always enabled
    commands.forEach { command ->
      command.setEnabled(true)
    }
  }

  /**
   * Adds and localizes a block to this form.
   *
   * @param block the block to add.
   */
  fun addBlock(block: VBlock) {
    blocks.add(block)
    block.localize(manager, locale)
    addActors(block.actors)
  }

  /**
   * addCommand in menu
   */
  override fun addActors(actorDefs: Array<VActor>?) {
    actorDefs?.forEach { actor ->
      if (actor is DefaultActor) {
        when (actor.code) {
          CMD_AUTOFILL -> autofillActor = actor
          CMD_EDITITEM -> editItemActor = actor
          CMD_EDITITEM_S -> editItemActor_S = actor
          CMD_NEWITEM -> newItemActor = actor
        }
      }
    }
    super.addActors(actorDefs)
  }

  fun setTextOnFieldLeave(): Boolean = false

  fun forceCheckList(): Boolean = true
  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  /**
   * Localize this form
   *
   */
  fun localize() {
    localize(manager)
  }

  /**
   * Localizes this form
   *
   * @param     manager         the manger to use for localization
   */
  private fun localize(manager: LocalizationManager) {
    if(ApplicationContext.getDefaultLocale() != locale) {
      val loc = manager.getFormLocalizer(source)

      setTitle(loc.getTitle())
      for (i in pages.indices) {
        pages[i] = loc.getPage(i)
      }
    }
  }

  // ----------------------------------------------------------------------
  // DISPLAY INTERFACE
  // ----------------------------------------------------------------------
  open fun prepareForm() {
    initIntern()
    val block: VBlock? = getActiveBlock()

    block?.leave(false)
    if (block != null) {
      block.enter()
    } else {
      // INSERTED to have an a correct state of this form
      enterBlock()
    }
    setCommandsEnabled(true)
  }

  /**
   * Informs model, that this action was executed on it.
   * For cleanUp/Update/....
   * -) THIS method should do as less as possible
   * -) THIS method should need be used to fix the model
   */
  override fun executedAction(action: Action?) {
    checkForm(action)
  }

  private fun checkForm(action: Action?) {
    // !!! fixes model (if left in a bad state)
    if (activeBlock == null) {
      var i = 0
      while (i < blocks.size) {
        if (blocks[i].isAccessible) {
          break
        }
        i++
      }
      assert(i < blocks.size) { threadInfo() + "No accessible block" }
      blocks[i].enter()
      // lackner 2003.07.31
      // - inserted to get information about the usage of this code
      // - can be removed if the method checkUI is removed
      try {
        ApplicationContext.reportTrouble("DForm checkUI " + Thread.currentThread(),
                                         "Where is this code used? $action",
                                         this.toString(),
                                         RuntimeException("CHECKUI: Entered  block " + blocks[i].name))
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
    // !! end of model fix
    for (i in blocks.indices) {
      blocks[i].checkBlock()
      blocks[i].updateBlockAccess()
      //      blocks[i].checkCommands();
    }
  }
  // ----------------------------------------------------------------------
  // Navigation
  // ----------------------------------------------------------------------
  /**
   * GOTO PAGE X
   * @exception        org.kopi.galite.visual.VException        an exception may be raised by field.leave
   */
  fun gotoPage(target: Int) {
    var block: VBlock? = null
    var i = 0
    while (block == null && i < blocks.size) {
      if (blocks[i].pageNumber == target && blocks[i].isAccessible) {
        block = blocks[i]
      }
      i++
    }
    if (block == null) {
      throw VExecFailedException(MessageCode.getMessage("VIS-00024"))
    }
    gotoBlock(block)
  }

  /**
   * GOTO BLOCK
   * @exception        org.kopi.galite.visual.VException        an exception may be raised by field.leave
   */
  fun gotoBlock(target: VBlock) {
    activeBlock?.leave(true)

    target.enter()
  }

  /**
   * Go to the next block
   * @exception        org.kopi.galite.visual.VException        an exception may be raised by field.leave
   */
  fun gotoNextBlock() {
    assert(activeBlock != null) { threadInfo() + "Active block is null" }
    if (!blockMoveAllowed) {
      return
    }
    var index = getBlockIndex(activeBlock!!)
    var target: VBlock? = null
    var i = 0
    while (target == null && i < blocks.size - 1) {
      index += 1
      if (index == blocks.size) {
        index = 0
      }
      if (blocks[index].isAccessible) {
        target = blocks[index]
      }
      i += 1
    }
    if (target == null) {
      throw VExecFailedException(MessageCode.getMessage("VIS-00025"))
    }
    gotoBlock(target)
  }

  fun enterBlock() {
    assert(activeBlock == null) { "active block = $activeBlock" }
    var i = 0
    while (i < blocks.size) {
      if (blocks[i].isAccessible) {
        break
      }
      i++
    }
    assert(i < blocks.size) { threadInfo() + "no accessible block" }
    gotoBlock(blocks[i])
  }

  /**
   * Returns true if the form contents have been changed by the user.
   *
   * NOTE: TRG_CHANGED returns true if form is considered changed
   */
  fun isChanged(): Boolean {
    return if (hasTrigger(VConstants.TRG_CHANGED)) {
      val res = try {
        callTrigger(VConstants.TRG_CHANGED)
      } catch (e: VException) {
        throw InconsistencyException()
      }
      res as Boolean
    } else {
      blocks.forEach { block ->
        if (block.isChanged) {
          return true
        }
      }
      false
    }
  }

  /**
   * Resets form to initial state
   *
   * NOTE: TRG_RESET returns true if reset handled by trigger
   * @exception        org.kopi.galite.visual.VException        an exception may be raised by field.leave
   */
  override fun reset() {
    if (hasTrigger(VConstants.TRG_RESET)) {
      val res = try {
        callTrigger(VConstants.TRG_RESET)
      } catch (e: VException) {
        e.printStackTrace()
        throw InconsistencyException()
      }
      if (res as Boolean) {
        return
      }
    }

    for (i in blocks.indices) {
      blocks[i].clear()
      blocks[i].setMode(VConstants.MOD_QUERY) // vincent 14.9.98
    }

    activeBlock?.leave(false)

    initialise()
    if (activeBlock == null) {
      // it is possible, that the INIT-Trigger of the form
      // has a gotoBlock(...)
      enterBlock()
    }
  }

  /**
   * create a list of items and return id of selected one or -1
   * @param        showUniqueItem        open a list if there is only one item also
   * @exception        org.kopi.galite.visual.VException        an exception may be raised by string formatters
   */
  fun singleMenuQuery(parent: VWindow, showUniqueItem: Boolean): Int {
    return getBlock(0).singleMenuQuery(showUniqueItem)
  }
  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------
  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected fun callTrigger(event: Int, index: Int = 0): Any? {
    return when (VConstants.TRG_TYPES[event]) {
      VConstants.TRG_VOID -> {
        executeVoidTrigger(VKT_Triggers[index][event])
        null
      }
      VConstants.TRG_BOOLEAN -> executeBooleanTrigger(VKT_Triggers[index][event])
      VConstants.TRG_INT -> executeIntegerTrigger(VKT_Triggers[index][event])
      else -> executeObjectTrigger(VKT_Triggers[index][event])
    }
  }

  /**
   * @return If there is trigger associated with event
   */
  protected fun hasTrigger(event: Int, index: Int = 0): Boolean {
    return VKT_Triggers[index][event] != null
  }

  override fun executeVoidTrigger(trigger: Trigger?) {
    trigger?.action?.method?.invoke()
    super.executeVoidTrigger(trigger)
  }

  @Suppress("UNCHECKED_CAST")
  fun executeObjectTrigger(trigger: Trigger?): Any {
    return (trigger?.action?.method as () -> Any).invoke()
  }

  @Suppress("UNCHECKED_CAST")
  fun executeBooleanTrigger(trigger: Trigger?): Boolean {
    return (trigger?.action?.method as () -> Boolean).invoke()
  }

  @Suppress("UNCHECKED_CAST")
  fun executeIntegerTrigger(trigger: Trigger?): Int {
    return (trigger?.action?.method as () -> Int).invoke()
  }

  // ----------------------------------------------------------------------
  // TRAILING
  // ----------------------------------------------------------------------
  /**
   * Sets form untrailed (commits changes).
   */
  open fun commitTrail() {
    for (i in blocks.indices) {
      blocks[i].commitTrail()
    }
  }

  /**
   * Restore trailed information.
   */
  open fun abortTrail() {
    for (i in blocks.indices) {
      blocks[i].abortTrail()
    }
  }

  /**
   * Handles transaction failure
   */
  fun handleAborted() {
    abortTrail()
  }

  /**
   * Handles transaction failure
   * @param        reason                the reason for the failure.
   * @exception        SQLException        an exception may be raised by DB
   */
  open fun handleAborted(reason: SQLException) {
    try {
      abortTrail()
    } finally {
      if (!retryableAbort(reason) || !retryProtected()) {
        throw reason
      }
    }
  }

  open fun handleAborted(reason: Error) {
    abortTrail()
    throw reason
  }

  open fun handleAborted(reason: java.lang.RuntimeException) {
    abortTrail()
    throw reason
  }

  open fun handleAborted(reason: VException) {
    abortTrail()
    throw reason
  }

  // ----------------------------------------------------------------------
  // UTILS
  // ----------------------------------------------------------------------
  /**
   * Gets the form (this)
   */
  fun getForm(): VForm = this

  fun fireCurrentBlockChanged(oldBlock: VBlock?, newBlock: VBlock?) {
    val listeners = formListener.listenerList

    for (i in listeners.size - 2 downTo 0 step 2) {
      if (listeners[i] === FormListener::class.java) {
        (listeners[i + 1] as FormListener).currentBlockChanged(oldBlock, newBlock)
      }
    }
  }

  /**
   * setBlockRecords
   * inform user about nb records fetched and current one
   */
  fun setFieldSearchOperator(op: Int) {
    val listeners = formListener.listenerList

    for (i in listeners.size - 2 downTo 0 step 2) {
      if (listeners[i] === FormListener::class.java) {
        (listeners[i + 1] as FormListener).setFieldSearchOperator(op)
      }
    }
  }

  /**
   * Returns the number of blocks.
   */
  fun getBlockCount(): Int = blocks.size

  /**
   * Returns the block with given index.
   * @param        index                the index of the specified block
   */
  fun getBlock(index: Int): VBlock {
    return blocks[index]
  }

  /**
   * return a Block from its name
   * @param        name                name of the block
   * @return    the first block with this name, or null if the block is not found.
   */
  fun getBlock(name: String): VBlock? {
    for (i in blocks.indices) {
      if (name == blocks[i].name) {
        return blocks[i]
      }
    }
    return null
  }

  /**
   * Returns the current block
   */
  fun getActiveBlock(): VBlock? {
    return activeBlock
  }

  /**
   * Sets the current block
   */
  fun setActiveBlock(block: VBlock?) {
    val old: VBlock? = activeBlock
    activeBlock = block
    // inform listener
    fireCurrentBlockChanged(old, activeBlock)
  }

  /**
   * Launch file preview
   */
  fun documentPreview(file: String) {
    (getDisplay() as UForm).launchDocumentPreview(file)
  }

  // ----------------------------------------------------------------------
  // LISTENER
  // ----------------------------------------------------------------------
  fun addFormListener(bl: FormListener?) {
    formListener.add(FormListener::class.java, bl)
  }

  fun removeFormListener(bl: FormListener?) {
    formListener.remove(FormListener::class.java, bl)
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------
  /*
   * Initialises the form.
   */
  protected fun initialise() {
    callTrigger(VConstants.TRG_INIT)
    for (i in blocks.indices) {
      blocks[i].initialise()
    }
  }

  /**
   * Returns the index of the specified block in the form
   */
  protected fun getBlockIndex(blk: VBlock): Int {
    for (i in blocks.indices) {
      if (blk === blocks[i]) {
        return i
      }
    }
    throw InconsistencyException()
  }

  protected abstract fun formClassName(): String

  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------
  /**
   *
   */
  fun getName(): String {
    val name: String = formClassName()
    val index = name.lastIndexOf(".")
    return if (index == -1) name else name.substring(index + 1)
  }

  fun genHelp(p: LatexPrintWriter,
              name: String,
              help: String,
              code: String) {
    for (i in blocks.indices) {
      addActors(blocks[i].actors)
    }
    VDocGenerator(p).helpOnForm(getName(),
                                commands,
                                blocks,
                                name,
                                help,
                                code)
  }

  fun genHelp(): String? {

    var description = getName()
    var localHelp: String? = null
    val surl = StringBuffer()
    val module = try {
      ApplicationContext.getMenu()!!.getModule(formClassName())
    } catch (npe: NullPointerException) {
      null
    }
    if (module != null) {
      description = module.description
      localHelp = module.help
    }
    val fileName = VHelpGenerator().helpOnForm(getName(),
                                               commands,
                                               blocks,
                                               description,
                                               localHelp,
                                               "")
    return if (fileName == null) {
      null
    } else {
      try {
        surl.append(File(fileName).toURI().toURL().toString())
      } catch (mue: MalformedURLException) {
        throw InconsistencyException(mue)
      }
      val field = getActiveBlock()!!.activeField
      if (field != null) {
        var anchor = field.label
        if (anchor == null) {
          anchor = field.name
        }
        anchor.replace(' ', '_')
        surl.append("#" + field.block!!.title.replace(' ', '_') + anchor)
      }
      surl.toString()
    }
  }

  fun showHelp() {
    VHelpViewer().showHelp(genHelp())
  }

  fun getDefaultActor(type: Int): VActor? {
    when (type) {
      CMD_NEWITEM -> return newItemActor
      CMD_EDITITEM -> return editItemActor
      CMD_EDITITEM_S -> return if (editItemActor_S == null) autofillActor else editItemActor_S
      CMD_AUTOFILL -> return autofillActor
    }
    throw InconsistencyException("UNDEFINED ACTOR: $type")
  }

  /**
   * Enables the active commands or disable all commands.
   */
  override fun setCommandsEnabled(enable: Boolean) {
    var enable = enable
    super.setCommandsEnabled(enable)
    // block-level commands
    blocks.forEach { block ->
      if (!enable || block == activeBlock) {
        // disable all commands
        // enable only the command of the currentblock
        block.setCommandsEnabled(enable)
      }
    }
    // form-level commands
    commands.forEachIndexed { index, command ->
      if (command.trigger != -1) {
        if (enable && hasTrigger(VConstants.TRG_CMDACCESS, index + 1)) {
          val active: Boolean = try {
            callTrigger(VConstants.TRG_CMDACCESS, index + 1) as Boolean
          } catch (e: VException) {
            // consider the command as active if trigger call fails.
            true
          }
          // The command is enabled if its access trigger returns <code>true</true>
          enable = active
        }
      }
      command.setEnabled(enable)
    }
  }

  override fun toString(): String = buildString {
    try {
      append("\n===========================================================\nFORM: ")
      append(super.toString())
      append(" ")
      append(getName())
      append("\n")

      append("activeBlock: ")
      if (activeBlock == null) {
        append("null")
      } else {
        append(activeBlock!!.name)
      }
      append("\n")

      // support better message
      blocks.forEach { block ->
        append(block.toString())
      }
    } catch (e: Exception) {
      append("exception while retrieving form information. \n")
    }
    append("===========================================================\n")
  }

  fun printFormScreen(): PrintJob? {
    return (getDisplay() as UForm).printForm()
  }
}
