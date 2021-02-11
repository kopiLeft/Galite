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

import org.kopi.galite.ui.vaadin.window.VActorPanel
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.UWindow
import org.kopi.galite.visual.VRuntimeException
import org.kopi.galite.visual.VWindow

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.server.ErrorEvent
import com.vaadin.flow.server.ErrorHandler

/**
 * The `DWindow` is an abstract implementation of an [UWindow] component.
 */
abstract class DWindow protected constructor(private val model: VWindow) : Div(), UWindow {

  //--------------------------------------------------------------
  // DATA MEMBERS
  //--------------------------------------------------------------

  //--------------------------------------------------------------
  private var inAction = false
  private var currentAction: Action? = null
  private val isProgressDialogAttached = false
  private val isWaitDialogAttached = false
  private val askUser = false
  private val actionRunner = ActionRunner()
  private val actionsQueue: ConcurrentLinkedQueue<QueuedAction>? = null
  private val actors : VActorPanel = VActorPanel()
  var runtimeDebugInfo: Throwable? = null

  init {
    add(actors)

    model.addVActionListener(this)
    model.addModelCloseListener(this)
    model.addWaitDialogListener(this)
    model.addProgressDialogListener(this)
    model.addFileProductionListener(this)
  }

  override fun onAttach(attachEvent: AttachEvent?) {
    model.actors.forEach { actor ->
      val dActor = DActor(actor!!)

      if (dActor.icon != null) {
        dActor.isEnabled = isEnabled
        addActor(dActor)
      }
    }
  }

  /**
   * Adds an actor to this window view.
   * @param actor The actor to be added.
   */
  open fun addActor(actor: Component) {
    actors.addActor(actor)
  }

  val returnCode: Int
    get() = TODO()

  /**
   * The current application instance.
   */
  val application: VApplication
    get() =  ApplicationContext.applicationContext.getApplication() as VApplication

  override fun run() {
    // TODO
  }

  override fun getModel(): VWindow {
    return model
  }

  override fun setTitle(title: String) {
    // TODO
  }

  override fun setInformationText(text: String?) {
    // TODO
  }

  override fun setTotalJobs(totalJobs: Int) {
    // TODO
  }

  override fun setCurrentJob(currentJob: Int) {
    // TODO
  }

  override fun updateWaitDialogMessage(message: String) {
    // TODO
  }

  override fun closeWindow() {
    // TODO
  }

  override fun setWindowFocusEnabled(enabled: Boolean) {
    // TODO
  }

  override fun performBasicAction(action: Action) {
    // TODO
  }

  override fun isEnabled(): Boolean {
    // TODO
    return true
  }

  override fun setEnabled(enabled: Boolean) {
    // TODO
  }

  override fun performAsyncAction(action: Action) {
    ui.ifPresent {
      it.access {
        performActionImpl(action, true)
      }
    }
  }

  /**
   * Performs the appropriate action asynchronously or synchronously.
   *
   *
   * You can use this method to perform any operation out of the UI event process
   *
   * @param action The [KopiAction] to be executed.
   * @param asynch Should the action run asynchronously ?
   */
  private fun performActionImpl(action: Action, asynch: Boolean) {
    /*if (inAction == true) {
      // put the action in the queue to be executed after the current action is finished
      actionsQueue.add(QueuedAction(action, asynch))
      // it can be that setInAction is called before queuing the action.
      // we test again if the current action is released, we fire manually the execution
      // queue.
      if (currentAction == null) {
        setInAction()
      }
      return
    }*/

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
        getModel().executedAction(currentAction!!)
      }
    } else {
      val currentThread: Thread = Thread(actionRunner)
      currentThread.start()
    }
  }

  override fun modelClosed(type: Int) {
    // TODO
  }

  override fun setWaitDialog(message: String, maxtime: Int) {
    // TODO
  }

  override fun unsetWaitDialog() {
    // TODO
  }

  override fun setWaitInfo(message: String) {
    // TODO
  }

  override fun unsetWaitInfo() {
    // TODO
  }

  override fun setProgressDialog(message: String, totalJobs: Int) {
    // TODO
  }

  override fun unsetProgressDialog() {
    // TODO
  }

  open fun reportError(e: VRuntimeException) {
    // TODO
  }

  open fun release() {
    // TODO
  }

  /**
   * Sets the window content.
   * @param content The window content.
   */
  open fun setContent(content: Component) {
    add(content)
  }

  //--------------------------------------------------------------
  // INNER CLASSES
  //--------------------------------------------------------------
  /**
   * The `ActionRunner` is the used to run all users
   * [Action].
   *
   *
   * There is only one instance of ActionRunner.
   * It calls user actions.
   */
  inner class ActionRunner : Runnable, ErrorHandler {
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
      TODO()
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
      val actions: MutableIterator<QueuedAction> = actionsQueue!!.iterator()
      while (actions.hasNext()) {
        val action: QueuedAction = actions.next() as QueuedAction
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
    protected fun handleAnyException(exc: Throwable) {
      TODO()
    }

    /**
     * Starts the execution of the action.
     */
    protected fun runAction() {
      //application.setErrorHandler(this)
      currentAction!!.run()
      if (getModel() != null) {
        // actions which close the window also
        // set the referenced model to null
        getModel().executedAction(currentAction!!)
      }
    }

    /**
     * Executed when action execution is terminated
     */
    protected fun endAction() {
      TODO()
    }

    /**
     * Runs the next pending action in the queue.
     */
    fun runNextPendingAction() {
      if (!actionsQueue!!.isEmpty()) {
        val action: QueuedAction? = actionsQueue.poll()
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
   */
  inner class QueuedAction(private val action: Action, private val asynch: Boolean) : Serializable {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    /**
     * Runs this queued action. The action will be executed
     * with window mechanism and will block other actions.
     */
    fun execute() {
      ui.ifPresent {
        it.access {
          performActionImpl(action, asynch)
        }
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
    ui.ifPresent {
      it.access {
        TODO()
      }
    }
  }
}
