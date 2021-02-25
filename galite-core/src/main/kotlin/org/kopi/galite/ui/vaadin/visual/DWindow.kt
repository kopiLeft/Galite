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
package org.kopi.galite.ui.vaadin.visual

import java.io.File
import java.io.Serializable
import java.util.concurrent.ConcurrentLinkedQueue

import org.kopi.galite.base.Utils
import org.kopi.galite.ui.vaadin.actor.Actor
import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler
import org.kopi.galite.ui.vaadin.base.BackgroundThreadHandler.access
import org.kopi.galite.ui.vaadin.main.MainWindow
import org.kopi.galite.ui.vaadin.notif.AbstractNotification
import org.kopi.galite.ui.vaadin.notif.ConfirmNotification
import org.kopi.galite.ui.vaadin.notif.ErrorNotification
import org.kopi.galite.ui.vaadin.notif.InformationNotification
import org.kopi.galite.ui.vaadin.notif.NotificationListener
import org.kopi.galite.ui.vaadin.notif.WarningNotification
import org.kopi.galite.ui.vaadin.progress.ProgressDialog
import org.kopi.galite.ui.vaadin.wait.WaitDialog
import org.kopi.galite.ui.vaadin.wait.WaitWindow
import org.kopi.galite.ui.vaadin.window.PopupWindow
import org.kopi.galite.ui.vaadin.window.VActorPanel
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.MessageCode
import org.kopi.galite.visual.MessageListener
import org.kopi.galite.visual.PropertyException
import org.kopi.galite.visual.UWindow
import org.kopi.galite.visual.VActor
import org.kopi.galite.visual.VRuntimeException
import org.kopi.galite.visual.VWindow
import org.kopi.galite.visual.VlibProperties
import org.kopi.galite.visual.WaitInfoListener

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.server.ErrorEvent
import com.vaadin.flow.server.ErrorHandler

/**
 * The `DWindow` is an abstract implementation of an [UWindow] component.
 *
 * @param model The window model.
 */
abstract class DWindow protected constructor(private val model: VWindow) : Div(), UWindow {

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
  private val actors : VActorPanel = VActorPanel()
  private var runtimeDebugInfo: Throwable? = null

  /**
   * Returns the exist code of this window.
   * @return The exist code of this window.
   */
  var returnCode = 0
    private set
  private var progressDialog: ProgressDialog = ProgressDialog()
  private var isProgressDialogAttached = false
  private var waitDialog: WaitDialog = WaitDialog()
  private var isWaitDialogAttached = false

  /**
   * Returns `true` if the used has been asked for a request.
   * @return `true` if the used has been asked for a request.
   */
  var isUserAsked = false
    private set
  private val actionRunner: ActionRunner = ActionRunner()
  private val actionsQueue: ConcurrentLinkedQueue<QueuedAction> = ConcurrentLinkedQueue<QueuedAction>()

  init {
    setCaption(model.getTitle())
    createEditMenu()
    model.addVActionListener(this)
    model.addModelCloseListener(this)
    model.addWaitDialogListener(this)
    model.addProgressDialogListener(this)
    model.addFileProductionListener(this)
    model.addWaitInfoListener(waitInfoHandler)
    model.addMessageListener(messageHandler)
    addActorsToGUI(model.actors)
    addAttachDetachListeners()
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Sets the window caption.
   * @param caption The window caption.
   */
  fun setCaption(caption: String) {

    // first look if we can set the title on the main window.
    val success: Boolean = maybeSetMainWindowCaption(caption)
    if (!success) {
      // window does not belong to main window
      // It may be then belong to a popup window
      maybeSetPopupWindowCaption(caption)
    }
  }

  /**
   * Sets the window caption if it belongs to the main window.
   * @param caption The window caption.
   * @return `true` if the caption is set.
   */
  private fun maybeSetMainWindowCaption(caption: String): Boolean {
    var parent: MainWindow? = null
    this.parent.ifPresent {
      parent = it as MainWindow
    }
    if (parent != null) {
      parent!!.updateWindowTitle(this, caption)
      return true
    }
    return false
  }

  /**
   * Sets the window caption if it belongs to a popup window.
   * @param caption The window caption.
   * @return `true` if the caption is set.
   */
  private fun maybeSetPopupWindowCaption(caption: String): Boolean {
    var parent: PopupWindow? = null
    this.parent.ifPresent {
      parent = it as PopupWindow
    }
    if (parent != null) {
      parent!!.setCaption(caption)
      return true
    }
    return false
  }

  /**
   * Sets the window content.
   * @param content The window content.
   */
  open fun setContent(content: Component) {
    add(content)
  }

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
          // model.notifyAll() TODO
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
    // Add actors panel
    add(actors)
    // Add each actor to the panel
    actorDefs.forEach { actorDef ->
      val actor = DActor(actorDef!!)
      addActor(actor)
    }
  }

