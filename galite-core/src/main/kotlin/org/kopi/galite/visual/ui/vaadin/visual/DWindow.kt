/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2021 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.visual

import java.io.File
import java.io.Serializable
import java.util.concurrent.ConcurrentLinkedQueue

import org.kopi.galite.visual.base.Utils
import org.kopi.galite.visual.ui.vaadin.actor.VActorsNavigationPanel
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.accessAndPush
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.releaseLock
import org.kopi.galite.visual.ui.vaadin.base.BackgroundThreadHandler.startAndWaitAndPush
import org.kopi.galite.visual.ui.vaadin.base.Utils.findMainWindow
import org.kopi.galite.visual.ui.vaadin.notif.AbstractNotification
import org.kopi.galite.visual.ui.vaadin.notif.ConfirmNotification
import org.kopi.galite.visual.ui.vaadin.notif.ErrorNotification
import org.kopi.galite.visual.ui.vaadin.notif.InformationNotification
import org.kopi.galite.visual.ui.vaadin.notif.NotificationListener
import org.kopi.galite.visual.ui.vaadin.notif.WarningNotification
import org.kopi.galite.visual.ui.vaadin.progress.ProgressDialog
import org.kopi.galite.visual.ui.vaadin.wait.WaitDialog
import org.kopi.galite.visual.ui.vaadin.wait.WaitWindow
import org.kopi.galite.visual.ui.vaadin.window.PopupWindow
import org.kopi.galite.visual.ui.vaadin.window.Window
import org.kopi.galite.visual.visual.Action
import org.kopi.galite.visual.visual.ApplicationContext
import org.kopi.galite.visual.visual.MessageCode
import org.kopi.galite.visual.visual.MessageListener
import org.kopi.galite.visual.visual.PropertyException
import org.kopi.galite.visual.visual.UWindow
import org.kopi.galite.visual.visual.VActor
import org.kopi.galite.visual.visual.VRuntimeException
import org.kopi.galite.visual.visual.VWindow
import org.kopi.galite.visual.visual.VlibProperties
import org.kopi.galite.visual.visual.WaitInfoListener

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.Shortcuts
import com.vaadin.flow.component.UI
import com.vaadin.flow.server.ErrorEvent
import com.vaadin.flow.server.ErrorHandler

/**
 * The `DWindow` is an abstract implementation of an [UWindow] component.
 *
 * @param model The window model.
 */
abstract class DWindow protected constructor(private var model: VWindow?) : Window(), UWindow {

  //--------------------------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------------------------
  private val waitInfoHandler: WaitInfoHandler = WaitInfoHandler()
  private val messageHandler: MessageHandler = MessageHandler()

  /**
   * `true` if an action is being performed.
   */
  var inAction = false
    private set
  private var currentAction: Action? = null
  private val askUser = false
  private var runtimeDebugInfo: Throwable? = null

  /**
   * Returns the exist code of this window.
   * @return The exist code of this window.
   */
  var returnCode = 0
    private set
  private var progressDialog: ProgressDialog = ProgressDialog()
  private var waitDialog: WaitDialog = WaitDialog()

  /**
   * Returns `true` if the used has been asked for a request.
   * @return `true` if the used has been asked for a request.
   */
  var isUserAsked = false
    private set
  private val actionRunner: ActionRunner = ActionRunner()
  private val actionsQueue: ConcurrentLinkedQueue<QueuedAction> = ConcurrentLinkedQueue<QueuedAction>()

