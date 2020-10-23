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

package org.kopi.galite.form

import java.sql.SQLException
import javax.swing.event.EventListenerList

import org.kopi.galite.db.DBContext
import org.kopi.galite.db.DBContextHandler
import org.kopi.galite.form.VConstants.Companion.TRG_CHANGED
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.util.PrintJob
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.VActor
import org.kopi.galite.visual.VCommand
import org.kopi.galite.visual.VException
import org.kopi.galite.visual.VWindow
import java.util.Locale

abstract class VForm : VWindow, VConstants {
  companion object {
    const val CMD_NEWITEM = -2
    const val CMD_EDITITEM = -3
    const val CMD_EDITITEM_S = -4
    const val CMD_AUTOFILL = -5
    
  }
  // ----------------------------------------------------------------------
  // CONSTRUCTOR
  // ----------------------------------------------------------------------
  /**
   * Constructor
   */
  protected constructor(ctxt: DBContextHandler) : super(ctxt) {
    TODO()
  }

  /**
   * Constructor
   */
  protected constructor(ctxt: DBContext) : super(ctxt) {
    TODO()
  }

  /**
   * Constructor
   */
  protected constructor() {
    initIntern(false)
  }

  /**
   * load form
   */
  private fun initIntern(enterField: Boolean) {
    TODO()
  }