  /**
   * Adds an actor to this window.
   * @param actor The actor to be added.
   */
  fun addActor(actor: Actor) {
    actors.addActor(actor)
  }

  /**
   * Adds progress bar and wait dialog attach and
   * detach listeners
   */
  private fun addAttachDetachListeners() {
    // BackgroundThreadHandler.access(Runnable { TODO
    progressDialog.addAttachListener {
      isProgressDialogAttached = true
    }
    progressDialog.addDetachListener {
      isProgressDialogAttached = false
    }
    waitDialog.addAttachListener {
      isWaitDialogAttached = true
    }
    waitDialog.addDetachListener {
      isWaitDialogAttached = false
    }
    //})
  }

  override fun performBasicAction(action: Action) {
    //BackgroundThreadHandler.access(Runnable {  TODO
    performActionImpl(action, false)
    //})
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
    getModel().setCommandsEnabled(false)
    runtimeDebugInfo = RuntimeException(currentAction.toString())
    if (!asynch || !getModel().allowAsynchronousOperation()) {
      // synchronus call
      actionRunner.run()
      if (getModel() != null) {
        // actions which close the window also
        // set the referenced model to null
        getModel().executedAction(currentAction)
      }
    } else {
      val currentThread = Thread(actionRunner)
      currentThread.start()
    }
  }

