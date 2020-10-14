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

package org.kopi.galite.visual

import java.awt.Frame
import java.awt.event.KeyEvent
import java.io.File

import javax.swing.event.EventListenerList

import org.kopi.galite.base.Image
import org.kopi.galite.base.UComponent
import org.kopi.galite.db.DBContext
import org.kopi.galite.db.DBDeadLockException
import org.kopi.galite.db.XInterruptProtectedException
import org.kopi.galite.l10n.LocalizationManager

/**
 * Creates a window
 *
 * @param dBContext The database context for this object.
 * if if is specified, it will create a window with a DB context
 */
abstract class VWindow(override open var dBContext: DBContext = ApplicationContext.getDBContext()
) : Executable, ActionHandler, VModel {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private val modelListener = EventListenerList()
  private var extraTitle: String? = null
  private var display: UWindow? = null
  private var actors: ArrayList<VActor> = arrayListOf()
  protected lateinit var windowTitle: String
  protected var smallIcon: Image? = null
  protected var isProtected = false
  protected var listenerList = EventListenerList() // List of listeners
  protected val f12: VActor
  open val source: String? = null // The localization source of this window.

  init {
    f12 = VActor("File",
            WINDOW_LOCALIZATION_RESOURCE,
            "GotoShortcuts",
            WINDOW_LOCALIZATION_RESOURCE,
            null,
            KeyEvent.VK_F12,
            0)
    f12.number = Constants.CMD_GOTO_SHORTCUTS
    f12.handler = this
    setActors(arrayOf(f12))
  }

  // ----------------------------------------------------------------------
  // DISPLAY INTERFACE
  // ----------------------------------------------------------------------
  /**
   * doModal
   * modal call to this form
   * @exception        VException        an exception may be raised by triggers
   */
  fun doModal(frame: Frame): Boolean = WindowController.windowController.doModal(this)

  /**
   * doModal
   * modal call to this form
   * @exception        VException        an exception may be raised by triggers
   */
  fun doModal(f: VWindow): Boolean = WindowController.windowController.doModal(this)

  /**
   * doModal
   * modal call to this form
   * @exception        VException        an exception may be raised by triggers
   */
  fun doModal(): Boolean = WindowController.windowController.doModal(this)

  /**
   * doNotModal
   * no modal call to this form
   * @exception        VException        an exception may be raised by triggers
   */
  override fun doNotModal() = WindowController.windowController.doNotModal(this)

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------
  /**
   * Returns true if asynchronous operations can be performed
   * Override to change behavior
   */
  fun allowAsynchronousOperation(): Boolean = true

  /**
   * Resets form to initial state
   */
  fun reset() {
    // do nothing

    // TODO set it abstract
  }

  /**
   * Returns true if it is allowed to quit this model
   * (the form for this model)
   */
  fun allowQuit(): Boolean = !inTransaction()

  /**
   * Destroy this class (break all references to help java to GC the form)
   */
  open fun destroyModel() {}

  /**
   * Informs model, that this action was executed on it.
   * For cleanUp/Update/....
   * -) THIS method should do as less as possible
   * -) THIS method should need be used to fix the model
   */
  fun executedAction(action: Action) {
    // overrriden in VForm
    // nothing to do here
  }

  @Deprecated("use method performAsynAction",
          ReplaceWith("performAsyncAction(action)"))
  override fun performAction(action: Action, block: Boolean) {
    performAsyncAction(action)
  }

  override fun performAsyncAction(action: Action) {
    val listeners = modelListener.listenerList

    for (i in listeners.size - 2 downTo 0 step 2) {
      if (listeners[i] == VActionListener::class.java) {
        (listeners[i + 1] as VActionListener).performAsyncAction(action)
      }
    }
  }

  // ----------------------------------------------------------------------
  // INFORMATION HANDLING
  // ----------------------------------------------------------------------
  fun notice(message: String) {
    var send = false
    val listeners = modelListener.listenerList
    for (i in listeners.size - 2 downTo 0 step 2) {
      if (listeners[i] == MessageListener::class.java) {
        (listeners[i + 1] as MessageListener).notice(message)
        send = true
      }
    }
    if (!send) {
      // use a 'default listener' that the message is
      // not lost (e.g .because this is happend in the
      // constructor)
      ApplicationContext.applicationContext.getApplication().notice(message)
    }
  }

  fun error(message: String) {
    var send = false
    val listeners = modelListener.listenerList
    for (i in listeners.size - 2 downTo 0 step 2) {
      if (listeners[i] == MessageListener::class.java) {
        (listeners[i + 1] as MessageListener).error(message)
        send = true
      }
    }
    if (!send) {
      // use a 'default listener' that the message is
      // not lost (e.g .because this is happened in the
      // constructor)
      ApplicationContext.applicationContext.getApplication().error(message)
    }
  }

  /**
   * Displays a warning message.
   */
  fun warn(message: String) {
    var send = false
    val listeners = modelListener.listenerList
    for (i in listeners.size - 2 downTo 0 step 2) {
      if (listeners[i] == MessageListener::class.java) {
        (listeners[i + 1] as MessageListener).warn(message)
        send = true
      }
    }
    if (!send) {
      // use a 'default listener' that the message is
      // not lost (e.g .because this is happend in the
      // constructor)
      ApplicationContext.applicationContext.getApplication().warn(message)
    }
  }

  /**
   * Displays an ask dialog box
   */
  fun ask(message: String, yesIsDefault: Boolean = false): Boolean {
    val listeners = modelListener.listenerList

    for (i in listeners.size - 2 downTo 0 step 2) {
      if (listeners[i] == MessageListener::class.java) {
        val value = (listeners[i + 1] as MessageListener).ask(message, yesIsDefault)

        when (value) {
          MessageListener.AWR_YES -> return true
          MessageListener.AWR_NO -> return false
          MessageListener.AWR_UNDEF -> {
          }
          else -> {
          }
        }
      }
    }
    return false
  }

  open fun getTitle(): String = windowTitle + if (extraTitle != null) " $extraTitle" else ""

  /**
   * Sets a the text to be appended to the title.
   */
  fun appendToTitle(text: String) {
    extraTitle = text
    display?.setTitle(getTitle())
  }

  /**
   * change the title of this form
   */
  open fun setTitle(title: String) {
    this.windowTitle = title
    display?.setTitle(getTitle())
  }

  /**
   * addCommand in menu
   * Called by code generated by the compiler
   */
  fun setActors(actorDefs: Array<VActor>) {
    if (actors.isEmpty()) {
      actors = ArrayList(actorDefs.size)
    }
    actors.addAll(actorDefs)
  }

  open fun getActor(at: Int): VActor = actors[at + 1] // "+1" because of the f12-Actor

  fun getActors(): ArrayList<VActor> {
    return actors
  }

  /**
   * Enables/disables the actor.
   */
  open fun setActorEnabled(position: Int, enabled: Boolean) {
    val actor: VActor = getActor(position)
    actor.handler = this
    actor.isEnabled = enabled
  }
  // ----------------------------------------------------------------------
  // LOCALIZATION
  // ----------------------------------------------------------------------
  /**
   * Localizes the actors of this window
   *
   * @param     manager         the manger to use for localization
   */
  fun localizeActors(manager: LocalizationManager) {
    actors.forEach {
      it.localize(manager)
    }
  }

  /**
   * close model if allowed
   */
  fun willClose(code: Int) {
    close(code)
  }

  /**
   * Inform close linstener that this model was closed
   */
  fun close(code: Int) {
    val listeners = modelListener.listenerList
    for (i in listeners.size - 2 downTo 0 step 2) {
      if (listeners[i] == ModelCloseListener::class.java) {
        (listeners[i + 1] as ModelCloseListener).modelClosed(code)
      }
    }
  }

  //----------------------------------------------------------------------
  // VMODEL IMPLEMENTATION
  //----------------------------------------------------------------------
  override fun getDisplay(): UWindow? = display

  override fun setDisplay(display: UComponent) {
    assert(display is UWindow) { "VWindow display should be instance of UWindow" }
    this.display = display as UWindow
    setTitle(windowTitle)
  }

  // ----------------------------------------------------------------------
  // UTILS
  // ----------------------------------------------------------------------
  /**
   * setInformationText
   */
  fun setInformationText(text: String) {
    display?.setInformationText(text)
  }

  /**
   * setProgressDialog
   */
  fun setProgressDialog(message: String, currentJob: Int) {
    val listeners = modelListener.listenerList
    for (i in listeners.size - 2 downTo 0 step 2) {
      if (listeners[i] == ProgressDialogListener::class.java) {
        (listeners[i + 1] as ProgressDialogListener).setProgressDialog(message, currentJob)
      }
    }
  }

  fun setTotalJobs(totalJobs: Int) {
    getDisplay()?.setTotalJobs(totalJobs)
  }

  fun setCurrentJob(currentJob: Int) {
    getDisplay()?.setCurrentJob(currentJob)
  }

  fun unsetProgressDialog() {
    val listeners = modelListener.listenerList
    for (i in listeners.size - 2 downTo 0 step 2) {
      if (listeners[i] == ProgressDialogListener::class.java) {
        (listeners[i + 1] as ProgressDialogListener).unsetProgressDialog()
      }
    }
  }

  fun updateWaitDialogMessage(message: String) {
    getDisplay()?.updateWaitDialogMessage(message)
  }

  /**
   * setWaitInfo
   */
  fun setWaitDialog(message: String, maxtime: Int) {
    val listeners = modelListener.listenerList
    for (i in listeners.size - 2 downTo 0 step 2) {
      if (listeners[i] == WaitDialogListener::class.java) {
        (listeners[i + 1] as WaitDialogListener).setWaitDialog(message, maxtime)
      }
    }
  }

  /**
   * change mode to free state
   */
  fun unsetWaitDialog() {
    val listeners = modelListener.listenerList
    for (i in listeners.size - 2 downTo 0 step 2) {
      if (listeners[i] == WaitDialogListener::class.java) {
        (listeners[i + 1] as WaitDialogListener).unsetWaitDialog()
      }
    }
  }

  /**
   * setWaitInfo
   */
  fun setWaitInfo(message: String) {
    val listeners = modelListener.listenerList
    for (i in listeners.size - 2 downTo 0 step 2) {
      if (listeners[i] == WaitInfoListener::class.java) {
        (listeners[i + 1] as WaitInfoListener).setWaitInfo(message)
      }
    }
  }

  /**
   * change mode to free state
   */
  fun unsetWaitInfo() {
    val listeners = modelListener.listenerList
    for (i in listeners.size - 2 downTo 0 step 2) {
      if (listeners[i] == WaitInfoListener::class.java) {
        (listeners[i + 1] as WaitInfoListener).unsetWaitInfo()
      }
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------
  open fun getType(): Int = Constants.MDL_UNKOWN

  fun enableCommands() {
    f12.isEnabled = (true)
  }

  fun setCommandsEnabled(enable: Boolean) {
    f12.isEnabled = enable
  }

  /**
   * Performs a void trigger
   *
   * @param        VKT_Type        the number of the trigger
   */
  override fun executeVoidTrigger(VKT_Type: Int) {
    if (VKT_Type == Constants.CMD_GOTO_SHORTCUTS) {
      try {
        ApplicationContext.getMenu().getDisplay().gotoShortcuts()
      } catch (npe: NullPointerException) {
        throw VExecFailedException(VlibProperties.getString("shortcuts-not-available"))
      }
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION OF DBContextHandler
  // ----------------------------------------------------------------------
  /**
   * Returns true if the exception allows a retry of the
   * transaction, false in the other case.
   *
   * @param reason the reason of the transaction failure
   * @return true if a retry is possible
   */
  fun retryableAbort(reason: Exception): Boolean {
    if (reason is DBDeadLockException) {
      return true
    }
    return reason is XInterruptProtectedException
  }

  /**
   * Asks the user, if she/he wants to retry the exception
   *
   * @return true, if the transaction should be retried.
   */
  fun retryProtected(): Boolean = ask(MessageCode.getMessage("VIS-00039"))

  /**
   * return wether this object handle a transaction at this time
   */
  fun inTransaction(): Boolean = isProtected

  /**
   * Returns the current user name
   */
  open fun getUserName(): String? = dBContext.defaultConnection.userName

  /**
   * Returns the user ID
   */
  open fun getUserID(): Int = dBContext.defaultConnection.getUserID()

  // ----------------------------------------------------------------------
  // MESSAGES HANDLING
  // ----------------------------------------------------------------------
  /**
   * Formats the message having the given identifier from the given source.
   *
   * @param     ident the message identifier
   * @param     param message parameter
   * @return    the requested message
   */
  protected fun formatMessage(ident: String, param: Any? = null): String? =
          formatMessage(ident, param, null)

  /**
   * Formats the message having the given identifier from the given source.
   *
   * @param     ident  the message identifier
   * @param     param1 the first message parameter
   * @param     param1 the second message parameter
   * @return    the requested message
   */
  protected fun formatMessage(ident: String, param1: Any?, param2: Any?): String? =
          formatMessage(ident, arrayOf(param1, param2))

  /**
   * Formats the message having the given identifier from the given source.
   *
   * @param     ident  the message identifier
   * @param     params the message parameters
   * @return    the requested message
   */
  protected fun formatMessage(ident: String, params: Array<Any?>): String? {
    return if (source != null) {
      Message.getMessage(source!!, ident, params)
    } else {
      null
    }
  }

  /**
   * Try to handle an exception
   */
  fun fatalError(data: Any, line: String, reason: Throwable) {
    TODO()
  }

  // ----------------------------------------------------------------------
  // Listener
  // ----------------------------------------------------------------------
  fun addMessageListener(ml: MessageListener) =
          modelListener.add(MessageListener::class.java, ml)

  fun removeMessageListener(ml: MessageListener) =
          modelListener.remove(MessageListener::class.java, ml)

  fun addWaitInfoListener(wil: WaitInfoListener) =
          modelListener.add(WaitInfoListener::class.java, wil)

  fun removeWaitInfoListener(wil: WaitInfoListener) =
          modelListener.remove(WaitInfoListener::class.java, wil)

  fun addWaitDialogListener(wil: WaitDialogListener) =
          modelListener.add(WaitDialogListener::class.java, wil)

  fun removeWaitDialogListener(wil: WaitDialogListener) =
          modelListener.remove(WaitDialogListener::class.java, wil)

  fun addProgressDialogListener(wil: ProgressDialogListener) =
          modelListener.add(ProgressDialogListener::class.java, wil)

  fun removeProgressDialogListener(wil: ProgressDialogListener) =
          modelListener.remove(ProgressDialogListener::class.java, wil)

  fun addVActionListener(al: VActionListener) =
          modelListener.add(VActionListener::class.java, al)

  fun removeVActionListener(al: VActionListener) =
          modelListener.remove(VActionListener::class.java, al)

  fun addModelCloseListener(mcl: ModelCloseListener) =
          modelListener.add(ModelCloseListener::class.java, mcl)

  fun removeModelCloseListener(mcl: ModelCloseListener) =
          modelListener.remove(ModelCloseListener::class.java, mcl)

  //--------------------------------------------------------------------
  // FILE PRODUCTION LISTENERS HANDLING
  // --------------------------------------------------------------------
  /**
   * Adds a listener to the list that's notified each time a production
   * of the report file occurs.
   *
   * @param l The FileProductionListener
   */
  fun addFileProductionListener(l: FileProductionListener) =
          listenerList.add(FileProductionListener::class.java, l)

  /**
   * Removes a listener from the list that's notified each time a
   * production of the report file occurs.
   *
   * @param l The FileProductionListener
   */
  fun removeReportListener(l: FileProductionListener) =
          listenerList.remove(FileProductionListener::class.java, l)
  /**
   * Notifies all listeners that the report file is produced.
   */
  /**
   * Notifies all listeners that the report file is produced.
   */
  fun fireFileProduced(file: File, name: String = file.name) {
    val listeners = listenerList.listenerList
    for (i in listeners.size - 2 downTo 0 step 2) {
      if (listeners[i] == FileProductionListener::class.java) {
        (listeners[i + 1] as FileProductionListener).fileProduced(file, name)
      }
    }
  }

  companion object {
    // ----------------------------------------------------------------------
    // DEBUGGING
    // ----------------------------------------------------------------------
    protected fun threadInfo(): String = "Thread: ${Thread.currentThread()}\n"

    // ----------------------------------------------------------------------
    // STATIC DATA MEMBERS
    // ----------------------------------------------------------------------
    const val CDE_QUIT = 0
    const val CDE_ESCAPED = 1
    const val CDE_VALIDATE = 2
    const val WINDOW_LOCALIZATION_RESOURCE = "org/kopi/galite/Window"
  }
}