  /**
   * load form (from compiler)
   */
  protected fun init() {}
  override fun getType(): Int {
    TODO()
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  @get:Deprecated("")
  val caller: VForm?
    get() = null

  /**
   * Returns true if it is allowed to quit this model
   * (the form for this model)
   */
  override fun allowQuit(): Boolean {
    TODO()
  }

  /**
   * close model if allowed
   */
  override fun willClose(code: Int) {
    TODO()
  }

  /**
   * close the form
   */
  override fun destroyModel() {
    TODO()
  }

  /**
   * implemented for compatiblity with old gui
   */
  @Deprecated("")
  fun executeAfterStart() {
    //do nothing
    // overrriden in Buchen.vf in fibu
  }

  /**
   * implemented for compatiblity with old gui
   * used in tib/Artikel.vf
   */
  @Deprecated("")
  fun checkUI() {
    // checkUI does now nothing
    // not useful to call
  }

  override fun enableCommands() {
    TODO()
  }

  fun setTextOnFieldLeave(): Boolean {
    return false
  }

  fun forceCheckList(): Boolean {
    return true
  }
  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  /**
   * Localize this form
   *
   * @param     locale  the locale to use
   */
  fun localize(locale: Locale?) {
    TODO()
  }

  /**
   * Localizes this form
   *
   * @param     manager         the manger to use for localization
   */
  private fun localize(manager: LocalizationManager) {
    TODO()
  }

  // ----------------------------------------------------------------------
  // DISPLAY INTERFACE
  // ----------------------------------------------------------------------
  private fun initActors() {
    TODO()
  }

  fun prepareForm() {
    val block = getActiveBlock()
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
  override fun executedAction(action: Action) {
    checkForm(action)
  }

  private fun checkForm(action: Action) {
    TODO()
  }
  // ----------------------------------------------------------------------
  // Navigation
  // ----------------------------------------------------------------------
  /**
   * GOTO PAGE X
   * @exception        org.kopi.vkopi.lib.visual.VException        an exception may be raised by field.leave
   */
  fun gotoPage(target: Int) {
    TODO()
  }

  /**
   * GOTO BLOCK
   * @exception        org.kopi.vkopi.lib.visual.VException        an exception may be raised by field.leave
   */
  fun gotoBlock(target: VBlock) {
    if (activeBlock != null) {
      activeBlock!!.leave(true)
    }
    target.enter()
  }

  /**
   * Go to the next block
   * @exception        org.kopi.vkopi.lib.visual.VException        an exception may be raised by field.leave
   */
  fun gotoNextBlock() {
    TODO()
  }

  fun enterBlock() {
    TODO()
  }

  /**
   * Returns true iff the form contents have been changed by the user.
   *
   * NOTE: TRG_CHANGED returns true iff form is considered changed
   */
  val isChanged: Boolean
    get() = if (hasTrigger(TRG_CHANGED)) {
      TODO()
    } else {
      TODO()
    }

  /**
   * Resets form to initial state
   *
   * NOTE: TRG_RESET returns true iff reset handled by trigger
   * @exception        org.kopi.vkopi.lib.visual.VException        an exception may be raised by field.leave
   */
  override fun reset() {
    TODO()
  }

  /**
   * create a list of items and return id of selected one or -1
   * @param        showUniqueItem        open a list if there is only one item also
   * @exception        org.kopi.vkopi.lib.visual.VException        an exception may be raised by string formaters
   */
  fun singleMenuQuery(parent: VWindow, showUniqueItem: Boolean): Int {
    TODO()
  }
  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------
  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  /**
   * Calls trigger for given event, returns last trigger called 's value.
   */
  protected fun callTrigger(event: Int, index: Int = 0): Any? {
    TODO()
  }
  /**
   * @return If there is trigger associated with event
   */
  /**
   * @return If there is trigger associated with event
   */
  protected fun hasTrigger(event: Int, index: Int = 0): Boolean {
    return VKT_Triggers[index][event] != 0
  }

  fun executeObjectTrigger(VKT_Type: Int): Any {
    TODO()
  }

  fun executeBooleanTrigger(VKT_Type: Int): Boolean {
    TODO()
  }

  fun executeIntegerTrigger(VKT_Type: Int): Int {
    TODO()
  }
  // ----------------------------------------------------------------------
  // TRAILING
  // ----------------------------------------------------------------------
  /**
   * Sets form untrailed (commits changes).
   */
  fun commitTrail() {
    for (i in blocks!!.indices) {
      blocks!![i].commitTrail()
    }
  }

  /**
   * Restore trailed information.
   */
  fun abortTrail() {
    for (i in blocks!!.indices) {
      blocks!![i].abortTrail()
    }
  }

  /**
   * Commits a protected transaction.
   * @exception        Exception        an exception may be raised by DB
   */
  fun commitProtected() {
    TODO()
  }

  /**
   * Handles transaction failure
   * @param        interrupt        interrupt transaction
   */
  fun abortProtected(interrupt: Boolean) {
    TODO()
  }

  /**
   * Handles transaction failure
   * @param        reason                the reason for the failure.
   * @exception        SQLException        an exception may be raised by DB
   */
  fun abortProtected(reason: SQLException?) {
    TODO()
  }

  fun abortProtected(reason: Error) {
    TODO()
  }

  fun abortProtected(reason: RuntimeException?) {
    TODO()
  }

  fun abortProtected(reason: VException) {
    TODO()
  }
  /**
   * Handles transaction failure
   * @param        reason                the reason for the failure.
   * @exception        org.kopi.vkopi.lib.visual.VException        an exception may be raised by DB
   */
  /* old version
  public void abortProtected(Exception reason) throws VException {
    try {
      abortTrail();
    } finally {
      super.abortProtected(reason);
    }
    }*/
  // ----------------------------------------------------------------------
  // UTILS
  // ----------------------------------------------------------------------
  /**
   * Gets the form (this)
   */
  val form: VForm
    get() = this

  fun fireCurrentBlockChanged(oldBlock: VBlock?, newBlock: VBlock?) {
    val listeners = formListener.listenerList
    var i = listeners.size - 2
    while (i >= 0) {
      if (listeners[i] === FormListener::class.java) {
        (listeners[i + 1] as FormListener).currentBlockChanged(oldBlock!!, newBlock!!)
      }
      i -= 2
    }
  }

  /**
   * setBlockRecords
   * inform user about nb records fetched and current one
   */
  fun setFieldSearchOperator(op: Int) {
    val listeners = formListener.listenerList
    var i = listeners.size - 2
    while (i >= 0) {
      if (listeners[i] === FormListener::class.java) {
        (listeners[i + 1] as FormListener).setFieldSearchOperator(op)
      }
      i -= 2
    }
  }

  /**
   * Returns the number of blocks.
   */
  fun getBlockCount(): Int {
    TODO()
  }

  /**
   * Returns the block with given index.
   * @param        index                the index of the specified block
   */
  fun getBlock(index: Int): VBlock {
    return blocks!![index]
  }

  /**
   * return a Block from its name
   * @param        name                name of the block
   * @return    the first block with this name, or null if the block is not found.
   */
  fun getBlock(name: String): VBlock? {
    TODO()
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
    val old = activeBlock
    activeBlock = block
    // inform listener
    fireCurrentBlockChanged(old, activeBlock)
  }

  /**
   * Launch file preview
   */
  fun documentPreview(file: String?) {
    (getDisplay() as UForm?)!!.launchDocumentPreview(file!!)
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
    TODO()
  }

  /**
   * Returns the index of the specified block in the form
   */
  protected fun getBlockIndex(blk: VBlock?): Int {
    TODO()
  }
  // ----------------------------------------------------------------------
  // HELP HANDLING
  // ----------------------------------------------------------------------
  /**
   *
   */
  fun getName(): String {
    TODO()
  }

  fun genHelp(p: LatexPrintWriter?,
              name: String?,
              help: String?,
              code: String?) {
    TODO()
  }

  fun genHelp(): String? {
    TODO()
  }

  fun showHelp(form: VForm?) {
    TODO()
  }

  /*package*/
  fun getDefaultActor(type: Int): VActor? {
    TODO()
  }

  /**
   * Enables the active commands or disable all commands.
   */
  override fun setCommandsEnabled(enable: Boolean) {
    TODO()
  }

  override fun toString(): String {
    TODO()
  }

  fun printFormScreen(): PrintJob {
    TODO()
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  // static (compiled) data
  override var source // qualified name of source file
          : String? = null
  var blocks: Array<VBlock>? = null
  lateinit var pages: Array<String>
    protected set

  /**
   * return the name of this field
   */
  var help: String? = null
    protected set
  protected lateinit var VKT_Triggers: Array<IntArray>

  // dynamic data
  private val blockMoveAllowed = true

  //  private VForm		caller;
  private var activeBlock: VBlock? = null

  //  private int		currentPage = -1;
  protected var commands : Array<VCommand>? = null
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
  //
  /*package*/
  val cmdAutofill: VCommand = VFieldCommand(this, CMD_AUTOFILL)

  /*package*/
  val cmdEditItem_S: VCommand = VFieldCommand(this, CMD_EDITITEM_S)

  /*package*/
  val cmdEditItem: VCommand = VFieldCommand(this, CMD_EDITITEM)

  /*package*/
  val cmdNewItem: VCommand = VFieldCommand(this, CMD_NEWITEM)
}