  /**
   * Disposes the window. Finalize and close this window.
   */
  private fun dispose() {
    // close the window by removing it from the application.
    // this should not be called in a separate transaction.
    // Modal windows are attached to a popup window. So it is not closed
    // like not modal windows. We should remove the popup window from the application
    val application = application
    if (parent.get() is PopupWindow) {
      // it is a modal window ==> we remove its parent
      application.removeWindow(parent.get())
    } else {
      // it is not a modal window, we need to remove it from the application.
      application.removeWindow(this)
    }
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
      model.removeVActionListener(this)
      model.removeWaitInfoListener(waitInfoHandler)
      model.removeMessageListener(messageHandler)
    }
    inAction = false
    currentAction = null
    runtimeDebugInfo = null
    returnCode = -1
    isProgressDialogAttached = false
    isWaitDialogAttached = false
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
    // footPanel.setStatisticsText(text);
  }

  //--------------------------------------------------------------
  // UWINDOW IMPLEMENTATION
  //--------------------------------------------------------------
  override fun setTotalJobs(totalJobs: Int) {
    //BackgroundThreadHandler.access(Runnable { TODO
    synchronized(progressDialog) {
      if (isProgressDialogAttached) {
        progressDialog.totalJobs = totalJobs
      }
    }
    //})
  }

  override fun performAsyncAction(action: Action) {
    //BackgroundThreadHandler.access(Runnable { TODO
    performActionImpl(action, true)
    //})
  }

  override fun modelClosed(type: Int) {
    close(type)
  }

  override fun setWaitDialog(message: String, maxtime: Int) {
    //BackgroundThreadHandler.access(Runnable { TODO
    synchronized(waitDialog) {
      waitDialog.setTitle(MessageCode.getMessage("VIS-00067"))
      waitDialog.setMessage(message)
      waitDialog.setMaxTime(maxtime)
      if (!isWaitDialogAttached) {
        application.attachComponent(waitDialog)
      }
    }
    //})
  }

  override fun unsetWaitDialog() {
    //BackgroundThreadHandler.access(Runnable { TODO
    synchronized(waitDialog) {
      if (isWaitDialogAttached) {
        waitDialog.setTitle(null)
        waitDialog.setMessage(null)
        waitDialog.setMaxTime(0)
        application.detachComponent(waitDialog)
      }
    }
    //})
  }

  override fun setProgressDialog(message: String, totalJobs: Int) {
    //BackgroundThreadHandler.access(Runnable { TODO
    synchronized(progressDialog) {
      progressDialog.setTitle(MessageCode.getMessage("VIS-00067"))
      progressDialog.setMessage(message)
      progressDialog.totalJobs = totalJobs
      if (!isProgressDialogAttached) {
        application.attachComponent(progressDialog)
      }
    }
    //})
  }

  override fun unsetProgressDialog() {
    //BackgroundThreadHandler.access(Runnable { TODO
    synchronized(progressDialog) {
      if (isProgressDialogAttached) {
        progressDialog.setTitle(null)
        progressDialog.setMessage(null)
        progressDialog.totalJobs = 0
        application.detachComponent(progressDialog)
      }
    }
    //})
  }

  override fun getModel(): VWindow {
    return model
  }

  override fun setCurrentJob(currentJob: Int) {
    // access { TODO
    synchronized(progressDialog) {
      if (isProgressDialogAttached) {
        progressDialog.setProgress(currentJob)
      }
    }
    //})
  }

  override fun setTitle(title: String) {
    // access { TODO
    setCaption(title)
    //})
  }

  override fun setInformationText(text: String?) {
    // footPanel.setInformationText(text);
  }

  override fun updateWaitDialogMessage(message: String) {
    // access { TODO
    synchronized(waitDialog) {
      if (isWaitDialogAttached) {
        waitDialog.setMessage(message)
      }
    }
    //})
  }

  override fun setWindowFocusEnabled(enabled: Boolean) {
    // do nothing
  }

  override fun setWaitInfo(message: String) {
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
    if (!getModel().allowQuit()) {
      return
    }
    getModel().willClose(VWindow.CDE_QUIT)
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
   * Sets the window error.
   * @param e The exception cause.
   */
  protected fun setWindowError(e: Throwable?) {
    // TODO
  }

  /**
   * Reports if a message is shown while in a transaction.
   * @param The message to be displayed.
   */
  protected fun verifyNotInTransaction(message: String) {
    if (getModel().inTransaction() && debugMessageInTransaction()) {
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
    protected get() = ApplicationContext.applicationContext.getApplication() as VApplication

  //--------------------------------------------------------------
  // MESSAGELISTENER IMPLEMENTATION
  //--------------------------------------------------------------
  /**
   * The `MessageHandler` is the window implementation
   * of the [MessageListener].
   */
  internal inner class MessageHandler : MessageListener {
    override fun notice(message: String) {
      val dialog = InformationNotification(VlibProperties.getString("Notice"), message, notificationLocale)

      showNotification(dialog)
    }

    override fun error(message: String?) {
      val dialog = ErrorNotification(VlibProperties.getString("Error"), message, notificationLocale)

      showNotification(dialog)
    }

    override fun warn(message: String) {
      val dialog = WarningNotification(VlibProperties.getString("Warning"), message, notificationLocale)

      showNotification(dialog)
    }

    /**
     * Displays a request dialog for a user interaction.
     * @param message The message to be displayed in the dialog box.
     */
    fun ask(message: String): Boolean {
      return ask(message, false) == MessageListener.AWR_YES
    }

    override fun ask(message: String, yesIsDefault: Boolean): Int {
      val dialog = ConfirmNotification(VlibProperties.getString("Question"), message, notificationLocale)

      dialog.yesIsDefault = yesIsDefault
      dialog.addNotificationListener(object : NotificationListener {
        override fun onClose(yes: Boolean) {
          value = if (yes) {
            MessageListener.AWR_YES
          } else {
            MessageListener.AWR_NO
          }
        }
      })
      // attach the notification to the application.
      showNotification(dialog)
      return value
    }

    private val notificationLocale get() = application.defaultLocale.toString()

    /**
     * Shows a notification.
     * @param notification The notification to be shown
     */
    internal fun showNotification(notification: AbstractNotification) {
      access {
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
    private var iswaitIndicatorAttached = false

    //-----------------------------------------------------------
    // CONSTRUCTOR
    //-----------------------------------------------------------
    init {
      // add attach and detach listeners to detect
      // wait indicator state.
      // access { TODO
      waitIndicator.addAttachListener {
        iswaitIndicatorAttached = true
      }
      waitIndicator.addDetachListener {
        iswaitIndicatorAttached = false
      }
      //})
    }

    //-----------------------------------------------------------
    // IMPLEMENTATIONS
    //-----------------------------------------------------------
    override fun setWaitInfo(message: String) {
      //BackgroundThreadHandler.access(Runnable { TODO
      synchronized(waitIndicator) {
        waitIndicator.setText(message)
        if (!iswaitIndicatorAttached) {
          application.attachComponent(waitIndicator)
        }
      }
      //})
    }

    override fun unsetWaitInfo() {
      //BackgroundThreadHandler.access(Runnable { TODO
      synchronized(waitIndicator) {
        if (iswaitIndicatorAttached) {
          waitIndicator.setText(null)
          application.detachComponent(waitIndicator)
        }
      }
      //})
    }
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
          getModel().setCommandsEnabled(true)
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
      setWindowError(exc)
      if (getModel() != null) {
        getModel().fatalError(getModel(), "VWindow.performActionImpl(final Action action)", exc)
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
        getModel().executedAction(currentAction)
      }
    }

    /**
     * Executed when action execution is terminated
     */
    protected fun endAction() {
      setInAction()
      /*synchronized(application) { BackgroundThreadHandler.updateUI() } TODO
      application.setErrorHandler(null)*/
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
      //BackgroundThreadHandler.access(Runnable {  TODO
      performActionImpl(action, asynch)
      //})
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
    access {
      // TODO: Use InformationNotification instead, and localize the message
      Dialog().also {
        it.add("File is generated to " + file.absoluteFile)
        it.open()
      }
    }
  }
}