  init {
    createEditMenu()
    model!!.addVActionListener(this)
    model!!.addModelCloseListener(this)
    model!!.addWaitDialogListener(this)
    model!!.addProgressDialogListener(this)
    model!!.addFileProductionListener(this)
    model!!.addWaitInfoListener(waitInfoHandler)
    model!!.addMessageListener(messageHandler)
    addActorsToGUI(model!!.actors)
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Displays an error message.
   * @param message The error message to be displayed.
   */
  fun displayError(message: String?) {
    messageHandler.error(message)
  }

  /**
   * Reports a visual error from a runtime exception.
   * @param e The runtime exception.
   */
  open fun reportError(e: VRuntimeException) {
    if (e.message != null) {
      displayError(e.message)
    }
  }

  /**
   * Closes the view and the model definitely.
   * @param code The exit code
   * @see .closeWindow
   */
  protected fun close(code: Int) {
    val model: VWindow? = model //destroyed in release()
    try {
      release()
      dispose()
      model?.destroyModel()
    } finally {
      // model can be destroyed here
      if (model != null) {
        synchronized(model) {

          // set the return code
          returnCode = code
          // Inform all threads who wait for this panel
          (model as Object).notifyAll()
        }
      }
    }
  }

  /**
   * Allow building of a customized edit menu.
   */
  fun createEditMenu() {}

  /**
   * Adds a command in the menu bar.
   * @param actorDefs The [VActor] definitions.
   */
  private fun addActorsToGUI(actorDefs: Array<VActor?>) {
    val panel = VActorsNavigationPanel()

    // Add actors panel
    add(actors)
    // Add each actor to the panel
    actorDefs.forEach { actorDef ->
      val actor = DActor(actorDef!!)

      panel.addActor(actor, navigationMenu)
      if(actor.icon != null) {
        addActor(actor)
      }
      registerShortcutKey(actor, actor.acceleratorKey, actor.modifiersKey)
      addActorsNavigationPanel(panel)
    }
  }

  /**
   * Registers a shortcut key on this window.
   */
  private fun registerShortcutKey(actor: DActor, acceleratorKey: Key, modifiersKey: KeyModifier?) {
    val registration = if (modifiersKey != null) {
      Shortcuts.addShortcutListener(this, actor::shortcutActionPerformed, acceleratorKey, modifiersKey)
    } else {
      Shortcuts.addShortcutListener(this, actor::shortcutActionPerformed, acceleratorKey)
    }

    registration.isBrowserDefaultAllowed = false
    registration.listenOn(this)
  }

  override fun performBasicAction(action: Action) {
    access(currentUI) {
      performActionImpl(action, false)
    }
  }

  /**
   * Performs the appropriate action asynchronously or synchronously.
   *
   *
   * You can use this method to perform any operation out of the UI event process
   *
   * @param action The [Action] to be executed.
   * @param asynch Should the action run asynchronously ?
   */
  private fun performActionImpl(action: Action, asynch: Boolean) {
    if (inAction) {
      // put the action in the queue to be executed after the current action is finished
      actionsQueue.add(QueuedAction(action, asynch))
      // it can be that setInAction is called before queuing the action.
      // we test again if the current action is released, we fire manually the execution
      // queue.
      if (currentAction == null) {
        setInAction()
      }
      return
    }
    inAction = true
    currentAction = action
    getModel()!!.setCommandsEnabled(false)
    runtimeDebugInfo = RuntimeException(currentAction.toString())
    if (!asynch || !getModel()!!.allowAsynchronousOperation()) {
      // synchronus call
      actionRunner.run()
      if (getModel() != null) {
        // actions which close the window also
        // set the referenced model to null
        getModel()!!.executedAction(currentAction)
      }
    } else {
      val currentThread = Thread(actionRunner)
      // Force the current UI in case the thread is started before attaching the window to the UI.
      if (currentUI == null) {
        currentUI = BackgroundThreadHandler.locateUI()
      }
      currentThread.start()
    }
  }

  /**
   * Disposes the window. Finalize and close this window.
   */
  private fun dispose() {
    // close the window by removing it from the application.
    // this should not be called in a separate transaction.
    val mainWindow = findMainWindow()

    access(currentUI) {
      if(!closeIfIsPopup(this)) {
        mainWindow?.removeWindow(this)
      }
    }
  }

  /**
   * Close [window] if it is a popup window
   *
   * @param window window to close
   * @return true is [window] is a popup and it was closed
   */
  private fun closeIfIsPopup(window: Component): Boolean {
    var closed  = false
    var popupWindow: PopupWindow? = null

    if(window is PopupWindow) {
      popupWindow = window
    } else {
      window.parent.ifPresent {
        it.parent.ifPresent { windowContainer ->
          if (windowContainer is PopupWindow) {
            popupWindow = windowContainer
          }
        }
      }
    }

    popupWindow?.let {
      it.close() // fire close event
      closed = true
    }

    return closed
  }

  /**
   *
   * Removes all registered listeners on this window.
   *
   * Removes all registered actions on this window
   *
   * Removes all components added to this window
   */
  @Synchronized
  open fun release() {
    if (model != null) {
      model!!.removeVActionListener(this)
      model!!.removeWaitInfoListener(waitInfoHandler)
      model!!.removeMessageListener(messageHandler)
    }
    model = null
    inAction = false
    currentAction = null
    runtimeDebugInfo = null
    returnCode = -1
    isUserAsked = false
    actionsQueue.clear()
    Utils.freeMemory()
  }

  /**
   * Use [.closeWindow] or [.close] instead.
   */
  @Deprecated("")
  fun close() {
    closeWindow()
  }

  /**
   * Sets the In action state
   * @see ActionRunner.setInAction
   */
  fun setInAction() {
    actionRunner.setInAction()
  }

  /**
   * Displays a text in the lower right corner of the window.
   * @param text The statistics text.
   */
  fun setStatisticsText(text: String?) {
    // footPanel.setStatisticsText(text); TODO
  }

  //--------------------------------------------------------------
  // UWINDOW IMPLEMENTATION
  //--------------------------------------------------------------
  override fun setTotalJobs(totalJobs: Int) {
    progressDialog.totalJobs = totalJobs
  }

  override fun performAsyncAction(action: Action) {
    access(currentUI) {
      performActionImpl(action, true)
    }
  }

  override fun modelClosed(type: Int) {
    close(type)
  }

  override fun setWaitDialog(message: String, maxtime: Int) {
    access(currentUI) {
      synchronized(waitDialog) {
        waitDialog.setTitle(MessageCode.getMessage("VIS-00067"))
        waitDialog.setMessage(message)
        waitDialog.setMaxTime(maxtime)
        if (!waitDialog.isOpened) {
          waitDialog.open()
        }
        currentUI?.push()
      }
    }
  }

  override fun unsetWaitDialog() {
    access(currentUI) {
      synchronized(waitDialog) {
        if (waitDialog.isOpened) {
          waitDialog.setTitle(null)
          waitDialog.setMessage(null)
          waitDialog.setMaxTime(0)
          waitDialog.close()
        }
        currentUI?.push()
      }
    }
  }

  override fun setProgressDialog(message: String, totalJobs: Int) {
    access(currentUI) {
      synchronized(progressDialog) {
        progressDialog.setTitle(MessageCode.getMessage("VIS-00067"))
        progressDialog.setMessage(message)
        progressDialog.totalJobs = totalJobs
        if (!progressDialog.isOpened) {
          progressDialog.open()
        }
        currentUI?.push()
      }
    }
  }

  override fun unsetProgressDialog() {
    access(currentUI) {
      synchronized(progressDialog) {
        if (progressDialog.isOpened) {
          progressDialog.setTitle(null)
          progressDialog.setMessage(null)
          progressDialog.totalJobs = 0
          progressDialog.close()
        }
        currentUI?.push()
      }
    }
  }

  override fun getModel(): VWindow? {
    return model
  }

  override fun setCurrentJob(currentJob: Int) {
    access(currentUI) {
      synchronized(progressDialog) {
        if (progressDialog.isOpened) {
          progressDialog.setProgress(currentJob)
        }
      }
    }
  }

  override fun setTitle(title: String) {
    accessAndPush(currentUI) {
      setCaption(title)
    }
  }

  override fun setInformationText(text: String?) {
    // footPanel.setInformationText(text);
  }

  override fun updateWaitDialogMessage(message: String) {
    access(currentUI) {
      synchronized(waitDialog) {
        waitDialog.setMessage(message)
        currentUI?.push()
      }
    }
  }

  override fun setWindowFocusEnabled(enabled: Boolean) {
    // do nothing
  }

  override fun setWaitInfo(message: String?) {
    waitInfoHandler.setWaitInfo(message)
  }

  override fun unsetWaitInfo() {
    waitInfoHandler.unsetWaitInfo()
  }

  /**
   * Called to close the view (from the user), it does not
   * definitly close the view(it may ask the user before)
   */
  override fun closeWindow() {
    if (!getModel()!!.allowQuit()) {
      return
    }
    getModel()!!.willClose(VWindow.CDE_QUIT)
  }

  /**
   * Displays the application information.
   * @param message The application information.
   */
  fun showApplicationInformation(message: String) {
//    verifyNotInTransaction("DWindow.showApplicationInformation(" + message + ")");
//
//    messageBox = MessageBox.showPlain(Icon.INFO,
//    		                      VlibProperties.getString("Notice"),
//    		                      message,
//    		                      ButtonId.CLOSE);
//    messageBox.getButton(ButtonId.CLOSE).setCaption(VlibProperties.getString("CLOSE"));
  }

  /**
   * Reports if a message is shown while in a transaction.
   * @param The message to be displayed.
   */
  protected fun verifyNotInTransaction(message: String) {
    if (getModel()!!.inTransaction() && debugMessageInTransaction()) {
      try {
        ApplicationContext.reportTrouble("DWindow",
                                         "$message IN TRANSACTION",
                                         this.toString(),
                                         RuntimeException("displayNotice in Transaction"))
      } catch (e: Throwable) {
        e.printStackTrace()
      }
    }
  }

  /**
   * Returns true if it should be checked whether a message is shown
   * while in a transaction.
   */
  private fun debugMessageInTransaction(): Boolean =
          try {
            ApplicationContext.getDefaults().debugMessageInTransaction()
          } catch (e: PropertyException) {
            false
          }

  /**
   * Returns the current application instance.
   * @return the current application instance.
   */
  protected val application: VApplication
    get() = ApplicationContext.applicationContext.getApplication() as VApplication

  //--------------------------------------------------------------
  // MESSAGELISTENER IMPLEMENTATION
  //--------------------------------------------------------------
  /**
   * The `MessageHandler` is the window implementation
   * of the [MessageListener].
   */
  internal inner class MessageHandler : MessageListener {
    override fun notice(message: String) {
      val dialog = InformationNotification(VlibProperties.getString("Notice"), message, notificationLocale, this@DWindow)
      val lock = Object()

      dialog.addNotificationListener(object : NotificationListener {
        override fun onClose(action: Boolean?) {
          releaseLock(lock)
        }
      })
      showNotification(dialog, lock)
    }

    override fun error(message: String?) {
      val dialog = ErrorNotification(VlibProperties.getString("Error"), message, notificationLocale, this@DWindow)
      val lock = Object()

      dialog.addNotificationListener(object : NotificationListener {
        override fun onClose(action: Boolean?) {
          application.windowError = null // remove any further error.
          releaseLock(lock)
        }
      })
      showNotification(dialog, lock)
    }

    override fun warn(message: String) {
      val dialog = WarningNotification(VlibProperties.getString("Warning"), message, notificationLocale, this@DWindow)
      val lock = Object()

      dialog.addNotificationListener(object : NotificationListener {
        override fun onClose(action: Boolean?) {
          releaseLock(lock)
        }
      })
      showNotification(dialog, lock)
    }

    /**
     * Displays a request dialog for a user interaction.
     * @param message The message to be displayed in the dialog box.
     */
    fun ask(message: String): Boolean {
      return ask(message, false) == MessageListener.AWR_YES
    }

    override fun ask(message: String, yesIsDefault: Boolean): Int {
      val dialog = ConfirmNotification(VlibProperties.getString("Question"), message, notificationLocale, this@DWindow)
      val lock = Object()

      dialog.yesIsDefault = yesIsDefault
      dialog.addNotificationListener(object : NotificationListener {
        override fun onClose(yes: Boolean?) {
          value = if (yes == true) {
            MessageListener.AWR_YES
          } else {
            MessageListener.AWR_NO
          }
          releaseLock(lock)
        }
      })
      showNotification(dialog, lock)
      return value
    }

    private val notificationLocale get() = application.defaultLocale.toString()

    /**
     * Shows a notification.
     * @param notification The notification to be shown
     */
    internal fun showNotification(notification: AbstractNotification, lock: Object) {
      startAndWaitAndPush(lock, currentUI) {
        notification.show()
      }
    }

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    private var value // only for use in ask(...)
            = 0
  }
  //--------------------------------------------------------------
  // WAITINFOLISTENER IMPLEMENTATION
  //--------------------------------------------------------------
  /**
   * The `WaitInfoHandler` is the window implementation
   * of the [WaitInfoListener]
   */
  internal inner class WaitInfoHandler : WaitInfoListener {

    //-----------------------------------------------------------
    // DATA MEMBERS
    //-----------------------------------------------------------
    private val waitIndicator = WaitWindow()

    //-----------------------------------------------------------
    // IMPLEMENTATIONS
    //-----------------------------------------------------------
    override fun setWaitInfo(message: String?) {
      access(currentUI) {
        synchronized(waitIndicator) {
          waitIndicator.setText(message)
          if (!waitIndicator.isOpened) {
            waitIndicator.show()
          }
          currentUI?.push()
        }
      }
    }

    override fun unsetWaitInfo() {
      access(currentUI) {
        synchronized(waitIndicator) {
          if (waitIndicator.isOpened) {
            waitIndicator.setText(null)
            waitIndicator.close()
          }
          currentUI?.push()
        }
      }
    }
  }
  var currentUI: UI? = null

  override fun onAttach(attachEvent: AttachEvent) {
    currentUI = attachEvent.ui
  }
  //--------------------------------------------------------------
  // ACTION RUNNER
  //--------------------------------------------------------------
  /**
   * The `ActionRunner` is the used to run all users
   * [Action].
   *
   *
   * There is only one instance of ActionRunner.
   * It calls user actions.
   */
  internal inner class ActionRunner : Runnable, ErrorHandler {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun run() {
      BackgroundThreadHandler.setUI(currentUI)
      try {
        if (currentAction != null) {
          runAction()
        }
      } catch (v: VRuntimeException) {
        handleRuntimeException(v)
      } catch (exc: Throwable) {
        // when unlocking the session in error handling implementation
        // all statements that previously locked the session will try to
        // release it. But since it is already done, a IllegalMonitorStateException
        // will be thrown. We will ignore this case since it is a fake error and caused
        // by a non standard treatment.
        if (sessionAlreadyUnlocked && exc is IllegalMonitorStateException) {
          sessionAlreadyUnlocked = false
        } else {
          handleAnyException(exc)
        }
      } finally {
        endAction()
      }
    }

    override fun error(event: ErrorEvent) {
      // TODO: not covered by a test.
      try {
        // unlock session when it is locked
        // this will release the communication
        // again and let the popup error to be displayed again.
        /*if (application.ui.get().session.hasLock()) { TODO
          application.ui.get().session.unlock()
          sessionAlreadyUnlocked = true
        }*/
        // The {@link event#getThrowable()} gives the ExecutionException
        // thrown by the AccessFuture created for running the action.
        // The original error is set as the cause of the ExecutionException
        // error so it would be the base of all exception handling here.
        if (event.throwable.cause is VRuntimeException) {
          handleRuntimeException(event.throwable.cause as VRuntimeException)
        } else {
          handleAnyException(event.throwable.cause)
        }
      } finally {
        endAction()
      }
    }

    /**
     * Executes the inner action of this runner.
     */
    @Synchronized
    fun setInAction() {
      try {
        currentAction = null
        inAction = false
        setWindowFocusEnabled(true)
      } finally {
        if (getModel() != null) {
          // commands like "Beenden" destroy the model
          // so it must be tested, that there is still a model
          getModel()!!.setCommandsEnabled(true)
          runNextPendingAction()
        }
      }
    }

    /**
     * Clears the action queue. This will remove all
     * action that can be cancelled.
     */
    protected fun clearActionQueue() {
      val actions = actionsQueue.iterator()
      while (actions.hasNext()) {
        val action = actions.next() as QueuedAction
        if (action.isCancellable) {
          actions.remove()
        }
      }
    }

    /**
     * Handles the visual runtime errors.
     * @param v The runtime error exception
     */
    protected fun handleRuntimeException(v: VRuntimeException) {
      v.printStackTrace()
      // close the wait info window if it is attached to avoid connector hierarchy corruption.
      unsetWaitInfo()
      reportError(v)
      // in case of error cancel all pending and cancellable actions
      clearActionQueue()
    }

    /**
     * Handles any errors that can occur during the action execution.
     * @param exc The exception instance.
     */
    protected fun handleAnyException(exc: Throwable?) {
      exc!!.printStackTrace()
      // close the wait info window if it is attached to avoid connector hierarchy corruption.
      unsetWaitInfo()
      application.windowError = exc
      if (getModel() != null) {
        getModel()!!.fatalError(getModel(), "VWindow.performActionImpl(final Action action)", exc)
      } else {
        application.displayError(null, MessageCode.getMessage("VIS-00041"))
      }
    }

    /**
     * Starts the execution of the action.
     */
    protected fun runAction() {
      // application.setErrorHandler(this) TODO
      currentAction!!.run()
      if (getModel() != null) {
        // actions which close the window also
        // set the referenced model to null
        getModel()!!.executedAction(currentAction)
      }
    }

    /**
     * Executed when action execution is terminated
     */
    protected fun endAction() {
      setInAction()
      synchronized(application) {
        BackgroundThreadHandler.updateUI(currentUI)
      }
      /*application.setErrorHandler(null) TODO */
    }

    /**
     * Runs the next pending action in the queue.
     */
    fun runNextPendingAction() {
      if (!actionsQueue.isEmpty()) {
        val action = actionsQueue.poll()
        action?.execute()
      }
    }

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    private var sessionAlreadyUnlocked = false
  }

  /**
   * Queued action hold information about action delayed in term
   * of execution because there is another action running when
   * this action comes.
   *
   * @param action The kopi action to be executed.
   * @param asynch Asynchronous execution ?
   */
  internal inner class QueuedAction(private val action: Action, private val asynch: Boolean) : Serializable {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    /**
     * Runs this queued action. The action will be executed
     * with window mechanism and will block other actions.
     */
    fun execute() {
      access(currentUI) {
        performActionImpl(action, asynch)
      }
    }

    /**
     * Returns `true` is this action can be cancelled.
     * @return `true` is this action can be cancelled.
     */
    val isCancellable: Boolean
      get() = action.isCancellable()
  }

  //---------------------------------------------------
  // FILE PRODUCTION IMPLEMENTATION
  //---------------------------------------------------

  override fun fileProduced(file: File, name: String) {
    access(currentUI) {
      val downloaderDialog = DownloaderDialog(file, name, application.defaultLocale.toString())

      downloaderDialog.open()
    }
  }
}
